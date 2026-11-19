package com.daffiyanet.addwan;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Add WAN FH Mobile — unlock Fiberhome HG6145F1/D2 (RP4423) & set uplink via HP.
 * Tool ini BUKAN produk resmi Fiberhome; gunakan hanya pada perangkat milik Anda.
 */
public class MainActivity extends Activity {
    private EditText etIp, etMac;
    private TextView tvLog;
    private Spinner spPort;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(buildUi());
    }

    private LinearLayout col() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(16);
        l.setPadding(pad, pad, pad, pad);
        return l;
    }

    private int dp(int v) { return Math.round(v * getResources().getDisplayMetrics().density); }

    private TextView label(String s, boolean small, int padTop) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(small ? 11 : 13);
        t.setTextColor(small ? 0xFF9DA5C8 : 0xFFFFFFFF);
        t.setPadding(0, padTop, 0, dp(4));
        return t;
    }

    private View buildUi() {
        ScrollView root = new ScrollView(this);
        root.setFillViewport(true);
        LinearLayout main = col();
        main.setBackgroundColor(0xFF050718);

        TextView title = new TextView(this);
        title.setText("⚡ ADD WAN FH");
        title.setTextSize(26);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(0xFFFFD700);
        title.setPadding(0, dp(4), 0, dp(2));
        main.addView(title);
        main.addView(label("Unlock Fiberhome HG6145F1/D2 & set uplink dari HP. HP harus satu jaringan/WiFi dengan ONT.", true, 0));

        main.addView(label("IP ONT", false, dp(8)));
        etIp = new EditText(this);
        etIp.setText("192.168.1.1");
        etIp.setTextColor(0xFF00E5FF);
        etIp.setTextSize(15);
        etIp.setSingleLine(true);
        main.addView(etIp);

        main.addView(label("MAC ONT (dari label belakang)", false, dp(10)));
        etMac = new EditText(this);
        etMac.setHint("94:6B:12:AB:CD:EF");
        etMac.setHintTextColor(0xFF5E6794);
        etMac.setTextColor(0xFFFFFFFF);
        etMac.setTextSize(15);
        etMac.setSingleLine(true);
        etMac.setInputType(0x00000001 | 0x00001000); // text | cap characters
        main.addView(etMac);

        LinearLayout row1 = new LinearLayout(this);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        row1.setPadding(0, dp(12), 0, 0);
        Button bCheck = mkBtn("1. CEK ONT", 0xFF12324A, 0xFFFFFFFF);
        Button bUnlock = mkBtn("2. UNLOCK", 0xFF8A5A00, 0xFFFFFFFF);
        row1.addView(bCheck, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        row1.addView(bUnlock, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        main.addView(row1);

        Button bStatus = mkBtn("3. STATUS UPLINK (get_uplink_info)", 0xFF0B3B2E, 0xFFFFFFFF);
        main.addView(bStatus);

        LinearLayout row3 = new LinearLayout(this);
        row3.setOrientation(LinearLayout.HORIZONTAL);
        row3.setPadding(0, dp(8), 0, 0);
        spPort = new Spinner(this);
        String[] ports = {"dev.eth.1", "dev.eth.2", "dev.eth.3", "dev.eth.4", "dev.eth.5"};
        ArrayAdapter<String> ad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ports);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPort.setAdapter(ad);
        Button bSet = mkBtn("4. SET UPLINK", 0xFF3B1257, 0xFFFFFFFF);
        LinearLayout.LayoutParams spLp = new LinearLayout.LayoutParams(dp(120), LinearLayout.LayoutParams.WRAP_CONTENT);
        row3.addView(spPort, spLp);
        row3.addView(bSet, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        main.addView(row3);

        main.addView(label("Log:", false, dp(10)));
        tvLog = new TextView(this);
        tvLog.setTextSize(11);
        tvLog.setTypeface(Typeface.MONOSPACE);
        tvLog.setTextColor(0xFF9FFFD0);
        tvLog.setBackgroundColor(0xFF000000);
        tvLog.setPadding(dp(10), dp(10), dp(10), dp(10));
        tvLog.setText("Siap. Sambungkan HP ke WiFi/LAN ONT, isi MAC, lalu tekan CEK ONT.\n");
        ScrollView logScroll = new ScrollView(this);
        logScroll.addView(tvLog);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        logScroll.setLayoutParams(lp);
        main.addView(logScroll, lp);

        bCheck.setOnClickListener(v -> runTask(this::doCheckOnt, bCheck, bUnlock, bStatus, bSet));
        bUnlock.setOnClickListener(v -> runTask(this::doUnlock, bCheck, bUnlock, bStatus, bSet));
        bStatus.setOnClickListener(v -> runTask(this::doStatusUplink, bCheck, bUnlock, bStatus, bSet));
        bSet.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Set uplink?")
                .setMessage("Uplink WAN akan memakai port LAN " + spPort.getSelectedItem() + ".\n\n"
                        + "HP harus satu jaringan dengan ONT. Lanjutkan?")
                .setPositiveButton("Ya, SET", (d, w) -> runTask(() -> doSetUplink(), bCheck, bUnlock, bStatus, bSet))
                .setNegativeButton("Batal", null)
                .show());

        root.addView(main);
        return root;
    }

    private Button mkBtn(String text, int bg, int fg) {
        Button b = new Button(this);
        b.setText(text);
        b.setAllCaps(false);
        b.setTextSize(12);
        b.setBackgroundColor(bg);
        b.setTextColor(fg);
        b.setPadding(dp(4), dp(10), dp(4), dp(10));
        return b;
    }

    private interface Work { void run(); }

    private void runTask(Work w, final Button... buttons) {
        for (Button b : buttons) b.setEnabled(false);
        io.execute(() -> {
            try { w.run(); }
            catch (Exception e) { log("ERROR: " + e); }
            finally {
                runOnUiThread(() -> { for (Button b : buttons) b.setEnabled(true); });
            }
        });
    }

    private void log(String s) {
        runOnUiThread(() -> tvLog.append(s + "\n"));
    }

    private String phoneIp() {
        try {
            Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();
            String fallback = null;
            while (ifs.hasMoreElements()) {
                NetworkInterface ni = ifs.nextElement();
                if (ni.isLoopback() || !ni.isUp()) continue;
                Enumeration<InetAddress> addrs = ni.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress a = addrs.nextElement();
                    if (a.isLoopbackAddress() || a.getHostAddress().indexOf('.') < 0) continue;
                    String h = a.getHostAddress();
                    if (h.startsWith("192.168.") || h.startsWith("10.") || h.startsWith("172.")) return h;
                    if (fallback == null) fallback = h;
                }
            }
            return fallback;
        } catch (Exception e) {
            return null;
        }
    }

    private String ontIp() {
        String ip = etIp.getText().toString().trim();
        return ip.isEmpty() ? "192.168.1.1" : ip;
    }

    private String macPassword(String mac) {
        String hex = mac.replaceAll("[^0-9a-fA-F]", "");
        if (hex.length() < 6) throw new IllegalArgumentException("MAC tidak valid: " + mac);
        return "Fh@" + hex.substring(hex.length() - 6).toUpperCase();
    }

    private void doCheckOnt() {
        String ip = ontIp();
        log("=== CEK ONT " + ip + " ===");
        // ping
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"ping", "-c1", "-W1", ip});
            boolean ok = p.waitFor(4, java.util.concurrent.TimeUnit.SECONDS) && p.exitValue() == 0;
            log("ping        : " + (ok ? "OK" : "GAGAL (lanjut cek port)"));
        } catch (Exception e) { log("ping        : " + e); }
        // TCP 23 / 80
        for (int[] pr : new int[][]{{23, 0}, {80, 0}}) {
            try (Socket s = new Socket()) {
                s.connect(new java.net.InetSocketAddress(ip, pr[0]), 2000);
                log("TCP " + pr[0] + "       : TERBUKA");
            } catch (Exception e) {
                log("TCP " + pr[0] + "       : tutup");
            }
        }
        // HTTP login page
        String login = httpRequest("GET", "http://" + ip + "/html/login_inter.html", null);
        log("login page  : HTTP " + login.split("\n", 2)[0] + " (" + login.length() + " bytes)");
        String logo = httpRequest("GET", "http://" + ip + "/image/logo_Intelbras_fiberhome.png", null);
        if (logo.startsWith("200") && logo.length() > 1000) {
            log("custom webui: AKTIF");
        } else {
            log("custom webui: belum terdeteksi — perlu UNLOCK dulu");
        }
        log("IP HP saya  : " + phoneIp());
    }

    private void doUnlock() {
        String ip = ontIp();
        String mac = etMac.getText().toString().trim();
        if (mac.replaceAll("[^0-9a-fA-F]", "").length() < 6) { log("MAC kosong/tidak valid — isi dulu"); return; }
        log("=== UNLOCK " + ip + " ===");
        String pass = macPassword(mac);
        log("password    : " + pass);
        String localIp = phoneIp();
        if (localIp == null) { log("ERROR: IP HP tidak terdeteksi — sambungkan ke WiFi ONT"); return; }
        log("IP HP       : " + localIp);

        FileServer server = new FileServer(localIp);
        Thread st = new Thread(server);
        st.setDaemon(true);
        st.start();
        String url = "http://" + localIp + ":" + server.port + "/custom-webui.sh";
        log("URL script  : " + url);

        try (Socket sock = new Socket()) {
            sock.connect(new java.net.InetSocketAddress(ip, 23), 5000);
            sock.setSoTimeout(12000);
            Telnet t = new Telnet(sock);
            t.readUntil("ogin:"); t.send("gpon\r\n");
            t.readUntil("assword:"); t.send(pass + "\r\n");
            t.readUntil(">"); log("login       : OK (masuk CLI)");
            t.send("enable\r\n"); t.readUntil("assword:"); t.send(pass + "\r\n");
            Thread.sleep(700); t.drain(); log("enable      : OK");
            for (String cmd : new String[]{
                    "wget " + url + " -O /var/custom-webui.sh",
                    "chmod +x /var/custom-webui.sh",
                    "/var/custom-webui.sh"}) {
                log("> " + cmd);
                t.send(cmd + "\r\n");
                String out = t.readAll(12000);
                for (String line : out.split("\n")) {
                    String s = line.trim();
                    if (!s.isEmpty()) log("    " + s);
                }
            }
            t.close();
            Thread.sleep(1500);
            server.stop();
            doCheckOnt();
            log("=== UNLOCK SELESAI — buka http://" + ip + " di browser HP ===");
        } catch (Exception e) {
            server.stop();
            log("UNLOCK GAGAL: " + e);
            log("Pastikan: telnet aktif (Security > ACL Settings), HP satu jaringan dengan ONT.");
        }
    }

    private void doStatusUplink() {
        String ip = ontIp();
        log("=== UPLINK STATUS " + ip + " ===");
        String resp = httpRequest("POST", "http://" + ip + "/cgi-bin/ajax",
                "ajaxmethod=get_uplink_info&_=" + (System.currentTimeMillis() % 1000000000));
        log(resp);
        if (resp.startsWith("200")) {
            log("\nuplink_type = dev.eth.N berarti port LAN itu jadi uplink.");
        } else {
            log("Gagal baca — coba unlock dulu, atau cek via F12 Console:");
            log("XHR.get(\"get_uplink_info\", null, function(d){console.log(JSON.stringify(d))})");
        }
    }

    private String doSetUplink() {
        String ip = ontIp();
        String port = String.valueOf(spPort.getSelectedItem());
        String body = "ajaxmethod=set_uplink_info&_=" + (System.currentTimeMillis() % 1000000000)
                + "&uplink_type=" + URLEncoder.encode(port, StandardCharsets.UTF_8);
        log("=== SET UPLINK " + ip + " -> " + port + " ===");
        String resp = httpRequest("POST", "http://" + ip + "/cgi-bin/ajax", body);
        log(resp);
        return resp;
    }

    private String httpRequest(String method, String url, String body) {
        HttpURLConnection conn = null;
        try {
            java.net.URL u = new java.net.URL(url);
            conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod(method);
            conn.setConnectTimeout(4000);
            conn.setReadTimeout(8000);
            conn.setInstanceFollowRedirects(false);
            if (body != null) {
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            }
            if (body != null) {
                byte[] b = body.getBytes(StandardCharsets.UTF_8);
                try (OutputStream os = conn.getOutputStream()) { os.write(b); }
            }
            int code = conn.getResponseCode();
            InputStream is = code < 400 ? conn.getInputStream() : conn.getErrorStream();
            byte[] buf = new byte[65536];
            int n = 0;
            int r;
            while (n < buf.length && (r = is.read(buf, n, buf.length - n)) != -1) n += r;
            String text = new String(buf, 0, n, StandardCharsets.UTF_8);
            return code + "\n" + (text.length() > 3000 ? text.substring(0, 3000) + "\n...(truncated)" : text);
        } catch (Exception e) {
            return "0\n" + e;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    /** Telnet minimal (strip IAC) untuk busybox ONT. */
    static final class Telnet {
        private final Socket s;
        private final java.io.BufferedInputStream in;
        private final OutputStream out;
        private byte[] buf = new byte[0];

        Telnet(Socket s) throws IOException {
            this.s = s;
            in = new java.io.BufferedInputStream(s.getInputStream());
            out = s.getOutputStream();
        }

        void send(String text) throws IOException { out.write(text.getBytes(StandardCharsets.US_ASCII)); out.flush(); }

        private String stripIac(byte[] d) {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while (i < d.length) {
                int b = d[i] & 0xFF;
                if (b == 255) {
                    int nxt = (i + 1 < d.length) ? (d[i + 1] & 0xFF) : -1;
                    if (nxt == 255) { sb.append((char) 183); i += 2; continue; }
                    if (nxt == 250) { // SB ... SE
                        int j = i + 2;
                        while (j + 1 < d.length && !((d[j] & 0xFF) == 255 && (d[j + 1] & 0xFF) == 240)) j++;
                        i = j + 2; continue;
                    }
                    i += (i + 2 < d.length) ? 3 : 2; continue;
                }
                sb.append((char) b);
                i++;
            }
            return sb.toString();
        }

        String readUntil(String marker) throws IOException { return readUntil(marker, 10000); }

        String readUntil(String marker, int timeoutMs) throws IOException {
            long deadline = System.currentTimeMillis() + timeoutMs;
            while (System.currentTimeMillis() < deadline) {
                pump(300);
                String acc = new String(buf, 0, buf.length, StandardCharsets.UTF_8);
                int idx = acc.indexOf(marker);
                if (idx >= 0) {
                    int end = idx + marker.length();
                    String found = acc.substring(0, end);
                    byte[] rest = new byte[buf.length - end];
                    System.arraycopy(buf, end, rest, 0, rest.length);
                    buf = rest;
                    return found;
                }
            }
            String acc = new String(buf, 0, buf.length, StandardCharsets.UTF_8);
            buf = new byte[0];
            return acc;
        }

        String readAll(int timeoutMs) throws IOException {
            long deadline = System.currentTimeMillis() + timeoutMs;
            while (System.currentTimeMillis() < deadline) pump(400);
            String acc = new String(buf, 0, buf.length, StandardCharsets.UTF_8);
            buf = new byte[0];
            return acc;
        }

        void drain() throws IOException { this.buf = new byte[0]; }

        private void pump(int millis) throws IOException {
            int avail = in.available();
            if (avail <= 0) { try { Thread.sleep(millis); } catch (InterruptedException ignored) {} return; }
            byte[] tmp = new byte[avail];
            int n = in.read(tmp);
            if (n <= 0) return;
            byte[] grown = new byte[buf.length + n];
            System.arraycopy(buf, 0, grown, 0, buf.length);
            System.arraycopy(tmp, 0, grown, buf.length, n);
            buf = grown;
        }

        void close() { try { s.close(); } catch (IOException ignored) {} }
    }

    /** Mini HTTP server: melayani custom-webui.sh dari assets untuk ONT. */
    final class FileServer implements Runnable {
        volatile boolean running = true;
        final int port;
        private final ServerSocket ss;

        FileServer(String bindIp) {
            try { ss = new ServerSocket(0); } catch (IOException e) { throw new RuntimeException(e); }
            this.port = ss.getLocalPort();
        }

        @Override public void run() {
            try { ss.setSoTimeout(45000); } catch (java.net.SocketException ignored) {}
            long deadline = System.currentTimeMillis() + 60000;
            while (running && System.currentTimeMillis() < deadline) {
                try {
                    Socket c = ss.accept();
                    new Thread(() -> serve(c)).start();
                } catch (java.net.SocketTimeoutException e) {
                    break;
                } catch (IOException e) {
                    break;
                }
            }
            try { ss.close(); } catch (IOException ignored) {}
        }

        private void serve(Socket c) {
            try (InputStream cis = c.getInputStream(); OutputStream cos = c.getOutputStream()) {
                byte[] head = new byte[8192];
                int n = cis.read(head);
                String req = new String(head, 0, Math.max(0, n), StandardCharsets.ISO_8859_1);
                if (!req.contains("custom-webui.sh")) {
                    cos.write("HTTP/1.1 404 Not Found\r\nContent-Length: 0\r\n\r\n".getBytes());
                    return;
                }
                byte[] script;
                try (InputStream is = getAssets().open("custom-webui.sh")) {
                    script = readAllBytes(is);
                }
                String header = "HTTP/1.1 200 OK\r\nContent-Type: application/octet-stream\r\nContent-Length: "
                        + script.length + "\r\nConnection: close\r\n\r\n";
                cos.write(header.getBytes(StandardCharsets.US_ASCII));
                cos.write(script);
                cos.flush();
            } catch (IOException ignored) {
            }
        }

        void stop() { running = false; try { ss.close(); } catch (IOException ignored) {} }
    }

    private static byte[] readAllBytes(InputStream is) throws IOException {
        java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
        byte[] tmp = new byte[8192];
        int n;
        while ((n = is.read(tmp)) != -1) bos.write(tmp, 0, n);
        return bos.toByteArray();
    }
}
