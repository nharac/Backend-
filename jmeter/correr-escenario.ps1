<#
  Corre un escenario de carga/estrés de SAESU con JMeter en modo consola (sin interfaz),
  guarda los resultados, genera el reporte HTML y registra el consumo del contenedor (docker stats).

  Uso (desde la carpeta backend\jmeter):
    .\correr-escenario.ps1 01-login-concurrente
    .\correr-escenario.ps1 02-consulta-masiva -Usuarios 80
    .\correr-escenario.ps1 05-saturacion-login          # corre las 4 fases: 50, 100, 200 y 300 usuarios
    .\correr-escenario.ps1 08-resistencia -Duracion 900 # 15 minutos
    .\correr-escenario.ps1 todos                        # 01 a 04 (carga) seguidos

  Requisitos: JMeter 5.6.3 (variable JMETER_HOME o jmeter.bat en el PATH), el backend en http://localhost:8080
  y el archivo usuarios.csv con las cuentas de prueba (correo,clave).
#>
param(
  [Parameter(Mandatory = $true)][string]$Escenario,
  [int]$Usuarios = 0,
  [int]$Rampa = 0,
  [int]$Vueltas = 0,
  [int]$Duracion = 0,
  [string]$Servidor = "localhost",
  [int]$Puerto = 8080,
  [string]$Protocolo = "http"
)
$ErrorActionPreference = "Stop"
$aqui = Split-Path -Parent $MyInvocation.MyCommand.Path

# --- JMeter ---
$jmeter = $null
if ($env:JMETER_HOME) { $jmeter = Join-Path $env:JMETER_HOME "bin\jmeter.bat" }
if (-not $jmeter -or -not (Test-Path $jmeter)) {
  $c = Get-Command jmeter.bat -ErrorAction SilentlyContinue
  if ($c) { $jmeter = $c.Source }
}
if (-not $jmeter -or -not (Test-Path $jmeter)) {
  Write-Error "No encuentro JMeter. Define JMETER_HOME (ej: `$env:JMETER_HOME='C:\apache-jmeter-5.6.3') o agrega su carpeta bin al PATH."
}
if (-not (Test-Path (Join-Path $aqui "usuarios.csv"))) {
  Write-Error "Falta usuarios.csv. Copia usuarios.ejemplo.csv como usuarios.csv y pon las cuentas de prueba."
}

function Correr([string]$nombre, [hashtable]$props, [string]$etiqueta) {
  $plan = Join-Path $aqui "escenarios\$nombre.jmx"
  if (-not (Test-Path $plan)) { Write-Error "No existe el escenario $nombre" }
  $sello = Get-Date -Format "yyyyMMdd-HHmmss"
  $dir = Join-Path $aqui "resultados\$nombre$etiqueta-$sello"
  New-Item -ItemType Directory -Force -Path $dir | Out-Null

  $argumentos = @("-n", "-t", $plan, "-l", (Join-Path $dir "resultados.jtl"), "-e", "-o", (Join-Path $dir "reporte"),
            "-j", (Join-Path $dir "jmeter.log"),
            "-Jhost=$Servidor", "-Jpuerto=$Puerto", "-Jprotocolo=$Protocolo",
            "-Jcuentas=$(Join-Path $aqui 'usuarios.csv')",
            "-Jjmeter.save.saveservice.output_format=csv")
  foreach ($k in $props.Keys) { $argumentos += "-J$k=$($props[$k])" }

  # Consumo del backend durante la prueba (CPU y memoria cada 2 s)
  $stats = Join-Path $dir "docker-stats.csv"
  "fecha,contenedor,cpu,memoria,mem_porcentaje" | Out-File -Encoding utf8 $stats
  $job = Start-Job -ScriptBlock {
    param($archivo)
    while ($true) {
      $f = Get-Date -Format "HH:mm:ss"
      docker stats --no-stream --format "{{.Name}},{{.CPUPerc}},{{.MemUsage}},{{.MemPerc}}" saesu-backend 2>$null |
        ForEach-Object { "$f,$_" } | Out-File -Append -Encoding utf8 $archivo
      Start-Sleep -Seconds 2
    }
  } -ArgumentList $stats

  Write-Host "`n=== $nombre$etiqueta ===" -ForegroundColor Cyan
  try { & $jmeter @argumentos } finally { Stop-Job $job | Out-Null; Remove-Job $job | Out-Null }

  Write-Host "Resultados: $dir" -ForegroundColor Green
  Write-Host "Reporte:    $(Join-Path $dir 'reporte\index.html')" -ForegroundColor Green
  Start-Process (Join-Path $dir "reporte\index.html")
}

$props = @{}
if ($Usuarios -gt 0) { $props["usuarios"] = $Usuarios }
if ($Rampa -gt 0)    { $props["rampa"] = $Rampa }
if ($Vueltas -gt 0)  { $props["vueltas"] = $Vueltas }
if ($Duracion -gt 0) { $props["duracion"] = $Duracion }

switch ($Escenario) {
  "05-saturacion-login" {
    foreach ($n in 50, 100, 200, 300) {
      Correr "05-saturacion-login" @{ usuarios = $n; rampa = 10; vueltas = 3 } "-fase-$n"
      Start-Sleep -Seconds 30   # deja que el backend se recupere entre fases
    }
  }
  "todos" {
    foreach ($e in "01-login-concurrente", "02-consulta-masiva", "03-registro-concurrente", "04-flujo-completo") {
      Correr $e @{} ""
      Start-Sleep -Seconds 20
    }
  }
  default { Correr $Escenario $props "" }
}
