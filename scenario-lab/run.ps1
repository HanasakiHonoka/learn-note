param(
    [ValidateSet('verify', 'test', 'package', 'run')][string]$Action = 'verify',
    [ValidateSet('', 'feed', 'seckill', 'payment', 'qrlogin', 'shortlink', 'orderexpiry', 'redpacket', 'im', 'ranking', 'scheduler', 'cache')][string]$Module = '',
    [string]$JdkHome = 'D:\DevKit\Java\jdk-17.0.6+10',
    [int]$Port = 8080,
    [switch]$Offline
)
$ErrorActionPreference = 'Stop'
if (!(Test-Path -LiteralPath "$JdkHome\bin\java.exe")) { throw "JDK missing: $JdkHome. Pass -JdkHome." }
if ($Action -eq 'run' -and !$Module) { throw 'Specify -Module when running an application.' }
$previousJava = $env:JAVA_HOME
$previousPath = $env:Path
try {
    $env:JAVA_HOME = $JdkHome
    $env:Path = "$JdkHome\bin;$previousPath"
    $pom = Join-Path $PSScriptRoot 'pom.xml'
    if ($Module) { $pom = Join-Path $PSScriptRoot "$Module\pom.xml" }
    $arguments = @('-f', $pom, '-Dstyle.color=never')
    if ($Offline) { $arguments += '-o' }
    if ($Action -eq 'run') {
        $arguments += @('spring-boot:run', "-Dspring-boot.run.arguments=--server.port=$Port")
    } else { $arguments += $Action }
    & mvn.cmd @arguments
    if ($LASTEXITCODE -ne 0) { throw "Maven failed: $LASTEXITCODE" }
} finally {
    $env:JAVA_HOME = $previousJava
    $env:Path = $previousPath
}
