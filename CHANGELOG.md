# Changelog

Format: [Keep a Changelog](https://keepachangelog.com/id/) - binari dirilis via [Releases](https://github.com/Indra725/addwan-fiberhome-all-version/releases).

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
