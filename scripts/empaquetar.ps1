$ErrorActionPreference = 'Stop'
$taskProject = (Get-Item -LiteralPath (Split-Path -Parent $PSScriptRoot)).FullName
$taskDist = Join-Path $taskProject 'dist'
New-Item -ItemType Directory -Path $taskDist -Force | Out-Null
$taskZipPath = Join-Path $taskDist 'academia-webflux-entrega.zip'
# Lista permitida: no recorrer target, .local, IDE ni materiales del profesor.
$taskFiles = @()
foreach ($taskName in @('pom.xml','mvnw','mvnw.cmd','.gitignore','.gitattributes','lombok.config','README.md','CLAUDE.md','.env.example','requests.http')) {
    $taskFile = Join-Path $taskProject $taskName
    if (!(Test-Path -LiteralPath $taskFile -PathType Leaf)) { throw "Falta el archivo de entrega: $taskName" }
    $taskFiles += Get-Item -LiteralPath $taskFile
}
foreach ($taskDirectory in @('src','.agents','.mvn','docs','scripts','testdata')) {
    $taskFiles += @(Get-ChildItem -LiteralPath (Join-Path $taskProject $taskDirectory) -Recurse -File |
        Where-Object { $_.Extension -notin @('.class','.jar','.log') })
}
$taskSecretValues = @()
$taskPrivateConfig = Join-Path $taskProject '.local\application-local.yaml'
if (Test-Path -LiteralPath $taskPrivateConfig) {
    $taskPrivateText = Get-Content -LiteralPath $taskPrivateConfig -Raw -Encoding UTF8
    foreach ($taskMatch in [regex]::Matches($taskPrivateText, '\$\{(?:JWT_SECRET|BOOTSTRAP_PASSWORD):([^}]+)\}')) {
        $taskSecretValues += $taskMatch.Groups[1].Value
    }
}
foreach ($taskFile in $taskFiles) {
    $taskRelative = $taskFile.FullName.Substring($taskProject.Length + 1).Replace('\','/')
    if ($taskRelative -match '(^|/)(\.local|\.idea|target|dist|\.git)(/|$)' -or $taskRelative -match '(^|/)application-local\.yaml$') {
        throw "Ruta privada en entrega: $taskRelative"
    }
    $taskContent = [IO.File]::ReadAllText($taskFile.FullName)
    foreach ($taskSecretValue in $taskSecretValues) {
        if ($taskSecretValue.Length -ge 12 -and $taskContent.Contains($taskSecretValue)) {
            throw "Un secreto local aparece en $taskRelative. No se generara el ZIP."
        }
    }
}
Add-Type -AssemblyName System.IO.Compression
Add-Type -AssemblyName System.IO.Compression.FileSystem
$taskStream = [IO.File]::Open($taskZipPath, [IO.FileMode]::Create)
try {
    $taskArchive = New-Object IO.Compression.ZipArchive($taskStream, [IO.Compression.ZipArchiveMode]::Create, $true)
    try {
        foreach ($taskFile in $taskFiles) {
            $taskEntry = 'academia-webflux/' + $taskFile.FullName.Substring($taskProject.Length + 1).Replace('\','/')
            [void][IO.Compression.ZipFileExtensions]::CreateEntryFromFile($taskArchive, $taskFile.FullName, $taskEntry, [IO.Compression.CompressionLevel]::Optimal)
        }
    } finally { $taskArchive.Dispose() }
} finally { $taskStream.Dispose() }
$taskReadArchive = [IO.Compression.ZipFile]::OpenRead($taskZipPath)
try {
    if ($taskReadArchive.Entries.Count -ne $taskFiles.Count) { throw 'El ZIP no contiene todos los archivos seleccionados.' }
    foreach ($taskEntry in $taskReadArchive.Entries) {
        if ($taskEntry.FullName -match '(^|/)(\.local|\.idea|target|dist|\.git)(/|$)') { throw 'El ZIP contiene una ruta excluida.' }
    }
} finally { $taskReadArchive.Dispose() }
$taskHash = (Get-FileHash -LiteralPath $taskZipPath -Algorithm SHA256).Hash
[IO.File]::WriteAllText((Join-Path $taskDist 'SHA256SUMS.txt'), ($taskHash.ToLowerInvariant() + '  academia-webflux-entrega.zip' + [Environment]::NewLine))
Write-Output "ZIP verificado: $taskZipPath. Archivos: $($taskFiles.Count). Sin secretos locales."
