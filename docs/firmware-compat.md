# Kompatibilitas firmware & ONT — BACA DULU sebelum flash

custom-webui.bin / custom-webui.sh dari proyek [hg6145f1-dz-custom-webui](https://github.com/numberonedz) **hanya untuk firmware RP4423** (menurut author-nya, hanya diuji pada perangkat yang beredar di Algeria).

| Firmware ONT | Status unlock | Tool |
|---|---|---|
| **RP4423** | Bisa — telnet CLI terbuka (`shell` tersedia), Local Upgrade patch | custom-webui.bin/sh di repo ini |
| **RP4307** (firmware user) | **TIDAK kompatibel** — CLI terkunci (`% Unknown command.`), tidak ada unlock publik | belum ada |
| Firmware lain | Perlu uji / tunggu tool spesifik | — |

## Konsekuensi kalau terlanjur flash RP4423 bin ke RP4307

- "Upgrade failed" bisa muncul tanpa efek — tidak berbahaya tapi **tidak membuka** menu
- Atau **brick** (bootloop) pada firmware dengan struktur berbeda — ONT bisa mati total

## Temuan dari config device Anda (HG6145F1, firmware RP4307)

Config report memuat parameter Fiberhome `X_FH_*`, mis.:
- `X_FH_LanInterface` = `dev.eth.3,dev.wla.*` — daftar port LAN yang terikat bridge LAN
- `NoBindForwardEnable`, `X_FH_Isolate` — isolation/binding port
- WAN service: `VLANID 200`, `service INTERNET`, PPPoE

Fitur uplink/port mapping pada firmware ini ada di tataran config `X_FH_*` — **bukan** di menu web umum, dan tidak bisa dibuka tanpa salah satu:
1. Tool unlock spesifik RP4307 (belum tersedia — GitHub: 0 hasil)
2. Config editing via upload/download config di web UI (resiko: salah edit bisa membuat ONT unreachable — tapi selalu bisa restore via config backup yang sudah Anda punya)

## Rekomendasi untuk RP4307

1. **Tanya vendor tempat Anda dapat Add_WAN_FH.exe** — mereka hampir pasti punya tool/jalur unlock untuk RP4307 (distributor resmi biasanya pegang ini)
2. Minta **menu "uplink"/"port mapping"** dari web UI operator/ISP yang mem-provision ONT Anda (sering tersedia di service-provider mode)
3. **Backup config** Anda (sudah ada: `usrconfig_conf_report.html`) — file ini bisa di-upload kembali via web UI kalau ONT bermasalah
