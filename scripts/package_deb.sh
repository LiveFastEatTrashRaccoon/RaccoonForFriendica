#!/bin/bash

# Exit on error
set -e

VERSION=$1

if [ -z "$VERSION" ]; then
  echo "Error: Version argument is missing."
  exit 1
fi

# Remove 'v' prefix if present
VERSION=${VERSION#v}

tmpDir="distribution/tmp"
mkdir -p "$tmpDir"

# Find the generated deb file
filename=$(ls desktopApp/build/compose/binaries/main/deb/*.deb | head -n 1)

if [ -z "$filename" ]; then
  echo "Error: No .deb file found in desktopApp/build/compose/binaries/main/deb/"
  exit 1
fi

echo "Processing $filename..."

dpkg -x "$filename" "$tmpDir"
mv "$filename" "$filename.bak"
cp -r distribution/deb/usr "$tmpDir"
cp -r distribution/deb/DEBIAN "$tmpDir"

# Automate changelog selection based on buildNumber
BUILD_NUMBER=$(grep "buildNumber=" gradle.properties | cut -d'=' -f2 | tr -d '\r')
CHANGELOG_SRC="fastlane/metadata/android/en-US/changelogs/${BUILD_NUMBER}.txt"
mkdir -p "$tmpDir/usr/share/doc/raccoon"

if [ -f "$CHANGELOG_SRC" ]; then
  echo "Using changelog from $CHANGELOG_SRC"
  cp "$CHANGELOG_SRC" "$tmpDir/usr/share/doc/raccoon/changelog"
elif [ -f "$tmpDir/DEBIAN/changelog" ]; then
  echo "Using fallback changelog from DEBIAN/changelog"
  mv "$tmpDir/DEBIAN/changelog" "$tmpDir/usr/share/doc/raccoon/changelog"
fi

if [ -f "$tmpDir/usr/share/doc/raccoon/changelog" ]; then
  gzip -9 "$tmpDir/usr/share/doc/raccoon/changelog"
  chmod 644 "$tmpDir/usr/share/doc/raccoon/changelog.gz"
fi
rm -f "$tmpDir/DEBIAN/changelog"

# Update version in control file if it's a version-like string
if [[ $VERSION =~ ^[0-9] ]]; then
  echo "Updating version in control file to $VERSION"
  sed -i "s/^Version: .*/Version: $VERSION/" "$tmpDir/DEBIAN/control"
else
  echo "Skipping version update in control file (not a version-like string: $VERSION)"
fi

# Build the package
dpkg -b "$tmpDir" "$filename"
rm -r "$tmpDir"

echo "Done! Repackaged $filename"
