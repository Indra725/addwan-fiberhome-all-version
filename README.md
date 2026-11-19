# Add WAN FH â€” Add_WAN_FH.exe

Tool Windows untuk teknisi ISP / installer jaringan yang bekerja dengan modem **Fiberhome ONU/ONT** (seri AN5506 dkk.). Tool ini menambahkan/mengaktifkan konfigurasi **WAN** pada perangkat Fiberhome sehingga bisa dipakai untuk akses internet (PPPoE/DHCP/Static sesuai provisioning ONU).

> âš ï¸ **Disclaimer**
> - Tool ini **BUKAN** produk resmi Fiberhome / FiberHome Technologies dan **tidak** berafiliasi, didukung, atau di-endorse oleh Fiberhome dalam bentuk apa pun.
> - Tool disediakan **as-is**, tanpa garansi apa pun. Penggunaan sepenuhnya tanggung jawab Anda.
> - Gunakan **hanya** pada perangkat yang Anda miliki atau yang Anda berwenang kelola (jaringan ISP / instalasi pelanggan yang Anda tangani secara resmi).
> - Dilarang menggunakan tool ini untuk mengakses perangkat yang bukan milik Anda / tanpa izin.
> - File `.exe` **tidak ditandatangani secara digital** â€” Windows SmartScreen mungkin menolak. Selalu verifikasi checksum sebelum menjalankan.

## ðŸ“± Add_WAN_FH Mobile (Android) â€” baru di v2.0

