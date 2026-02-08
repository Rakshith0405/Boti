#!/usr/bin/env bash
# Build a Boti distribution for end users: one folder with launcher, JAR, and
# a bundled JRE. Your friend can unzip it and run Boti without installing Java or Maven.

set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
VERSION="1.0"
DIST_NAME="boti-$VERSION"
DIST="$ROOT/dist/$DIST_NAME"

echo "Building Boti distribution: $DIST_NAME"
echo "  Root: $ROOT"
echo ""

# Build JAR
cd "$ROOT"
mvn -q package -DskipTests

# Clean and create layout
rm -rf "$DIST"
mkdir -p "$DIST/bin" "$DIST/lib"

# Bundle a minimal JRE (so user does not need Java installed)
if [ -z "$JAVA_HOME" ]; then
  echo "ERROR: JAVA_HOME is not set. Set it to a JDK 21+ to build the bundled JRE." >&2
  echo "  e.g. export JAVA_HOME=\$(/usr/libexec/java_home -v 21)" >&2
  exit 1
fi
# macOS JDK layout: JAVA_HOME might be .../Contents/Home
if [ -d "$JAVA_HOME/Contents/Home" ]; then
  JAVA_HOME="$JAVA_HOME/Contents/Home"
fi
if [ ! -x "$JAVA_HOME/bin/jlink" ]; then
  echo "ERROR: jlink not found in JAVA_HOME ($JAVA_HOME). Set JAVA_HOME to a JDK 21+." >&2
  exit 1
fi
echo "Bundling JRE with jlink (this may take a minute)..."
"$JAVA_HOME/bin/jlink" \
  --add-modules java.base \
  --strip-debug \
  --no-header-files \
  --no-man-pages \
  --output "$DIST/jre"

# Copy JAR and launchers
cp "$ROOT/target/boti-1.0-SNAPSHOT.jar" "$DIST/lib/"
cp "$ROOT/bin/boti" "$DIST/bin/"
cp "$ROOT/bin/boti.bat" "$DIST/bin/"
chmod +x "$DIST/bin/boti"

# Create zips for distribution and for GitHub Releases
cd "$ROOT/dist"
zip -r -q "$DIST_NAME.zip" "$DIST_NAME"

# OS-specific zip for one-command install (install.sh downloads the right one)
case "$(uname -s)" in
  Darwin)  OS="darwin" ;;
  Linux)   OS="linux" ;;
  *)       OS="unknown" ;;
esac
if [ "$OS" != "unknown" ]; then
  cp "$DIST_NAME.zip" "$DIST_NAME-$OS.zip"
  echo "  Zip (this OS): $ROOT/dist/$DIST_NAME-$OS.zip"
fi

# Optional: native binary zip for instant startup (if GraalVM is available)
NATIVE_BIN="$ROOT/target/boti"
if [ -x "$NATIVE_BIN" ] || ( [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/native-image" ] 2>/dev/null ); then
  if [ ! -x "$NATIVE_BIN" ]; then
    echo "Building native binary for fast-start zip..."
    cd "$ROOT" && mvn -q package -DskipTests -Pnative 2>/dev/null && cd - >/dev/null || true
  fi
  if [ -x "$NATIVE_BIN" ] && [ "$OS" != "unknown" ]; then
    NATIVE_DIST="$ROOT/dist/$DIST_NAME-native"
    rm -rf "$NATIVE_DIST"
    mkdir -p "$NATIVE_DIST/bin"
    cp "$NATIVE_BIN" "$NATIVE_DIST/bin/"
    chmod +x "$NATIVE_DIST/bin/boti"
    cd "$ROOT/dist"
    zip -r -q "$DIST_NAME-$OS-native.zip" "$DIST_NAME-native"
    rm -rf "$NATIVE_DIST"
    echo "  Zip (native, instant start): $ROOT/dist/$DIST_NAME-$OS-native.zip"
  fi
fi

echo ""
echo "Done. Distribution:"
echo "  Folder: $DIST"
echo "  Zip:    $ROOT/dist/$DIST_NAME.zip"
echo ""
echo "To let friends install from the internet (like Java/Python):"
echo "  1. Create a release on GitHub: tag v$VERSION, upload dist/boti-$VERSION-darwin.zip,"
echo "     boti-$VERSION-linux.zip (build on Linux for that one), and optionally boti-$VERSION-windows.zip."
echo "  2. They run: curl -sSL https://raw.githubusercontent.com/YOUR_USERNAME/Boti/main/Boti/scripts/install.sh | bash"
echo "     (Replace YOUR_USERNAME and repo path with your GitHub repo.)"
echo "  No Java or Maven needed on their machine."
