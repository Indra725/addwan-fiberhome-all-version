# Changelog

Format: [Keep a Changelog](https://keepachangelog.com/id/) - binari dirilis via [Releases](https://github.com/Indra725/addwan-fiberhome-all-version/releases).

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

