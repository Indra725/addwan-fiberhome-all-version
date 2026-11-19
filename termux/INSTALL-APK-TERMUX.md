# Install Add_WAN_FH_Mobile.apk dari Termux (tanpa PC, tanpa file manager)

Cara tercepat mengunduh & memasang APK di HP langsung dari dalam Termux â€” melewati file manager yang sering error ("Ada masalah saat mengurai paket").

## 1. Install Termux (kalau belum)

Install dari **F-Droid** (bukan Play Store): https://f-droid.org/packages/com.termux/

## 2. Install dependency

```bash
pkg update && pkg upgrade
pkg install curl termux-api
```

## 3. Download APK langsung dari GitHub Releases

Versi terbaru: **v2.2**

```bash
curl -L -o Add_WAN_FH_Mobile.apk \
  https://github.com/Indra725/addwan-fiberhome-all-version/releases/download/v2.2/Add_WAN_FH_Mobile.apk
```

> `-L` = ikuti redirect (GitHub relaese download pakai HTTP 302). Kalau `curl` belum ada:
> ```bash
> pkg install wget
> wget -O Add_WAN_FH_Mobile.apk \
>   https://github.com/Indra725/addwan-fiberhome-all-version/releases/download/v2.2/Add_WAN_FH_Mobile.apk
> ```

## 4. Verifikasi APK (wajib)

```bash
sha256sum Add_WAN_FH_Mobile.apk
ls -l Add_WAN_FH_Mobile.apk
```

Hasilnya **HARUS**:

```
CF20F20F3C4EE0506377EF15CA6446BC1223D9F7A3CAA6B9DBF7C088288CD8E1  Add_WAN_FH_Mobile.apk
455529 bytes
```

Kalau berbeda â†’ file korup/terpotong â†’ hapus & ulangi langkah 3 (coba ganti jaringan WiFi dulu).

## 5. Install â€” biar sistem yang membuka installer

```bash
termux-open Add_WAN_FH_Mobile.apk
```

Ini memunculkan dialog sistem Android:
**"Apakah Anda ingin menginstal aplikasi ini?" â†’ Install**

Kalau `termux-open` tidak memunculkan installer:
- pastikan `pkg install termux-api` sudah dijalankan
- coba: `termux-open-file Add_WAN_FH_Mobile.apk`
- atau: `am start -a android.intent.action.VIEW -d file:///$(pwd)/Add_WAN_FH_Mobile.apk -t application/vnd.android.package-archive`

## 6. Persetujuan tambahan di Android 16

Di Android 16, sideload perlu **Install unknown apps** untuk aplikasi yang Anda gunakan meng-install:
Buka Pengaturan â†’ **Apps** â†’ **Special app access** â†’ **Install unknown apps** â†’ aktifkan untuk **Termux** (atau Chrome/Files, sesuai cara Anda membuka .apk).

Dan/atau tombol **"Install anyway"** di dialog Play Protect (bukan matikan Play Protect permanen â€” cukup sekali ini).

## 7. Uninstall / reinstall

```bash
# lihat apakah terinstall
pm list packages | grep addwan
# uninstall
pm uninstall com.daffiyanet.addwan
```

## Ringkas (copy-paste semua)

```bash
pkg update && pkg install curl termux-api
curl -L -o Add_WAN_FH_Mobile.apk https://github.com/Indra725/addwan-fiberhome-all-version/releases/download/v2.2/Add_WAN_FH_Mobile.apk
sha256sum Add_WAN_FH_Mobile.apk
termux-open Add_WAN_FH_Mobile.apk
```

## Kalau masih gagal

| Gejala | Solusi |
|---|---|
| "Ada masalah saat mengurai paket" (dari file manager) | Jangan buka dari file manager â€” gunakan `termux-open` / notifikasi download |
| "App not installed" | Aktifkan Install unknown apps untuk Termux (langkah 6) |
| "Ditolak oleh Play Protect" | Tap **"Install anyway"** (sekali) |
| `termux-open: command not found` | `pkg install termux-api` |
| SHA256 tidak cocok | Download ulang â€” itu tanda file korup |



