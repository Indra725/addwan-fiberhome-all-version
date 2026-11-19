# Laporan Perbaikan UX Output — v2.17

Tanggal: 2026-11-20
APK: `Add_WAN_FH_Mobile.apk` (versionCode 18, versionName 2.17)
SHA256: `520446DB8867F935E596FF50D09DFE498A236D42ACEE883993CE888545880F74`

---

## 1. Permintaan yang diperbaiki

> "Di kolom output tunjukkan bahwa proses sedang berjalan dan bisa di-scroll serta digulir ke bawah/atas agar jelas apa yang ditampilkan layar output."

| Permintaan | Implementasi v2.17 |
|---|---|
| Tanda proses sedang berjalan **di kolom output** | Spinner `ProgressBar` + baris `⏳ BERJALAN — <aksi>` yang berubah menjadi `⏳ BERJALAN 3s`, `4s`, ... setiap 0,5 detik |
| Status akhir yang jelas | `✅ SELESAI 5 detik` (hijau) atau `❌ GAGAL 5 detik` (merah) |
| Riwayat aktivitas | Setiap baris log diberi **timestamp** `HH:mm:ss` |
| Bisa digulir ke atas | Log punya ScrollView sendiri; gestur gulir tidak direbut ScrollView halaman |
| Tidak ditarik balik ke bawah saat membaca | Auto-scroll **hanya** jika posisi log sudah di paling bawah. Kalau pengguna menggulir ke atas, posisi dipertahankan |
| Kembali cepat ke baris terbaru | Tombol `⤓` muncul di header output saat posisi tidak di bawah; sekali tap langsung ke bawah |
| Log tidak membengkak | Otomatis dipangkas setelah ±700 baris (menyisakan ±500 baris terakhir) |
| Tombol salin | **SALIN** menyalin seluruh isi log ke clipboard |

---

## 2. Perilaku yang akan terlihat di HP

1. Tekan tombol apa pun → header output langsung menampilkan spinner dan `⏳ BERJALAN — CEK ONT`.
2. Timer berjalan (`⏳ BERJALAN 1s`, `2s`, ...) selama proses berlangsung — layar tidak lagi terlihat diam.
3. Setiap langkah muncul dengan jam: `12:04:31  ▶ BACA DAFTAR WAN`, `12:04:32  login web UI ONT: OK`, dst.
4. Selesai → `✅ SELESAI 3 detik`, spinner hilang.
5. Gagal → `❌ GAGAL 3 detik` + baris `ERROR: ...` di log.
6. Gulir ke atas di dalam kotak output → teks tetap di posisi itu; tombol `⤓` muncul.
7. Tap `⤓` → kembali ke baris terbaru.

---

## 3. Pengujian

| Uji | Hasil | Bukti |
|---|---|---|
| Build release | **LULUS** | `BUILD SUCCESSFUL in 17s` |
| Metadata APK | **LULUS** | `versionCode=18`, `versionName=2.17`, label `Add WAN FH + Config` |
| Kode indikator masuk dex | **LULUS** | `BERJALAN` ×2, `SELESAI` ×5, `GAGAL` ×4, `taskStarted` ×3, `taskFinished` ×2, `updateFollowButton` ×1 |
| Tombol SALIN | **LULUS** (kode) | `SALIN` ×1 di dex |
| Fix protokol v2.16 tetap ada | **LULUS** | `get_refresh_sessionid` ×2, `session_valid` ×3 |
| Uji gulir di perangkat fisik | **BELUM** | tidak ada perangkat/emulator yang bisa dijalankan di sesi ini |
| Uji tampilan spinner di perangkat | **BELUM** | idem |

> Yang bisa saya buktikan dari sesi ini: build, metadata, dan kode benar-benar masuk ke APK. Perilaku sentuh/scroll perlu Anda konfirmasi di HP.

---

## 4. Catatan teknis

- `logScroll.setOnTouchListener(...)` memanggil `requestDisallowInterceptTouchEvent(true)` saat `ACTION_DOWN`, sehingga ScrollView halaman tidak merebut gestur gulir di dalam log.
- `isLogAtBottom()` dihitung **sebelum** teks ditambahkan, sehingga penambahan baris baru tidak memaksa posisi kembali ke bawah saat pengguna sedang membaca riwayat.
- Ticker memakai `Handler(Looper.getMainLooper())` dan dihentikan saat tidak ada tugas aktif (`activeTasks == 0`).
- `taskStarted`/`taskFinished` juga dipakai oleh AUTO-LOGIN, BACA CONFIG, ENCRYPT CONFIG, TAMBAH WAN, SIMPAN WAN, dan HAPUS WAN.
