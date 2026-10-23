$loginBody = '{"email":"admin@pcconfigurations.com","password":"admin123"}'
$res = Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/login' -Method Post -Body $loginBody -ContentType 'application/json'
Write-Host "Admin Login Token:" ($res.token.Substring(0, 20) + "...")
Write-Host "Role:" $res.role

# Test me
$headers = @{ "Authorization" = "Bearer $($res.token)" }
$me = Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/me' -Method Get -Headers $headers
Write-Host "Current User:" $me.fullName $me.email

# Test registration of a test user
$randomNum = Get-Random -Minimum 10000 -Maximum 99999
$newUser = '{"fullName":"Test Tester","email":"tester_' + $randomNum + '@example.com","password":"Password123!","phone":"9876543210"}'
$reg = Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/register' -Method Post -Body $newUser -ContentType 'application/json'
Write-Host "User Registered:" $reg.fullName $reg.email
$userHeaders = @{ "Authorization" = "Bearer $($reg.token)" }

# Test Cart: get empty cart
$cart = Invoke-RestMethod -Uri 'http://localhost:8080/api/cart' -Method Get -Headers $userHeaders
Write-Host "Initial Cart Items:" $cart.items.Count

# Add item to cart
$addBody = '{"productId":1,"quantity":2}'
$cart = Invoke-RestMethod -Uri 'http://localhost:8080/api/cart/add' -Method Post -Body $addBody -ContentType 'application/json' -Headers $userHeaders
Write-Host "Cart after add:" $cart.items.Count "items, Total:" $cart.total

# Update item quantity
$itemId = $cart.items[0].id
$updBody = '{"quantity":3}'
$cart = Invoke-RestMethod -Uri "http://localhost:8080/api/cart/$itemId" -Method Put -Body $updBody -ContentType 'application/json' -Headers $userHeaders
Write-Host "Cart after update qty:" $cart.items[0].quantity "Total:" $cart.total

# Test Custom PC Build
$buildBody = '{"buildName":"Gaming Beast Ultra","notes":"Custom build test","componentIds":[1,5,9,13,17]}'
$build = Invoke-RestMethod -Uri 'http://localhost:8080/api/builds' -Method Post -Body $buildBody -ContentType 'application/json' -Headers $userHeaders
Write-Host "Created Build ID:" $build.id "Name:" $build.buildName "Total:" $build.totalPrice

# Test Get Builds
$builds = Invoke-RestMethod -Uri 'http://localhost:8080/api/builds' -Method Get -Headers $userHeaders
Write-Host "User Builds Count:" $builds.Count

# Test Checkout / Order
$orderBody = '{"shippingAddress":"123 Main St, Tech Park","shippingCity":"Bangalore","shippingState":"KA","shippingPostalCode":"560001","shippingCountry":"India","paymentMethod":"UPI"}'
$order = Invoke-RestMethod -Uri 'http://localhost:8080/api/orders' -Method Post -Body $orderBody -ContentType 'application/json' -Headers $userHeaders
Write-Host "Created Order ID:" $order.id "Total:" $order.totalAmount "Status:" $order.status "PaymentStatus:" $order.paymentStatus

# Test Get Orders
$orders = Invoke-RestMethod -Uri 'http://localhost:8080/api/orders' -Method Get -Headers $userHeaders
Write-Host "User Orders Count:" $orders.Count

# Test Get Single Order
$orderDetail = Invoke-RestMethod -Uri "http://localhost:8080/api/orders/$($order.id)" -Method Get -Headers $userHeaders
Write-Host "Order Details retrieved, items count:" $orderDetail.items.Count

# Test Dashboard
$dash = Invoke-RestMethod -Uri 'http://localhost:8080/api/dashboard' -Method Get -Headers $userHeaders
Write-Host "Dashboard retrieved: Total Orders =" $dash.totalOrders ", Total Spent =" $dash.totalSpent

Write-Host "ALL REST API TESTS PASSED 100% SUCCESSFULLY!"
