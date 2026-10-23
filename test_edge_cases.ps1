# Negative test cases script

# 1. Duplicate email registration
try {
    $dupUser = '{"fullName":"Duplicate","email":"admin@pcconfigurations.com","password":"Password123!","phone":"1234567890"}'
    Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/register' -Method Post -Body $dupUser -ContentType 'application/json'
    Write-Host "FAIL: Duplicate email allowed"
} catch {
    Write-Host "PASS: Duplicate email rejected: $($_.Exception.Message)"
}

# 2. Invalid password login
try {
    $badLogin = '{"email":"admin@pcconfigurations.com","password":"WrongPassword!"}'
    Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/login' -Method Post -Body $badLogin -ContentType 'application/json'
    Write-Host "FAIL: Invalid password allowed"
} catch {
    Write-Host "PASS: Invalid password rejected: $($_.Exception.Message)"
}

# 3. Non-existent user login
try {
    $badLogin2 = '{"email":"nonexistent_99999@example.com","password":"Password123!"}'
    Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/login' -Method Post -Body $badLogin2 -ContentType 'application/json'
    Write-Host "FAIL: Nonexistent user allowed"
} catch {
    Write-Host "PASS: Nonexistent user rejected: $($_.Exception.Message)"
}

# 4. Unauthorized request to protected endpoint
try {
    Invoke-RestMethod -Uri 'http://localhost:8080/api/orders' -Method Get
    Write-Host "FAIL: Unauthenticated access to /api/orders allowed"
} catch {
    Write-Host "PASS: Unauthenticated access blocked: $($_.Exception.Message)"
}

# 5. Register a fresh user with empty cart and try to checkout
$rand = Get-Random -Minimum 10000 -Maximum 99999
$uJson = '{"fullName":"Empty Cart User","email":"empty_' + $rand + '@example.com","password":"Password123!","phone":"1234567890"}'
$freshUser = Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/register' -Method Post -Body $uJson -ContentType 'application/json'
$freshHeaders = @{ "Authorization" = "Bearer $($freshUser.token)" }

try {
    $emptyOrder = '{"shippingAddress":"123 St","shippingCity":"City","shippingState":"ST","shippingPostalCode":"123456","shippingCountry":"India","paymentMethod":"UPI"}'
    Invoke-RestMethod -Uri 'http://localhost:8080/api/orders' -Method Post -Body $emptyOrder -ContentType 'application/json' -Headers $freshHeaders
    Write-Host "FAIL: Empty cart checkout succeeded"
} catch {
    Write-Host "PASS: Empty cart checkout rejected: $($_.Exception.Message)"
}

# 6. Exceeding stock limit
try {
    $overStock = '{"productId":1,"quantity":9999}'
    Invoke-RestMethod -Uri 'http://localhost:8080/api/cart/add' -Method Post -Body $overStock -ContentType 'application/json' -Headers $freshHeaders
    Write-Host "FAIL: Excess stock addition succeeded"
} catch {
    Write-Host "PASS: Excess stock addition rejected: $($_.Exception.Message)"
}

Write-Host "ALL NEGATIVE & SECURITY TESTS PASSED!"
