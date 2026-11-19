# Add WAN FH — arsip semua versi tool

Tool teknisi ISP / installer jaringan untuk modem **Fiberhome ONU/ONT** (seri AN5506, dan HG6145F1/D2 firmware RP4423): menambahkan/mengaktifkan konfigurasi **WAN** agar bisa dipakai internet (PPPoE/DHCP/Static sesuai provisioning ONT).

> **Disclaimer**
> - Tool ini **BUKAN** produk resmi Fiberhome / FiberHome Technologies dan **tidak** berafiliasi, didukung, atau di-endorse oleh Fiberhome dalam bentuk apa pun.
> - Tool disediakan **as-is**, tanpa garansi apa pun. Penggunaan sepenuhnya tanggung jawab Anda.
> - Gunakan **hanya** pada perangkat yang Anda miliki atau yang Anda berwenang kelola (jaringan ISP / instalasi pelanggan yang Anda tangani secara resmi).
> - Dilarang menggunakan tool ini untuk mengakses perangkat yang bukan milik Anda / tanpa izin.
> - File `.exe` **tidak ditandatangani secara digital** — Windows SmartScreen mungkin menolak. Selalu verifikasi checksum sebelum menjalankan.

## Tersedia di 3 platform

| Platform | Tool | Install | Root |
|---|---|---|---|
| Windows | `Add_WAN_FH.exe` (v1.0), `FiberhomeConfigUtility.exe` (v1.1) | [Releases](https://github.com/Indra725/addwan-fiberhome-all-version/releases) | tidak perlu |
| Android APK | `Add_WAN_FH_Mobile.apk` (v2.4) | [Releases](https://github.com/Indra725/addwan-fiberhome-all-version/releases/tag/v2.4) | tidak perlu |
| **Termux (HP)** | `termux/addwan.py` (v2.3) | `pkg install python git && git clone` (lihat di bawah) | **tidak perlu** |

## Android APK (v2.4)

**Download:** https://github.com/Indra725/addwan-fiberhome-all-version/releases/tag/v2.4 — `Add_WAN_FH_Mobile.apk` (1.0 MB)

**Fitur:** CEK ONT (ping/TCP/custom webui) · UNLOCK via telnet + push custom-webui.sh dari HP (mini HTTP server internal) · STATUS & SET UPLINK (`get_uplink_info` / `set_uplink_info`)

**Kebutuhan:** Android 4.1+, HP satu jaringan/WiFi/LAN dengan ONT, telnet aktif (`Security > ACL Settings`), firmware RP4423, backup config ONT dulu.

**Verifikasi (v2.4):** `SHA256: A09081061AD3FF2837D55699D345A4166C96B7D3B545524CBDE378D032CA6EFE` (1.096.490 byte)

**Build dari source:** `cd android-gradle && .\gradlew.bat assembleRelease` (JDK 17+ & Android SDK)

## Termux (HP, tanpa PC, tanpa ROOT)

Tidak mau sideload APK? Jalankan langsung dari HP lewat **Termux** — Python mandiri, hanya koneksi TCP ke ONT:

```bash
pkg update && pkg install python git
git clone https://github.com/Indra725/addwan-fiberhome-all-version.git
cd addwan-fiberhome-all-version
python termux/addwan.py
```

**Menu:** CEK ONT → UNLOCK ONT → STATUS UPLINK → SET UPLINK → ganti IP → keluar.

**Kebutuhan:** Termux (install dari **F-Droid**, bukan Play Store), HP satu jaringan/WiFi dengan ONT, telnet aktif, firmware RP4423.

**Panduan lengkap:** [	ermux/INSTALL.md](termux/INSTALL.md) (toolkit & menu) · [	ermux/INSTALL-APK-TERMUX.md](termux/INSTALL-APK-TERMUX.md) (download & install APK langsung dari Termux)

## Isi repo

Repo ini menyimpan **arsip semua versi** tool Add_WAN_FH & utility Fiberhome. Binari didistribusikan lewat **[Releases](https://github.com/Indra725/addwan-fiberhome-all-version/releases)**. Riwayat: [`CHANGELOG.md`](CHANGELOG.md). Source: `android/` (APK), `termux/` (Termux toolkit + payload).

| Versi | File | Tanggal | Rilis |
|---|---|---|---|
| v2.3 | `Add_WAN_FH_Mobile.apk` (UI redesign Holo gelap, versi Termux rilis di repo) | 2026-11-19 | [releases/v2.3](https://github.com/Indra725/addwan-fiberhome-all-version/releases/tag/v2.4) |
| v2.3 | `termux/addwan.py` + `termux/custom-webui.bin` (Local Upgrade web UI) | 2026-11-19 | (source di repo) |
| v2.2 | `termux/addwan.py` (+ simulator test_mock.py) | 2026-11-19 | (source di repo) |
| v2.1 | `termux/addwan.py` awal | 2026-11-19 | (source di repo) |
| v2.2 | `Add_WAN_FH_Mobile.apk` (Gradle/AGP) | 2026-11-19 | [releases/v2.2](https://github.com/Indra725/addwan-fiberhome-all-version/releases/tag/v2.2) |
| v2.1 | `Add_WAN_FH_Mobile.apk` (rusak: resources.arsc deflate) | 2026-11-19 | [releases/v2.1](https://github.com/Indra725/addwan-fiberhome-all-version/releases/tag/v2.1) |
| v1.1 | `FiberhomeConfigUtility.exe` | 2026-10-22 | [releases/v1.1](https://github.com/Indra725/addwan-fiberhome-all-version/releases/tag/v1.1) |
| v1.0 | `Add_WAN_FH.exe` | 2026-10-21 | [releases/v1.0](https://github.com/Indra725/addwan-fiberhome-all-version/releases/tag/v1.0) |

## Verifikasi (v1.0)

```
SHA256: B936642B5A31BA408AB2B9CC67017D34B5971E4555F82A1375F0BF548A52DB60
MD5   : B581DADADA3E1A2D43D05D8FB5422527
```

## Verifikasi (v1.1) — FiberhomeConfigUtility

```
SHA256: 5289A36AE7D4C87080D8B3499E0EB5A3FC843D257898D49513E7796DA89A7879
MD5   : BAD6A5C629EA4BE141447CD93184692F
```

## Cara pakai umum (Windows)

1. Download `Add_WAN_FH.exe` dari **Releases**.
2. Verifikasi checksum SHA256.
3. Hubungkan PC ke ONT (LAN / jaringan yang sama).
4. Jalankan sebagai administrator, ikuti langkah di layar tool.
5. Setelah selesai, restart ONT bila diminta dan tes koneksi.

## Troubleshooting

| Gejala | Solusi |
|---|---|
| SmartScreen memblokir | More info -> Run anyway (setelah verifikasi checksum) |
| Tidak bisa konek ke ONT | Pastikan satu jaringan; telnet tidak di-block firewall/antivirus; telnet aktif (`Security > ACL Settings`) |
| WAN tidak muncul | Restart ONT; cek ulang web UI ONT |
| "Not a valid Win32 application" | Jalankan sebagai Administrator |
| APK "App not installed" | Aktifkan *Install unknown apps* untuk aplikasi pembuka .apk; matikan Play Protect "Improve harmful app detection" |
| Termux tidak ping ONT | Termux pakai TCP check (bukan ICMP) — pastikan ONT & HP satu subnet (192.168.1.x) |

## Lisensi & hak cipta

Tool adalah milik pembuat/distributor aslinya. Distribusi di repo ini atas izin dari pemilik/distributor. Pertanyaan lisensi: buka Issue di repo ini.

## Donasi / support

- **Halaman donasi:** https://indra725.github.io/donasi/
- **QRIS:** scan dari semua e-wallet / m-banking (DAFFIYANET, Konter - NMID ID1025401523762)
- **E-Wallet (DANA / GoPay / OVO / ShopeePay):** **08516163719** a.n. **Daffiyanet**
- **WhatsApp:** [wa.me/628516163719](https://wa.me/628516163719)

## Lapor masalah

Buka tab **Issues**. Sertakan: versi tool, firmware ONT, langkah reproduksi.






