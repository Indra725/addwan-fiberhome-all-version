# Changelog

Format: [Keep a Changelog](https://keepachangelog.com/id/) - binari dirilis via [Releases](https://github.com/Indra725/addwan-fiberhome-all-version/releases).

## [v2.17] - 2026-11-20

### Ditambahkan
- **Indikator proses di kolom output**: spinner + `⏳ BERJALAN — <aksi>` dan timer `1s`, `2s`, ... setiap 0,5 detik; berakhir `✅ SELESAI n detik` / `❌ GAGAL n detik`.
- **Timestamp** `HH:mm:ss` di setiap baris log.
- Tombol `⤓` untuk kembali ke baris terbaru saat log digulir ke atas.
- Log otomatis dipangkas setelah ±700 baris agar tetap ringan.

### Diperbaiki
- **Gulir log tidak bisa ke atas** - ScrollView halaman tidak lagi merebut gestur di dalam kotak output (`requestDisallowInterceptTouchEvent`).
- **Auto-scroll menarik balik saat membaca** - auto-scroll hanya aktif bila posisi log sudah di paling bawah.
- HAPUS WAN, TAMBAH/SIMPAN WAN, BACA CONFIG, ENCRYPT CONFIG, dan AUTO-LOGIN kini ikut menampilkan indikator proses.

## [v2.16] - 2026-11-20

### Diperbaiki
- **Output tidak terlihat** - kartu log dipindah ke atas (tepat di bawah header), ditambah status bar `⏳/✅/❌` dan tombol **SALIN** log.
- **JSON WAN selalu gagal parse** - `httpRequest` menempelkan kode HTTP di depan body; ditambahkan `bodyOf()` untuk membuangnya sebelum `new JSONObject(...)`.
- **session_valid:0** - `ontCgi` sekarang memanggil `get_refresh_sessionid` dan memakai sessionid baru, sama seperti `XHR.post` asli ONT.
- **Login tahan spasi** - `"login_result": 0` kini dikenali.
- **ADD WAN terlihat macet** - log progres `1/6` … `6/6` per langkah telnet, timeout 6 detik per langkah.
- Login ulang otomatis sekali bila ONT membalas `session_valid:0`.

### Ditambahkan
- `docs/TEST-REPORT-v2.16.md` - temuan bug, perbaikan, dan bukti tes protokol 7/7 lulus terhadap server ONT tiruan.

## [v2.15] - 2026-11-20

### Ditambahkan
- **ADD WAN — load preconfig IDN_XL (EXE)** — fungsi asli `Add_WAN_FH.exe` diporting apa adanya ke telnet Android: `gpon` → `Fh@SN` → `en` → `cd factorydir` → `load preconfig IDN_XL`.
- **BACA DAFTAR WAN** — `get_allwan_info`, tampil nama/tipe/addressing/VLAN/NAT/enable.
- **TAMBAH WAN** — `wan_add_new` dengan PPPoE Route, IPoE DHCP, IPoE Static, Bridge; VLAN ID, priority, MTU, NAT.
- **EDIT WAN** — `wan_modify`, field terisi otomatis dari WAN terpilih.
- **HAPUS WAN** — `wan_delete` dengan konfirmasi ganda.
- Field **SN ONT** opsional untuk menurunkan password telnet persis seperti EXE (`Fh@` + `Mid(SN,5,6)`).
- `docs/TEST-REPORT-v2.15.md` — analisis EXE + hasil pengujian jujur.
- `windows/Add_WAN_FH.exe` — aplikasi Windows asli dikembalikan ke repo.

### Diubah
- `ontCgi` sekarang menyertakan `sessionid` (wajib untuk endpoint WAN).

## [v2.14] - 2026-11-19

### Diperbaiki
- **Macet & tidak ada output** - executor berubah dari single-thread ke cached (tombol tidak lagi terblokir di belakang auto-login).
- **Ping dihapus** - pakai TCP connect ke ONT (port 80/23) - tidak pernah hang.
- **Timeout HTTP dipercepat** (1.5s connect / 4s read), tombol memberi feedback langsung.
- Rilis **v2.14** (stabil). Semua rilis lama dihapus.

## [v2.18] - 2026-11-19

### Diperbaiki
- **UI log output diperbesar & auto-scroll**: kotak log 260dp, teks 12sp, JSON ONT dirapikan per-baris, separator mudah dibaca.
- **Release v2.13** menjadi rilis stabil utama. Semua rilis lama (v2.0-v2.12) dihapus - repositori bersih, hanya 1 APK terbaru.

## [v2.17] - 2026-11-19

### Diubah
- **Windows EXE dihapus dari repo & Releases** (v1.0 Add_WAN_FH.exe, v1.1 FiberhomeConfigUtility.exe) - semua fungsinya sudah masuk ke satu APK Android.
- `android/` (build manual lama) dihapus dari repo - digantikan `android-gradle/`.
- README ditulis ulang: Satu Produk (Add_WAN_FH_Mobile APK).

## [v2.16] - 2026-11-19

### Ditambahkan
- **ENCRYPT CONFIG** - buat file config terenkripsi dari teks decrypted (untuk upload kembali ke ONT via Local Upgrade). Meng-encode `XOR 0x2537` -> gzip -> untar.
- **ENCRYPT/DECRYPT STRING AES-ECB** (key `ABCDEFGHIJKLMNOP`) - enkripsi/dekripsi nilai config satu per satu.
- Semua fungsi FiberhomeConfigUtility.exe kini terintegrasi di satu APK.

## [v2.15] - 2026-11-19

### Ditambahkan
- **Add_WAN_FH + Config** — kedua aplikasi (Add_WAN_FH.exe + FiberhomeConfigUtility.exe) **digabung jadi satu APK** (`Add WAN FH + Config`). Add WAN FH.exe = telnet client otomatis (add WAN), ConfigUtility = dekripsi/encrypt config - sekarang keduanya dalam 1 app, bisa dipakai di HP tanpa PC, tanpa custom webUI terpisah.
- Nama aplikasi diubah: `Add WAN FH + Config` (label, judul, package tetap `com.daffiyanet.addwan`).
- Rilis **v2.9** (`versionCode=10`, SHA256 `01498DB7...`, 1.100.578 byte).

## [v2.14] - 2026-11-19

### Ditambahkan
- **Tombol "6. BACA CONFIG (file ONT)"** - pilih file backup `usrconfig_conf` (dari web UI ONT), app dekripsi: XOR `0x2537` -> gunzip -> untar -> tampilkan config di log. Nilai terenkripsi AES-ECB (WiFi/TR-069) otomatis dideteksi dan diannotasi `=> [AES] ...` di baris yang sama.
- Rilis **v2.8** (`versionCode=9`, SHA256 `81561032...`, 1.100.517 byte).

## [v2.13] - 2026-11-19

### Ditambahkan
- **Tombol PASTE di kolom MAC** - sekali tap, isi MAC dari clipboard terformat otomatis (`948b12abcdef` -> `94:8B:12:AB:CD:EF`; case/spasi/dash diabaikan). Tidak perlu ketik manual lagi.
- Rilis **v2.7** (`versionCode=8`, SHA256 `E5359AC3...`, 1.097.766 byte).

## [v2.12] - 2026-11-19

### Diperbaiki
- Output log sekarang **bisa di-scroll** dan **otomatis lompat ke baris terbaru** setelah setiap perintah dijalankan (CEK/UNLOCK/STATUS/SET).
- Rilis **v2.6** (`versionCode=7`, SHA256 `AD46B777...`, 1.096.919 byte).

## [v2.11] - 2026-11-19

### Diperbaiki
- **Header tidak terpotong** di HP dengan status bar/notch — padding atas sekarang otomatis sesuai `status_bar_height` HP Anda.
- Rilis **v2.5** (`versionCode=6`, SHA256 `BDB36AE8...`, 1.096.633 byte).

## [v2.10] - 2026-11-19

### Ditambahkan
- **Tombol donasi/subscribe opsional** di Add_WAN_FH_Mobile — **tidak mengunci fitur apa pun**: Donasi via QRIS (08516163719 a.n. Daffiyanet / https://indra725.github.io/donasi/), Subscribe YouTube @affiyaofficial.
- **Tombol "5. AKTIFKAN ACL"** — membuka web UI ONT (`Security > ACL Settings`) untuk perangkat milik Anda.
- Recovery total fungsionalitas: CEK ONT / UNLOCK / STATUS UPLINK / SET UPLINK dikembalikan (setelah versi stub).
- Spinner port `dev.eth.1..5` di baris SET UPLINK.
- Rilis **v2.4** (`versionCode=5`).

## [v2.9] - 2026-11-19

### Diperbaiki
- **UI Add_WAN_FH_Mobile v2.3** - redesign total: tema Holo gelap, kartu rounded dengan border, header gold + subtitle, input dengan kontras tinggi (teks IP/MAC terlihat jelas - sebelumnya teks tidak terlihat karena tema light dipakai di background gelap), tombol bergaya penuh warna (CEK=cyan, UNLOCK=amber, STATUS=hijau, SET=ungu), paket log terminal-style.
- `android-gradle/`: versionCode=4 / versionName=2.3, tema `Theme.Holo.NoActionBar`.
- Rilis **v2.3** (SHA256 `D371BBD5...`, 1.095.241 byte). README/dokumen mengarah ke v2.3.

## [v2.8] - 2026-11-19

### Diperbaiki
- **Add_WAN_FH_Mobile.apk di-rebuild sepenuhnya dengan Gradle/AGP** (AGP 9.1.1 + Gradle 9.3.1) - struktur APK identik dengan billing-daffiyanet.apk yang terbukti installable di HP Anda (res/arsc stored, dex deflate, pipeline resmi). Menggantikan build manual (aapt2/d8 + zip-recompress) yang gagal mengurai paket.
- `android-gradle/` ditambahkan: proyek Gradle lengkap (gradlew, wrapper, AGP 9.1.1) - `gradlew.bat assembleRelease` untuk build ulang.
- Rilis **v2.2** dibintangi. README/dokumen Termux diarahkan ke v2.2 (SHA256 `CF20F20F...`, 1.095.241 byte).

## [v2.7] - 2026-11-19

### Perbaikan
- **`resources.arsc` disimpan (stored), bukan deflate** di `Add_WAN_FH_Mobile.apk` - penyebab "Ada masalah saat mengurai paket" (konvensi APK v1 signing wajib stored). APK sekarang cocok dengan struktur APK billing yang terbukti jalan di Android HP Anda.
- Rilis baru: **v2.1** (prerelease) + dokumen Termux diarahkan ke v2.1 (SHA256 `F4CACF...`).
- Build script: `android/build-apk.ps1` kini mem-preserve kompresi asli tiap entry saat repackage.

## [v2.6] - 2026-11-19

### Ditambahkan
- **`termux/INSTALL-APK-TERMUX.md`** - panduan download & install `Add_WAN_FH_Mobile.apk` langsung dari Termux (curl/wget + verifikasi SHA256 + `termux-open` ke PackageInstaller). Mengatasi error "Ada masalah saat mengurai paket" dari file manager pihak ketiga.
- Link dari README ke dokumen baru.

## [v2.5] - 2026-11-19

### Ditambahkan
- **`docs/firmware-compat.md`** - peringatan keras: custom-webui.bin hanya RP4423; RP4307 TIDAK kompatibel (CLI terkunci + 0 tool publik); temuan config `X_FH_LanInterface` dari report user.

## [v2.4] - 2026-11-19

### Ditambahkan
- **`docs/uplink-permanen.md`** - checklist jujur "menu uplink terbuka & permanen" (step 0 firmware check, unlock Local Upgrade, TR-069 patch, persistence, set uplink, backup config, restore setelah factory reset). Jelas membedakan: bertahan reboot / hilang saat factory reset.

## [v2.3] - 2026-11-19

### Ditambahkan
- `termux/custom-webui.bin` (file unlock web UI, dari proyek numberonedz, MIT)
- `addwan.py`: auto-probe shell tersembunyi (shell/sh/system/busybox/debug/su/execute/run/linux/console/cmd) dan fallback ke panduan **Local Upgrade** bila CLI ONT terkunci (`% Unknown command.`)
- `INSTALL.md`: section "CLI terkunci -> Local Upgrade"
- Mock test kini 13/13 PASS (dua skenario: RP4423 shell terbuka & CLI terkunci)

## [v2.2] - 2026-11-19

### Ditambahkan
- **`termux/test_mock.py`** — simulator ONT Fiberhome (telnet CLI + CGI tiruan) untuk uji tanpa hardware. **Hasil: 10/10 PASS** (check/unlock/status/set terverifikasi end-to-end).
- Port ONT bisa dikonfigurasi via env (`AWAN_TELNET_PORT`, `AWAN_HTTP_PORT`, default 23/80).

## [v2.1] - 2026-11-19

### Ditambahkan
- **`termux/addwan.py`** (+ `termux/INSTALL.md`, + `termux/custom-webui.sh`) - toolkit Add WAN Fiberhome di HP via **Termux**, **tanpa PC, tanpa root**:
  - Menu interaktif: CEK ONT / UNLOCK / STATUS UPLINK / SET UPLINK
  - CLI mode: `check` / `unlock` / `status` / `set`
  - Semua koneksi TCP biasa (telnet/HTTP), tanpa dependency eksternal
  - Panduan lengkap: install Termux dari F-Droid, one-liner clone & run

### Diperbaiki
- README.md & CHANGELOG.md di-rewrite full ASCII (menghilangkan mojibake dari commit sebelumnya)

## [v2.0] - 2026-11-19

### Ditambahkan
- **Add_WAN_FH_Mobile.apk** (Android, 455 KB) - tool Add WAN di HP, tanpa PC/laptop:
  - CEK ONT (ping / TCP 23 / TCP 80 / deteksi custom webui)
  - UNLOCK HG6145F1/D2 via telnet + push `custom-webui.sh` dari HP (mini HTTP server didalam aplikasi)
  - STATUS & SET uplink (`get_uplink_info` / `set_uplink_info`, `dev.eth.1`-`dev.eth.5`)
  - Source: `android/` - build ulang via `android/build-apk.ps1`
- Manifest diperbaiki: `versionCode=2`, `versionName=2.0`, `minSdk=16`, `targetSdk=36`, ikon aplikasi

### Verifikasi v2.0
- SHA256: `C68CC55F7816DFA5A5E8465201B23B4E77829606B9852E2E59450647B950D0CB`
- Ukuran: 455.529 byte

## [v1.1] - 2026-11-19

### Ditambahkan
- `FiberhomeConfigUtility.exe` (dibuat 2026-10-22) - utility konfigurasi Fiberhome (port / bridge / LAN uplink).
- Tabel "Isi repo" di README dengan link rilis langsung.
- Section donasi (https://indra725.github.io/donasi/).

### Verifikasi v1.1
- SHA256: `5289A36AE7D4C87080D8B3499E0EB5A3FC843D257898D49513E7796DA89A7879`
- MD5: `BAD6A5C629EA4BE141447CD93184692F`
- Ukuran: 10.700.931 byte
- Unsigned - Windows Defender: tidak ada ancaman (diperiksa 2026-11-19)

## [v1.0] - 2026-11-19 (arsip versi lama)

### Ditambahkan
- `Add_WAN_FH.exe` (dibuat 2026-10-21) - tool menambah konfigurasi WAN pada Fiberhome ONU/ONT.
- README awal: disclaimer, kebutuhan (Windows + akses LAN/telnet), cara pakai umum, troubleshooting, checksum.

### Verifikasi v1.0
- SHA256: `B936642B5A31BA408AB2B9CC67017D34B5971E4555F82A1375F0BF548A52DB60`
- MD5: `B581DADADA3E1A2D43D05D8FB5422527`
- Ukuran: 12.528.392 byte
- Unsigned - Windows Defender: tidak ada ancaman (diperiksa 2026-11-19)

