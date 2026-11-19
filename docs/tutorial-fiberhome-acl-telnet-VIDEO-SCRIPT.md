# SKRIP VIDEO — Cara Aktifkan ACL & Telnet di Router Fiberhome (Semua Versi)

**Durasi target:** 5–6 menit · **Bahasa:** Indonesia · **Format:** 1080p 30fps

**Tone:** santai teknis, seolah-olah ngajarin teman sesama teknisi.

---

## SCENE 1 — Intro (0:00–0:20)

**[Visual: Logo Add WAN FH muncul dengan fade-in + teks "Tutorial Fiberhome · Bahasa Indonesia"]**

**NARATOR (suara bariton, ramah):**
"Halo teknisi! Di video ini gw bakal tunjukin cara buka ACL dan Telnet di router Fiberhome — semua seri. HG6145F1, HG6145D2, HG6245, AN5506 — semuanya sama caranya, cuma beda menu sedikit. Lo ikutan sampai habis, dijamin bisa. Gaskeun!"

---

## SCENE 2 — Persiapan (0:20–0:45)

**[Visual: HP/laptop konek ke WiFi ONT, buka browser]**

**NARATOR:**
"Pertama, konek dulu ke jaringan ONT — bisa WiFi, bisa kabel LAN. Terus buka browser, ketik di address bar: `192.168.1.1`. Kalau enggak muncul, coba `http://192.168.100.1` — itu default sebagian ONT bawaan ISP."

---

## SCENE 3 — Login & User (0:45–1:15)

**[Visual: Form login ONT muncul]**

**NARATOR:**
"Di sini Anda ketemu form login. Masukkan: username `admin`, password-nya — kalau ONT baru bawaan ISP, lihat stiker di bawah ONT, password default biasanya `Fh@` diikuti 6 karakter hex dari MAC, contoh `Fh@AB12CD`. Kalau ONT bekas/di-rebranding, passwordnya mungkin sudah diganti teknisi. Coba sampai masuk."

---

## SCENE 4 — Aktifkan ACL (1:15–2:45)

**[Visual: Menu navigasi kiri, highlight tab Security]**

**NARATOR:**
"Setelah masuk, cari menu bernama `Security` atau `Firewall`. Di dalamnya ada `ACL Settings` atau `Access Control List`. Klik."

**[Visual: Halaman ACL — dua radio button Enable / Disable]**

**NARATOR:**
"Di sini ada dua radio button — Enable sama Disable. Centang **Enable**, lalu **Apply**. Sampai sini ACL sudah nyala."

**[Visual: Klik tombol `Add` → form Aturan ACL]**

**NARATOR:**
"Kalau Anda mau kasih aturan spesifik — misalnya cuma IP lokal kita yang boleh telnet — klik **Add**. Isi Start IP-nya `192.168.1.0` sampai End IP `192.168.1.254`, protocol `Telnet` atau `ALL`, Direction `LAN`, Enable `Yes`. Klik **Apply**."

---

## SCENE 5 — Aktifkan Telnet (2:45–3:30)

**[Visual: Masih di halaman ACL]**

**NARATOR:**
"Telnet itu praktis pasti udah nyala kalau ACL-nya aktif dengan rule Telnet. Tapi beberapa ONT punya menu khusus untuk remote access — cari di dalam `Security → Access Control` atau `System → Remote Access`. Pastikan `Telnet Access = Enable`."

**[Visual: Terminal / CMD — `ping 192.168.1.1` dan `telnet 192.168.1.1`]**

**NARATOR:**
"Coba test: dari CMD, ketik `telnet 192.168.1.1`. Kalau muncul prompt login, itu sukses. Login dengan username `gpon` dan password `Fh@` + 6 hex terakhir MAC ONT."

---

## SCENE 6 — Verifikasi (3:30–4:10)

**[Visual: Telnet session berhasil masuk — command prompt `User>` / `Config#`]**

**NARATOR:**
"Setelah login, Anda berada di CLI ONT. Coba `display version`, `system show`, atau `?` untuk melihat daftar perintah. Kalau output muncul, berarti semuanya jalan."

---

## SCENE 7 — Perbedaan Firmware & Custom Webui (4:10–5:00)

**[Visual: Slidetabel firmware: HG6145F1 RP4423, RP4307, AN5506, HG6245]**

**NARATOR:**
"Catatan: di firmware baru (RP4423 ke atas) menu ACL ada di `Security → ACL Settings`. Di firmware lama seperti AN5506, kadang di `Firewall → IP Filter`. Dan kalau ONT Anda di-lock TR-069 sama ISP, perubahan ACL bisa tidak tersimpan — solusinya cuma satu: minta ISP atau pakai **custom webui** yang di-upload lewat menu Local Upgrade."

---

## SCENE 8 — Penutup (5:00–5:30)

**[Visual: Logo + link donasi]**

**NARATOR:**
"Itu tadi cara aktifkan ACL dan Telnet di semua ONT Fiberhome. Kalau bermanfaat, support saya: donasi di `indra725.github.io/donasi` atau WA saya `08516163719`. Sampai jumpa di tutorial berikutnya. Bye!"

---

## REKAMAN TIPS

- Tools: **OBS Studio** (gratis) untuk screen capture + mic
- Voice: rekam di ruangan tenang, pakai earphone untuk mengurangi echo
- Edit: **CapCut** atau **DaVinci Resolve** — potong keheningan, tambah musik latar royalty-free
- Upload: YouTube, title "Cara Aktifkan ACL & Telnet di Router Fiberhome (Semua Versi)", thumbnail custom (ikon ONT + teks besar)
- Deskripsi: sertakan link `https://github.com/Indra725/addwan-fiberhome-all-version`
