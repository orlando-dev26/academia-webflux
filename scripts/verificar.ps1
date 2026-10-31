param(
    [ValidateSet('compile', 'verify', 'test', 'package')]
    [string]$Mode = 'verify',
    [string]$JdkHome
)
$ErrorActionPreference = 'Stop'
$taskProject = Split-Path -Parent $PSScriptRoot
if (!(Test-Path -LiteralPath (Join-Path $taskProject 'pom.xml'))) { throw 'No se encuentra el proyecto Maven.' }
$taskJdkRoots = Join-Path ([Environment]::GetFolderPath('UserProfile')) '.jdks'
$taskCandidates = @($JdkHome, $env:JAVA_HOME)
if (Test-Path -LiteralPath $taskJdkRoots) {
    $taskCandidates += @(Get-ChildItem -LiteralPath $taskJdkRoots -Directory | Select-Object -ExpandProperty FullName)
}
$taskSelected = $null
foreach ($taskCandidate in $taskCandidates) {
    if (!$taskCandidate) { continue }
    $taskRelease = Join-Path $taskCandidate 'release'
    if ((Test-Path -LiteralPath $taskRelease) -and
        ((Get-Content -LiteralPath $taskRelease -Raw) -match 'JAVA_VERSION="25[.\"]') -and
        (Test-Path -LiteralPath (Join-Path $taskCandidate 'bin\javac.exe'))) {
        $taskSelected = $taskCandidate
        break
    }
}
if (!$taskSelected) { throw 'Configura JAVA_HOME con un JDK 25 o pasa -JdkHome con su ruta.' }
$env:JAVA_HOME = $taskSelected
$env:PATH = (Join-Path $taskSelected 'bin') + [IO.Path]::PathSeparator + $env:PATH
$taskLocal = Join-Path $taskProject '.local'
New-Item -ItemType Directory -Path $taskLocal -Force | Out-Null
$taskSocketTemp = Join-Path $taskLocal 'tmp'
New-Item -ItemType Directory -Path $taskSocketTemp -Force | Out-Null
# Java 25 utiliza sockets locales para sus selectores en Windows. Una ruta
# corta evita problemas con TEMP virtualizado y los limites de AF_UNIX.
Add-Type -MemberDefinition '[System.Runtime.InteropServices.DllImport("kernel32.dll", CharSet=System.Runtime.InteropServices.CharSet.Unicode)] public static extern uint GetShortPathName(string path, System.Text.StringBuilder result, uint size);' -Name ShortPath -Namespace AcademiaRuntime
$taskShortBuffer = New-Object Text.StringBuilder 1024
[void][AcademiaRuntime.ShortPath]::GetShortPathName($taskSocketTemp, $taskShortBuffer, 1024)
$taskSocketPath = $taskShortBuffer.ToString()
if (!$taskSocketPath) { $taskSocketPath = $taskSocketTemp }
$taskMavenArgs = @('-B', '-ntp', $Mode, ('-DargLine=-Djdk.net.unixdomain.tmpdir="' + $taskSocketPath + '"'))
Push-Location -LiteralPath $taskProject
try {
    Write-Output "JDK: $taskSelected. Maven: $Mode."
    # Windows PowerShell representa stderr nativo como ErrorRecord, incluso para warnings.
    $ErrorActionPreference = 'Continue'
    & (Join-Path $taskProject 'mvnw.cmd') @taskMavenArgs 2>&1 |
        ForEach-Object { $_.ToString() } | Tee-Object -FilePath (Join-Path $taskLocal "maven-$Mode.log")
    $taskResult = $LASTEXITCODE
    $ErrorActionPreference = 'Stop'
} finally { Pop-Location }
exit $taskResult
