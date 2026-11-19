#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Add WAN FH — Termux edition (tanpa PC, tanpa ROOT).

Tools sederhana untuk HG6145F1/D2 (firmware RP4423) langsung dari HP lewat Termux:
  1) CEK ONT   — ping TCP 23/80, cek login page & custom webui
  2) UNLOCK    — telnet gpon/Fh@MAC, push custom-webui.sh dari HP, jalankan
  3) STATUS    — get_uplink_info (port mana yang jadi uplink)
  4) SET       — set_uplink_info dev.eth.X (dengan konfirmasi)

Semua koneksi = TCP keluar biasa (telnet/HTTP) — tidak butuh root.
HP harus satu jaringan/WiFi dengan ONT (mis. konek SSID ONT: 192.168.1.1).

Jalankan di Termux:
  python addwan.py            # menu interaktif
  python addwan.py check 192.168.1.1
  python addwan.py unlock 192.168.1.1 94:6b:12:ab:cd:ef
  python addwan.py status 192.168.1.1
  python addwan.py set 192.168.1.1 dev.eth.1
"""

import os
import re
import socket
import sys
import json
import time
import urllib.request
import urllib.error
import urllib.parse
import http.server
import threading

HERE = os.path.dirname(os.path.abspath(__file__))
SCRIPT = os.path.join(HERE, 'custom-webui.sh')
TIMEOUT = 5

TELNET_IAC, TELNET_DONT, TELNET_DO, TELNET_WONT, TELNET_WILL, TELNET_SB, TELNET_SE = 255, 254, 253, 252, 251, 250, 240


def clear():
    os.system('clear' if os.name == 'posix' else 'cls')


def print_banner():
    print('=' * 46)
    print('  ADD WAN FH - Termux Edition')
    print('  Fiberhome HG6145F1/D2 (RP4423)')
    print('=' * 46)


def http_req(method, url, body=None, timeout=TIMEOUT):
    data = body.encode() if body else None
    req = urllib.request.Request(url, data=data, method=method, headers={
        'User-Agent': 'AddWANFH-Termux/1.0',
        'Content-Type': 'application/x-www-form-urlencoded',
    })
    try:
        with urllib.request.urlopen(req, timeout=timeout) as r:
            return r.status, r.read().decode('utf-8', 'ignore')
    except urllib.error.HTTPError as e:
        try:
            return e.code, e.read().decode('utf-8', 'ignore')
        except Exception:
            return e.code, ''
    except Exception as e:
        return 0, str(e)


def local_ip_for(target):
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    try:
        s.connect((target, 80))
        return s.getsockname()[0]
    except OSError:
        return None
    finally:
        s.close()


def tcp_open(ip, port, timeout=2):
    s = socket.socket()
    s.settimeout(timeout)
    try:
        s.connect((ip, port))
        return True
    except OSError:
        return False
    finally:
        s.close()


def check_ont(ip):
    print('\n[1/4] ping TCP 23/80...')
    for port, name in ((23, 'telnet'), (80, 'http')):
        ok = tcp_open(ip, port)
        print('      TCP %-3s (%-6s) %s' % (port, name, 'TERBUKA' if ok else 'tutup'))
    print('[2/4] login page ONT...')
    code, body = http_req('GET', 'http://%s/html/login_inter.html' % ip)
    print('      HTTP %s (%d bytes)' % (code, len(body)))
    print('[3/4] custom webui...')
    code, body = http_req('GET', 'http://%s/image/logo_Intelbras_fiberhome.png' % ip)
    if code == 200 and len(body) > 1000:
        print('      AKTIF')
    else:
        print('      belum terdeteksi (HTTP %s) - perlu UNLOCK dulu' % code)
    print('[4/4] IP HP saya untuk ONT:', local_ip_for(ip) or 'tidak terdeteksi')


class Telnet:
    def __init__(self, host, port=23, timeout=8):
        self.s = socket.create_connection((host, port), timeout=timeout)
        self.s.settimeout(1.0)
        self.buf = b''

    def _strip(self, data):
        out = bytearray()
        i = 0
        while i < len(data):
            b = data[i]
            if b == TELNET_IAC:
                nxt = data[i + 1] if i + 1 < len(data) else None
                if nxt == TELNET_IAC:
                    out.append(TELNET_IAC); i += 2; continue
                if nxt == TELNET_SB:
                    j = i + 2
                    while j + 1 < len(data) and not (data[j] == TELNET_IAC and data[j + 1] == TELNET_SE):
                        j += 1
                    i = j + 2; continue
                i += 3 if i + 2 < len(data) else 2; continue
            out.append(b); i += 1
        return bytes(out)

    def send(self, text):
        self.s.sendall(text.encode() + b'\r\n')

    def read_until(self, marker, timeout=8):
        deadline = time.time() + timeout
        while time.time() < deadline:
            try:
                chunk = self.s.recv(4096)
            except socket.timeout:
                time.sleep(0.1); continue
            if not chunk:
                break
            self.buf += self._strip(chunk)
            idx = self.buf.find(marker)
            if idx != -1:
                out, self.buf = self.buf[:idx], self.buf[idx + len(marker):]
                return out
        out, self.buf = self.buf, b''
        return out

    def read_all(self, timeout=6):
        out = bytearray()
        deadline = time.time() + timeout
        while time.time() < deadline:
            try:
                chunk = self.s.recv(4096)
            except socket.timeout:
                break
            if not chunk:
                break
            out += self._strip(chunk)
        return bytes(out)

    def close(self):
        try:
            self.s.close()
        except OSError:
            pass


def mac_password(mac):
    hexpart = re.sub(r'[^0-9a-fA-F]', '', mac)
    if len(hexpart) < 6:
        raise ValueError('MAC tidak valid: %s' % mac)
    return 'Fh@' + hexpart[-6:].upper()


class FileHandler(http.server.SimpleHTTPRequestHandler):
    def log_message(self, *a):
        pass


def unlock_ont(ip, mac):
    password = mac_password(mac)
    print('\n[UNLOCK] %s  password: %s' % (ip, password))
    if not os.path.isfile(SCRIPT):
        print('ERROR: custom-webui.sh tidak ditemukan di', HERE)
        return 1
    local_ip = local_ip_for(ip)
    if not local_ip:
        print('ERROR: HP tidak tersambung ke ONT — konek ke WiFi ONT dulu')
        return 1
    print('IP HP:', local_ip)
    handler = lambda *a, **kw: FileHandler(*a, directory=HERE, **kw)
    httpd = http.server.ThreadingHTTPServer(('', 0), handler)
    port = httpd.server_address[1]
    url = 'http://%s:%d/custom-webui.sh' % (local_ip, port)
    print('Serve:', url)
    threading.Thread(target=httpd.serve_forever, daemon=True).start()
    try:
        t = Telnet(ip)
        t.read_until(b'ogin:')
        t.send('gpon')
        t.read_until(b'assword:')
        t.send(password)
        t.read_until(b'>')
        print('login OK')
        t.send('enable')
        t.read_until(b'assword:')
        t.send(password)
        t.read_all(timeout=3)
        for cmd in ('wget %s -O /var/custom-webui.sh' % url,
                    'chmod +x /var/custom-webui.sh',
                    '/var/custom-webui.sh'):
            print('> %s' % cmd)
            t.send(cmd)
            out = t.read_all(timeout=12).decode('utf-8', 'ignore')
            for line in out.splitlines():
                line = line.strip()
                if line:
                    print('    ', line)
        t.close()
    except Exception as e:
        print('UNLOCK GAGAL:', e)
    finally:
        httpd.shutdown()
    print('\nSelesai. Buka http://%s di browser HP.' % ip)
    return 0


def cgi_call(ip, method, params=None):
    params = params or {}
    body = '&'.join('%s=%s' % (k, urllib.parse.quote(str(v))) for k, v in params.items())
    body = (body + '&' if body else '') + 'ajaxmethod=%s&_=%d' % (method, int(time.time() * 1000) % 10**9)
    code, raw = http_req('POST', 'http://%s/cgi-bin/ajax' % ip, body)
    print('HTTP %s' % code)
    print(raw[:2000])
    return code, raw


def getline(prompt, default=None):
    v = input(prompt + ((' [%s]' % default) if default else '') + ': ').strip()
    return v or (default or '')


def menu():
    clear()
    print_banner()
    ont = getline('IP ONT', '192.168.1.1')
    while True:
        print('\n----------------------------------------')
        print('1) CEK ONT')
        print('2) UNLOCK ONT')
        print('3) STATUS UPLINK (get_uplink_info)')
        print('4) SET UPLINK (set_uplink_info)')
        print('5) Ganti IP ONT  (sekarang: %s)' % ont)
        print('0) Keluar')
        print('----------------------------------------')
        c = getline('Pilih')
        if c == '1':
            check_ont(ont)
        elif c == '2':
            mac = getline('MAC ONT (dari label)', '94:6B:00:00:00:00')
            unlock_ont(ont, mac)
        elif c == '3':
            cgi_call(ont, 'get_uplink_info')
        elif c == '4':
            port = getline('Port uplink (dev.eth.1..5)', 'dev.eth.1')
            print('Akan mengirim: set_uplink_info uplink_type=%s' % port)
            if getline('Ketik SET untuk lanjut') == 'SET':
                cgi_call(ont, 'set_uplink_info', {'uplink_type': port})
            else:
                print('batalkan.')
        elif c == '5':
            ont = getline('IP ONT', ont)
        elif c == '0':
            break
        else:
            print('pilihan tidak dikenal')


def main():
    args = sys.argv[1:]
    if not args:
        menu()
        return 0
    cmd = args[0].lower()
    ont = args[1] if len(args) > 1 else '192.168.1.1'
    if cmd == 'check':
        check_ont(ont)
    elif cmd == 'unlock':
        mac = args[2] if len(args) > 2 else getline('MAC ONT')
        return unlock_ont(ont, mac)
    elif cmd == 'status':
        cgi_call(ont, 'get_uplink_info')
    elif cmd == 'set':
        port = args[2] if len(args) > 2 else 'dev.eth.1'
        print('MENGIRIM: set_uplink_info uplink_type=%s (1 percobaan)' % port)
        if getline('Ketik SET untuk lanjut') != 'SET':
            print('dibatalkan')
            return 0
        cgi_call(ont, 'set_uplink_info', {'uplink_type': port})
    else:
        print(__doc__)
        return 2
    return 0


if __name__ == '__main__':
    sys.exit(main())
