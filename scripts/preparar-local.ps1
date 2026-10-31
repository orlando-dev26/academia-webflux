$ErrorActionPreference = 'Stop'
$taskProject = Split-Path -Parent $PSScriptRoot
$taskLocal = Join-Path $taskProject '.local'
New-Item -ItemType Directory -Path $taskLocal -Force | Out-Null
$taskConfig = Join-Path $taskLocal 'application-local.yaml'
if (Test-Path -LiteralPath $taskConfig) {
    Write-Output 'Se conserva la configuracion privada existente en .local.'
    exit 0
}
$taskRandom = [System.Security.Cryptography.RandomNumberGenerator]::Create()
try {
    $taskKeyBytes = New-Object byte[] 48
    $taskPasswordBytes = New-Object byte[] 18
    $taskRandom.GetBytes($taskKeyBytes)
    $taskRandom.GetBytes($taskPasswordBytes)
} finally { $taskRandom.Dispose() }
$taskSecret = [Convert]::ToBase64String($taskKeyBytes)
$taskPassword = [Convert]::ToBase64String($taskPasswordBytes).Replace('+','-').Replace('/','_')
$taskYaml = @(
    'security:', '  jwt:', ('    secret: ${JWT_SECRET:' + $taskSecret + '}'),
    'app:', '  bootstrap:', '    enabled: ${BOOTSTRAP_ENABLED:true}',
    '    username: ${BOOTSTRAP_USERNAME:admin}', ('    password: ${BOOTSTRAP_PASSWORD:' + $taskPassword + '}')
)
[IO.File]::WriteAllLines($taskConfig, $taskYaml, (New-Object Text.UTF8Encoding $false))
[IO.File]::WriteAllLines((Join-Path $taskLocal 'ACCESO.txt'), @(
    'Configuracion privada local; no publicar.', 'Usuario: admin',
    ('Password: ' + $taskPassword), 'Login: POST http://localhost:8080/login'
), (New-Object Text.UTF8Encoding $false))
Write-Output 'Configuracion creada. Consulta el usuario y password en .local/ACCESO.txt.'
