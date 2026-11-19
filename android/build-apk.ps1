# Build Add_WAN_FH_Mobile.apk (Windows, JDK 11+ / Android SDK / Python 3 / keystore Anda sendiri)
$ErrorActionPreference = 'Stop'

$AppDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$Sdk = 'C:\Users\andik\android-sdk'
$BT  = Join-Path $Sdk 'build-tools\36.0.0'
$Jar = Join-Path $Sdk 'platforms\android-36\android.jar'
$Build = Join-Path $AppDir 'build'
$Flat = Join-Path $Build 'flat'
$Keystore = 'C:\Users\andik\addwan-debug.keystore'

function Assert-Exit($step) { if ($LASTEXITCODE -ne 0) { throw "$step gagal (exit $LASTEXITCODE)" } }

Remove-Item -Recurse -Force $Build -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force $Flat | Out-Null
New-Item -ItemType Directory -Force (Join-Path $Build 'classes') | Out-Null

Write-Host '[1/7] aapt2 compile (resources)...'
& "$BT\aapt2.exe" compile --dir (Join-Path $AppDir 'app\res') -o $Flat
Assert-Exit 'aapt2 compile'

Write-Host '[2/7] aapt2 link (manifest + assets + resources)...'
$flats = (Get-ChildItem $Flat -Recurse -Filter *.flat | ForEach-Object { $_.FullName })
& "$BT\aapt2.exe" link -I $Jar `
  --manifest (Join-Path $AppDir 'app\AndroidManifest.xml') `
  -A (Join-Path $AppDir 'app\assets') `
  -R $flats `
  -o (Join-Path $Build 'base.apk')
Assert-Exit 'aapt2 link'

Write-Host '[3/7] javac...'
& javac -source 8 -target 8 -nowarn -cp $Jar `
  -d (Join-Path $Build 'classes') `
  (Join-Path $AppDir 'app\src\com\daffiyanet\addwan\MainActivity.java')
Assert-Exit 'javac'

Write-Host '[4/7] d8 -> classes.dex...'
$classFiles = (Get-ChildItem (Join-Path $Build 'classes') -Recurse -Filter *.class | ForEach-Object { $_.FullName })
& "$BT\d8.bat" --release --min-api 24 --lib $Jar `
  --output $Build `
  $classFiles
Assert-Exit 'd8'

Write-Host '[5/7] insert classes.dex -> unsigned.apk (preserve compression)...'
$merge = @'
import zipfile
src = zipfile.ZipFile(r'__BASE__')
dst = zipfile.ZipFile(r'__UNSIGNED__', 'w', zipfile.ZIP_DEFLATED)
for item in src.infolist():
    data = src.read(item.filename)
    zi = zipfile.ZipInfo(item.filename, date_time=item.date_time)
    zi.compress_type = item.compress_type
    zi.external_attr = item.external_attr
    dst.writestr(zi, data)
dst.write(r'__DEX__', 'classes.dex', zipfile.ZIP_DEFLATED)
dst.close(); src.close()
'@
$merge = $merge.Replace('__BASE__', "$Build\base.apk").Replace('__UNSIGNED__', "$Build\unsigned.apk").Replace('__DEX__', "$Build\classes.dex")
python -c $merge
if ($LASTEXITCODE -ne 0) { throw 'merge classes.dex gagal' }

Write-Host '[6/7] zipalign...'
& "$BT\zipalign.exe" -f 4 (Join-Path $Build 'unsigned.apk') (Join-Path $Build 'aligned.apk')
Assert-Exit 'zipalign'

Write-Host '[7/7] apksigner sign + verify...'
& "$BT\apksigner.bat" sign --ks $Keystore --ks-pass pass:android --ks-key-alias addwan `
  --out (Join-Path $Build 'Add_WAN_FH_Mobile.apk') (Join-Path $Build 'aligned.apk')
Assert-Exit 'apksigner sign'
& "$BT\apksigner.bat" verify --print-certs (Join-Path $Build 'Add_WAN_FH_Mobile.apk')
Assert-Exit 'apksigner verify'

Write-Host ''
Write-Host 'OK: build\Add_WAN_FH_Mobile.apk' -ForegroundColor Green
