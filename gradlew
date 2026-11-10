#!/usr/bin/env sh
set -e
GRADLE_VERSION="8.13"
GRADLE_URL="https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip"
GRADLE_HOME="$HOME/.gradle/wrapper/dists/gradle-${GRADLE_VERSION}"
if [ ! -f "$GRADLE_HOME/gradle-${GRADLE_VERSION}/bin/gradle" ]; then
  echo "Downloading Gradle ${GRADLE_VERSION}..."
  mkdir -p "$GRADLE_HOME"
  if command -v wget >/dev/null; then
    wget -q "$GRADLE_URL" -O /tmp/gradle.zip || curl -L "$GRADLE_URL" -o /tmp/gradle.zip
  else
    curl -L "$GRADLE_URL" -o /tmp/gradle.zip
  fi
  unzip -q /tmp/gradle.zip -d "$GRADLE_HOME"
fi
exec "$GRADLE_HOME/gradle-${GRADLE_VERSION}/bin/gradle" "$@"
