# Changelog

Format: [Keep a Changelog](https://keepachangelog.com/id/) · binari dirilis via [Releases](https://github.com/Indra725/addwan-fiberhome-all-version/releases).

## [v2.0] — 2026-11-19

### Ditambahkan
- **Add_WAN_FH_Mobile.apk** (Android, 451 KB) — tools di HP, tanpa PC/laptop:
  - CEK ONT (ping / TCP 23 / TCP 80 / deteksi custom webui)
  - UNLOCK HG6145F1/D2 via telnet + push `custom-webui.sh` dari HP (mini HTTP server di dalam aplikasi)
  - STATUS & SET uplink (`get_uplink_info` / `set_uplink_info`, `dev.eth.1`–`dev.eth.5`)
  - Source: `android/` — build ulang via `android/build-apk.ps1`

### Verifikasi v2.0
- SHA256: `16BD35D689AE3E5FBCA9B1A9DC64A1D274B2BCC23343B5A8D23A0AA83CFFDCDE`
- Ukuran: 451.027 byte

## [v1.1] — 2026-11-19

### Ditambahkan
- `FiberhomeConfigUtility.exe` (dibuat 2026-10-22) — utility konfigurasi Fiberhome (port / bridge / LAN uplink).
- Tabel "Isi repo" di README dengan link rilis langsung.
- Section donasi (https://indra725.github.io/donasi/).

### Verifikasi v1.1
- SHA256: `5289A36AE7D4C87080D8B3499E0EB5A3FC843D257898D49513E7796DA89A7879`
- MD5: `BAD6A5C629EA4BE141447CD93184692F`
- Ukuran: 10.700.931 byte
- Unsigned · Windows Defender: tidak ada ancaman (diperiksa 2026-11-19)

## [v1.0] — 2026-11-19 (arsip versi lama)

### Ditambahkan
- `Add_WAN_FH.exe` (dibuat 2026-10-21) — tool menambah konfigurasi WAN pada Fiberhome ONU/ONT.
- README awal: disclaimer, kebutuhan (Windows + akses LAN/telnet), cara pakai umum, troubleshooting, checksum.

### Verifikasi v1.0
- SHA256: `B936642B5A31BA408AB2B9CC67017D34B5971E4555F82A1375F0BF548A52DB60`
- MD5: `B581DADADA3E1A2D43D05D8FB5422527`
- Ukuran: 12.528.392 byte
- Unsigned · Windows Defender: tidak ada ancaman (diperiksa 2026-11-19)
