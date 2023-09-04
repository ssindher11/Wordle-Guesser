ICON_DIR="src/main/resources/drawables/launcher_icons"
mkdir -p $ICON_DIR
ORIGINAL_ICON="$ICON_DIR/original.png"
cp "$1" "$ORIGINAL_ICON"

# Linux
echo "🌀 Creating icon for Linux..."
convert -resize x128 "$ORIGINAL_ICON" "$ICON_DIR/linux.png"

# Windows
echo "🌀 Creating icon for Windows..."
convert -resize x128 "$ORIGINAL_ICON" "$ICON_DIR/windows.ico"

# MacOS
echo "🌀 Creating icon for macOS..."
convert -resize x128 "$ORIGINAL_ICON" "$ICON_DIR/macos.icns"

## Printing code
echo "Add this to your build.gradle.kts ⬇️"

OUTPUT="
val iconsRoot = project.file(\"src/main/resources/drawables\")

linux {
  iconFile.set(iconsRoot.resolve(\"launcher_icons/linux.png\"))
}

windows {
  iconFile.set(iconsRoot.resolve(\"launcher_icons/windows.ico\"))
}

macOS {
  iconFile.set(iconsRoot.resolve(\"launcher_icons/macos.icns\"))
}"
echo "$OUTPUT"