**[â¬‡ï¸ Download Add_WAN_FH_Mobile.apk](https://github.com/Indra725/addwan-fiberhome-all-version/releases/tag/v2.0)** â€” tools yang sama dalam satu APK Android, **tanpa PC/laptop**.

**Fitur:**
- **CEK ONT** â€” ping, cek TCP 23 (telnet) & 80, verifikasi custom webui aktif
- **UNLOCK** â€” telnet `gpon`/`Fh@MAC`, push `custom-webui.sh` dari HP via mini HTTP server internal (tanpa server eksternal)
- **STATUS UPLINK** â€” `get_uplink_info` (port mana yang jadi uplink)
- **SET UPLINK** â€” `set_uplink_info` (pilih port `dev.eth.1`â€“`dev.eth.5`)

**Kebutuhan:**
- HP Android 7+ dengan aplikasi ini terinstall (aktifkan *Install unknown apps*)
- HP **satu jaringan/WiFi/LAN** dengan ONT (mis. konek ke WiFi ONT `192.168.1.1`)
- ONT HG6145F1/D2 firmware RP4423 (custom webui unlock)
- Telnet aktif di ONT: `Security > ACL Settings` (lihat repo [hg6145-uplink-htb-media-converter](https://github.com/Indra725/hg6145-uplink-htb-media-converter))
- Backup config ONT dulu â€” unlock memodifikasi firmware (risiko brick)

**Verifikasi (v2.0):**
```
SHA256: C68CC55F7816DFA5A5E8465201B23B4E77829606B9852E2E59450647B950D0CB
Ukuran: 455.529 byte
```

**Build dari source** (Windows, JDK 17 + Android SDK + keystore Anda sendiri):
```powershell
cd android
.\build-apk.ps1
```

## Isi repo

Repo ini menyimpan **arsip semua versi** tool Add_WAN_FH (Add WAN Fiberhome) & utility konfigurasi Fiberhome. Binari didistribusikan lewat **[Releases](https://github.com/Indra725/addwan-fiberhome-all-version/releases)**, bukan di git tree. Riwayat perubahan: [`CHANGELOG.md`](CHANGELOG.md).

| Versi | File | Tanggal | Rilis |
|---|---|---|---|
| v2.0 | `Add_WAN_FH_Mobile.apk` (Android) | 2026-11-19 | [releases/v2.0](https://github.com/Indra725/addwan-fiberhome-all-version/releases/tag/v2.0) |
| v1.1 | `FiberhomeConfigUtility.exe` | 2026-10-22 | [releases/v1.1](https://github.com/Indra725/addwan-fiberhome-all-version/releases/tag/v1.1) |
| v1.0 | `Add_WAN_FH.exe` | 2026-10-21 | [releases/v1.0](https://github.com/Indra725/addwan-fiberhome-all-version/releases/tag/v1.0) |

## Verifikasi (v1.0)

```
SHA256: B936642B5A31BA408AB2B9CC67017D34B5971E4555F82A1375F0BF548A52DB60
MD5   : B581DADADA3E1A2D43D05D8FB5422527
Ukuran: 12.528.392 byte (â‰ˆ 11,9 MB)
```

## Verifikasi (v1.1) â€” FiberhomeConfigUtility (upplink/LAN)

```
SHA256: 5289A36AE7D4C87080D8B3499E0EB5A3FC843D257898D49513E7796DA89A7879
MD5   : BAD6A5C629EA4BE141447CD93184692F
Ukuran: 10.700.931 byte
```

`FiberhomeConfigUtility.exe` â€” utility konfigurasi Fiberhome (port / bridge / LAN uplink). Sama seperti v1.0: unsigned, Windows Defender tidak mendeteksi ancaman. Cara verifikasi sama:

```powershell
Get-FileHash .\FiberhomeConfigUtility.exe -Algorithm SHA256
```

Cara verifikasi di Windows PowerShell:

```powershell
Get-FileHash .\Add_WAN_FH.exe -Algorithm SHA256
```

## Kebutuhan

- Windows 7 / 8 / 10 / 11 (32/64 bit)
- Akses jaringan (LAN/telnet) ke perangkat Fiberhome ONT yang akan dikonfigurasi
- Jalankan sebagai **Administrator** bila diminta
- Antivirus/SmartScreen mungkin memblokir karena file tidak signed â†’ whitelist manual setelah checksum cocok

## Cara pakai (umum)

1. Download `Add_WAN_FH.exe` dari tab **Releases**.
2. Verifikasi checksum SHA256 (harus `B936642B5A31BA408AB2B9CC67017D34B5971E4555F82A1375F0BF548A52DB60`).
3. Hubungkan PC ke ONU Fiberhome (LAN port / jaringan yang sama).
4. Jalankan `Add_WAN_FH.exe` sebagai administrator.
5. Ikuti petunjuk di layar tool untuk menambahkan WAN pada ONU.
6. Setelah selesai, restart ONU bila diminta dan tes koneksi.

## Masalah umum

| Gejala | Kemungkinan solusi |
|---|---|
| SmartScreen memblokir | Klik *More info* â†’ *Run anyway* setelah verifikasi checksum |
| Tidak bisa konek ke ONU | Pastikan PC satu jaringan dengan ONU, telnet tidak di-block firewall/antivirus |
| WAN tidak muncul setelah proses | Restart ONU (cabut-colok power), cek ulang di web UI ONU |
| Error "not a valid Win32 application" | Jalankan sebagai administrator; non-aktifkan kompatibilitas Windows lama |

## Lisensi & hak cipta

- Tool adalah milik pembuat/distributor aslinya. Hak cipta tetap pada pemiliknya masing-masing.
- Distribusi di repo ini atas izin dari pemilik/distributor tool. Pertanyaan lisensi: buka issue di repo ini.
- Repo ini berisi dokumentasi & arsip binari untuk memudahkan teknisi; bukan klaim kepemilikan tool.

## Donasi / support

Kalau repo ini bermanfaat buat pekerjaan Anda, boleh dukung pengembangan & maintenance Daffiyanet:

- **Halaman donasi:** https://indra725.github.io/donasi/
- **QRIS:** scan dari semua e-wallet / m-banking (DAFFIYANET, Konter â€” NMID ID1025401523762)
- **E-Wallet (DANA / GoPay / OVO / ShopeePay):** **08516163719** a.n. **Daffiyanet**
- **WhatsApp:** [wa.me/628516163719](https://wa.me/628516163719)

## Lapor masalah

Buka tab **Issues** di repo ini. Sertakan: versi tool, versi firmware ONU, dan langkah reproduksi.


