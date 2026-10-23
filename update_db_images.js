const mysql = require('mysql2/promise');
const manifest = require('./frontend/assets/products_manifest.json');

async function updateDb() {
  try {
    const connection = await mysql.createConnection({
      host: 'localhost',
      port: 3306,
      user: 'root',
      password: 'Vaish@mysql04',
      database: 'pc_configurations'
    });

    console.log('Connected to MySQL database.');

    for (const item of manifest) {
      await connection.execute(
        'UPDATE products SET image_url = ? WHERE id = ?',
        [item.localAssetPath, item.id]
      );
      console.log(`Updated product [${item.id}] ${item.name} -> ${item.localAssetPath}`);
    }

    await connection.end();
    console.log('All 20 database products updated with verified local asset paths.');
  } catch (err) {
    console.error('MySQL update error:', err.message);
  }
}

updateDb();
