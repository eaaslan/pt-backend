# Check if Docker is running
try {
    $dockerInfo = docker info 2>&1
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Docker is not running"
        exit 1
    }
} catch {
    Write-Host "Docker is not running or not installed"
    exit 1
}

# Check if the specific container is running
$containerId = "97ca767a94ed9d7e4614ba659a260d9914535de0ec4ef79119dab066bfa3af62"
$runningContainers = docker ps --format "{{.ID}}"

if ($runningContainers -notcontains $containerId) {
    Write-Host "Specific container is not running"
    exit 1
}

Write-Host "Specific container is running"
exit 0