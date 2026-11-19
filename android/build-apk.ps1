# Build Add_WAN_FH_Mobile.apk (Windows)
# Butuh: JDK 11+, Android SDK (build-tools 36+, platform android-36), Python 3, keystore di C:\Users\andik\addwan-debug.keystore
$ErrorActionPreference = 'Stop'

$AppDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$Sdk = 'C:\Users\andik\android-sdk'
$BT  = Join-Path $Sdk 'build-tools\36.0.0'
$Jar = Join-Path $Sdk 'platforms\android-36\android.jar'
$Build = Join-Path $AppDir 'build'
$Keystore = 'C:\Users\andik\addwan-debug.keystore'

New-Item -ItemType Directory -Force $Build | Out-Null
New-Item -ItemType Directory -Force (Join-Path $Build 'classes') | Out-Null

Write-Host '[1/6] aapt2 link (manifest + assets)...'
& "$BT\aapt2.exe" link -I $Jar `
  --manifest (Join-Path $AppDir 'app\AndroidManifest.xml') `
  -A (Join-Path $AppDir 'app\assets') `
  -o (Join-Path $Build 'base.apk')

Write-Host '[2/6] javac...'
& javac -source 8 -target 8 -nowarn -cp $Jar `
  -d (Join-Path $Build 'classes') `
  (Join-Path $AppDir 'app\src\com\daffiyanet\addwan\MainActivity.java')

Write-Host '[3/6] d8 -> classes.dex...'
$classFiles = (Get-ChildItem (Join-Path $Build 'classes') -Recurse -Filter *.class | ForEach-Object { $_.FullName })
& "$BT\d8.bat" --release --min-api 24 --lib $Jar `
  --output $Build `
  $classFiles

Write-Host '[4/6] insert classes.dex -> unsigned.apk...'
python -c "import zipfile, shutil; src=zipfile.ZipFile(r'$Build\base.apk'); dst=zipfile.ZipFile(r'$Build\unsigned.apk','w',zipfile.ZIP_DEFLATED); [dst.writestr(n, src.read(n)) for n in src.namelist()]; dst.write(r'$Build\classes.dex','classes.dex'); dst.close(); src.close()"

Write-Host '[5/6] zipalign...'
& "$BT\zipalign.exe" -f 4 (Join-Path $Build 'unsigned.apk') (Join-Path $Build 'aligned.apk')

Write-Host '[6/6] apksigner sign...'
& "$BT\apksigner.bat" sign --ks $Keystore --ks-pass pass:android --ks-key-alias addwan `
  --out (Join-Path $Build 'Add_WAN_FH_Mobile.apk') (Join-Path $Build 'aligned.apk')

& "$BT\apksigner.bat" verify --verbose (Join-Path $Build 'Add_WAN_FH_Mobile.apk')
Write-Host ''
Write-Host 'SELESAI: build\Add_WAN_FH_Mobile.apk' -ForegroundColor Green
