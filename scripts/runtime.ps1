function Get-AcademiaRuntime([string]$ProjectPath, [string]$JdkHome) {
    $taskRoots = Join-Path ([Environment]::GetFolderPath('UserProfile')) '.jdks'
    $taskCandidates = @($JdkHome, $env:JAVA_HOME)
    if (Test-Path -LiteralPath $taskRoots) {
        $taskCandidates += @(Get-ChildItem -LiteralPath $taskRoots -Directory | Select-Object -ExpandProperty FullName)
    }
    $taskSelected = $null
    foreach ($taskCandidate in $taskCandidates) {
        if (!$taskCandidate) { continue }
        $taskRelease = Join-Path $taskCandidate 'release'
        if ((Test-Path -LiteralPath $taskRelease) -and
            ((Get-Content -LiteralPath $taskRelease -Raw) -match 'JAVA_VERSION="25[.\"]') -and
            (Test-Path -LiteralPath (Join-Path $taskCandidate 'bin\java.exe'))) {
            $taskSelected = $taskCandidate
            break
        }
    }
    if (!$taskSelected) { throw 'Configura JAVA_HOME con JDK 25 o pasa -JdkHome con su ruta.' }
    $taskTemp = Join-Path $ProjectPath '.local\tmp'
    New-Item -ItemType Directory -Path $taskTemp -Force | Out-Null
    if (!('AcademiaLauncher.ShortPath' -as [type])) {
        Add-Type -MemberDefinition '[System.Runtime.InteropServices.DllImport("kernel32.dll", CharSet=System.Runtime.InteropServices.CharSet.Unicode)] public static extern uint GetShortPathName(string path, System.Text.StringBuilder result, uint size);' -Name ShortPath -Namespace AcademiaLauncher
    }
    $taskBuffer = New-Object Text.StringBuilder 1024
    [void][AcademiaLauncher.ShortPath]::GetShortPathName($taskTemp, $taskBuffer, 1024)
    $taskShort = $taskBuffer.ToString()
    if (!$taskShort) { $taskShort = $taskTemp }
    return [PSCustomObject]@{ Java = (Join-Path $taskSelected 'bin\java.exe'); SocketTemp = $taskShort }
}
