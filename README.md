# Add WAN FH — Add_WAN_FH.exe

Tool Windows untuk teknisi ISP / installer jaringan yang bekerja dengan modem **Fiberhome ONU/ONT** (seri AN5506 dkk.). Tool ini menambahkan/mengaktifkan konfigurasi **WAN** pada perangkat Fiberhome sehingga bisa dipakai untuk akses internet (PPPoE/DHCP/Static sesuai provisioning ONU).

> ⚠️ **Disclaimer**
> - Tool ini **BUKAN** produk resmi Fiberhome / FiberHome Technologies dan **tidak** berafiliasi, didukung, atau di-endorse oleh Fiberhome dalam bentuk apa pun.
> - Tool disediakan **as-is**, tanpa garansi apa pun. Penggunaan sepenuhnya tanggung jawab Anda.
> - Gunakan **hanya** pada perangkat yang Anda miliki atau yang Anda berwenang kelola (jaringan ISP / instalasi pelanggan yang Anda tangani secara resmi).
> - Dilarang menggunakan tool ini untuk mengakses perangkat yang bukan milik Anda / tanpa izin.
> - File `.exe` **tidak ditandatangani secara digital** — Windows SmartScreen mungkin menolak. Selalu verifikasi checksum sebelum menjalankan.

## Isi repo

Repo ini menyimpan **arsip semua versi** tool Add_WAN_FH (Add WAN Fiberhome). Binari didistribusikan lewat **[Releases](https://github.com/Indra725/addwan-fiberhome-all-version/releases)**, bukan di git tree.

| Versi | File | Tanggal |
|---|---|---|
| v1.0 | `Add_WAN_FH.exe` | 2026-10-21 |

## Verifikasi (v1.0)

```
SHA256: B936642B5A31BA408AB2B9CC67017D34B5971E4555F82A1375F0BF548A52DB60
MD5   : B581DADADA3E1A2D43D05D8FB5422527
Ukuran: 12.528.392 byte (≈ 11,9 MB)
```

Cara verifikasi di Windows PowerShell:

```powershell
Get-FileHash .\Add_WAN_FH.exe -Algorithm SHA256
```

## Kebutuhan

- Windows 7 / 8 / 10 / 11 (32/64 bit)
- Akses jaringan (LAN/telnet) ke perangkat Fiberhome ONT yang akan dikonfigurasi
- Jalankan sebagai **Administrator** bila diminta
- Antivirus/SmartScreen mungkin memblokir karena file tidak signed → whitelist manual setelah checksum cocok

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
| SmartScreen memblokir | Klik *More info* → *Run anyway* setelah verifikasi checksum |
| Tidak bisa konek ke ONU | Pastikan PC satu jaringan dengan ONU, telnet tidak di-block firewall/antivirus |
| WAN tidak muncul setelah proses | Restart ONU (cabut-colok power), cek ulang di web UI ONU |
| Error "not a valid Win32 application" | Jalankan sebagai administrator; non-aktifkan kompatibilitas Windows lama |

## Lisensi & hak cipta

- Tool adalah milik pembuat/distributor aslinya. Hak cipta tetap pada pemiliknya masing-masing.
- Distribusi di repo ini atas izin dari pemilik/distributor tool. Pertanyaan lisensi: buka issue di repo ini.
- Repo ini berisi dokumentasi & arsip binari untuk memudahkan teknisi; bukan klaim kepemilikan tool.

## Donasi / support

Kalau repo ini bermanfaat buat pekerjaan Anda, boleh dukung pengembangan & maintenance Daffiyanet:

- **08516163719** a/n Daffiyanet (QRIS / transfer)

## Lapor masalah

Buka tab **Issues** di repo ini. Sertakan: versi tool, versi firmware ONU, dan langkah reproduksi.
