param([int]$Port = 8080, [switch]$Restart, [string]$JdkHome)
$ErrorActionPreference = 'Stop'
$taskProject = (Get-Item -LiteralPath (Split-Path -Parent $PSScriptRoot)).FullName
. (Join-Path $PSScriptRoot 'runtime.ps1')
$taskRuntime = Get-AcademiaRuntime $taskProject $JdkHome
if (!(Test-Path -LiteralPath (Join-Path $taskProject '.local\application-local.yaml'))) {
    & (Join-Path $PSScriptRoot 'preparar-local.ps1')
}
$taskJar = Join-Path $taskProject 'target\academia-webflux-0.0.1-SNAPSHOT.jar'
if (!(Test-Path -LiteralPath $taskJar)) { throw 'Genera primero el JAR con scripts/verificar.ps1 -Mode verify.' }
$taskListeners = @(Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue |
    Select-Object -ExpandProperty OwningProcess -Unique)
foreach ($taskPid in $taskListeners) {
    $taskProcess = Get-CimInstance Win32_Process -Filter "ProcessId=$taskPid"
    $taskOwned = $taskProcess.Name -ieq 'java.exe' -and
        $taskProcess.CommandLine.Contains($taskProject) -and
        ($taskProcess.CommandLine.Contains('com.orlandini.academiawebflux.AcademiaWebfluxApplication') -or
         $taskProcess.CommandLine.Contains('academia-webflux-0.0.1-SNAPSHOT.jar'))
    if (!$taskOwned) { throw "El puerto $Port pertenece a otro programa. Elige -Port con otro valor." }
    if (!$Restart) { throw 'La aplicacion ya usa ese puerto. Agrega -Restart para reiniciarla.' }
    Stop-Process -Id $taskPid -ErrorAction Stop
    Wait-Process -Id $taskPid -Timeout 10 -ErrorAction SilentlyContinue
}
$env:PORT = $Port.ToString()
$taskLocal = Join-Path $taskProject '.local'
$taskJavaArgs = '-Djdk.net.unixdomain.tmpdir="' + $taskRuntime.SocketTemp + '" -jar "' + $taskJar + '"'
$taskProcess = Start-Process -FilePath $taskRuntime.Java -WindowStyle Hidden -WorkingDirectory $taskProject `
    -ArgumentList $taskJavaArgs `
    -RedirectStandardOutput (Join-Path $taskLocal 'aplicacion.log') `
    -RedirectStandardError (Join-Path $taskLocal 'aplicacion-error.log') -PassThru
[IO.File]::WriteAllText((Join-Path $taskLocal 'aplicacion.pid'), $taskProcess.Id.ToString())
for ($taskAttempt = 0; $taskAttempt -lt 60; $taskAttempt++) {
    Start-Sleep -Milliseconds 500
    $taskProcess.Refresh()
    if ($taskProcess.HasExited) { throw 'La aplicacion se detuvo. Revisa .local/aplicacion.log y aplicacion-error.log.' }
    $taskLog = Get-Content -LiteralPath (Join-Path $taskLocal 'aplicacion.log') -Raw -ErrorAction SilentlyContinue
    if ($taskLog -match 'Started AcademiaWebfluxApplication') {
        # La semilla se ejecuta después del mensaje Started; comprobar respuesta HTTP.
        try {
            Invoke-WebRequest -Uri "http://localhost:$Port/v1/cursos" -UseBasicParsing -TimeoutSec 2 | Out-Null
        } catch {
            if ($_.Exception.Response -and [int]$_.Exception.Response.StatusCode -eq 401) {
                Write-Output "Aplicacion iniciada en http://localhost:$Port. PID: $($taskProcess.Id)."
                Write-Output 'Credenciales privadas: .local/ACCESO.txt. Login: POST /login o POST /v2/login.'
                exit 0
            }
        }
    }
}
throw 'El arranque excedio el tiempo de espera. Revisa los logs de .local.'
