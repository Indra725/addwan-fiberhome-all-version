# Add WAN FH via Termux (tanpa PC, tanpa ROOT)

Jalankan tools Add WAN Fiberhome (HG6145F1/D2) langsung dari HP Anda lewat terminal [Termux](https://github.com/termux) — seluruh koneksi adalah TCP biasa (telnet/HTTP), **tidak butuh root**.

> ⚠️ Hanya untuk perangkat milik Anda sendiri / aset ISP yang Anda kelola. Bukan produk resmi Fiberhome. Backup config ONT dulu. Firmware RP4423.

## 1. Install Termux (PENTING: bukan dari Play Store)

Versi Play Store **sudah deprecated** dan rusak. Install dari:

- **F-Droid** (disarankan): https://f-droid.org/packages/com.termux/
- atau **GitHub Releases**: https://github.com/termux/termux-app/releases

## 2. Setup packages

Buka Termux, lalu:

```bash
pkg update && pkg upgrade
pkg install python git nano
python --version   # pastikan 3.x
```

## 3. Clone repo ini

```bash
git clone https://github.com/Indra725/addwan-fiberhome-all-version.git
cd addwan-fiberhome-all-version
```

## 4. Jalankan

**Menu interaktif (direkomendasikan untuk HP):**
```bash
python termux/addwan.py
```

```
==============================================
  ADD WAN FH - Termux Edition
  Fiberhome HG6145F1/D2 (RP4423)
==============================================
IP ONT [192.168.1.1]:
--------------------------------------------
1) CEK ONT
2) UNLOCK ONT
3) STATUS UPLINK (get_uplink_info)
4) SET UPLINK (set_uplink_info)
5) Ganti IP ONT
0) Keluar
--------------------------------------------
```

**Mode baris perintah:**
```bash
python termux/addwan.py check 192.168.1.1
python termux/addwan.py unlock 192.168.1.1 94:6b:12:ab:cd:ef
python termux/addwan.py status 192.168.1.1
python termux/addwan.py set 192.168.1.1 dev.eth.1
```

## 5. Alur kerja di lokasi pelanggan

1. HP konek ke WiFi/LAN ONT (mis. SSID ONT sendiri, IP `192.168.1.1`)
2. `1) CEK ONT` → pastikan TCP 23 & 80 terbuka dan custom webui terdeteksi
3. Jika belum unlock → `2) UNLOCK ONT` → isi MAC dari label ONT → selesai
4. `3) STATUS UPLINK` → lihat port mana yang jadi uplink
5. `4) SET UPLINK` → pilih `dev.eth.1..5` → ketik `SET` → selesai

## Troubleshooting

| Gejala | Solusi |
|---|---|
| Termux tidak bisa mengakses jaringan | Android 10+ boleh akses LAN tanpa izin khusus — pastikan HP benar-benar di jaringan ONT, bukan data seluler |
| `wget` di ONT tidak bisa download dari HP | Matikan hotspot sementara HP? Tidak — pastikan HP & ONT satu subnet (192.168.1.x) |
| Unlock gagal / ONT tidak respond | Telnet harus aktif dulu: login web ONT → `Security > ACL Settings` → enable telnet |
| Tidak bisa clone repo (network error) | Cek koneksi data/WiFi HP; coba `ping 8.8.8.8` di Termux; test `curl -I https://github.com` |
| MAC ONT tidak kebaca | Lihat label belakang ONT (`MAC: xx:xx:xx:xx:xx:xx`) |

## Detail teknis

- Semua operasi hanya lewat **socket TCP 23 (telnet)** dan **TCP 80 (HTTP)** — tidak menyentuh shell sistem Android, tidak butuh root.
- `custom-webui.sh` di-serve dari HP lewat HTTP mini-server internal (`http.server`) — port dinamis otomatis, tertutup otomatis setelah unlock.
- Tidak ada koneksi keluar ke internet selain `git clone` (hanya sekali).
