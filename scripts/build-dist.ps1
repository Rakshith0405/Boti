# Build Boti distribution on Windows. Run in PowerShell from the Boti project root.
# Creates dist/boti-1.0-windows.zip for GitHub Releases (so Windows friends can install).
# Requires: JDK 21, Maven (mvn on PATH).

$ErrorActionPreference = "Stop"
$VERSION = "1.0"
$DIST_NAME = "boti-$VERSION"
$ROOT = $PSScriptRoot | Split-Path -Parent
$DIST = Join-Path $ROOT "dist" $DIST_NAME

Write-Host "Building Boti distribution: $DIST_NAME"
Write-Host "  Root: $ROOT"
Write-Host ""

# Build JAR
Set-Location $ROOT
& mvn -q package -DskipTests
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

# Clean and create layout
if (Test-Path $DIST) { Remove-Item $DIST -Recurse -Force }
New-Item -ItemType Directory -Path (Join-Path $DIST "bin") -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $DIST "lib") -Force | Out-Null

# Bundle minimal JRE (so user doesn't need Java installed)
$javaHome = $env:JAVA_HOME
if (-not $javaHome) {
  Write-Error "JAVA_HOME is not set. Set it to a JDK 21+ to build the bundled JRE."
  exit 1
}
$jlink = Join-Path $javaHome "bin\jlink.exe"
if (-not (Test-Path $jlink)) {
  Write-Error "jlink not found in JAVA_HOME ($javaHome). Set JAVA_HOME to a JDK 21+."
  exit 1
}
Write-Host "Bundling JRE with jlink (this may take a minute)..."
& $jlink --add-modules java.base --strip-debug --no-header-files --no-man-pages --output (Join-Path $DIST "jre")
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

# Copy JAR and launchers
Copy-Item (Join-Path $ROOT "target\boti-1.0-SNAPSHOT.jar") (Join-Path $DIST "lib\")
Copy-Item (Join-Path $ROOT "bin\boti") (Join-Path $DIST "bin\") -ErrorAction SilentlyContinue
Copy-Item (Join-Path $ROOT "bin\boti.bat") (Join-Path $DIST "bin\")

# Create zip
$distParent = Join-Path $ROOT "dist"
$zipPath = Join-Path $distParent "$DIST_NAME-windows.zip"
if (Test-Path $zipPath) { Remove-Item $zipPath -Force }
Compress-Archive -Path $DIST -DestinationPath $zipPath -Force

Write-Host ""
Write-Host "Done. Distribution:"
Write-Host "  Folder: $DIST"
Write-Host "  Zip:    $zipPath"
Write-Host ""
Write-Host "Upload $zipPath to your GitHub Release (e.g. v$VERSION) so Windows users can install with:"
Write-Host "  irm https://raw.githubusercontent.com/Rakshith0405/Boti/main/scripts/install.ps1 | iex"
