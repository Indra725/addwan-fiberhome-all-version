# Menu UPLINK terbuka & permanen — checklist jujur (HG6145F1/D2)

Tujuan Anda: menu pengaturan uplink (port mapping / WAN-on-Ethernet) **terbuka** dan **tetap ada**. Berikut yang benar-benar bisa dan yang tidak bisa.

## Yang TIDAK bisa (jujur dulu)

- **Permanen melewati factory/forced reset** — mustahil untuk firmware apa pun tanpa flash penuh. Reset paksa = ONT kembali ke stock, semua modifikasi hilang. Mitigasi = **backup config** (lihat step 6).
- **Permanen melewati update firmware resmi** — update dari ISP akan menimpa semuanya.
- Tidak ada "permanen selamanya" tanpa hubungan ke prosedur restore.

## Yang BISA (dan cara mencapainya)

### Step 0 — Cek versi firmware ONT dulu
`Management > System Information / About` (atau stiker label). **Kustom-webui.bin hanya untuk firmware RP4423.** Kalau bukan RP4423, jangan flash dulu — sebutkan versi firmware-nya ke saya.

### Step 1 — Unlock via Local Upgrade (bukan telnet)
1. `http://192.168.1.1` → login admin
2. Menu: `Management > Device Management > Local Upgrade` (atau `System > Firmware`)
3. Upload `termux/custom-webui.bin` → **Update File**
4. "Upgrade failed" = normal → **refresh** → custom webui aktif
   → Menu jaringan tersembunyi (termasuk pengaturan **uplink/port mapping**) terbuka via endpoint patched AJAX.

### Step 2 — TR-069 patch (WAJIB)
TR-069 re-enable tiap boot dan akan menimpa setting Anda.
- Custom WebUI → **WebUI Options** → **Disable TR-069** → Apply Patch, atau
- edit config manual (lihat `docs/disable-tr069.md` di repo hg6145-uplink).

### Step 3 — Aktifkan "persistence across reboots" (jika ada)
WebUI Options → centang **Enable persistence across reboots** → Apply.
(Menurut README upstream, opsi ini tersedia untuk RP4423. Kalau tidak ada, webui hilang tiap reboot — settings tetap.)

### Step 4 — Set uplink
Via Broadband Settings (halaman terbuka pasca-unlock) atau console browser:
```js
XHR.get("get_uplink_info", null, function(d){ console.log(JSON.stringify(d, null, 2)) })
XHR.post("set_uplink_info", { uplink_type: "dev.eth.1" }, function(d){ console.log(JSON.stringify(d)) })
```
(Ganti `dev.eth.X` sesuai port yang dipakai.) — Setting ini **tersimpan di partisi config** → bertahan reboot.

### Step 5 — Reboot & verifikasi
Cabut-colok power ONT → tunggu boot → buka `http://192.168.1.1`:
- custom webui kembali? → persistence bekerja
- uplink_type tetap seperti di-set? → setting permanen untuk reboot biasa

### Step 6 — Backup config (ini yang membuat Anda "aman" setelah reset paksa)
1. Web UI → download config ONT
2. Dekripsi dengan [fiberhome-config-utility](https://github.com/numberonedz/fiberhome-config-utility)
3. Simpan di tempat aman (**BUKAN** di repo publik — berisi password!)
4. Simpan juga versi terenkripsinya (siap upload ulang)

**Setelah factory/forced reset — restore:**
1. Flash ulang `custom-webui.bin` (Local Upgrade)
2. Upload config backup
3. Disable TR-069 lagi
4. Selesai — itu satu-satunya "permanen" yang nyata

## Ringkasan status

| Kondisi | Yang bertahan |
|---|---|
| Reboot biasa | ✅ Settings (uplink_type dll.) + custom webui (jika persistence aktif) |
| Custom webui hilang tapi settings ada | Unlock ulang (1-2 menit) — settings tidak perlu di-set ulang |
| Factory / forced reset | ❌ Semua hilang → restore via step 6 → selesai |
| Update firmware resmi oleh ISP | ❌ Semua hilang → restore via step 6 |
