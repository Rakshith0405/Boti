#!/usr/bin/env bash
# One-command install for Boti. Run:
#   curl -sSL https://raw.githubusercontent.com/YOUR_USERNAME/Boti/main/Boti/scripts/install.sh | bash
# Or with options:
#   BOTI_VERSION=1.0 BOTI_INSTALL_DIR=~/boti curl -sSL ... | bash

set -e

# GitHub repo for releases (override with env: BOTI_REPO=owner/repo)
BOTI_REPO="${BOTI_REPO:-Rakshith0405/Boti}"
BOTI_VERSION="${BOTI_VERSION:-latest}"
BOTI_INSTALL_DIR="${BOTI_INSTALL_DIR:-$HOME/.local/boti}"

# Detect OS for the right release asset (darwin, linux, windows)
case "$(uname -s)" in
  Darwin)  OS="darwin" ;;
  Linux)   OS="linux" ;;
  MINGW*|MSYS*|CYGWIN*) OS="windows" ;;
  *)
    echo "Unsupported OS: $(uname -s). Download manually from GitHub Releases." >&2
    exit 1
    ;;
esac

# Resolve latest version from GitHub API
TAG=""
if [ "$BOTI_VERSION" = "latest" ]; then
  echo "Fetching latest Boti version..."
  TAG=$(curl -sSf "https://api.github.com/repos/$BOTI_REPO/releases/latest" | sed -n 's/.*"tag_name": *"\([^"]*\)".*/\1/p' | head -1)
  [ -z "$TAG" ] && { echo "Could not get latest version. Set BOTI_REPO and try again." >&2; exit 1; }
  BOTI_VERSION="${TAG#v}"
  echo "Latest version: $BOTI_VERSION"
else
  TAG="v${BOTI_VERSION}"
fi

# Prefer native zip (instant startup), then OS-specific, then generic
NATIVE_URL="https://github.com/$BOTI_REPO/releases/download/${TAG}/boti-${BOTI_VERSION}-${OS}-native.zip"
DOWNLOAD_URL="https://github.com/$BOTI_REPO/releases/download/${TAG}/boti-${BOTI_VERSION}-${OS}.zip"
FALLBACK_URL="https://github.com/$BOTI_REPO/releases/download/${TAG}/boti-${BOTI_VERSION}.zip"
echo "Downloading Boti $BOTI_VERSION for $OS..."

TMP_ZIP=$(mktemp -t boti-install.XXXXXX.zip)
TMP_DIR=$(mktemp -d -t boti-install.XXXXXX)
cleanup() { rm -rf "$TMP_ZIP" "$TMP_DIR"; }
trap cleanup EXIT

if curl -sSLf -o "$TMP_ZIP" "$NATIVE_URL" 2>/dev/null; then
  echo "  (native build – instant startup)"
elif curl -sSLf -o "$TMP_ZIP" "$DOWNLOAD_URL"; then
  echo "  (JVM build)"
elif curl -sSLf -o "$TMP_ZIP" "$FALLBACK_URL"; then
  echo "  (generic zip)"
else
  echo "Download failed. The release must contain one of:" >&2
  echo "  - boti-${BOTI_VERSION}-${OS}-native.zip  (native, instant start)" >&2
  echo "  - boti-${BOTI_VERSION}-${OS}.zip        (for this OS)" >&2
  echo "  - boti-${BOTI_VERSION}.zip               (generic)" >&2
  echo "See: https://github.com/$BOTI_REPO/releases" >&2
  exit 1
fi

unzip -q -o "$TMP_ZIP" -d "$TMP_DIR"
# Zip contains boti-1.0/ (or boti-$VERSION/) at top level
TOP_DIR=$(find "$TMP_DIR" -maxdepth 1 -type d -name 'boti-*' | head -1)
if [ ! -d "$TOP_DIR" ]; then
  echo "Unexpected zip layout. Install failed." >&2
  exit 1
fi

mkdir -p "$BOTI_INSTALL_DIR"
cp -R "$TOP_DIR"/* "$BOTI_INSTALL_DIR/"

# Find the actual bin dir (may be BOTI_INSTALL_DIR/bin or BOTI_INSTALL_DIR/boti-*/bin if zip had nested layout)
if [ -x "$BOTI_INSTALL_DIR/bin/boti" ] 2>/dev/null; then
  BOTI_BIN="$BOTI_INSTALL_DIR/bin"
elif find "$BOTI_INSTALL_DIR" -maxdepth 2 -type f -name boti -executable 2>/dev/null | head -1 | grep -q .; then
  BOTI_BIN="$(dirname "$(find "$BOTI_INSTALL_DIR" -maxdepth 2 -type f -name boti -executable 2>/dev/null | head -1)")"
else
  BOTI_BIN="$BOTI_INSTALL_DIR/bin"
fi
chmod +x "$BOTI_BIN/boti" 2>/dev/null || true

# Add to PATH in both .zshrc and .bashrc so it works in either shell
PATH_LINE="export PATH=\"\$PATH:$BOTI_BIN\""
if [ -n "$BOTI_SKIP_PATH" ]; then
  echo "Skipping PATH update (BOTI_SKIP_PATH is set)."
else
  for RC in "$HOME/.zshrc" "$HOME/.bashrc"; do
    if [ -f "$RC" ]; then
      if grep -q "Boti\|$BOTI_BIN\|\.local/boti" "$RC" 2>/dev/null; then
        : # already added
      else
        echo "" >> "$RC"
        echo "# Boti" >> "$RC"
        echo "$PATH_LINE" >> "$RC"
        echo "Added to $RC"
      fi
    fi
  done
  if [ ! -f "$HOME/.zshrc" ] && [ ! -f "$HOME/.bashrc" ]; then
    echo "Add to your shell config: $PATH_LINE"
  fi
fi

echo ""
echo "Boti $BOTI_VERSION installed to $BOTI_INSTALL_DIR"
echo "Run:  boti           (REPL)   or   boti script.boti"
echo "If 'boti' is not found, open a new terminal or run: source ~/.zshrc  (or ~/.bashrc)"
echo "No Java or Maven needed."
