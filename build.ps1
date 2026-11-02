# Volume Client Standalone Build Script
param(
    [switch]$Clean
)

$ErrorActionPreference = "Stop"

$ClientDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$SrcJava = Join-Path $ClientDir "src\main\java"
$SrcRes = Join-Path $ClientDir "src\main\resources"
$BuildDir = Join-Path $ClientDir "build"
$ClassesDir = Join-Path $BuildDir "classes\java\main"
$LibsDir = Join-Path $BuildDir "libs"
$JarOut = Join-Path $LibsDir "volume-client-1.0.0.jar"

if ($Clean -and (Test-Path $BuildDir)) {
    Remove-Item -Recurse -Force $BuildDir
}

New-Item -ItemType Directory -Force -Path $ClassesDir | Out-Null
New-Item -ItemType Directory -Force -Path $LibsDir | Out-Null

$Javac = "C:\Program Files\Java\jdk-25.0.4.1\bin\javac.exe"
$Jar = "C:\Program Files\Java\jdk-25.0.4.1\bin\jar.exe"

if (-not (Test-Path $Javac)) {
    $Javac = "javac"
}
if (-not (Test-Path $Jar)) {
    $Jar = "jar"
}

# Collect Minecraft 26.3 JAR and all libraries
$McHome = "$env:APPDATA\VolumeLauncher\minecraft"
$McJar = Join-Path $McHome "versions\26.3\26.3.jar"
$LibDir = Join-Path $McHome "libraries"

$ClasspathEntries = [System.Collections.Generic.List[string]]::new()
$ClasspathEntries.Add($McJar)

Get-ChildItem -Path $LibDir -Filter "*.jar" -Recurse | ForEach-Object {
    $ClasspathEntries.Add($_.FullName)
}

$Classpath = [string]::Join(";", $ClasspathEntries)

# Collect all Java sources
$JavaFiles = Get-ChildItem -Path $SrcJava -Filter "*.java" -Recurse | ForEach-Object { $_.FullName }

if ($JavaFiles.Count -eq 0) {
    Write-Host "No Java files found to compile."
    exit 0
}

# Write sources to argfile to avoid CLI length limits (UTF-8 without BOM)
$ArgFile = Join-Path $BuildDir "sources.txt"
[System.IO.File]::WriteAllLines($ArgFile, $JavaFiles)

$CpFile = Join-Path $BuildDir "classpath.txt"
[System.IO.File]::WriteAllText($CpFile, $Classpath)

Write-Host "Compiling $($JavaFiles.Count) Java source files with Java 25..."
& $Javac -encoding UTF-8 --release 25 -d $ClassesDir "@$ArgFile" -cp "@$CpFile"

if ($LASTEXITCODE -ne 0) {
    Write-Error "Java compilation failed with code $LASTEXITCODE"
    exit $LASTEXITCODE
}

# Copy resources into classes directory
Copy-Item -Path "$SrcRes\*" -Destination $ClassesDir -Recurse -Force

Write-Host "Packaging into $JarOut..."
& $Jar --create --file $JarOut -C $ClassesDir .

if ($LASTEXITCODE -ne 0) {
    Write-Error "JAR packaging failed with code $LASTEXITCODE"
    exit $LASTEXITCODE
}

$JarSize = (Get-Item $JarOut).Length
Write-Host "Successfully built $JarOut ($([math]::Round($JarSize / 1KB, 1)) KB)"
