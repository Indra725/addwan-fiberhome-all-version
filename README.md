# Add WAN FH — satu aplikasi Android untuk HG6145F1/D2 & config ONT

Aplikasi Android all-in-one untuk teknisi ISP / installer jaringan yang mengelola modem **Fiberhome ONU/ONT** (HG6145F1/HG6145D2, firmware RP4423/RP4307): tambah/konfigurasi WAN, baca & dekripsi config ONT, baca status uplink, kelola ACL — semua dari satu APK.

> Disclaimer: Tool ini **BUKAN** produk resmi Fiberhome / FiberHome Technologies. Tidak berafiliasi dengan mereka. Gunakan hanya pada perangkat milik Anda sendiri atau yang Anda berhak kelola secara resmi.

## Download (APK)

**Rilis terbaru: v2.13**
https://github.com/Indra725/addwan-fiberhome-all-version/releases/tag/v2.13

`Add_WAN_FH_Mobile.apk` - SHA256: `41D2F774D0EDDC6065B88C939C7797CDC64B4B32AFAE6D5F8038079F0CC8756D` (1.107.385 byte)

## Fitur

- **CEK ONT** — ping, cek TCP 23/80, deteksi custom webui, baca `uplink_info` & `acl_info` (login web UI via `do_login` + AES-128-CBC)
- **UNLOCK** — telnet `gpon`/`Fh@MAC` (+ enable) — untuk perangkat yang mendukung
- **STATUS UPLINK** — `get_uplink_info`, `get_acl_info`, `get_refresh_sessionid`
- **SET UPLINK** — `set_uplink_info` (pilih port `dev.eth.1..5`)
- **AKTIFKAN / MATIKAN ACL** — `get_acl_info` then `set_acl_info` toggle
- **BACA CONFIG** — dekripsi file `usrconfig_conf` (XOR `0x2537` + gunzip + untar, anotasi AES-ECB otomatis: WiFi/TR-069 password terbaca)
- **ENCRYPT CONFIG** — buat file config terenkripsi dari teks untuk upload kembali ke ONT (Local Upgrade)
- **ENCRYPT/DECRYPT STRING** — AES-ECB satu per satu (key `ABCDEFGHIJKLMNOP`)
- **PASTE MAC** — isi MAC dari clipboard terformat otomatis
- **LOG auto-scroll**
- **Donasi optional** — link donasi Daffiyanet + subscribe YouTube (tidak mengunci fitur apa pun)

## Kebutuhan

- Android 4.1+
- HP satu jaringan/WiFi/LAN dengan ONT (192.168.1.x)
- Akses web UI ONT (`Security > ACL Settings` untuk mengaktifkan telnet bila memakai UNLOCK)
- Firmware RP4423 (RP4307 untuk config baca saja)

## Install (tanpa Google Play)

1. Download `Add_WAN_FH_Mobile.apk` dari Releases
2. Aktifkan **Install unknown apps** untuk aplikasi pemuat .apk Anda
3. Tap file → **Install** / **Update**

## Build dari source

```powershell
cd android-gradle
.\gradlew.bat assembleRelease
```
( JDK 17+ & Android SDK di `C:\\Users\\andik\\android-sdk ` )

## Repo contents

| Path | Isi |
|---|---|
| `android-gradle/` | Proyek Gradle/AGP (build resmi, AGP 9.1.1) |
| `termux/` | Termux toolkit Python (alternatif tanpa install APK) |
| `docs/` | Dokumentasi teknis (uplink, kompatibilitas firmware) |

## Versi

Lihat [`CHANGELOG.md`](CHANGELOG.md) untuk riwayat versi.

## Lisensi & hak cipta

Tool adalah milik pembuat/distributor aslinya. Distribusi di repo ini atas izin dari pemilik/distributor. IP/Trademark Fiberhome adalah milik pemegang trademark masing-masing.

## Donasi / support

- **Donasi:** https://indra725.github.io/donasi/ (QRIS semua e-wallet)
- **E-Wallet:** 08516163719 a.n. Daffiyanet
- **WhatsApp:** https://wa.me/628516163719

## Lapor masalah

Buka tab **Issues**. Sertakan: versi tool, firmware ONT, langkah reproduksi.

