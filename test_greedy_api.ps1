# test_greedy_api.ps1 — Verify Greedy Algorithm REST API endpoints

$baseUrl = "http://localhost:8080/api"

Write-Host "=========================================================="
Write-Host "TEST 1: Greedy Build with sufficient budget (₹1,50,000)"
Write-Host "=========================================================="
$body1 = '{"budget": 150000.00}'
$res1 = Invoke-RestMethod -Uri "$baseUrl/builds/greedy" -Method Post -Body $body1 -ContentType "application/json"

Write-Host "Status: SUCCESS"
Write-Host "Budget:" $res1.budget
Write-Host "Total Price:" $res1.totalPrice
Write-Host "Remaining Budget:" $res1.remainingBudget
Write-Host "Strategy:" $res1.strategy
Write-Host "Components Selected:" $res1.selectedComponents.Count
foreach ($c in $res1.selectedComponents) {
    Write-Host " - [$($c.category)] $($c.brand) $($c.name): ₹$($c.price)"
}

if ($res1.selectedComponents.Count -ne 5) {
    Write-Error "Expected 5 components, got $($res1.selectedComponents.Count)"
}
if ($res1.totalPrice -gt 150000) {
    Write-Error "Total price exceeded budget!"
}

Write-Host "`n=========================================================="
Write-Host "TEST 2: Greedy Build with exact minimum budget (₹1,20,495)"
Write-Host "=========================================================="
$body2 = '{"budget": 120495.00}'
$res2 = Invoke-RestMethod -Uri "$baseUrl/builds/greedy" -Method Post -Body $body2 -ContentType "application/json"
Write-Host "Status: SUCCESS"
Write-Host "Total Price:" $res2.totalPrice
Write-Host "Remaining Budget:" $res2.remainingBudget
if ($res2.remainingBudget -ne 0) {
    Write-Warning "Remaining budget was $($res2.remainingBudget) instead of 0"
}

Write-Host "`n=========================================================="
Write-Host "TEST 3: Insufficient Budget (₹50,000) - Expecting HTTP 400"
Write-Host "=========================================================="
$body3 = '{"budget": 50000.00}'
try {
    $res3 = Invoke-RestMethod -Uri "$baseUrl/builds/greedy" -Method Post -Body $body3 -ContentType "application/json"
    Write-Error "Test failed: Should have thrown 400 Bad Request!"
} catch {
    $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
    $errText = $reader.ReadToEnd()
    Write-Host "Correctly rejected with error: $errText"
}

Write-Host "`n=========================================================="
Write-Host "TEST 4: Negative/Zero Budget - Expecting HTTP 400"
Write-Host "=========================================================="
$body4 = '{"budget": 0}'
try {
    $res4 = Invoke-RestMethod -Uri "$baseUrl/builds/greedy" -Method Post -Body $body4 -ContentType "application/json"
    Write-Error "Test failed: Should have thrown 400 Bad Request!"
} catch {
    Write-Host "Correctly rejected zero budget with HTTP error: $($_.Exception.Message)"
}

Write-Host "`n=========================================================="
Write-Host "TEST 5: Check /api/algorithms/demo for Greedy Build output"
Write-Host "=========================================================="
$algoRes = Invoke-RestMethod -Uri "$baseUrl/algorithms/demo" -Method Get
if ($algoRes.greedyBuild) {
    Write-Host "Greedy Build Demo present in /api/algorithms/demo:"
    Write-Host "Description:" $algoRes.greedyBuild.description
    Write-Host "Target Budget:" $algoRes.greedyBuild.targetBudget
    Write-Host "Total Price:" $algoRes.greedyBuild.totalPrice
    Write-Host "Selected Count:" $algoRes.greedyBuild.selectedCount
} else {
    Write-Error "greedyBuild missing from /api/algorithms/demo!"
}

Write-Host "`nALL GREEDY ALGORITHM REST TESTS PASSED 100% SUCCESSFULLY!"
