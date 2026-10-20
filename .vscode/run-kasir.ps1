# Compile semua file Java di project lalu jalankan aplikasi Kasir Warkop.
$ErrorActionPreference = 'Stop'
Set-Location (Split-Path $PSScriptRoot -Parent)

$out = 'target/classes'
New-Item -ItemType Directory -Force $out | Out-Null
$files = Get-ChildItem -Recurse -Filter *.java src/main/java | ForEach-Object { $_.FullName }

Write-Host "Compile $($files.Count) file Java..."
javac -encoding UTF-8 -d $out $files
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "Menjalankan Kasir Warkop Kelompok 4..."
java -cp $out com.mycompany.project.gui.pemrograman.kel4.ProjectGuiPemrogramanKel4
