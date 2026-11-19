# Laporan Perbaikan & Pengujian — v2.16

Tanggal: 2026-11-20
APK: `Add_WAN_FH_Mobile.apk` (versionCode 17, versionName 2.16)
SHA256: `C7B34805A0BF090969129759F8BEEED090B8C8D0EABE55C70CA60F2426462D5B`

---

## 1. Keluhan yang diperbaiki

> "Tidak berhasil / tidak ada respon di output."

Setelah audit kode v2.15, ditemukan **empat penyebab nyata**:

| No | Penyebab | Akibat |
|---|---|---|
| 1 | Kartu **output** berada jauh di bawah tombol (setelah kartu WAN) | Pengguna menekan tombol, log bertambah tetapi **tidak terlihat** karena di luar layar |
| 2 | `httpRequest()` mengembalikan `"200\n{...json...}"`, lalu `new JSONObject(resp)` | **Semua parse JSON WAN gagal** — BACA/EDIT/HAPUS WAN tidak pernah bisa membaca daftar |
| 3 | `ontCgi()` memakai sessionid lama dari login | ONT membalas `{"session_valid":0}`; `XHR.post` asli selalu memanggil `get_refresh_sessionid` dulu |
| 4 | `ADD WAN IDN_XL` hanya menulis satu baris lalu menunggu hingga 70 detik | Terlihat seperti macet / tidak ada respon |

---

## 2. Perbaikan di v2.16

| Perbaikan | Detail |
|---|---|
| **Output pindah ke atas** | Kartu log sekarang langsung di bawah header — selalu terlihat tanpa scroll |
| **Status bar** | Baris status di atas menampilkan `⏳ <aksi> ...` → `✅ <aksi> selesai` / `❌ <aksi> gagal` |
| **Log instan** | `runTask` menulis `▶ <nama aksi>` di UI thread **sebelum** pekerjaan dimulai |
| **Tombol SALIN** | Menyalin seluruh isi log ke clipboard (memudahkan lapor bug) |
| **Log bisa diseleksi** | `setTextIsSelectable(true)` |
| **`bodyOf()`** | Membuang baris kode HTTP sebelum JSON diparse |
| **Refresh sessionid** | `ontCgi` memanggil `get_refresh_sessionid` lalu memakai sessionid baru, sama seperti `XHR.post` |
| **Login tahan spasi** | `"login_result": 0` dan `"login_result":0` sama-sama dikenali |
| **Progres ADD WAN** | Log `1/6` … `6/6` di setiap langkah telnet, timeout per langkah 6 detik |
| **`session_valid:0`** | Login ulang otomatis sekali, lalu memberi pesan jelas bila tetap ditolak |
| **Catatan WAN** | Kartu WAN menyatakan bahwa BACA/TAMBAH/EDIT/HAPUS butuh username + password web UI |

---

## 3. Bukti pengujian protokol (server ONT palsu)

Karena tidak ada perangkat fisik yang dapat diakses dari sesi ini, logika jaringan yang **sama persis** dengan `MainActivity` dijalankan terhadap server ONT tiruan yang meniru:

- `get_acs_random` → `do_login` (AES-128-CBC) → `get_refresh_sessionid`
- `get_allwan_info`, `wan_add_new`, `wan_modify`, `wan_delete`
- Server **menolak** POST yang tidak memakai sessionid hasil refresh (`session_valid:0`)

Hasil:

```
PASS  login admin/secret123  sid=ACSSID1
PASS  get_allwan_info tanpa prefix HTTP  awal='{"session_valid": 1, "wan": [...'
PASS  get_allwan_info berisi array wan
PASS  session_valid bukan 0
PASS  wan_add_new   {"success": true, "added": true}
PASS  wan_modify    {"success": true, "modified": true}
PASS  wan_delete    {"success": true, "deleted": true}
=== SEMUA TES LULUS ===
```

Bukti tambahan dari state server:

```
acs_calls=1 login_calls=1 refresh_calls=4
post sessionids: do_login=ACSSID1,
                 get_allwan_info=REFRESH2,
                 wan_add_new=REFRESH2,
                 wan_modify=REFRESH2,
                 wan_delete=REFRESH2
wan_add vlan=300 conn=PPPoE_Routed user=test@isp
wan_modify vlan=301
wan_delete index=2 session=1
```

Artinya: JSON kini bersih, sessionid benar-benar di-refresh, dan parameter WAN terkirim sesuai skema `broadband_inter.js`.

---

## 4. Yang masih belum teruji (jujur)

| Uji | Status |
|---|---|
| Build APK release | **LULUS** (BUILD SUCCESSFUL) |
| Metadata APK | **LULUS** (versionCode 17, versionName 2.16, label `Add WAN FH + Config`) |
| String kode baru masuk dex | **LULUS** (`get_refresh_sessionid` ×2, `session_valid` ×3, `1/6`, `SALIN`, WAN endpoints) |
| Protokol login + WAN CRUD vs server tiruan | **LULUS** (7/7) |
| Login ke ONT fisik | **BELUM** — password web UI ONT tidak tersedia di sesi ini |
| BACA/TAMBAH/EDIT/HAPUS WAN di ONT fisik | **BELUM** — menunggu login valid |
| `ADD WAN IDN_XL` di ONT fisik | **BELUM** — CLI ONT uji terkunci (`% Unknown command.`) |
| Instalasi di HP Android | **BELUM** — belum dijalankan di perangkat |
| HG6145F1 / HG6145D2 fisik | **BELUM** — tidak ada perangkat |

> Tidak ada klaim pengujian perangkat fisik yang berhasil. Yang lulus adalah build, integritas APK, dan protokol terhadap server tiruan.

---

## 5. Cara menguji di HP

1. Install `Add_WAN_FH_Mobile.apk` v2.16.
2. Sambungkan HP ke WiFi/LAN ONT.
3. Isi **IP ONT** (default `192.168.1.1`), **MAC ONT**, **USERNAME** dan **PASSWORD web UI ONT**.
4. Tekan **CEK ONT** — status di atas akan menunjukkan `⏳ CEK ONT ...` lalu hasil TCP 80/23.
5. Tekan **BACA DAFTAR WAN** — log langsung terlihat di bawah header.
6. Bila log menampilkan `login GAGAL: ... login_result:4`, password web UI salah.
7. Bila `session_valid:0`, aplikasi otomatis login ulang sekali.
8. Baru setelah daftar WAN terbaca, uji TAMBAH/EDIT/HAPUS dengan VLAN yang belum dipakai.

Tombol **SALIN** di kartu output menyalin seluruh log — kirimkan hasilnya bila masih ada masalah.
