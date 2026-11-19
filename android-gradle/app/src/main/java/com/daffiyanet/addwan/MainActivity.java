package com.daffiyanet.addwan;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Add WAN FH Mobile v2.3 — unlock Fiberhome HG6145F1/D2 (RP4423) & set uplink via HP.
 * Tool ini BUKAN produk resmi Fiberhome; gunakan hanya pada perangkat milik Anda.
 */
public class MainActivity extends Activity {
    private EditText etIp, etMac;
    private TextView tvLog;
    private Spinner spPort;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    private static final int BG          = 0xFF070B1A;
    private static final int CARD        = 0xFF111831;
    private static final int CARD_STROKE = 0xFF26304E;
    private static final int INPUT_BG    = 0xFF0D1430;
    private static final int INPUT_STR   = 0xFF2F4B7C;
    private static final int GOLD        = 0xFFFFD54A;
    private static final int MINT        = 0xFF7CFFC4;
    private static final int TEXT        = 0xFFE8F6FF;
    private static final int MUTED       = 0xFF8E94B8;
    private static final int CYAN        = 0xFF00E5FF;
    private static final int AMBER       = 0xFFF59E0B;
    private static final int GREEN       = 0xFF22C55E;
    private static final int PURPLE      = 0xFF8B5CF6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(buildUi());
    }

    private int dp(int v) { return Math.round(v * getResources().getDisplayMetrics().density); }

    private GradientDrawable round(int fill, int stroke, int radiusDp, int strokeWDp) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.RECTANGLE);
        d.setCornerRadius(dp(radiusDp));
        d.setColor(fill);
        if (stroke != 0 && strokeWDp > 0) d.setStroke(dp(strokeWDp), stroke);
        return d;
    }

    private TextView label(String s, boolean small, int padTop) {
        TextView t = new TextView(this);
        t.setText(s.toUpperCase());
        t.setTextSize(small ? 10 : 12);
        t.setLetterSpacing(0.14f);
        t.setTypeface(Typeface.DEFAULT_BOLD);
        t.setTextColor(GOLD);
        t.setPadding(0, padTop, 0, dp(5));
        return t;
    }

    private EditText field(String hint, String initial) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setHintTextColor(0xFF5F7694);
        e.setTextColor(TEXT);
        e.setTextSize(15);
        e.setSingleLine(true);
        e.setPadding(dp(14), dp(11), dp(14), dp(11));
        e.setBackground(round(INPUT_BG, INPUT_STR, 12, 1));
        if (initial != null) e.setText(initial);
        return e;
    }

    private Button actionBtn(String text, int bg) {
        Button b = new Button(this);
        b.setText(text);
        b.setAllCaps(false);
        b.setTextSize(13);
        b.setTypeface(Typeface.DEFAULT_BOLD);
        b.setLetterSpacing(0.06f);
        b.setTextColor(0xFFFFFFFF);
        b.setMinHeight(dp(50));
        b.setBackground(round(bg, 0, 14, 0));
        b.setPadding(dp(8), 0, dp(8), 0);
        return b;
    }

    private View buildUi() {
        ScrollView root = new ScrollView(this);
        root.setFillViewport(true);
        root.setBackgroundColor(BG);

        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(16);
        main.setPadding(pad, dp(10), pad, dp(16));
        main.setBackgroundColor(BG);

        // ===== HEADER =====
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(0, dp(4), 0, dp(12));

        TextView title = new TextView(this);
        title.setText("⚡ ADD WAN FH");
        title.setTextSize(30);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(GOLD);
        title.setShadowLayer(dp(8), 0, 0, 0x80FFD54A);
        header.addView(title);

        TextView sub = new TextView(this);
        sub.setText("Unlock HG6145F1/D2  ·  Set uplink  ·  via HP");
        sub.setTextSize(12);
        sub.setTextColor(MUTED);
        sub.setPadding(0, dp(2), 0, 0);
        header.addView(sub);

        main.addView(header);

        // ===== CARD: KONEKSI ONT =====
        LinearLayout card1 = card();
        main.addView(card1);

        card1.addView(label("Koneksi ONT", true, dp(0)));

        card1.addView(label("IP ONT", false, dp(8)));
        etIp = field("192.168.1.1", "192.168.1.1");
        etIp.setTextColor(CYAN);
        card1.addView(etIp);

        card1.addView(label("MAC ONT (label belakang)", false, dp(14)));
        etMac = field("94:6B:12:AB:CD:EF", null);
        etMac.setTextColor(GOLD);
        etMac.setInputType(0x00000001 | 0x00001000 | 0x00000080); // text | cap characters
        card1.addView(etMac);

        // ===== BUTTONS =====
        LinearLayout row1 = new LinearLayout(this);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        row1.setPadding(0, dp(14), 0, 0);
        Button bCheck = actionBtn("1. CEK ONT", 0xFF0891B2);
        Button bUnlock = actionBtn("2. UNLOCK", AMBER);
        row1.addView(bCheck, lp(0, 1f, 0, 0, 0, 0));
        row1.addView(bUnlock, lp(0, 1f, dp(8), 0, 0, 0));
        card1.addView(row1);

        Button bStatus = actionBtn("3. STATUS UPLINK", 0xFF15803D);
        bStatus.setLayoutParams(lp(ViewGroup.LayoutParams.MATCH_PARENT, 0f, 0, dp(8), 0, 0));
        card1.addView(bStatus);

        LinearLayout row3 = new LinearLayout(this);
        row3.setOrientation(LinearLayout.HORIZONTAL);
        row3.setPadding(0, dp(8), 0, 0);
        spPort = new Spinner(this);
        String[] ports = {"dev.eth.1", "dev.eth.2", "dev.eth.3", "dev.eth.4", "dev.eth.5"};
        ArrayAdapter<String> ad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ports);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPort.setAdapter(ad);
        spPort.setBackground(round(INPUT_BG, INPUT_STR, 12, 1));
        spPort.setPadding(dp(10), 0, dp(10), 0);
        Button bSet = actionBtn("4. SET UPLINK", PURPLE);
        row3.addView(spPort, new LinearLayout.LayoutParams(dp(118), LinearLayout.LayoutParams.WRAP_CONTENT));
        row3.addView(bSet, lp(0, 1f, dp(8), 0, 0, 0));
        card1.addView(row3);

        // ===== CARD: LOG =====
        LinearLayout card2 = card();
        main.addView(card2);

        LinearLayout logHeader = new LinearLayout(this);
        logHeader.setOrientation(LinearLayout.HORIZONTAL);
        logHeader.setGravity(Gravity.CENTER_VERTICAL);
        logHeader.setPadding(0, dp(2), 0, dp(8));
        TextView dot = new TextView(this);
        dot.setText("● ● ●");
        dot.setTextSize(10);
        dot.setTextColor(0xFFFF5C57);
        logHeader.addView(dot);
        TextView logTitle = new TextView(this);
        logTitle.setText("  output");
        logTitle.setTextColor(MINT);
        logTitle.setTypeface(Typeface.MONOSPACE);
        logTitle.setTextSize(12);
        logHeader.addView(logTitle);
        card2.addView(logHeader);

        tvLog = new TextView(this);
        tvLog.setTextSize(10);
        tvLog.setTypeface(Typeface.MONOSPACE);
        tvLog.setTextColor(MINT);
        tvLog.setBackgroundColor(0xFF05070F);
        tvLog.setPadding(dp(12), dp(10), dp(12), dp(10));
        tvLog.setText("Siap. Sambungkan HP ke WiFi/LAN ONT,\nisi MAC, tekan CEK ONT.\n\n");
        tvLog.setBackground(round(0xFF05070F, 0xFF1E2A4A, 12, 1));
        ScrollView logScroll = new ScrollView(this);
        logScroll.addView(tvLog);
        LinearLayout.LayoutParams lpLog = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(160));
        logScroll.setLayoutParams(lpLog);
        card2.addView(logScroll);

        TextView footer = new TextView(this);
        footer.setText("github.com/Indra725/addwan-fiberhome-all-version · BUKAN produk resmi Fiberhome");
        footer.setTextColor(0xFF4A5180);
        footer.setTextSize(10);
        footer.setPadding(0, dp(10), 0, 0);
        main.addView(footer);

        bCheck.setOnClickListener(v -> runTask(this::doCheckOnt, bCheck, bUnlock, bStatus, bSet));
        bUnlock.setOnClickListener(v -> runTask(this::doUnlock, bCheck, bUnlock, bStatus, bSet));
        bStatus.setOnClickListener(v -> runTask(this::doStatusUplink, bCheck, bUnlock, bStatus, bSet));
        bSet.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Set uplink?")
                .setMessage("Uplink WAN akan memakai port LAN " + spPort.getSelectedItem() + ".\n\n"
                        + "HP harus satu jaringan dengan ONT. Lanjutkan?")
                .setPositiveButton("Ya, SET", (d, w) -> runTask(this::doSetUplink, bCheck, bUnlock, bStatus, bSet))
                .setNegativeButton("Batal", null)
                .show());

        root.addView(main);
        return root;
    }

    private LinearLayout.LayoutParams lp(int w, float wt, int pl, int pt, int pr, int pb) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(w, LinearLayout.LayoutParams.WRAP_CONTENT, wt);
        lp.setMargins(pl, pt, pr, pb);
        return lp;
    }

    private LinearLayout card() {
        LinearLayout c = new LinearLayout(this);
        c.setOrientation(LinearLayout.VERTICAL);
        c.setBackground(round(CARD, CARD_STROKE, 18, 1));
        c.setPadding(dp(16), dp(14), dp(16), dp(16));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, dp(12));
        c.setLayoutParams(lp);
        return c;
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
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"ping", "-c1", "-W1", ip});
            boolean ok = p.waitFor(4, java.util.concurrent.TimeUnit.SECONDS) && p.exitValue() == 0;
            log("ping        : " + (ok ? "OK" : "GAGAL (lanjut cek port)"));
        } catch (Exception e) { log("ping        : " + e); }
        for (int[] pr : new int[][]{{23, 0}, {80, 0}}) {
            try (Socket s = new Socket()) {
                s.connect(new java.net.InetSocketAddress(ip, pr[0]), 2000);
                log("TCP " + pr[0] + "       : TERBUKA");
            } catch (Exception e) {
                log("TCP " + pr[0] + "       : tutup");
            }
        }
        String login = httpRequest("GET", "http://" + ip + "/html/login_inter.html", null);
        log("login page  : HTTP " + login.split("\n", 2)[0] + " (" + login.length() + " bytes)");
        String logo = httpRequest("GET", "http://" + ip + "/image/logo_Intelbras_fiberhome.png", null);
        if (logo.startsWith("200") && logo.length() > 1000) {
            log("custom webui: AKTIF");
        } else {
            log("custom webui: belum terdeteksi -------- perlu UNLOCK dulu");
        }
        log("IP HP saya  : " + phoneIp());
    }

    private void doUnlock() {
        String ip = ontIp();
        String mac = etMac.getText().toString().trim();
        if (mac.replaceAll("[^0-9a-fA-F]", "").length() < 6) { log("MAC kosong/tidak valid -------- isi dulu"); return; }
        log("=== UNLOCK " + ip + " ===");
        String pass = macPassword(mac);
        log("password    : " + pass);
        String localIp = phoneIp();
        if (localIp == null) { log("ERROR: IP HP tidak terdeteksi -------- sambungkan ke WiFi ONT"); return; }
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
            log("=== UNLOCK SELESAI -------- buka http://" + ip + " di browser HP ===");
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
            log("Gagal baca -------- coba unlock dulu, atau cek via F12 Console:");
            log("XHR.get(\"get_uplink_info\", null, function(d){console.log(JSON.stringify(d))})");
        }
    }

    private String doSetUplink() {
        String ip = ontIp();
        String port = String.valueOf(spPort.getSelectedItem());
        String body = "ajaxmethod=set_uplink_info&_=" + (System.currentTimeMillis() % 1000000000)
                + "&uplink_type=" + URLEncoder.encode(port);
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
                byte[] b = body.getBytes("UTF-8");
                try (OutputStream os = conn.getOutputStream()) { os.write(b); }
            }
            int code = conn.getResponseCode();
            InputStream is = code < 400 ? conn.getInputStream() : conn.getErrorStream();
            byte[] buf = new byte[65536];
            int n = 0;
            int r;
            while (n < buf.length && (r = is.read(buf, n, buf.length - n)) != -1) n += r;
            String text = new String(buf, 0, n, "UTF-8");
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

        void send(String text) throws IOException { out.write(text.getBytes("US-ASCII")); out.flush(); }

        private String stripIac(byte[] d) {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while (i < d.length) {
                int b = d[i] & 0xFF;
                if (b == 255) {
                    int nxt = (i + 1 < d.length) ? (d[i + 1] & 0xFF) : -1;
                    if (nxt == 255) { sb.append((char) 183); i += 2; continue; }
                    if (nxt == 250) {
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
                String acc = new String(buf, 0, buf.length, "UTF-8");
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
            String acc = new String(buf, 0, buf.length, "UTF-8");
            buf = new byte[0];
            return acc;
        }

        String readAll(int timeoutMs) throws IOException {
            long deadline = System.currentTimeMillis() + timeoutMs;
            while (System.currentTimeMillis() < deadline) pump(400);
            String acc = new String(buf, 0, buf.length, "UTF-8");
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
                String req = new String(head, 0, Math.max(0, n), "ISO-8859-1");
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
                cos.write(header.getBytes("US-ASCII"));
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
