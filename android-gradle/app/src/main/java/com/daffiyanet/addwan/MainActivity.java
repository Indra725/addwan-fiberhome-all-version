package com.daffiyanet.addwan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
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

/**
 * Add WAN FH Mobile — v2.4
 * Tool ISP sendiri untuk HG6145F1/D2 pada perangkat yang Anda miliki / kelola.
 */
public class MainActivity extends Activity {

    private EditText etIp, etMac;
    private TextView tvLog;
    private android.widget.Spinner spPort;
    private final java.util.concurrent.ExecutorService io = java.util.concurrent.Executors.newSingleThreadExecutor();

    private static final int BG = 0xFF070B1A;
    private static final int CARD = 0xFF111831;
    private static final int CARD_STROKE = 0xFF26304E;
    private static final int INPUT_BG = 0xFF0D1430;
    private static final int INPUT_STR = 0xFF2F4B7C;
    private static final int GOLD = 0xFFFFD54A;
    private static final int MINT = 0xFF7CFFC4;
    private static final int TEXT = 0xFFE8F6FF;
    private static final int MUTED = 0xFF8E94B8;

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

    private GradientDrawable gradientBg() {
        GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{0xFF10122B, BG});
        g.setShape(GradientDrawable.RECTANGLE);
        return g;
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

