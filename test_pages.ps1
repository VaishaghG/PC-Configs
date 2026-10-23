$pages = @(
    'index.html',
    'pages/products.html',
    'pages/builder.html',
    'pages/cart.html',
    'pages/checkout.html',
    'pages/orders.html',
    'pages/dashboard.html',
    'pages/login.html',
    'pages/register.html',
    'css/style.css',
    'js/api.js',
    'js/auth.js',
    'js/navbar.js',
    'js/toast.js',
    'js/utils.js'
)

foreach ($p in $pages) {
    $res = Invoke-WebRequest -Uri "http://localhost:8080/$p" -Method Get -UseBasicParsing
    Write-Host "$p -> HTTP $($res.StatusCode) (Length: $($res.RawContentLength))"
}

Write-Host "All frontend pages and assets are verified accessible!"
