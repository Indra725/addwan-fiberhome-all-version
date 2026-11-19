#!/usr/bin/env python3
"""
test_mock.py — simulator ONT (Fiberhome HG6145F1 tiruan) untuk menguji addwan.py
tanpa hardware asli. Jalankan:

    python test_mock.py

Keluarannya PASS/FAIL untuk check/unlock/status/set.
Meng-set env AWAN_TELNET_PORT & AWAN_HTTP_PORT -> 127.0.0.1:2323/8180 (ubah via TEST_*_PORT).
"""

import json
import os
import re
import socket
import subprocess
import sys
import threading
import time
import urllib.parse
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer

TELNET_PORT = int(os.environ.get('TEST_TELNET_PORT', '2323'))
HTTP_PORT = int(os.environ.get('TEST_HTTP_PORT', '8180'))
MAC = '94:6B:12:AB:CD:EF'
PASSWORD = 'Fh@ABCDEF'  # 6 hex terakhir MAC (uppercase)

LOG = []  # (kind, method/params) tercatat dari HTTP
CLI_MODE = ['open']  # 'open' = shell bebas (RP4423) | 'locked' = CLI terkunci (% Unknown command.)


def telnet_server():
    srv = socket.socket()
    srv.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    srv.bind(('127.0.0.1', TELNET_PORT))
    srv.listen(2)
    while True:
        conn, _ = srv.accept()
        threading.Thread(target=handle_client, args=(conn,), daemon=True).start()


def handle_client(conn):
    conn.settimeout(10)
    conn.sendall(b'Fiberhome Telnet\r\nLogin: ')
    transcript = []

    def readline():
        line = b''
        while True:
            ch = conn.recv(1)
            if not ch or ch == b'\n':
                break
            if ch != b'\r':
                line += ch
        transcript.append(line.decode(errors='ignore'))
        return transcript[-1]

    try:
        login = readline()
        assert login == 'gpon', 'login != gpon: %r' % login
        conn.sendall(b'Password: ')
        pw = readline()
        assert pw == PASSWORD, 'password: %r' % pw
        conn.sendall(b'\r\nUser> ')
        cmd = readline()
        assert cmd == 'enable', 'enable != %r' % cmd
        conn.sendall(b'Password: ')
        pw = readline()
        assert pw == PASSWORD, 'enable password: %r' % pw
        conn.sendall(b'\r\nenable ok\r\nConfig# ')
        while True:
            cmd = readline()
            if cmd == '':
                break
            if CLI_MODE[0] == 'locked':
                conn.sendall(b'% Unknown command.\r\nConfig# ')
                continue
            if cmd.strip() == 'shell':
                conn.sendall(b'busybox shell\r\n~ # ')
                continue
            if 'wget' in cmd and 'custom-webui.sh' in cmd:
                conn.sendall(b'saving to /var/custom-webui.sh... 567854 bytes written\r\nConfig# ')
            elif cmd.startswith('chmod'):
                conn.sendall(b'Config# ')
            elif cmd.strip() == '/var/custom-webui.sh':
                conn.sendall(b'Extracting payload...\r\nDone\r\nConfig# ')
            else:
                conn.sendall(b'% Unknown command.\r\nConfig# ')
    except (BrokenPipeError, ConnectionResetError, ConnectionAbortedError, socket.timeout, AssertionError, OSError):
        pass
    conn.close()


