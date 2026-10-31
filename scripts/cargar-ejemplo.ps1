param([string]$BaseUrl = 'http://localhost:8080')
$ErrorActionPreference = 'Stop'
$taskProject = Split-Path -Parent $PSScriptRoot
$taskUri = [Uri]$BaseUrl
if ($taskUri.Host -notin @('localhost','127.0.0.1','::1')) { throw 'Este ejemplo solo escribe en la API local.' }
$taskAccess = Get-Content -LiteralPath (Join-Path $taskProject '.local\ACCESO.txt') -Raw
$taskUsername = if ($env:BOOTSTRAP_USERNAME) { $env:BOOTSTRAP_USERNAME } else { [regex]::Match($taskAccess,'(?m)^Usuario:\s*(.+)$').Groups[1].Value.Trim() }
$taskPassword = if ($env:BOOTSTRAP_PASSWORD) { $env:BOOTSTRAP_PASSWORD } else { [regex]::Match($taskAccess,'(?m)^Password:\s*(.+)$').Groups[1].Value.Trim() }
$taskLogin = @{ username = $taskUsername; password = $taskPassword } | ConvertTo-Json
$taskAuth = Invoke-RestMethod -Method Post -Uri "$BaseUrl/login" -ContentType 'application/json; charset=utf-8' -Body $taskLogin
$taskHeaders = @{ Authorization = 'Bearer ' + $taskAuth.access_token }
$taskIds = @{}
$taskStudentIndex = 0
foreach ($taskStudent in (Get-Content -LiteralPath (Join-Path $taskProject 'testdata\estudiantes.json') -Raw | ConvertFrom-Json)) {
    $taskStudentIndex++
    $taskResult = Invoke-RestMethod -Method Post -Uri "$BaseUrl/v1/estudiantes" -Headers $taskHeaders -ContentType 'application/json; charset=utf-8' -Body ($taskStudent | ConvertTo-Json)
    $taskIds['ESTUDIANTE_' + $taskStudentIndex] = $taskResult.id
}
$taskCourseIndex = 0
foreach ($taskCourse in (Get-Content -LiteralPath (Join-Path $taskProject 'testdata\cursos.json') -Raw | ConvertFrom-Json)) {
    $taskCourseIndex++
    $taskResult = Invoke-RestMethod -Method Post -Uri "$BaseUrl/v2/cursos" -Headers $taskHeaders -ContentType 'application/json; charset=utf-8' -Body ($taskCourse | ConvertTo-Json)
    $taskIds['CURSO_' + $taskCourseIndex] = $taskResult.id
}
$taskMatricula = Get-Content -LiteralPath (Join-Path $taskProject 'testdata\matricula.json') -Raw | ConvertFrom-Json
$taskMatricula.estudiante.id = $taskIds[$taskMatricula.estudiante.id]
foreach ($taskReference in $taskMatricula.cursos) { $taskReference.id = $taskIds[$taskReference.id] }
$taskResult = Invoke-RestMethod -Method Post -Uri "$BaseUrl/v2/matriculas" -Headers $taskHeaders -ContentType 'application/json; charset=utf-8' -Body ($taskMatricula | ConvertTo-Json -Depth 6)
$taskIds['MATRICULA_1'] = $taskResult.id
$taskIds | ConvertTo-Json | Set-Content -LiteralPath (Join-Path $taskProject '.local\datos-ejemplo.json') -Encoding UTF8
Write-Output "Creados $taskStudentIndex estudiantes, $taskCourseIndex cursos y una matricula. IDs en .local/datos-ejemplo.json."
