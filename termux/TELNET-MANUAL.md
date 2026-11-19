# Login Telnet Manual di Termux — yang paling sederhana

## 1. Install telnet client

```bash
pkg update && pkg install busybox
```

## 2. Sambung

```bash
busybox telnet 192.168.1.1
```

> JANGAN pakai `nc 192.168.1.1 23` — layar akan terlihat kacau (`ÿþÿþ...`) karena `nc` tidak mengerti protokol telnet. Kalau melihat karakter kacau, berarti Anda pakai `nc` — ulangi dengan `busybox telnet`.

## 3. Yang akan tampil di layar (semua normal)

```
Fiberhome Telnet
Login:            -> ketik: gpon
Password:         -> ketik: Fh@ + 6 hex terakhir MAC ONT (contoh di bawah)
User>             -> ketik: enable
Password:         -> ketik lagi password yang sama
Config#           -> Anda sudah di CLI admin ONT
```

## 4. Cara hitung password (manual)

MAC ONT: `94:6B:12:AB:CD:EF` → password = `Fh@` + `ABCDEF` = **`Fh@ABCDEF`**
Huruf besar semua. Tidak ada yang diketik selain pasangan MAC itu sendiri.

## 5. Contoh sesi lengkap

```
$ busybox telnet 192.168.1.1
Fiberhome Telnet
Login: gpon
Password: Fh@ABCDEF     (tidak tampil saat diketik — normal)
User> enable
Password: Fh@ABCDEF
Config#
```

## Kalau gagal

| Gejala | Artinya | Solusi |
|---|---|---|
| `telnet: can't connect to remote host` | Telnet ditolak / ONT tidak bisa dijangkau | Aktifkan telnet di web ONT: `Security > ACL Settings` |
| Login: gpon -> "password error" / kembali ke Login | Password salah | Cek 6 hex terakhir MAC di stiker ONT (bukan OUI depan) |
| Masuk tapi `Config#` tidak muncul | CLI lambat / ROM berbeda | Tunggu 3–5 detik, atau kirim `enable` sekali lagi |
| `wget: not found` | ONT tidak punya wget | Gunakan USB stick berisi custom-webui.sh (lihat README) |

## Kalau tetap bingung — pakai mode otomatis

Script `addwan.py` melakukan semua langkah di atas tanpa Anda lihat:

```bash
python termux/addwan.py unlock 192.168.1.1 94:6B:12:AB:CD:EF
```
