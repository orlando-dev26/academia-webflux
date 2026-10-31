param([string]$BaseUrl = 'http://localhost:8080', [switch]$CargarEjemplo)
$ErrorActionPreference = 'Stop'
$taskProject = Split-Path -Parent $PSScriptRoot
if (([Uri]$BaseUrl).Host -notin @('localhost', '127.0.0.1', '::1')) { throw 'La comprobacion solo usa una API local.' }
$taskAccess = Get-Content -LiteralPath (Join-Path $taskProject '.local\ACCESO.txt') -Raw -Encoding UTF8
$taskUsername = if ($env:BOOTSTRAP_USERNAME) { $env:BOOTSTRAP_USERNAME } else { [regex]::Match($taskAccess,'(?m)^Usuario:\s*(.+)$').Groups[1].Value.Trim() }
$taskPassword = if ($env:BOOTSTRAP_PASSWORD) { $env:BOOTSTRAP_PASSWORD } else { [regex]::Match($taskAccess,'(?m)^Password:\s*(.+)$').Groups[1].Value.Trim() }
$taskLoginBody = @{ username = $taskUsername; password = $taskPassword } | ConvertTo-Json
$taskChecks = @()
foreach ($taskVersion in @('v1', 'v2')) {
    $taskDenied = $false
    try { Invoke-WebRequest "$BaseUrl/$taskVersion/cursos" -UseBasicParsing -TimeoutSec 10 | Out-Null }
    catch {
        if ($_.Exception.Response -and [int]$_.Exception.Response.StatusCode -eq 401) { $taskDenied = $true }
        else { throw }
    }
    if (!$taskDenied) { throw "Se esperaba 401 sin token en $taskVersion." }
    $taskChecks += "$taskVersion sin JWT: 401"
}
$taskAuth = $null
foreach ($taskLoginPath in @('/login', '/v2/login')) {
    $taskAuth = Invoke-RestMethod -Method Post -Uri "$BaseUrl$taskLoginPath" -ContentType 'application/json; charset=utf-8' -Body $taskLoginBody
    if (!$taskAuth.access_token) { throw 'El login no devolvio access_token.' }
    $taskChecks += "Login ${taskLoginPath}: correcto"
}
$taskHeaders = @{ Authorization = 'Bearer ' + $taskAuth.access_token }
if ($CargarEjemplo) { & (Join-Path $PSScriptRoot 'cargar-ejemplo.ps1') -BaseUrl $BaseUrl }
foreach ($taskVersion in @('v1', 'v2')) {
    foreach ($taskOrder in @('asc','desc')) {
        $taskStudentResponse = Invoke-RestMethod -Uri "$BaseUrl/$taskVersion/estudiantes/edad/$taskOrder" -Headers $taskHeaders
        $taskStudents = @($taskStudentResponse)
        if ($CargarEjemplo -and $taskStudents.Count -lt 4) { throw 'No aparecen los cuatro estudiantes de ejemplo.' }
        for ($taskIndex = 1; $taskIndex -lt $taskStudents.Count; $taskIndex++) {
            $taskPrevious = [int]$taskStudents[$taskIndex - 1].edad
            $taskNext = [int]$taskStudents[$taskIndex].edad
            if (($taskOrder -eq 'asc' -and $taskPrevious -gt $taskNext) -or
                ($taskOrder -eq 'desc' -and $taskPrevious -lt $taskNext)) { throw 'La edad no esta ordenada.' }
        }
        $taskChecks += "$taskVersion edad $taskOrder`: 200 y orden correcto ($($taskStudents.Count) registros)"
    }
    if ($CargarEjemplo) {
        $taskIds = Get-Content -LiteralPath (Join-Path $taskProject '.local\datos-ejemplo.json') -Raw -Encoding UTF8 | ConvertFrom-Json
        $taskMatricula = Invoke-RestMethod -Uri "$BaseUrl/$taskVersion/matriculas/$($taskIds.MATRICULA_1)" -Headers $taskHeaders
        if ($taskMatricula.estudiante.id -ne $taskIds.ESTUDIANTE_1 -or $taskMatricula.cursos.Count -ne 2 -or
            !$taskMatricula.estudiante.nombres -or !$taskMatricula.cursos[0].nombre) { throw 'Matricula incompleta.' }
        $taskChecks += "$taskVersion matricula: 200 con estudiante y dos cursos completos"
    }
}
$taskReport = @{ fecha = (Get-Date -Format o); baseUrl = $BaseUrl; ejemploInsertado = [bool]$CargarEjemplo; comprobaciones = $taskChecks }
$taskReport | ConvertTo-Json -Depth 5 | Set-Content -LiteralPath (Join-Path $taskProject '.local\comprobacion-api.json') -Encoding UTF8
$taskChecks | Write-Output
Write-Output 'Resultado guardado en .local/comprobacion-api.json, sin tokens ni passwords.'
