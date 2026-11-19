# Laporan Analisis & Pengujian — v2.15

Tanggal: 2026-11-20
APK: `Add_WAN_FH_Mobile.apk` (versionCode 16, versionName 2.15)
SHA256: `9120B9CCE79C0BE321730CAFCF89C7D1A1B426FAB73EAC16AC156C0C3965AEB2`

---

## 1. Analisis aplikasi asli `Add_WAN_FH.exe`

| Item | Hasil |
|---|---|
| Ukuran | 12.528.392 byte |
| SHA256 | `B936642B5A31BA408AB2B9CC67017D34B5971E4555F82A1375F0BF548A52DB60` |
| Bahasa | **Object Pascal (Delphi)** — 1.335 string `Delphi`, 352 `TForm`, `System.SysUtils` |
| Framework | **VCL** (bukan .NET, bukan Python, bukan Electron) |
| Arsitektur | PE32 (32-bit, i386), 12 section khas Delphi (`.text .itext .data .bss .idata .didata .edata .tls .rdata .reloc .rsrc .debug`) |
| Komunikasi | **Winsock TCP mentah** (`ws2_32`, `WSAStartup`, `socket`, `connect`, `send`, `recv`) + komponen **Indy `TIdTelnet`** (`Terminal = dumb`) |
| Autentikasi | Telnet ONT: user `gpon`, password `Fh@` + 6 hex dari SN |
| Form | `TForm1` — caption `WAN Fiberhome [ Bery Indo ]` |
| Kontrol | `edtSN` (`FHTT9C7D6CA8`), `edtHost` (`192.168.31.217`), `edtPort` (`23`), `memoOutput`, `btnConnect` (`Connect`) |
| Event | `btnConnectClick`, `IdTelnet1Connected/Disconnected/DataAvailable` |

### Urutan perintah yang dikirim EXE (dari literal string di dalam binary)

```
login:                ->  gpon\r\n
password:             ->  Fh@<6 hex dari SN>\r\n
user>                 ->  en\r\n
config#               ->  cd factorydir\r\n
config\factorydir#    ->  load preconfig IDN_XL\r\n
selesai               ->  ------ SUKSES ------
```

**Kesimpulan penting:** EXE asli **bukan** GUI manajemen WAN. Tidak ada form Add/Edit/Delete WAN, tidak ada VLAN/NAT/bridge/backup. EXE hanya menjalankan **satu profil pabrik** `load preconfig IDN_XL` melalui telnet. Itulah "fungsi asli" yang wajib dipertahankan.

### Rumus password telnet

Fiberhome: `SN = FHTT` + 6 hex terakhir MAC + 2 hex checksum.
Jadi `Fh@` + `Mid(SN,5,6)` = `Fh@` + 6 hex MAC terakhir.

Diverifikasi silang pada ONT nyata: MAC `28:6D:DA:7D:3E:E0` → password `Fh@7D3EE0` → login telnet **berhasil**.

---

## 2. Peta fitur EXE → APK v2.15

| Fungsi EXE | Status di APK | Keterangan |
|---|---|---|
| Konek telnet ke ONT | **ADA** | `Telnet` (socket + strip IAC) |
| Login `gpon` | **ADA** | sama |
| Password `Fh@` dari SN/MAC | **ADA** | field SN opsional + fallback MAC |
| `en` (enable) | **ADA** | dengan fallback password bila diminta |
| `cd factorydir` | **ADA** | sama |
| `load preconfig IDN_XL` | **ADA (baru v2.15)** | tombol `ADD WAN — load preconfig IDN_XL (EXE)` |
| Memo output | **ADA** | log scrollable + auto-scroll |

---

## 3. Fitur WAN tambahan (CGI asli ONT)

Endpoint diambil dari JS resmi firmware ONT (`broadband_inter.js`, `pppoe_wan_inter.js`):