class HTTPHandler(BaseHTTPRequestHandler):
    def _send_json(self, obj):
        body = json.dumps(obj).encode()
        self.send_response(200)
        self.send_header('Content-type', 'application/json')
        self.send_header('Content-Length', str(len(body)))
        self.end_headers()
        self.wfile.write(body)

    def _send_raw(self, body, ctype='application/octet-stream'):
        self.send_response(200)
        self.send_header('Content-type', ctype)
        self.send_header('Content-Length', str(len(body)))
        self.end_headers()
        self.wfile.write(body)

    def do_GET(self):
        if self.path == '/html/login_inter.html':
            self._send_raw(b'<html>HG6145F1 mock login</html>', 'text/html')
        elif self.path == '/image/logo_Intelbras_fiberhome.png':
            self._send_raw(b'\x89PNG' + b'0' * 2000, 'image/png')
        else:
            self.send_error(404)

    def do_POST(self):
        if self.path != '/cgi-bin/ajax':
            self.send_error(404)
            return
        length = int(self.headers.get('Content-Length', 0))
        body = self.rfile.read(length).decode()
        params = urllib.parse.parse_qs(body)
        method = params.get('ajaxmethod', [''])[0]
        LOG.append(('cgi', method, params))
        if method == 'get_uplink_info':
            self._send_json({'uplink_info': {'uplink_type': 'dev.eth.2', 'uplink_port': 2}})
        elif method == 'set_uplink_info':
            self._send_json({'set_uplink_info': 'success', 'uplink_type': params.get('uplink_type', [''])[0]})
        else:
            self._send_json({'error': 'unknown method'})

    def log_message(self, *a):
        pass


def run_tool(env, args, stdin=None, timeout=40):
    tool = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'addwan.py')
    return subprocess.run([sys.executable, tool, *args],
                          capture_output=True, text=True, input=stdin,
                          env=env, timeout=timeout)


def main():
    threading.Thread(target=telnet_server, daemon=True).start()
    httpd = ThreadingHTTPServer(('127.0.0.1', HTTP_PORT), HTTPHandler)
    threading.Thread(target=httpd.serve_forever, daemon=True).start()
    time.sleep(0.3)

    env = dict(os.environ)
    env['AWAN_TELNET_PORT'] = str(TELNET_PORT)
    env['AWAN_HTTP_PORT'] = str(HTTP_PORT)

    results = []
    def check(name, cond, detail=''):
        results.append((name, bool(cond)))
        print('[%s] %s%s' % ('PASS' if cond else 'FAIL', name,
                             ('  | ' + detail.replace('\n', ' / ')) if detail and not cond else ''))

    r = run_tool(env, ['check', '127.0.0.1'])
    check('check: TCP telnet&http terbuka', 'TERBUKA' in r.stdout, r.stdout)
    check('check: login page HTTP 200', 'HTTP 200' in r.stdout, r.stdout)
    check('check: custom webui AKTIF', 'AKTIF' in r.stdout, r.stdout)

    r = run_tool(env, ['unlock', '127.0.0.1', MAC], timeout=40)
    check('unlock: login OK', 'login OK' in r.stdout, r.stdout)
    check('unlock: enable handshake OK (mock membalas saving-to)',
          'saving to /var/custom-webui.sh' in r.stdout, r.stdout)
    check('unlock: semua perintah terkirim',
          all(s in r.stdout for s in ('wget', 'chmod', '/var/custom-webui.sh')), r.stdout)

    r = run_tool(env, ['status', '127.0.0.1'])
    check('status: HTTP 200', 'HTTP 200' in r.stdout, r.stdout)
    check('status: dev.eth.2 dari mock', 'dev.eth.2' in r.stdout, r.stdout)

    r = run_tool(env, ['set', '127.0.0.1', 'dev.eth.3'], stdin='SET\n')
    check('set: HTTP 200/success', 'HTTP 200' in r.stdout and 'success' in r.stdout, r.stdout)
    set_calls = [p for _, m, p in LOG if m == 'set_uplink_info']
    check('set: parameter benar di mock', bool(set_calls) and set_calls[0].get('uplink_type') == ['dev.eth.3'], str(set_calls))

    CLI_MODE[0] = 'locked'
    r = run_tool(env, ['unlock', '127.0.0.1', MAC], timeout=40)
    check('locked: tool mendeteksi CLI terkunci', 'CLI ONT TERKUNCI' in r.stdout, r.stdout)
    check('locked: tool memberikan panduan Local Upgrade', 'Local Upgrade' in r.stdout, r.stdout)
    check('locked: keluar bersih (rc=0)', r.returncode == 0, 'rc=%s' % r.returncode)

    print()
    failed = [n for n, ok in results if not ok]
    print('HASIL: %s' % ('SEMUA PASS (%d)' % len(results) if not failed else 'FAIL: %s' % ', '.join(failed)))
    return 1 if failed else 0


if __name__ == '__main__':
    sys.exit(main())
