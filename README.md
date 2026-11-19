# Add WAN FH — satu aplikasi Android untuk HG6145F1/D2 & config ONT

Aplikasi Android all-in-one untuk teknisi ISP / installer jaringan yang mengelola modem **Fiberhome ONU/ONT** (HG6145F1/HG6145D2, firmware RP4423/RP4307): tambah/konfigurasi WAN, baca & dekripsi config ONT, baca status uplink, kelola ACL — semua dari satu APK.

> Disclaimer: Tool ini **BUKAN** produk resmi Fiberhome / FiberHome Technologies. Tidak berafiliasi dengan mereka. Gunakan hanya pada perangkat milik Anda sendiri atau yang Anda berhak kelola secara resmi.

## Download

**Rilis terbaru: v2.17**
https://github.com/Indra725/addwan-fiberhome-all-version/releases/tag/v2.17

| File | Isi |
|---|---|
| `Add_WAN_FH_Mobile.apk` | APK Android (versionCode 18 / v2.17) |
| `Add_WAN_FH.exe` | Aplikasi Windows asli (arsip, tidak diubah) |

APK SHA256: `520446DB8867F935E596FF50D09DFE498A236D42ACEE883993CE888545880F74` (1.117.487 byte)

## Fitur

- **ADD WAN — load preconfig IDN_XL** — **fungsi asli `Add_WAN_FH.exe`**, dijalankan ulang lewat telnet: `gpon` → `Fh@SN` → `en` → `cd factorydir` → `load preconfig IDN_XL`
- **BACA DAFTAR WAN** — `get_allwan_info` (nama, tipe, addressing, VLAN, NAT, enable)
- **TAMBAH WAN** — `wan_add_new`: PPPoE Route, IPoE DHCP, IPoE Static, Bridge; VLAN ID, VLAN priority, MTU, NAT, username/password
- **EDIT WAN** — `wan_modify` (field terisi otomatis dari WAN terpilih)
- **HAPUS WAN** — `wan_delete` (konfirmasi ganda)
- **CEK ONT** - ping, cek TCP 23/80, deteksi custom webui, baca `uplink_info` & `acl_info` (login web UI via `do_login` + AES-128-CBC)
- **UNLOCK** — telnet `gpon`/`Fh@MAC` (+ enable) — untuk perangkat yang mendukung
- **STATUS UPLINK** — `get_uplink_info`, `get_acl_info`, `get_refresh_sessionid`
- **SET UPLINK** — `set_uplink_info` (pilih port `dev.eth.1..5`)
- **AKTIFKAN / MATIKAN ACL** — `get_acl_info` then `set_acl_info` toggle
- **BACA CONFIG** — dekripsi file `usrconfig_conf` (XOR `0x2537` + gunzip + untar, anotasi AES-ECB otomatis: WiFi/TR-069 password terbaca)
- **ENCRYPT CONFIG** — buat file config terenkripsi dari teks untuk upload kembali ke ONT (Local Upgrade)
- **ENCRYPT/DECRYPT STRING** — AES-ECB satu per satu (key `ABCDEFGHIJKLMNOP`)
- **PASTE MAC** — isi MAC dari clipboard terformat otomatis
- **LOG di atas + indikator proses + auto-scroll pintar + tombol SALIN** — spinner & timer `⏳ BERJALAN`, timestamp per baris, bisa digulir ke atas tanpa ditarik balik, tombol `⤓` ke baris terbaru
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
| `windows/Add_WAN_FH.exe` | Aplikasi Windows asli (Delphi/VCL, tidak diubah) |
| `termux/` | Termux toolkit Python (alternatif tanpa install APK) |
| `docs/` | Dokumentasi teknis + [`TEST-REPORT-v2.17.md`](docs/TEST-REPORT-v2.17.md) |

## Aplikasi Windows asli

`Add_WAN_FH.exe` tetap disertakan apa adanya. Hasil analisis:

- Delphi (Object Pascal) + VCL, komunikasi telnet lewat Indy `TIdTelnet`
- Form hanya berisi SN, Host, Port, tombol Connect, dan memo output
- Perintah yang dijalankan: `gpon` → `Fh@<6 hex SN>` → `en` → `cd factorydir` → `load preconfig IDN_XL`

APK v2.15 menjalankan urutan yang sama pada tombol **ADD WAN — load preconfig IDN_XL (EXE)**, lalu menambahkan manajemen WAN lewat CGI resmi ONT.

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