    private LinearLayout.LayoutParams lp(int w, float wt, int l, int t, int r, int b) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(w, LinearLayout.LayoutParams.WRAP_CONTENT, wt);
        lp.setMargins(l, t, r, b);
        return lp;
    }

    private EditText field(String hint, String initial) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setHintTextColor(0xFF5F7694);
        e.setTextColor(TEXT);
        e.setTextSize(16);
        e.setSingleLine(true);
        e.setPadding(dp(14), dp(11), dp(14), dp(11));
        e.setBackground(round(INPUT_BG, INPUT_STR, 12, 1));
        if (initial != null) e.setText(initial);
        return e;
    }

    private Button act(String text, int bg) {
        Button b = new Button(this);
        b.setText(text);
        b.setAllCaps(false);
        b.setTextSize(13);
        b.setTypeface(Typeface.DEFAULT_BOLD);
        b.setLetterSpacing(0.06f);
        b.setTextColor(0xFFFFFFFF);
        b.setMinHeight(dp(52));
        b.setBackground(round(bg, 0, 14, 0));
        b.setPadding(dp(8), 0, dp(8), 0);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, dp(8));
        b.setLayoutParams(lp);
        return b;
    }

    private TextView sectionTitle(String s) {
        TextView t = new TextView(this);
        t.setText(s.toUpperCase());
        t.setTextSize(11);
        t.setLetterSpacing(0.18f);
        t.setTypeface(Typeface.DEFAULT_BOLD);
        t.setTextColor(GOLD);
        t.setPadding(dp(2), dp(2), 0, dp(6));
        return t;
    }

    private View buildUi() {
        ScrollView root = new ScrollView(this);
        root.setFillViewport(true);
        root.setBackground(gradientBg());

        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(16);
        main.setPadding(pad, dp(12), pad, dp(14));
        main.setBackgroundColor(BG);

        // ===== HEADER =====
        TextView badge = new TextView(this);
        badge.setText("  ⚡ D A F F I Y A N E T  ");
        badge.setTextColor(GOLD);
        badge.setTextSize(10);
        badge.setTypeface(Typeface.DEFAULT_BOLD);
        badge.setLetterSpacing(0.22f);
        badge.setGravity(Gravity.CENTER);
        badge.setBackground(round(0xFF2A2210, 0xFF4A3E18, 10, 1));
        badge.setPadding(dp(10), dp(4), dp(10), dp(4));
        LinearLayout.LayoutParams badgeLp = lp(LinearLayout.LayoutParams.WRAP_CONTENT, 0f, 0, dp(4), 0, dp(10));
        badge.setLayoutParams(badgeLp);
        main.addView(badge);

        TextView title = new TextView(this);
        title.setText("ADD WAN FH");
        title.setTextSize(31);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(GOLD);
        title.setShadowLayer(dp(10), 0, 0, 0xB0FFD54A);
        main.addView(title);

        TextView sub = new TextView(this);
        sub.setText("Unlock & kelola HG6145F1 / HG6145D2 dari HP — tanpa PC, tanpa root.");
        sub.setTextSize(12);
        sub.setTextColor(MUTED);
        sub.setPadding(0, dp(4), 0, dp(12));
        main.addView(sub);

        // ===== CARD: KONEKSI =====
        LinearLayout c = card();
        c.addView(sectionTitle("Koneksi ONT"));

        TextView ipLabel = new TextView(this);
        ipLabel.setText("IP ONT");
        ipLabel.setTextColor(MUTED);
        ipLabel.setTextSize(10);
        ipLabel.setLetterSpacing(0.16f);
        c.addView(ipLabel);
        etIp = field("192.168.1.1", "192.168.1.1");
        etIp.setTextColor(0xFF00E5FF);
        c.addView(etIp);

        TextView macLabel = new TextView(this);
        macLabel.setText("MAC ONT");
        macLabel.setTextColor(MUTED);
        macLabel.setTextSize(10);
        macLabel.setLetterSpacing(0.16f);
        macLabel.setPadding(0, dp(10), 0, 0);
        c.addView(macLabel);
        etMac = field("94:6B:12:AB:CD:EF", null);
        etMac.setTextColor(GOLD);
        etMac.setInputType(0x00000001 | 0x00001000);
        c.addView(etMac);
        main.addView(c);

        // ===== CARD: AKSI =====
        c = card();
        c.addView(sectionTitle("Aksi"));

        Button bCheck = act("1.  CEK ONT", 0xFF0891B2);
        Button bUnlock = act("2.  UNLOCK", 0xFFB45309);
        Button bStatus = act("3.  STATUS UPLINK", 0xFF15803D);
        Button bSet = act("4.  SET UPLINK", 0xFF7C3AED);
        Button bAcl = act("5.  AKTIFKAN ACL (buka web UI)", 0xFF1E40AF);
        c.addView(bCheck);
        c.addView(bUnlock);
        c.addView(bStatus);
        // baris SET: spinner port + tombol
        LinearLayout rowSet = new LinearLayout(this);
        rowSet.setOrientation(LinearLayout.HORIZONTAL);
        spPort = new android.widget.Spinner(this);
        String[] ports = {"dev.eth.1", "dev.eth.2", "dev.eth.3", "dev.eth.4", "dev.eth.5"};
        ArrayAdapter<String> ad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ports);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPort.setAdapter(ad);
        spPort.setBackground(round(INPUT_BG, INPUT_STR, 12, 1));
        spPort.setPadding(dp(10), 0, dp(10), 0);
        rowSet.addView(spPort, new LinearLayout.LayoutParams(dp(120), LinearLayout.LayoutParams.WRAP_CONTENT));
        rowSet.addView(bSet, lp(0, 1f, dp(8), 0, 0, 0));
        c.addView(rowSet);
        c.addView(bAcl);
        main.addView(c);

        // ===== CARD: LOG =====
        c = card();
        TextView logHeader = new TextView(this);
        logHeader.setText("● ● ●   output");
        logHeader.setTextSize(10);
        logHeader.setTypeface(Typeface.MONOSPACE);
        logHeader.setTextColor(0xFFFF5C5C);
        logHeader.setPadding(0, 0, 0, dp(8));
        c.addView(logHeader);

        tvLog = new TextView(this);
        tvLog.setTextSize(10);
        tvLog.setTypeface(Typeface.MONOSPACE);
        tvLog.setTextColor(MINT);
        tvLog.setBackgroundColor(0xFF05070F);
        tvLog.setPadding(dp(12), dp(10), dp(12), dp(10));
        tvLog.setBackground(round(0xFF05070F, 0xFF1E2A4A, 12, 1));
        tvLog.setText("Selamat datang di Add WAN FH.\nSambungkan HP ke WiFi/LAN ONT,\nisi MAC, tekan CEK ONT.\n\n");
        ScrollView logScroll = new ScrollView(this);
        logScroll.addView(tvLog);
        logScroll.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(150)));
        c.addView(logScroll);
        main.addView(c);

        // ===== CARD: DONASI / COMMIT (opsional) =====
        c = card();
        c.addView(sectionTitle("Dukung Daffiyanet — opsional"));

        TextView devInfo = new TextView(this);
        devInfo.setText("Tool ini gratis & open source. Kalau bermanfaat, dukung pengembangannya:");
        devInfo.setTextSize(12);
        devInfo.setTextColor(MUTED);
        devInfo.setPadding(0, 0, 0, dp(8));
        c.addView(devInfo);

        Button bDonate = new Button(this);
        bDonate.setText("💛  Donasi via QRIS · 08516163719");
        bDonate.setAllCaps(false);
        bDonate.setTextColor(0xFF1A1200);
        bDonate.setTypeface(Typeface.DEFAULT_BOLD);
        bDonate.setBackground(round(0xFFFFD54A, 0, 12, 0));
        bDonate.setMinHeight(dp(46));
        bDonate.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://indra725.github.io/donasi/"));
            startActivity(i);
        });
        c.addView(bDonate);

        Button bSub = new Button(this);
        bSub.setText("▶  Subscribe @affiyaofficial di YouTube");
        bSub.setAllCaps(false);
        bSub.setTextColor(0xFFFFFFFF);
        bSub.setBackground(round(0xFF7C3AED, 0, 12, 0));
        bSub.setMinHeight(dp(46));
        bSub.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/@affiyaofficial"));
            startActivity(i);
        });
        c.addView(bSub);

        TextView note = new TextView(this);
        note.setText("Tidak mengunci fitur apa pun — semua jalan gratis.");
        note.setTextSize(10);
        note.setTextColor(0xFF4A5180);
        note.setPadding(0, dp(6), 0, 0);
        c.addView(note);
        main.addView(c);

        TextView footer = new TextView(this);
        footer.setText("github.com/Indra725/addwan-fiberhome-all-version · BUKAN produk resmi Fiberhome");
        footer.setTextSize(10);
        footer.setTextColor(0xFF4A5180);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(0, dp(4), 0, 0);
        main.addView(footer);

        bCheck.setOnClickListener(v -> runTask(() -> doCheckOnt(), bCheck, bUnlock, bStatus, bSet, bAcl));
        bUnlock.setOnClickListener(v -> runTask(() -> doUnlock(), bCheck, bUnlock, bStatus, bSet, bAcl));
        bStatus.setOnClickListener(v -> runTask(() -> doStatusUplink(), bCheck, bUnlock, bStatus, bSet, bAcl));
        bSet.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Set uplink?")
                .setMessage("WAN akan memakai port LAN " + spPort.getSelectedItem() + ".\n\nHP harus satu jaringan dengan ONT. Lanjutkan?")
                .setPositiveButton("Ya, SET", (d, w) -> runTask(() -> doSetUplink(), bCheck, bUnlock, bStatus, bSet, bAcl))
                .setNegativeButton("Batal", null)
                .show());
        bAcl.setOnClickListener(v -> {
            String ip = etIp.getText().toString().trim();
            if (ip.isEmpty()) ip = "192.168.1.1";
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + ip));
            startActivity(i);
            log("Membuka web UI ONT di http://" + ip + "\nSecurity > ACL Settings → Enable, isi IP, Save.");
        });

        root.addView(main);
        return root;
    }

    private interface Work { void run(); }

    private void runTask(Work w, final Button... buttons) {
        for (Button b : buttons) b.setEnabled(false);
        io.execute(() -> {
            try { w.run(); }
            catch (Exception e) { log("ERROR: " + e); }
            finally { runOnUiThread(() -> { for (Button b : buttons) b.setEnabled(true); }); }
        });
    }

    private void log(String s) { runOnUiThread(() -> tvLog.append(s + "\n")); }

    // ════════════════════════ LOGIKA ════════════════════════

    private String phoneIp() {
        try {
            java.util.Enumeration<java.net.NetworkInterface> ifs = java.net.NetworkInterface.getNetworkInterfaces();
            String fallback = null;
            while (ifs.hasMoreElements()) {
                java.net.NetworkInterface ni = ifs.nextElement();
                if (ni.isLoopback() || !ni.isUp()) continue;
                java.util.Enumeration<java.net.InetAddress> addrs = ni.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    java.net.InetAddress a = addrs.nextElement();
                    if (a.isLoopbackAddress() || a.getHostAddress().indexOf('.') < 0) continue;
                    String h = a.getHostAddress();
                    if (h.startsWith("192.168.") || h.startsWith("10.") || h.startsWith("172.")) return h;
                    if (fallback == null) fallback = h;
                }
            }
            return fallback;
        } catch (Exception e) { return null; }
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
            log("ping        : " + (ok ? "OK" : "GAGAL"));
        } catch (Exception e) { log("ping        : " + e); }
        for (int[] pr : new int[][]{{23, 0}, {80, 0}}) {
            try (java.net.Socket s = new java.net.Socket()) {
                s.connect(new java.net.InetSocketAddress(ip, pr[0]), 2000);
                log("TCP " + pr[0] + "       : TERBUKA");
            } catch (Exception e) { log("TCP " + pr[0] + "       : tutup"); }
        }
        String login = httpRequest("GET", "http://" + ip + "/html/login_inter.html", null);
        log("login page  : HTTP " + login.split("\n", 2)[0] + " (" + login.length() + " bytes)");
        String logo = httpRequest("GET", "http://" + ip + "/image/logo_Intelbras_fiberhome.png", null);
        log(logo.startsWith("200") && logo.length() > 1000 ? "custom webui: AKTIF" : "custom webui: belum terdeteksi");
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

        FileServer server = new FileServer();
        Thread st = new Thread(server);
        st.setDaemon(true);
        st.start();
        String url = "http://" + localIp + ":" + server.port + "/custom-webui.sh";
        log("URL script  : " + url);

        try (java.net.Socket sock = new java.net.Socket()) {
            sock.connect(new java.net.InetSocketAddress(ip, 23), 5000);
            sock.setSoTimeout(12000);
            Telnet t = new Telnet(sock);
            t.readUntil("ogin:"); t.send("gpon\r\n");
            t.readUntil("assword:"); t.send(pass + "\r\n");
            t.readUntil(">"); log("login       : OK");
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
        if (resp.startsWith("200")) log("\nuplink_type = dev.eth.N artinya port LAN itu jadi uplink.");
        else log("Gagal baca — coba unlock dulu, atau cek via F12 Console: XHR.get(\"get_uplink_info\",null,d=>console.log(d))");
    }

    private String doSetUplink() {
        String ip = ontIp();
        String port = String.valueOf(spPort.getSelectedItem());
        String body = "ajaxmethod=set_uplink_info&_=" + (System.currentTimeMillis() % 1000000000)
                + "&uplink_type=" + java.net.URLEncoder.encode(port);
        log("=== SET UPLINK " + ip + " -> " + port + " ===");
        String resp = httpRequest("POST", "http://" + ip + "/cgi-bin/ajax", body);
        log(resp);
        return resp;
    }

    private String httpRequest(String method, String url, String body) {
        java.net.HttpURLConnection conn = null;
        try {
            java.net.URL u = new java.net.URL(url);
            conn = (java.net.HttpURLConnection) u.openConnection();
            conn.setRequestMethod(method);
            conn.setConnectTimeout(4000);
            conn.setReadTimeout(8000);
            conn.setInstanceFollowRedirects(false);
            if (body != null) {
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                try (java.io.OutputStream os = conn.getOutputStream()) { os.write(body.getBytes("UTF-8")); }
            }
            int code = conn.getResponseCode();
            java.io.InputStream is = code < 400 ? conn.getInputStream() : conn.getErrorStream();
            byte[] buf = new byte[65536];
            int n = 0, r;
            while (n < buf.length && (r = is.read(buf, n, buf.length - n)) != -1) n += r;
            String text = new String(buf, 0, n, "UTF-8");
            return code + "\n" + (text.length() > 3000 ? text.substring(0, 3000) + "\n...(truncated)" : text);
        } catch (Exception e) {
            return "0\n" + e;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    static final class Telnet {
        private final java.net.Socket s;
        private final java.io.BufferedInputStream in;
        private final java.io.OutputStream out;
        private byte[] buf = new byte[0];

        Telnet(java.net.Socket s) throws java.io.IOException {
            this.s = s;
            in = new java.io.BufferedInputStream(s.getInputStream());
            out = s.getOutputStream();
        }

        void send(String text) throws java.io.IOException { out.write(text.getBytes("US-ASCII")); out.flush(); }

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

        String readUntil(String marker) throws java.io.IOException { return readUntil(marker, 10000); }

        String readUntil(String marker, int timeoutMs) throws java.io.IOException {
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

        String readAll(int timeoutMs) throws java.io.IOException {
            long deadline = System.currentTimeMillis() + timeoutMs;
            while (System.currentTimeMillis() < deadline) pump(400);
            String acc = new String(buf, 0, buf.length, "UTF-8");
            buf = new byte[0];
            return acc;
        }

        void drain() throws java.io.IOException { this.buf = new byte[0]; }

        private void pump(int millis) throws java.io.IOException {
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

        void close() { try { s.close(); } catch (java.io.IOException ignored) {} }
    }

    final class FileServer implements Runnable {
        volatile boolean running = true;
        final int port;
        private final java.net.ServerSocket ss;

        FileServer() {
            try { ss = new java.net.ServerSocket(0); } catch (java.io.IOException e) { throw new RuntimeException(e); }
            this.port = ss.getLocalPort();
        }

        public void run() {
            try { ss.setSoTimeout(45000); } catch (java.net.SocketException ignored) {}
            long deadline = System.currentTimeMillis() + 60000;
            while (running && System.currentTimeMillis() < deadline) {
                try {
                    java.net.Socket c = ss.accept();
                    new Thread(() -> serve(c)).start();
                } catch (java.net.SocketTimeoutException e) {
                    break;
                } catch (java.io.IOException e) {
                    break;
                }
            }
            try { ss.close(); } catch (java.io.IOException ignored) {}
        }

        private void serve(java.net.Socket c) {
            try (java.io.InputStream cis = c.getInputStream(); java.io.OutputStream cos = c.getOutputStream()) {
                byte[] head = new byte[8192];
                int n = cis.read(head);
                String req = new String(head, 0, Math.max(0, n), "ISO-8859-1");
                if (!req.contains("custom-webui.sh")) {
                    cos.write("HTTP/1.1 404 Not Found\r\nContent-Length: 0\r\n\r\n".getBytes());
                    return;
                }
                byte[] script;
                try (java.io.InputStream is = getAssets().open("custom-webui.sh")) {
                    script = readAllBytes(is);
                }
                String header = "HTTP/1.1 200 OK\r\nContent-Type: application/octet-stream\r\nContent-Length: "
                        + script.length + "\r\nConnection: close\r\n\r\n";
                cos.write(header.getBytes("US-ASCII"));
                cos.write(script);
                cos.flush();
            } catch (java.io.IOException ignored) {
            }
        }

        void stop() { running = false; try { ss.close(); } catch (java.io.IOException ignored) {} }
    }

    private static byte[] readAllBytes(java.io.InputStream is) throws java.io.IOException {
        java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
        byte[] tmp = new byte[8192];
        int n;
        while ((n = is.read(tmp)) != -1) bos.write(tmp, 0, n);
        return bos.toByteArray();
    }
}
