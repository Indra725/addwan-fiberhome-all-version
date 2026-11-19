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
 * Add WAN FH Mobile â€” v2.4
 * Tool ISP sendiri untuk HG6145F1/D2 pada perangkat yang Anda miliki / kelola.
 */
public class MainActivity extends Activity {

    private EditText etIp, etMac, etUser, etPass, etSn;
    private TextView tvLog, tvStatus;
    private android.widget.ScrollView logScroll;
    private android.widget.Spinner spPort;
    private String ontSessionId = null;
    private final java.util.concurrent.ExecutorService io = java.util.concurrent.Executors.newCachedThreadPool();

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
        loadSavedOntCredentials();
        autoRefreshOnt();
    }

    private void autoRefreshOnt() {
        if (etPass == null || etPass.getText().length() == 0) return;
        io.execute(() -> {
            try {
                log("auto-login ke ONT...");
                String sid = ontLogin();
                if (sid == null) {
                    log("auto-login gagal");
                    return;
                }
                logJson("uplink_info", ontCgi("get_uplink_info", null));
                logJson("acl_info", ontCgi("get_acl_info", null));
            } catch (Exception e) {
                log("auto-refresh error: " + e);
            }
        });
    }

    private int dp(int v) { return Math.round(v * getResources().getDisplayMetrics().density); }

    private int statusBarHeight() {
        int resId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        return resId > 0 ? getResources().getDimensionPixelSize(resId) : dp(24);
    }

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
        main.setPadding(pad, dp(12) + statusBarHeight(), pad, dp(14));
        main.setBackgroundColor(BG);

        // ===== HEADER =====
        TextView badge = new TextView(this);
        badge.setText("  âš¡ D A F F I Y A N E T  ");
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
        title.setText("ADD WAN FH + CONFIG");
        title.setTextSize(31);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(GOLD);
        title.setShadowLayer(dp(10), 0, 0, 0xB0FFD54A);
        main.addView(title);

        TextView sub = new TextView(this);
        sub.setText("Unlock & kelola HG6145F1 / HG6145D2 dari HP â€” tanpa PC, tanpa root.");
        sub.setTextSize(12);
        sub.setTextColor(MUTED);
        sub.setPadding(0, dp(4), 0, dp(12));
        main.addView(sub);

        // ===== STATUS BAR =====
        tvStatus = new TextView(this);
        tvStatus.setText("SIAP \u2014 isi IP ONT + MAC, lalu tekan tombol.");
        tvStatus.setTextSize(11);
        tvStatus.setTypeface(Typeface.DEFAULT_BOLD);
        tvStatus.setTextColor(0xFF7CFFC4);
        tvStatus.setBackground(round(0xFF07130E, 0xFF1E4A38, 10, 1));
        tvStatus.setPadding(dp(12), dp(8), dp(12), dp(8));
        LinearLayout.LayoutParams stLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        stLp.setMargins(0, 0, 0, dp(12));
        tvStatus.setLayoutParams(stLp);
        main.addView(tvStatus);

        // ===== CARD: LOG (di atas supaya output selalu terlihat) =====
        LinearLayout logCard = card();
        LinearLayout logHead = new LinearLayout(this);
        logHead.setOrientation(LinearLayout.HORIZONTAL);
        logHead.setGravity(Gravity.CENTER_VERTICAL);

        TextView logHeader = new TextView(this);
        logHeader.setText("* * *   output");
        logHeader.setTextSize(11);
        logHeader.setTypeface(Typeface.MONOSPACE);
        logHeader.setTextColor(0xFFFF5C5C);
        logHeader.setPadding(0, 0, 0, dp(8));
        logHead.addView(logHeader, lp(0, 1f, 0, 0, dp(8), 0));

        Button bCopyLog = new Button(this);
        bCopyLog.setText("SALIN");
        bCopyLog.setAllCaps(false);
        bCopyLog.setTextSize(10);
        bCopyLog.setTextColor(0xFF00E5FF);
        bCopyLog.setTypeface(Typeface.DEFAULT_BOLD);
        bCopyLog.setMinWidth(dp(64));
        bCopyLog.setMinHeight(dp(36));
        bCopyLog.setBackground(round(INPUT_BG, 0xFF00E5FF, 10, 1));
        bCopyLog.setOnClickListener(v -> copyLog());
        logHead.addView(bCopyLog, new LinearLayout.LayoutParams(dp(68), LinearLayout.LayoutParams.WRAP_CONTENT));
        logCard.addView(logHead);

        tvLog = new TextView(this);
        tvLog.setTextSize(12);
        tvLog.setTypeface(Typeface.MONOSPACE);
        tvLog.setTextColor(MINT);
        tvLog.setTextIsSelectable(true);
        tvLog.setPadding(dp(12), dp(10), dp(12), dp(10));
        tvLog.setBackground(round(0xFF05070F, 0xFF1E2A4A, 12, 1));
        tvLog.setText("Selamat datang di Add WAN FH.\nSambungkan HP ke WiFi/LAN ONT,\nisi MAC, tekan CEK ONT.\n\n");
        ScrollView logScroll = new ScrollView(this);
        logScroll.setPadding(0, 0, 0, 0);
        logScroll.setFillViewport(true);
        logScroll.setScrollbarFadingEnabled(false);
        logScroll.addView(tvLog);
        logScroll.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(240)));
        this.logScroll = logScroll;
        logCard.addView(logScroll);
        main.addView(logCard);

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
        macLabel.setPadding(0, dp(10), 0, dp(4));
        c.addView(macLabel);

        LinearLayout macRow = new LinearLayout(this);
        macRow.setOrientation(LinearLayout.HORIZONTAL);
        etMac = field("94:6B:12:AB:CD:EF", null);
        etMac.setTextColor(GOLD);
        etMac.setInputType(0x00000001 | 0x00001000);
        macRow.addView(etMac, lp(0, 1f, 0, 0, dp(8), 0));

        Button bPaste = new Button(this);
        bPaste.setText("PASTE");
        bPaste.setAllCaps(false);
        bPaste.setTextSize(11);
        bPaste.setTextColor(0xFF00E5FF);
        bPaste.setTypeface(Typeface.DEFAULT_BOLD);
        bPaste.setMinWidth(dp(74));
        bPaste.setMinHeight(dp(44));
        bPaste.setBackground(round(INPUT_BG, 0xFF00E5FF, 12, 1));
        bPaste.setOnClickListener(v -> pasteMacFromClipboard());
        macRow.addView(bPaste, new LinearLayout.LayoutParams(dp(74), LinearLayout.LayoutParams.WRAP_CONTENT));
        c.addView(macRow);

        TextView snLabel = new TextView(this);
        snLabel.setText("SN ONT (opsional \u2014 dipakai EXE untuk password telnet)");
        snLabel.setTextColor(MUTED);
        snLabel.setTextSize(10);
        snLabel.setLetterSpacing(0.16f);
        snLabel.setPadding(0, dp(10), 0, dp(4));
        c.addView(snLabel);
        etSn = field("FHTT9C7D6CA8", null);
        etSn.setTextColor(0xFF00E5FF);
        etSn.setInputType(0x00000001 | 0x00001000);
        c.addView(etSn);
        main.addView(c);

        // ===== CARD: AKUN ONT =====
        c = card();
        c.addView(sectionTitle("Akun ONT (untuk login web UI)"));

        TextView userLabel = new TextView(this);
        userLabel.setText("USERNAME");
        userLabel.setTextColor(MUTED);
        userLabel.setTextSize(10);
        userLabel.setLetterSpacing(0.16f);
        c.addView(userLabel);
        etUser = field("admin", "admin");
        etUser.setTextColor(TEXT);
        c.addView(etUser);

        TextView passLabel = new TextView(this);
        passLabel.setText("PASSWORD");
        passLabel.setTextColor(MUTED);
        passLabel.setTextSize(10);
        passLabel.setLetterSpacing(0.16f);
        passLabel.setPadding(0, dp(10), 0, dp(4));
        c.addView(passLabel);
        etPass = field("password web UI ONT", null);
        etPass.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etPass.setTextColor(TEXT);
        etPass.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) saveOntCredentials();
        });
        c.addView(etPass);

        android.widget.CheckBox cbSave = new android.widget.CheckBox(this);
        cbSave.setText("Simpan username & password di HP");
        cbSave.setTextColor(MUTED);
        cbSave.setChecked(true);
        cbSave.setOnCheckedChangeListener((btn, checked) -> {
            getSharedPreferences("ont", MODE_PRIVATE).edit().putBoolean("save", checked).apply();
            if (checked) saveOntCredentials();
        });
        cbSave.setPadding(0, dp(8), 0, 0);
        c.addView(cbSave);
        main.addView(c);

        // ===== CARD: AKSI =====
        c = card();
        c.addView(sectionTitle("Aksi"));

        Button bCheck = act("1.  CEK ONT", 0xFF0891B2);
        Button bUnlock = act("2.  UNLOCK", 0xFFB45309);
        Button bStatus = act("3.  STATUS UPLINK", 0xFF15803D);
        Button bSet = act("4.  SET UPLINK", 0xFF7C3AED);
        Button bAcl = act("5.  AKTIFKAN ACL (buka web UI)", 0xFF1E40AF);
        Button bCfg = act("6.  BACA CONFIG (file ONT)", 0xFFB45309);
        Button bEnc = act("7.  ENCRYPT CONFIG (buat file upload ONT)", 0xFF0F766E);
        Button bAesE = act("8.  ENCRYPT STRING (AES/ECB)", 0xFF334155);
        Button bAesD = act("9.  DECRYPT STRING (AES/ECB)", 0xFF334155);
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
        c.addView(bCfg);
        c.addView(bEnc);
        c.addView(bAesE);
        c.addView(bAesD);
        main.addView(c);

        // ===== CARD: WAN (fungsi asli EXE + CGI ONT) =====
        c = card();
        c.addView(sectionTitle("WAN \u2014 fungsi asli EXE + CGI ONT"));

        TextView wanNote = new TextView(this);
        wanNote.setText("ADD WAN (IDN_XL) = perintah asli Add_WAN_FH.exe lewat telnet (port 23).\nBACA/TAMBAH/EDIT/HAPUS WAN = CGI ONT, butuh USERNAME + PASSWORD web UI di kartu Koneksi ONT.");
        wanNote.setTextSize(10);
        wanNote.setTextColor(MUTED);
        wanNote.setPadding(0, 0, 0, dp(6));
        c.addView(wanNote);

        Button bPre = act("ADD WAN \u2014 load preconfig IDN_XL (EXE)", 0xFFB45309);
        Button bWanList = act("BACA DAFTAR WAN", 0xFF0891B2);
        Button bWanAdd = act("TAMBAH WAN (PPPoE / IPoE / Static)", 0xFF15803D);
        Button bWanEdit = act("EDIT WAN", 0xFF7C3AED);
        Button bWanDel = act("HAPUS WAN", 0xFFB91C1C);
        c.addView(bPre);
        c.addView(bWanList);
        c.addView(bWanAdd);
        c.addView(bWanEdit);
        c.addView(bWanDel);
        main.addView(c);

        // ===== CARD: DONASI / COMMIT (opsional) =====
        c = card();
        c.addView(sectionTitle("Dukung Daffiyanet â€” opsional"));

        TextView devInfo = new TextView(this);
        devInfo.setText("Tool ini gratis & open source. Kalau bermanfaat, dukung pengembangannya:");
        devInfo.setTextSize(12);
        devInfo.setTextColor(MUTED);
        devInfo.setPadding(0, 0, 0, dp(8));
        c.addView(devInfo);

        Button bDonate = new Button(this);
        bDonate.setText("ðŸ’›  Donasi via QRIS Â· 08516163719");
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
        bSub.setText("â–¶  Subscribe @affiyaofficial di YouTube");
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
        note.setText("Tidak mengunci fitur apa pun â€” semua jalan gratis.");
        note.setTextSize(10);
        note.setTextColor(0xFF4A5180);
        note.setPadding(0, dp(6), 0, 0);
        c.addView(note);
        main.addView(c);

        TextView footer = new TextView(this);
        footer.setText("github.com/Indra725/addwan-fiberhome-all-version Â· BUKAN produk resmi Fiberhome");
        footer.setTextSize(10);
        footer.setTextColor(0xFF4A5180);
        footer.setGravity(Gravity.CENTER);
        footer.setPadding(0, dp(4), 0, 0);
        main.addView(footer);

        bCheck.setOnClickListener(v -> runTask("CEK ONT", () -> doCheckOnt(), bCheck, bUnlock, bStatus, bSet, bAcl));
        bUnlock.setOnClickListener(v -> runTask("UNLOCK", () -> doUnlock(), bCheck, bUnlock, bStatus, bSet, bAcl));
        bStatus.setOnClickListener(v -> runTask("STATUS UPLINK", () -> doStatusUplink(), bCheck, bUnlock, bStatus, bSet, bAcl));
        bSet.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Set uplink?")
                .setMessage("WAN akan memakai port LAN " + spPort.getSelectedItem() + ".\n\nHP harus satu jaringan dengan ONT. Lanjutkan?")
                .setPositiveButton("Ya, SET", (d, w) -> runTask("SET UPLINK", () -> doSetUplink(), bCheck, bUnlock, bStatus, bSet, bAcl))
                .setNegativeButton("Batal", null)
                .show());
        bAcl.setOnClickListener(v -> {
            String ip = etIp.getText().toString().trim();
            if (ip.isEmpty()) ip = "192.168.1.1";
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + ip));
            startActivity(i);
            log("Membuka web UI ONT di http://" + ip + "\nSecurity > ACL Settings â†’ Enable, isi IP, Save.");
        });
        bCfg.setOnClickListener(v -> pickConfigFile());
        bEnc.setOnClickListener(v -> pickEncryptFile());
        bAesE.setOnClickListener(v -> showAesDialog(true));
        bAesD.setOnClickListener(v -> showAesDialog(false));

        bPre.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("ADD WAN \u2014 load preconfig IDN_XL")
                .setMessage("Perintah asli Add_WAN_FH.exe:\n\ncd factorydir\nload preconfig IDN_XL\n\nProfil WAN ISP (IDN_XL) akan dimuat ke ONT lewat telnet. Lanjutkan?")
                .setPositiveButton("Ya, JALANKAN", (d, w) -> runTask("ADD WAN IDN_XL", () -> doAddWanPreconfig(), bPre, bWanList, bWanAdd, bWanEdit, bWanDel))
                .setNegativeButton("Batal", null).show());
        bWanList.setOnClickListener(v -> runTask("BACA DAFTAR WAN", () -> doReadWan(), bPre, bWanList, bWanAdd, bWanEdit, bWanDel));
        bWanAdd.setOnClickListener(v -> showWanDialog(null));
        bWanEdit.setOnClickListener(v -> runTask("EDIT WAN", () -> pickWanForEdit(), bPre, bWanList, bWanAdd, bWanEdit, bWanDel));
        bWanDel.setOnClickListener(v -> runTask("HAPUS WAN", () -> pickWanForDelete(), bPre, bWanList, bWanAdd, bWanEdit, bWanDel));

        root.addView(main);
        return root;
    }

    private interface Work { void run(); }

    private void setStatus(String s) {
        runOnUiThread(() -> { if (tvStatus != null) tvStatus.setText(s); });
    }

    private void copyLog() {
        try {
            android.content.ClipboardManager cm = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            if (cm != null && tvLog != null) {
                cm.setPrimaryClip(android.content.ClipData.newPlainText("log", tvLog.getText().toString()));
                log("log disalin ke clipboard");
            }
        } catch (Exception e) { log("gagal salin log: " + e); }
    }

    private void runTask(String label, Work w, final Button... buttons) {
        for (Button b : buttons) b.setEnabled(false);
        setStatus("\u23F3 " + label + " ...");
        log("\u25B6 " + label);
        io.execute(() -> {
            try {
                w.run();
                setStatus("\u2705 " + label + " selesai");
            } catch (Exception e) {
                log("ERROR: " + e);
                setStatus("\u274C " + label + " gagal: " + e);
            } finally {
                runOnUiThread(() -> {
                    for (Button b : buttons) b.setEnabled(true);
                    log("<< selesai");
                });
            }
        });
    }

    private void log(String s) {
        runOnUiThread(() -> {
            tvLog.append(s + "\n");
            if (logScroll != null) {
                logScroll.post(() -> logScroll.fullScroll(android.view.View.FOCUS_DOWN));
            }
        });
    }

    private void logJson(String label, String json) {
        log(label + ":");
        try {
            String pretty = json.trim()
                    .replace("\",", "\",\n  ")
                    .replace("\"}", "\"\n}")
                    .replace("{", "{\n  ")
                    .replace("}", "\n}");
            log(pretty);
        } catch (Exception e) {
            log(json);
        }
    }

    // â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â• LOGIKA â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•

    private void loadSavedOntCredentials() {
        String u = getSharedPreferences("ont", MODE_PRIVATE).getString("user", "admin");
        String p = getSharedPreferences("ont", MODE_PRIVATE).getString("pass", "");
        boolean save = getSharedPreferences("ont", MODE_PRIVATE).getBoolean("save", true);
        etUser.setText(u);
        if (!p.isEmpty()) etPass.setText(p);
    }

    private void saveOntCredentials() {
        boolean save = getSharedPreferences("ont", MODE_PRIVATE).getBoolean("save", true);
        if (save) {
            getSharedPreferences("ont", MODE_PRIVATE).edit()
                    .putString("user", etUser.getText().toString().trim())
                    .putString("pass", etPass.getText().toString())
                    .apply();
        }
    }

    private String ontLogin() {
        String ip = ontIp();
        String user = etUser.getText().toString().trim();
        String pass = etPass.getText().toString();
        if (pass.isEmpty()) {
            log("password kosong - isi di kartu Akun ONT");
            return null;
        }
        try {
            String acs = httpRequest("GET", "http://" + ip + "/cgi-bin/ajax?ajaxmethod=get_acs_random&_=" + (System.currentTimeMillis() % 1000000000), null);
            String sessionid = jsonValue(acs, "sessionid");
            if (sessionid == null) { log("get_acs_random gagal: " + acs); return null; }
            javax.crypto.Cipher c = javax.crypto.Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(javax.crypto.Cipher.ENCRYPT_MODE,
                    new javax.crypto.spec.SecretKeySpec("6HTT4DL67FVDkLVL".getBytes("US-ASCII"), "AES"),
                    new javax.crypto.spec.IvParameterSpec("6HTT4DL67FVDkLVL".getBytes("US-ASCII")));
            byte[] enc = c.doFinal(pass.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : enc) sb.append(String.format("%02X", b));
            String body = "ajaxmethod=do_login&username=" + java.net.URLEncoder.encode(user, "UTF-8")
                    + "&loginpd=" + sb + "&port=0&sessionid=" + sessionid + "&_=" + (System.currentTimeMillis() % 1000000000);
            String resp = httpRequest("POST", "http://" + ip + "/cgi-bin/ajax", body);
            saveOntCredentials();
            if (resp.replace(" ", "").contains("\"login_result\":0")) {
                ontSessionId = sessionid;
                log("login web UI ONT: OK");
                return sessionid;
            }
            log("login GAGAL: " + resp);
            return null;
        } catch (Exception e) {
            log("login ERROR: " + e);
            return null;
        }
    }

    private String jsonValue(String json, String key) {
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]*)\"").matcher(json);
        return m.find() ? m.group(1) : null;
    }

    private static String bodyOf(String resp) {
        if (resp == null) return "";
        int nl = resp.indexOf('\n');
        return nl >= 0 ? resp.substring(nl + 1) : resp;
    }

    /** POST CGI persis XHR.post asli: refresh sessionid dulu, lalu kirim body form-urlencoded. */
    private String ontCgi(String ajaxmethod, String params) {
        String ip = ontIp();
        try {
            String r = httpRequest("GET", "http://" + ip + "/cgi-bin/ajax?ajaxmethod=get_refresh_sessionid&_=" + (System.currentTimeMillis() % 1000000000), null);
            String s = jsonValue(r, "sessionid");
            if (s != null && !s.isEmpty()) ontSessionId = s;
        } catch (Exception ignored) { }
        String body = "ajaxmethod=" + ajaxmethod
                + (params == null || params.isEmpty() ? "" : "&" + params)
                + (ontSessionId == null ? "" : "&sessionid=" + ontSessionId)
                + "&_=" + (System.currentTimeMillis() % 1000000000);
        return bodyOf(httpRequest("POST", "http://" + ip + "/cgi-bin/ajax", body));
    }

    private static final int REQ_CONFIG = 1001;
    private static final int REQ_ENCRYPT = 1002;
    private static final int REQ_SAVE_CONFIG = 1003;
    private byte[] pendingConfigBytes;

    private void pickConfigFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQ_CONFIG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null || data.getData() == null) return;
        try {
            java.io.InputStream is = getContentResolver().openInputStream(data.getData());
            if (is == null) { log("ERROR: tidak bisa membaca file"); return; }
            byte[] bytes = readAllBytes(is);
            is.close();
            if (requestCode == REQ_CONFIG) {
                io.execute(() -> decryptAndShowConfig(bytes));
            } else if (requestCode == REQ_ENCRYPT) {
                io.execute(() -> encryptAndSaveConfig(bytes));
            } else if (requestCode == REQ_SAVE_CONFIG && pendingConfigBytes != null) {
                try (java.io.OutputStream out = getContentResolver().openOutputStream(data.getData())) {
                    if (out == null) { log("ERROR: tidak bisa tulis file"); return; }
                    out.write(pendingConfigBytes);
                    log("TERSIMPAN: " + data.getData().getPath());
                    log("Upload file ini di web UI ONT:");
                    log("  Management > Device Management > Local Upgrade");
                } catch (Exception e) {
                    log("ERROR menulis file: " + e);
                }
                pendingConfigBytes = null;
            }
        } catch (Exception e) {
            log("ERROR membaca file: " + e);
        }
    }

    private byte[] xorTransform(byte[] data, int key) {
        int size = data.length;
        int sizeDivKey = size / key;
        byte[] out = new byte[size];
        for (int i = 0; i < size; i++) {
            out[i] = (byte) (data[i] ^ ((size + key + i - sizeDivKey) & 0xFF));
        }
        return out;
    }

    private byte[] gunzip(byte[] data) throws java.io.IOException {
        java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
        try (java.util.zip.GZIPInputStream gis = new java.util.zip.GZIPInputStream(new java.io.ByteArrayInputStream(data))) {
            byte[] tmp = new byte[4096];
            int n;
            while ((n = gis.read(tmp)) != -1) bos.write(tmp, 0, n);
        }
        return bos.toByteArray();
    }

    private byte[] gzipBytes(byte[] data) throws java.io.IOException {
        java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
        try (java.util.zip.GZIPOutputStream gos = new java.util.zip.GZIPOutputStream(bos)) {
            gos.write(data);
        }
        return bos.toByteArray();
    }

    private void tarPutString(byte[] header, int offset, String value, int maxLen) {
        byte[] b = value.getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        int len = Math.min(b.length, maxLen - 1);
        for (int i = 0; i < len; i++) header[offset + i] = b[i];
    }

    private void tarPutOctal(byte[] header, int offset, int value, int fieldLen) {
        String s = Integer.toOctalString(value);
        int len = Math.min(s.length(), fieldLen - 1);
        int pad = fieldLen - 1 - len;
        for (int i = 0; i < pad; i++) header[offset + i] = '0';
        for (int i = 0; i < len; i++) header[offset + pad + i] = (byte) s.charAt(i);
        header[offset + fieldLen - 1] = 0;
    }

    private byte[] tarUsrconfig(String text) {
        byte[] content = text.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] header = new byte[512];
        tarPutString(header, 0, "usrconfig_conf", 100);
        tarPutString(header, 100, "0000644\0", 8);
        tarPutString(header, 108, "0000000\0", 8);
        tarPutString(header, 116, "0000000\0", 8);
        tarPutOctal(header, 124, content.length, 12);
        tarPutOctal(header, 136, (int) (System.currentTimeMillis() / 1000), 12);
        for (int i = 148; i < 156; i++) header[i] = ' ';
        header[156] = '0';
        header[257] = 'u'; header[258] = 's'; header[259] = 't'; header[260] = 'a'; header[261] = 'r';
        long sum = 0;
        for (byte b : header) sum += (b & 0xFF);
        tarPutOctal(header, 148, (int) sum, 8);
        java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
        try {
            bos.write(header);
            bos.write(content);
            int remainder = content.length % 512;
            if (remainder != 0) bos.write(new byte[512 - remainder]);
            bos.write(new byte[1024]); // end-of-archive two zero blocks
        } catch (java.io.IOException ignored) {}
        return bos.toByteArray();
    }

    private void encryptAndSaveConfig(byte[] plainBytes) {
        log("=== ENCRYPT CONFIG ===");
        log("input: " + plainBytes.length + " bytes (teks config)");
        try {
            String text = new String(plainBytes, java.nio.charset.StandardCharsets.UTF_8);
            byte[] tar = tarUsrconfig(text);
            byte[] gz = gzipBytes(tar);
            byte[] enc = xorTransform(gz, 0x2537);
            log("tar.gz+xor: " + enc.length + " bytes");
            pendingConfigBytes = enc;
            Intent save = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            save.addCategory(Intent.CATEGORY_OPENABLE);
            save.setType("application/octet-stream");
            save.putExtra(Intent.EXTRA_TITLE, "usrconfig_conf");
            startActivityForResult(save, REQ_SAVE_CONFIG);
            log("pilih lokasi penyimpanan file...");
        } catch (Exception e) {
            log("ENCRYPT ERROR: " + e);
        }
    }

    private String untarUsrconfig(byte[] data) {
        return untarMember(data, "usrconfig_conf");
    }

    private String untarMember(byte[] data, String wanted) {
        long offset = 0;
        while (offset + 512 <= data.length) {
            StringBuilder name = new StringBuilder();
            for (int i = 0; i < 100 && data[(int) offset + i] != 0; i++) name.append((char) (data[(int) offset + i] & 0xFF));
            if (name.length() == 0) return null;
            int size = 0;
            for (int i = 124; i < 136 && data[(int) offset + i] != 0; i++) {
                char c = (char) (data[(int) offset + i] & 0xFF);
                if (c >= '0' && c <= '7') size = size * 8 + (c - '0');
            }
            String n = name.toString();
            if (n.equals(wanted) || n.endsWith("/" + wanted)) {
                return new String(data, (int) offset + 512, size, java.nio.charset.StandardCharsets.UTF_8);
            }
            long blocks = (size + 511) / 512;
            offset += 512 + blocks * 512;
        }
        return null;
    }

    private String aesDecryptHex(String hex) {
        try {
            javax.crypto.Cipher c = javax.crypto.Cipher.getInstance("AES/ECB/NoPadding");
            byte[] key = "ABCDEFGHIJKLMNOP".getBytes("ASCII");
            c.init(javax.crypto.Cipher.DECRYPT_MODE, new javax.crypto.spec.SecretKeySpec(key, "AES"));
            byte[] out = c.doFinal(hexStringToBytes(hex));
            int end = out.length;
            while (end > 0 && out[end - 1] == 0) end--;
            if (end == 0) return null;
            for (int i = 0; i < end; i++) {
                int b = out[i] & 0xFF;
                if (b < 32 || b > 126) return null;
            }
            return new String(out, 0, end, "UTF-8");
        } catch (Exception e) {
            return null;
        }
    }

    private byte[] hexStringToBytes(String s) {
        int len = s.length();
        byte[] out = new byte[len / 2];
        for (int i = 0; i < len; i += 2) out[i / 2] = (byte) Integer.parseInt(s.substring(i, i + 2), 16);
        return out;
    }

    private String annotateHexValues(String line) {
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("([0-9A-F]{32,64})").matcher(line);
        StringBuffer sb = new StringBuffer();
        boolean found = false;
        while (m.find()) {
            String hex = m.group(1);
            String dec = aesDecryptHex(hex);
            if (dec != null) {
                m.appendReplacement(sb, hex + "  =>  [AES] " + Matcher_quote(dec));
                found = true;
            }
        }
        if (!found) return null;
        m.appendTail(sb);
        return sb.toString();
    }

    private static String Matcher_quote(String s) {
        return java.util.regex.Matcher.quoteReplacement(s);
    }

    private void decryptAndShowConfig(byte[] bytes) {
        log("=== BACA CONFIG ===");
        log("file: " + bytes.length + " bytes");
        byte[] d = xorTransform(bytes, 0x2537);
        if (d.length >= 2 && (d[0] & 0xFF) == 0x1F && (d[1] & 0xFF) == 0x8B) {
            try {
                d = gunzip(d);
                log("gzip: OK");
            } catch (Exception e) {
                log("gzip ERR: " + e);
                return;
            }
        } else {
            log("bukan gzip, coba parse langsung");
        }
        String text;
        if (d.length > 262 && d[257] == 'u' && d[258] == 's' && d[259] == 't' && d[260] == 'a' && d[261] == 'r') {
            text = untarUsrconfig(d);
            if (text == null) { log("usrconfig_conf tidak ditemukan dalam archive"); return; }
            log("isi: usrconfig_conf (" + text.split("\n").length + " baris)");
            StringBuilder tarOut = new StringBuilder();
            String voice = untarMember(d, "voice_digitmap_conf");
            if (voice != null) {
                tarOut.append("=== voice_digitmap_conf ===\n").append(voice).append("\n");
            }
            if (tarOut.length() > 0) log(tarOut.toString());
            printSecretsSummary(text);
        } else {
            try {
                text = new String(d, "UTF-8");
            } catch (java.io.UnsupportedEncodingException e) {
                text = new String(d, java.nio.charset.StandardCharsets.UTF_8);
            }
            log("isi: file tunggal (" + text.split("\n").length + " baris)");
        }
        log("");
        for (String line : text.split("\n")) {
            String ann = annotateHexValues(line);
            log(ann != null ? ann : line);
        }
        log("=== SELESAI ===");
    }

    private void printSecretsSummary(String text) {
        java.util.HashMap<String, String> m = new java.util.HashMap<>();
        for (String line : text.split("\n")) {
            java.util.regex.Matcher mm = java.util.regex.Pattern
                    .compile("option\\s+(\\S+)\\s+'([^']*)'").matcher(line);
            if (mm.find()) {
                String v = mm.group(2);
                if (v.matches("[0-9A-Fa-f]{32}")) {
                    String dec = aesDecryptHex(v);
                    if (dec != null) m.put(mm.group(1), dec);
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== RINGKASAN PASSWORD ===\n");
        String[][] groups = {
                {"PreSharedKey", "WiFi 2.4G Password"},
                {"KeyPassphrase", "WiFi Key"},
                {"Username", "ACS Username"},
                {"Password", "ACS Password"},
                {"ConnectionRequestUsername", "ACS ConnRequest User"},
                {"ConnectionRequestPassword", "ACS ConnRequest Pass"}
        };
        for (String[] g : groups) {
            String v = m.get(g[0]);
            if (v != null) sb.append(g[1]).append(": ").append(v).append("\n");
        }
        String out = sb.toString().trim();
        if (!out.isEmpty()) log(out);
    }


    private void pickEncryptFile() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.setType("*/*");
        i.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(i, REQ_ENCRYPT);
    }

    private void showAesDialog(boolean encrypt) {
        android.widget.EditText input = new android.widget.EditText(this);
        input.setHint(encrypt ? "teks biasa -> hex terenkripsi" : "hex -> teks biasa");
        input.setMinLines(3);
        input.setGravity(Gravity.TOP | Gravity.START);
        new AlertDialog.Builder(this)
                .setTitle(encrypt ? "ENCRYPT STRING (AES/ECB)" : "DECRYPT STRING (AES/ECB)")
                .setView(input)
                .setPositiveButton("PROSES", (d, w) -> {
                    String raw = input.getText().toString().trim();
                    if (raw.isEmpty()) { log("input kosong"); return; }
                    log((encrypt ? "ENCRYPT STRING => " : "DECRYPT STRING => ") + (encrypt ? aesEncryptToHex(raw) : aesDecryptHexToText(raw)));
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private String aesEncryptToHex(String text) {
        try {
            javax.crypto.Cipher c = javax.crypto.Cipher.getInstance("AES/ECB/NoPadding");
            byte[] key = "ABCDEFGHIJKLMNOP".getBytes("US-ASCII");
            c.init(javax.crypto.Cipher.ENCRYPT_MODE, new javax.crypto.spec.SecretKeySpec(key, "AES"));
            byte[] data = text.getBytes("UTF-8");
            int rem = data.length % 16;
            if (rem != 0) {
                byte[] padded = java.util.Arrays.copyOf(data, data.length + (16 - rem));
                data = padded;
            }
            byte[] out = c.doFinal(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : out) sb.append(String.format("%02X", b));
            return sb.toString();
        } catch (Exception e) {
            return "ERROR: " + e;
        }
    }

    private String aesDecryptHexToText(String hex) {
        try {
            javax.crypto.Cipher c = javax.crypto.Cipher.getInstance("AES/ECB/NoPadding");
            byte[] key = "ABCDEFGHIJKLMNOP".getBytes("US-ASCII");
            c.init(javax.crypto.Cipher.DECRYPT_MODE, new javax.crypto.spec.SecretKeySpec(key, "AES"));
            byte[] out = c.doFinal(hexStringToBytes(hex));
            int end = out.length;
            while (end > 0 && out[end - 1] == 0) end--;
            return new String(out, 0, end, "UTF-8");
        } catch (Exception e) {
            return "ERROR: " + e;
        }
    }

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

    private void pasteMacFromClipboard() {
        try {
            android.content.ClipboardManager cm = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            if (cm == null || !cm.hasPrimaryClip()) { log("Clipboard kosong â€” copy MAC dari ONT dulu"); return; }
            android.content.ClipData.Item item = cm.getPrimaryClip().getItemAt(0);
            String raw = item.getText() == null ? "" : item.getText().toString();
            String hex = raw.replaceAll("[^0-9a-fA-F]", "").toUpperCase();
            if (hex.length() < 12) { log("Clipboard tidak berisi MAC valid (" + hex.length() + " hex)"); return; }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 12; i += 2) {
                if (i > 0) sb.append(':');
                sb.append(hex, i, i + 2);
            }
            String mac = sb.toString();
            etMac.setText(mac);
            log("MAC ditempel: " + mac);
        } catch (Exception e) {
            log("Gagal tempel: " + e);
        }
    }

    private void doCheckOnt() {
        String ip = ontIp();
        log("=== CEK ONT " + ip + " ===");
        boolean reachable = false;
        for (int[] pr : new int[][]{{80, 0}, {23, 0}}) {
            try (java.net.Socket s = new java.net.Socket()) {
                s.connect(new java.net.InetSocketAddress(ip, pr[0]), 1500);
                log("TCP " + pr[0] + "       : TERBUKA");
                reachable = true;
            } catch (Exception e) { log("TCP " + pr[0] + "       : tutup"); }
        }
        if (!reachable) {
            log("ONT tidak terjangkau - cek HP satu jaringan dengan ONT (192.168.1.x)");
            return;
        }
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
        if (etPass != null && etPass.getText().length() > 0) {
            log("");
            log("Membaca data dari ONT (login web UI)...");
            String sid = ontLogin();
            if (sid != null) {
                logJson("uplink_info", ontCgi("get_uplink_info", null));
                logJson("acl_info", ontCgi("get_acl_info", null));
            }
        } else {
            log("isi password di kartu Akun ONT untuk baca uplink/ACL langsung");
        }
        log("IP HP saya  : " + phoneIp());
    }

    private void doUnlock() {
        String ip = ontIp();
        String mac = etMac.getText().toString().trim();
        if (mac.replaceAll("[^0-9a-fA-F]", "").length() < 6) { log("MAC kosong/tidak valid â€” isi dulu"); return; }
        log("=== UNLOCK " + ip + " ===");
        String pass = macPassword(mac);
        log("password    : " + pass);
        String localIp = phoneIp();
        if (localIp == null) { log("ERROR: IP HP tidak terdeteksi â€” sambungkan ke WiFi ONT"); return; }
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
            log("=== UNLOCK SELESAI â€” buka http://" + ip + " di browser HP ===");
        } catch (Exception e) {
            server.stop();
            log("UNLOCK GAGAL: " + e);
            log("Pastikan: telnet aktif (Security > ACL Settings), HP satu jaringan dengan ONT.");
        }
    }

    private void doStatusUplink() {
        String ip = ontIp();
        log("=== STATUS / UPLINK " + ip + " ===");
        String sid = ontLogin();
        if (sid == null) { log("login gagal - cek password di kartu Akun ONT"); return; }
        logJson("uplink_info", ontCgi("get_uplink_info", null));
        logJson("acl_info", ontCgi("get_acl_info", null));
        logJson("refresh_sessionid", ontCgi("get_refresh_sessionid", null));
    }

    private String doSetUplink() {
        String ip = ontIp();
        String port = String.valueOf(spPort.getSelectedItem());
        log("=== SET UPLINK " + ip + " -> " + port + " ===");
        String sid = ontLogin();
        if (sid == null) { log("login gagal"); return null; }
        String resp;
        try {
            resp = ontCgi("set_uplink_info", "uplink_type=" + java.net.URLEncoder.encode(port, "UTF-8"));
        } catch (java.io.UnsupportedEncodingException e) {
            resp = ontCgi("set_uplink_info", "uplink_type=" + port);
        }
        log("set_uplink_info: " + resp);
        log("cek: " + ontCgi("get_uplink_info", null));
        return resp;
    }

    private void doToggleAcl() {
        String sid = ontLogin();
        if (sid == null) return;
        String before = ontCgi("get_acl_info", null);
        logJson("acl sebelum", before);
        boolean enabled = before.contains("\"Enable\":\"1\"");
        String target = enabled ? "0" : "1";
        String resp = ontCgi("set_acl_info", "action=enable&Enable=" + target);
        logJson("set_acl_info", resp);
        String after = ontCgi("get_acl_info", null);
        logJson("acl sesudah", after);
        log(enabled ? "ACL sekarang NONAKTIF" : "ACL sekarang AKTIF");
    }

    // ================= WAN: fungsi asli EXE + CGI ONT =================

    /** Password telnet: EXE memakai SN (Fh@ + Mid(SN,5,6)); fallback ke MAC. */
    private String telnetPassword() {
        String sn = etSn == null ? "" : etSn.getText().toString().trim();
        String hexSn = sn.replaceAll("[^0-9a-fA-F]", "");
        if (hexSn.length() >= 10) return "Fh@" + hexSn.substring(4, 10).toUpperCase();
        return macPassword(etMac.getText().toString().trim());
    }

    /** Urutan perintah persis Add_WAN_FH.exe: gpon -> Fh@SN -> en -> cd factorydir -> load preconfig IDN_XL */
    private void doAddWanPreconfig() {
        String ip = ontIp();
        log("=== ADD WAN (load preconfig IDN_XL) " + ip + " ===");
        String pass = telnetPassword();
        log("password telnet : " + pass);
        try (java.net.Socket sock = new java.net.Socket()) {
            log("1/6  menyambung telnet " + ip + ":23 ...");
            sock.connect(new java.net.InetSocketAddress(ip, 23), 4000);
            sock.setSoTimeout(15000);
            Telnet t = new Telnet(sock);

            String s1 = t.readUntil("ogin:", 6000);
            log("2/6  prompt login   : " + oneLine(s1));
            t.send("gpon\r\n");

            String s2 = t.readUntil("assword:", 6000);
            log("3/6  prompt password: " + oneLine(s2));
            t.send(pass + "\r\n");

            String s3 = t.readUntil("user>", 6000);
            log("4/6  setelah login  : " + oneLine(s3));
            t.send("en\r\n");

            String s4 = t.readUntil("config#", 6000);
            if (s4.toLowerCase().contains("assword")) {
                t.send(pass + "\r\n");
                s4 = t.readUntil("config#", 6000);
            }
            log("5/6  prompt config  : " + oneLine(s4));

            t.send("cd factorydir\r\n");
            String s5 = t.readUntil("factorydir#", 6000);
            log("     cd factorydir  : " + oneLine(s5));

            t.send("load preconfig IDN_XL\r\n");
            log("6/6  menunggu hasil load preconfig ...");
            String out = t.readAll(15000);
            for (String line : out.split("\n")) {
                String s = line.replace("\r", "").trim();
                if (!s.isEmpty()) log("    " + s);
            }
            t.close();
            String low = out.toLowerCase();
            log(low.contains("sukses") || low.contains("success")
                    ? "=== SELESAI: profil IDN_XL dimuat ==="
                    : "=== SELESAI: cek output di atas ===");
        } catch (Exception e) {
            log("ADD WAN GAGAL: " + e);
            log("Pastikan telnet aktif (port 23) & HP satu jaringan dengan ONT.");
        }
    }

    private static String oneLine(String s) {
        if (s == null) return "(kosong)";
        String x = s.replace("\r", " ").replace("\n", " ").trim();
        if (x.isEmpty()) return "(kosong)";
        return x.length() > 120 ? x.substring(0, 120) + "..." : x;
    }

    private String enc(String s) {
        try { return java.net.URLEncoder.encode(s == null ? "" : s, "UTF-8"); }
        catch (Exception e) { return s == null ? "" : s; }
    }

    private org.json.JSONArray wanArray() {
        String resp = ontCgi("get_allwan_info", null);
        if (resp == null || resp.isEmpty()) { log("respons kosong dari get_allwan_info"); return null; }
        try {
            org.json.JSONObject o = new org.json.JSONObject(resp);
            if (o.optInt("session_valid", 1) == 0) {
                log("ONT menolak sesi (session_valid=0) - login ulang...");
                ontSessionId = null;
                if (ontLogin() == null) return null;
                resp = ontCgi("get_allwan_info", null);
                o = new org.json.JSONObject(resp);
            }
            org.json.JSONArray arr = o.optJSONArray("wan");
            if (arr == null) { log("respons tanpa array 'wan': " + resp); return null; }
            return arr;
        } catch (Exception e) { log("parse WAN gagal: " + e); log("respons: " + resp); return null; }
    }

    private void doReadWan() {
        log("=== DAFTAR WAN ===");
        if (ontLogin() == null) return;
        String resp = ontCgi("get_allwan_info", null);
        log("respons: " + (resp == null ? "null" : (resp.length() > 1500 ? resp.substring(0, 1500) + "..." : resp)));
        try {
            org.json.JSONObject o = new org.json.JSONObject(resp);
            if (o.optInt("session_valid", 1) == 0) {
                log("sesi ditolak ONT (session_valid=0). Cek username/password web UI.");
                return;
            }
            org.json.JSONArray arr = o.optJSONArray("wan");
            if (arr == null) { log("tidak ada array 'wan'"); return; }
            if (arr.length() == 0) log("(belum ada WAN terdaftar)");
            for (int i = 0; i < arr.length(); i++) {
                org.json.JSONObject w = arr.getJSONObject(i);
                log(String.format("[%d] %s | %s | %s | VLAN %s | NAT %s | Enable %s",
                        i,
                        w.optString("Name", "?"),
                        w.optString("ConnectionType", "?"),
                        w.optString("AddressingType", "-"),
                        w.optString("VLANID", w.optString("vlanid", "?")),
                        w.optString("NATEnabled", "?"),
                        w.optString("Enable", "?")));
            }
            log("total: " + arr.length() + " WAN");
        } catch (Exception e) { log("parse: " + e); }
    }

    private void pickWanForDelete() {
        if (ontLogin() == null) return;
        final org.json.JSONArray arr = wanArray();
        if (arr == null || arr.length() == 0) { log("tidak ada WAN untuk dihapus"); return; }
        final String[] labels = new String[arr.length()];
        for (int i = 0; i < arr.length(); i++) {
            org.json.JSONObject w = arr.optJSONObject(i);
            labels[i] = (i + 1) + ". " + w.optString("Name", "?") + "  (" + w.optString("ConnectionType", "?") + ")";
        }
        runOnUiThread(() -> new AlertDialog.Builder(this)
                .setTitle("Hapus WAN")
                .setItems(labels, (d, which) -> {
                    final org.json.JSONObject w = arr.optJSONObject(which);
                    new AlertDialog.Builder(this)
                            .setTitle("Hapus " + w.optString("Name", "?"))
                            .setMessage("WAN dihapus dari ONT. Koneksi internet bisa terputus.\n\nLanjutkan?")
                            .setPositiveButton("HAPUS", (dd, ww) -> io.execute(() -> deleteWan(w)))
                            .setNegativeButton("Batal", null).show();
                }).show());
    }

    private void deleteWan(org.json.JSONObject w) {
        log("=== HAPUS WAN " + w.optString("Name", "?") + " ===");
        String resp = ontCgi("wan_delete",
                "wan_index=" + enc(w.optString("wan_index"))
                        + "&wan_session_index=" + enc(w.optString("wan_session_index"))
                        + "&wan_iporppp_old=" + enc(w.optString("iporppp")));
        log("respons: " + resp);
        doReadWan();
    }

    private void pickWanForEdit() {
        if (ontLogin() == null) return;
        final org.json.JSONArray arr = wanArray();
        if (arr == null || arr.length() == 0) { log("tidak ada WAN untuk diedit"); return; }
        final String[] labels = new String[arr.length()];
        for (int i = 0; i < arr.length(); i++) {
            org.json.JSONObject w = arr.optJSONObject(i);
            labels[i] = (i + 1) + ". " + w.optString("Name", "?") + "  (" + w.optString("ConnectionType", "?") + ")";
        }
        runOnUiThread(() -> new AlertDialog.Builder(this)
                .setTitle("Edit WAN")
                .setItems(labels, (d, which) -> showWanDialog(arr.optJSONObject(which)))
                .show());
    }

    private EditText dlgField(LinearLayout parent, String label, String value) {
        TextView tv = new TextView(this);
        tv.setText(label);
        tv.setTextSize(10);
        tv.setTextColor(MUTED);
        tv.setPadding(0, dp(8), 0, dp(2));
        parent.addView(tv);
        EditText e = new EditText(this);
        e.setText(value);
        e.setTextSize(13);
        e.setTextColor(TEXT);
        e.setBackground(round(INPUT_BG, INPUT_STR, 10, 1));
        e.setPadding(dp(10), dp(8), dp(10), dp(8));
        parent.addView(e);
        return e;
    }

    private Spinner dlgSpinner(LinearLayout parent, String label, String[] items) {
        TextView tv = new TextView(this);
        tv.setText(label);
        tv.setTextSize(10);
        tv.setTextColor(MUTED);
        tv.setPadding(0, dp(8), 0, dp(2));
        parent.addView(tv);
        Spinner sp = new Spinner(this);
        ArrayAdapter<String> a = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(a);
        sp.setBackground(round(INPUT_BG, INPUT_STR, 10, 1));
        parent.addView(sp);
        return sp;
    }

    private void showWanDialog(final org.json.JSONObject existing) {
        final boolean edit = existing != null;
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(18), dp(6), dp(18), dp(6));
        ScrollView sc = new ScrollView(this);
        sc.addView(box);

        final EditText fName = dlgField(box, "Nama WAN", edit ? existing.optString("Name", "INTERNET") : "INTERNET");
        final Spinner spMode = dlgSpinner(box, "Mode", new String[]{"Route", "Bridge"});
        final Spinner spType = dlgSpinner(box, "Tipe koneksi", new String[]{"PPPoE", "IPoE DHCP", "IPoE Static"});
        final EditText fVlan = dlgField(box, "VLAN ID (0 = tanpa VLAN)", "0");
        final EditText fPrio = dlgField(box, "VLAN Priority (0-7)", "0");
        final EditText fMtu = dlgField(box, "MTU", "1492");
        final EditText fUser = dlgField(box, "Username PPPoE", "");
        final EditText fPass = dlgField(box, "Password PPPoE", "");
        final EditText fIp = dlgField(box, "IP statis (IPoE Static)", "");
        final EditText fMask = dlgField(box, "Subnet mask", "255.255.255.0");
        final EditText fGw = dlgField(box, "Gateway", "");
        final EditText fDns = dlgField(box, "DNS (pisah koma)", "");

        if (edit) {
            String ct = existing.optString("ConnectionType", "").toLowerCase();
            if (ct.contains("bridged")) spMode.setSelection(1);
            if (ct.startsWith("pppoe")) spType.setSelection(0);
            fVlan.setText(existing.optString("VLANID", existing.optString("vlanid", "0")));
            fPrio.setText(existing.optString("8021", existing.optString("p8021", "0")));
            fMtu.setText(existing.optString("MTU", existing.optString("mtu", "1492")));
            fUser.setText(existing.optString("Username", ""));
        }

        new AlertDialog.Builder(this)
                .setTitle(edit ? "Edit WAN" : "Tambah WAN")
                .setView(sc)
                .setPositiveButton(edit ? "SIMPAN" : "TAMBAH", (d, w) -> {
                    final String name = fName.getText().toString().trim();
                    final String mode = spMode.getSelectedItem().toString();
                    final String type = spType.getSelectedItem().toString();
                    final String vlan = fVlan.getText().toString().trim();
                    final String prio = fPrio.getText().toString().trim();
                    final String mtu = fMtu.getText().toString().trim();
                    final String user = fUser.getText().toString().trim();
                    final String pass = fPass.getText().toString();
                    final String ip = fIp.getText().toString().trim();
                    final String mask = fMask.getText().toString().trim();
                    final String gw = fGw.getText().toString().trim();
                    final String dns = fDns.getText().toString().trim();
                    io.execute(() -> applyWan(existing, name, mode, type, vlan, prio, mtu, user, pass, ip, mask, gw, dns));
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void applyWan(org.json.JSONObject existing, String name, String mode, String type,
                          String vlan, String prio, String mtu, String user, String pass,
                          String ip, String mask, String gw, String dns) {
        log("=== " + (existing == null ? "TAMBAH" : "EDIT") + " WAN: " + name + " ===");
        if (ontLogin() == null) return;

        boolean bridge = mode.equals("Bridge");
        String connType;
        String iporppp;
        String addr = "";
        if (bridge) { connType = "PPPoE_Bridged"; iporppp = "2"; }
        else if (type.startsWith("PPPoE")) { connType = "PPPoE_Routed"; iporppp = "2"; }
        else { connType = "IP_Routed"; iporppp = "1"; addr = type.contains("DHCP") ? "DHCP" : "Static"; }

        StringBuilder b = new StringBuilder();
        b.append("wan_iporppp_new=").append(iporppp);
        b.append("&ConnectionType=").append(enc(connType));
        b.append("&ServiceList=INTERNET");
        b.append("&IPMode=IPv4");
        b.append("&mtu=").append(enc(mtu));
        b.append("&VLANEnable=").append(vlan.isEmpty() || "0".equals(vlan) ? "0" : "1");
        b.append("&vlanid=").append(enc(vlan.isEmpty() ? "0" : vlan));
        b.append("&p8021=").append(enc(prio));
        b.append("&LanInterface=");
        if (!addr.isEmpty()) b.append("&AddressingType=").append(addr);
        if (!user.isEmpty()) b.append("&Username=").append(enc(user));
        if (!pass.isEmpty()) b.append("&WPd=").append(enc(pass));
        if ("Static".equals(addr)) {
            b.append("&ExternalIPAddress=").append(enc(ip));
            b.append("&SubnetMask=").append(enc(mask));
            b.append("&DefaultGateway=").append(enc(gw));
            b.append("&DNSServers=").append(enc(dns));
        }
        b.append("&NATEnabled=").append(bridge ? "0" : "1");
        b.append("&Enable=1");
        b.append("&X_FH_AutoConnection=1");

        String action;
        if (existing == null) {
            action = "wan_add_new";
        } else {
            action = "wan_modify";
            b.append("&wan_index=").append(enc(existing.optString("wan_index")));
            b.append("&wan_session_index=").append(enc(existing.optString("wan_session_index")));
            b.append("&wan_iporppp_old=").append(enc(existing.optString("iporppp")));
            b.append("&wan_name=").append(enc(name));
        }
        log("POST " + action);
        String resp = ontCgi(action, b.toString());
        log("respons: " + resp);
        doReadWan();
    }

    private String httpRequest(String method, String url, String body) {
        java.net.HttpURLConnection conn = null;
        try {
            java.net.URL u = new java.net.URL(url);
            conn = (java.net.HttpURLConnection) u.openConnection();
            conn.setRequestMethod(method);
            conn.setConnectTimeout(1500);
            conn.setReadTimeout(4000);
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

