#!/usr/bin/env bash
# Remove Boti from your machine (for testing a fresh install).
# Does NOT touch the project source code—only the installed copy and PATH.

set -e

INSTALL_DIR="${BOTI_INSTALL_DIR:-$HOME/.local/boti}"

echo "Removing installed Boti..."
if [ -d "$INSTALL_DIR" ]; then
  rm -rf "$INSTALL_DIR"
  echo "  Deleted: $INSTALL_DIR"
else
  echo "  (none found at $INSTALL_DIR)"
fi

echo "Removing Boti from shell PATH..."
for RC in "$HOME/.zshrc" "$HOME/.bashrc"; do
  if [ -f "$RC" ] && grep -q "\.local/boti\|# Boti" "$RC" 2>/dev/null; then
    cp "$RC" "${RC}.bak"
    if [ "$(uname)" = "Darwin" ]; then
      sed -i '' '/# Boti/d; /\.local\/boti/d' "$RC"
    else
      sed -i '/# Boti/d; /\.local\/boti/d' "$RC"
    fi
    echo "  Cleaned: $RC (backup: ${RC}.bak)"
  fi
done

echo "Done. Test a fresh install with:"
echo "  curl -sSL https://raw.githubusercontent.com/Rakshith0405/Boti/main/scripts/install.sh | bash"
echo "Open a new terminal so PATH is refreshed."