| Aksi | Endpoint | Field |
|---|---|---|
| Baca daftar WAN | `get_allwan_info` | — |
| Tambah WAN | `wan_add_new` | `ConnectionType`, `AddressingType`, `wan_iporppp_new`, `VLANEnable`, `vlanid`, `p8021`, `mtu`, `Username`, `WPd`, `NATEnabled`, `ServiceList`, `IPMode`, `sessionid` |
| Edit WAN | `wan_modify` | idem + `wan_index`, `wan_session_index`, `wan_iporppp_old`, `wan_name` |
| Hapus WAN | `wan_delete` | `wan_index`, `wan_session_index`, `wan_iporppp_old`, `sessionid` |
| NAT | `get_nat_info` / `set_nat_info` | tersedia di firmware (belum diekspos di UI) |

Mode yang didukung UI: **PPPoE Route, IPoE DHCP, IPoE Static, Bridge**, VLAN ID, VLAN priority, MTU, NAT.

---

## 4. Hasil pengujian

| No | Uji | Hasil | Bukti |
|---|---|---|---|
| 1 | Analisis EXE (bahasa, framework, form) | **LULUS** | PE header + resource TPF0 + literal string |
| 2 | Ekstraksi urutan perintah EXE | **LULUS** | 831 literal Unicode diparse, urutan konsisten |
| 3 | Build APK release | **LULUS** | `BUILD SUCCESSFUL in 2m 17s` |
| 4 | APK valid & metadata | **LULUS** | `aapt2 dump badging`: versionCode 16, versionName 2.15, label `Add WAN FH + Config` |
| 5 | Kode baru masuk ke dex | **LULUS** | `load preconfig IDN_XL` ×7, `wan_add_new` ×1, `wan_delete` ×1, `wan_modify` ×1, `get_allwan_info` ×1 |
| 6 | Kesesuaian field CGI dengan JS ONT | **LULUS** | nama field 1:1 dengan `broadband_inter.js` |
| 7 | Login web UI ONT live | **GAGAL / TERBLOKIR** | `{"session_valid":1,"login_result":4}` = username/password salah; password web UI tidak tersedia di sesi ini |
| 8 | Baca `get_allwan_info` live | **BELUM** | butuh login valid (lihat no. 7) |
| 9 | Tambah WAN live (PPPoE/DHCP/Static) | **BELUM** | butuh login valid + risiko memutus internet |
| 10 | Edit / Hapus WAN live | **BELUM** | idem |
| 11 | Uji HG6145F1 fisik | **BELUM** | tidak ada akses perangkat dari sesi ini |
| 12 | Uji HG6145D2 fisik | **BELUM** | tidak ada perangkat |
| 13 | Instalasi APK di Android 10–16 | **BELUM** | belum dijalankan di perangkat |

> Sesuai instruksi: pengujian perangkat **tidak diklaim berhasil**. Yang lulus adalah build, integritas APK, dan kesesuaian protokol dengan JS resmi ONT.

---

## 5. Batasan yang harus diketahui

1. **ACS/TR-069 dapat menimpa perubahan lokal.** Pada ONT uji (RP4307), `set_acl_info` mengembalikan `{}` tetapi status ACL tidak berubah — dikendalikan ACS. WAN juga berpotensi dikembalikan saat inform berikutnya.
2. **`load preconfig IDN_XL` hanya berhasil bila firmware memiliki direktori/profil `factorydir`.** Bila CLI terkunci (`% Unknown command.`), perintah akan ditolak — bukan bug aplikasi.
3. **`custom-webui.bin/.sh` hanya untuk RP4423.** Jangan flash ke RP4307 — risiko brick. Tombol UNLOCK memakai skrip ini; gunakan hanya pada firmware yang cocok.
4. **Mengubah/menghapus WAN dapat memutus internet.** Lakukan dari koneksi LAN dan catat konfigurasi lama.
5. **Backup/Restore config** yang tersedia adalah decrypt/encrypt file `usrconfig_conf`, bukan backup penuh NVRAM.

---

## 6. Rekomendasi pengujian berikutnya (di perangkat Anda)

1. Isi IP ONT, MAC, dan password web UI yang benar → tekan **BACA DAFTAR WAN**. Kirim screenshot log.
2. Baru setelah daftar WAN terbaca, uji **TAMBAH WAN** dengan VLAN yang belum dipakai.
3. Uji **ADD WAN (IDN_XL)** pada ONT yang firmware-nya mendukung `factorydir`.
4. Simpan output `get_allwan_info` sebelum & sesudah perubahan sebagai bukti.
