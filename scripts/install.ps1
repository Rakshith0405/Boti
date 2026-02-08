# One-command install for Boti on Windows. Run in PowerShell:
#   irm https://raw.githubusercontent.com/Rakshith0405/Boti/main/scripts/install.ps1 | iex
# Or:  powershell -ExecutionPolicy Bypass -Command "irm ... | iex"

$ErrorActionPreference = "Stop"
$BOTI_REPO = if ($env:BOTI_REPO) { $env:BOTI_REPO } else { "Rakshith0405/Boti" }
$BOTI_VERSION = if ($env:BOTI_VERSION) { $env:BOTI_VERSION } else { "latest" }
$InstallDir = if ($env:BOTI_INSTALL_DIR) { $env:BOTI_INSTALL_DIR } else { "$env:LOCALAPPDATA\boti" }

# Resolve latest version from GitHub API
$TAG = ""
if ($BOTI_VERSION -eq "latest") {
  Write-Host "Fetching latest Boti version..."
  $api = Invoke-RestMethod -Uri "https://api.github.com/repos/$BOTI_REPO/releases/latest" -Headers @{ "Accept" = "application/vnd.github.v3+json" }
  $TAG = $api.tag_name
  $BOTI_VERSION = $TAG -replace "^v", ""
  Write-Host "Latest version: $BOTI_VERSION"
} else {
  $TAG = "v$BOTI_VERSION"
}

$zipName = "boti-$BOTI_VERSION-windows.zip"
$downloadUrl = "https://github.com/$BOTI_REPO/releases/download/$TAG/$zipName"
Write-Host "Downloading Boti $BOTI_VERSION for Windows..."
Write-Host "  $downloadUrl"

$tempZip = Join-Path $env:TEMP "boti-install-$([Guid]::NewGuid().ToString('n')).zip"
$tempDir = Join-Path $env:TEMP "boti-install-$([Guid]::NewGuid().ToString('n'))"
try {
  Invoke-WebRequest -Uri $downloadUrl -OutFile $tempZip -UseBasicParsing
  Expand-Archive -Path $tempZip -DestinationPath $tempDir -Force
  $topDir = Get-ChildItem -Path $tempDir -Directory -Filter "boti-*" | Select-Object -First 1
  if (-not $topDir) {
    Write-Error "Unexpected zip layout. Install failed."
    exit 1
  }
  New-Item -ItemType Directory -Path $InstallDir -Force | Out-Null
  Copy-Item -Path "$($topDir.FullName)\*" -Destination $InstallDir -Recurse -Force
  $binPath = Join-Path $InstallDir "bin"
  $userPath = [Environment]::GetEnvironmentVariable("Path", "User")
  if ($userPath -notlike "*$binPath*") {
    [Environment]::SetEnvironmentVariable("Path", "$userPath;$binPath", "User")
    $env:Path = "$env:Path;$binPath"
    Write-Host "Added to PATH (User)."
  }
  Write-Host ""
  Write-Host "Boti $BOTI_VERSION installed to $InstallDir"
  Write-Host "Run:  boti           (REPL)   or   boti script.boti"
  Write-Host "If 'boti' is not found, open a new terminal (PowerShell or CMD)."
  Write-Host "No Java or Maven needed."
} finally {
  if (Test-Path $tempZip) { Remove-Item $tempZip -Force }
  if (Test-Path $tempDir) { Remove-Item $tempDir -Recurse -Force }
}
