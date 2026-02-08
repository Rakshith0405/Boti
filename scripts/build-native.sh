#!/usr/bin/env bash
# Build a native Boti binary for instant startup (milliseconds instead of 1–3 sec).
# Requires GraalVM with native-image. After this, bin/boti uses the native binary.

set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

echo "Building native Boti binary (instant startup)..."
echo "  Root: $ROOT"
echo ""

if [ -z "$JAVA_HOME" ]; then
  echo "ERROR: JAVA_HOME is not set. Point it to GraalVM (JDK 21 with native-image)." >&2
  echo "  Install: sdk install java 21.0.2-graalce && sdk use java 21.0.2-graalce" >&2
  echo "  Then:   export JAVA_HOME=\$(/usr/libexec/java_home -v 21)" >&2
  exit 1
fi
if [ -d "$JAVA_HOME/Contents/Home" ]; then
  JAVA_HOME="$JAVA_HOME/Contents/Home"
fi
if [ ! -x "$JAVA_HOME/bin/native-image" ] && [ ! -x "$JAVA_HOME/bin/gu" ]; then
  echo "ERROR: This JDK is not GraalVM (no native-image or gu)." >&2
  echo "  Install GraalVM: https://www.graalvm.org/downloads/" >&2
  echo "  Or: sdk install java 21.0.2-graalce" >&2
  exit 1
fi

cd "$ROOT"
mvn -q package -DskipTests -Pnative

echo ""
echo "Done. Native binary: $ROOT/target/boti"
echo "  Run: bin/boti   (launcher will use it; startup is instant)"
echo "  Or:  target/boti"
