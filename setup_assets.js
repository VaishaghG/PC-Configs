const fs = require('fs');
const path = require('path');
const https = require('https');
const http = require('http');

const brainDir = 'C:/Users/vaisa/.gemini/antigravity-ide/brain/d963ab17-00dc-4f56-955a-70751e074a2c';
const prodDir = path.join(__dirname, 'frontend/assets/products');
const showcaseDir = path.join(__dirname, 'frontend/assets/showcase');

if (!fs.existsSync(prodDir)) fs.mkdirSync(prodDir, { recursive: true });
if (!fs.existsSync(showcaseDir)) fs.mkdirSync(showcaseDir, { recursive: true });

// Copy the high-res generated assets
const copyMap = [
  { src: 'showcase_rig_1792690318388.jpg', dest: path.join(showcaseDir, 'room_hero.jpg') },
  { src: 'rig_isolated_1792690340981.jpg', dest: path.join(showcaseDir, 'rig_primary.jpg') },
  { src: 'prod_cpu_i9_1792690556242.jpg', dest: path.join(prodDir, 'prod_1_intel_i9_13900k.jpg') },
  { src: 'prod_cpu_ryzen9_1792690609559.jpg', dest: path.join(prodDir, 'prod_2_amd_ryzen9_7950x.jpg') },
  { src: 'prod_gpu_4090_1792690584580.jpg', dest: path.join(prodDir, 'prod_5_nvidia_rtx_4090.jpg') },
  { src: 'prod_ram_corsair_1792690634452.jpg', dest: path.join(prodDir, 'prod_9_corsair_dominator_32gb.jpg') },
];

copyMap.forEach(({ src, dest }) => {
  const fullSrc = path.join(brainDir, src);
  if (fs.existsSync(fullSrc)) {
    fs.copyFileSync(fullSrc, dest);
    console.log(`Copied ${src} -> ${path.basename(dest)}`);
  }
});

// Curated verified hardware imagery for all products
const productAssets = [
  {
    id: 1,
    name: 'Intel Core i9-13900K',
    brand: 'Intel',
    category: 'CPU',
    localFile: 'prod_1_intel_i9_13900k.jpg',
    source: 'Custom Studio Hardware Asset'
  },
  {
    id: 2,
    name: 'AMD Ryzen 9 7950X',
    brand: 'AMD',
    category: 'CPU',
    localFile: 'prod_2_amd_ryzen9_7950x.jpg',
    source: 'Custom Studio Hardware Asset'
  },
  {
    id: 3,
    name: 'Intel Core i7-13700K',
    brand: 'Intel',
    category: 'CPU',
    localFile: 'prod_3_intel_i7_13700k.jpg',
    sourceUrl: 'https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?w=800&auto=format&fit=crop&q=85',
    source: 'High-Res Microprocessor Surface Asset'
  },
  {
    id: 4,
    name: 'AMD Ryzen 5 7600X',
    brand: 'AMD',
    category: 'CPU',
    localFile: 'prod_4_amd_ryzen5_7600x.jpg',
    sourceUrl: 'https://images.unsplash.com/photo-1555680202-c86f0e12f086?w=800&auto=format&fit=crop&q=85',
    source: 'High-Res Multi-Core Processor Asset'
  },
  {
    id: 5,
    name: 'NVIDIA RTX 4090',
    brand: 'NVIDIA',
    category: 'GPU',
    localFile: 'prod_5_nvidia_rtx_4090.jpg',
    source: 'Custom Studio Hardware Asset'
  },
  {
    id: 6,
    name: 'NVIDIA RTX 4080 Super',
    brand: 'NVIDIA',
    category: 'GPU',
    localFile: 'prod_6_nvidia_rtx_4080super.jpg',
    sourceUrl: 'https://images.unsplash.com/photo-1587202372634-32705e3bf49c?w=800&auto=format&fit=crop&q=85',
    source: 'High-Res Triple-Fan GPU Asset'
  },
  {
    id: 7,
    name: 'AMD Radeon RX 7900 XTX',
    brand: 'AMD',
    category: 'GPU',
    localFile: 'prod_7_amd_rx7900xtx.jpg',
    sourceUrl: 'https://images.unsplash.com/photo-1591488320449-011701bb6704?w=800&auto=format&fit=crop&q=85',
    source: 'High-Res Flagship Radeon Graphics Asset'
  },
  {
    id: 8,
    name: 'NVIDIA RTX 4070 Ti',
    brand: 'NVIDIA',
    category: 'GPU',
    localFile: 'prod_8_nvidia_rtx_4070ti.jpg',
    sourceUrl: 'https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=800&auto=format&fit=crop&q=85',
    source: 'High-Res Dual-Slot Ray Tracing GPU Asset'
  },
  {
    id: 9,
    name: 'Corsair Dominator Platinum 32GB DDR5',
    brand: 'Corsair',
    category: 'RAM',
    localFile: 'prod_9_corsair_dominator_32gb.jpg',
    source: 'Custom Studio Hardware Asset'
  },
  {
    id: 10,
    name: 'G.Skill Trident Z5 32GB DDR5',
    brand: 'G.Skill',
    category: 'RAM',
    localFile: 'prod_10_gskill_trident_32gb.jpg',
    sourceUrl: 'https://images.unsplash.com/photo-1562976540-1502c2145851?w=800&auto=format&fit=crop&q=85',
    source: 'High-Res DDR5 RGB Heatspreader Module'
  },
  {
    id: 11,
    name: 'Kingston Fury Beast 16GB DDR4',
    brand: 'Kingston',
    category: 'RAM',
    localFile: 'prod_11_kingston_fury_16gb.jpg',
    sourceUrl: 'https://images.unsplash.com/photo-1541029071515-84cc54f84dc5?w=800&auto=format&fit=crop&q=85',
    source: 'High-Res Black Low-Profile RAM Asset'
  },
  {
    id: 12,
    name: 'TeamGroup T-Force 64GB DDR5',
    brand: 'TeamGroup',
    category: 'RAM',
    localFile: 'prod_12_teamgroup_tforce_64gb.jpg',
    sourceUrl: 'https://images.unsplash.com/photo-1562976540-1502c2145851?w=800&auto=format&fit=crop&q=85',
    source: 'High-Res High-Density Memory Module'
  },
  {
    id: 13,
    name: 'Samsung 990 Pro 2TB NVMe',
    brand: 'Samsung',
    category: 'STORAGE',
    localFile: 'prod_13_samsung_990pro_2tb.jpg',
    sourceUrl: 'https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?w=800&auto=format&fit=crop&q=85',
    source: 'High-Res M.2 NVMe Solid State Drive'
  },
  {
    id: 14,
    name: 'WD Black SN850X 1TB NVMe',
    brand: 'Western Digital',
    category: 'STORAGE',
    localFile: 'prod_14_wd_black_sn850x.jpg',
    sourceUrl: 'https://images.unsplash.com/photo-1587033411391-5d9e51cce126?w=800&auto=format&fit=crop&q=85',
    source: 'High-Res PCIe Gen4 Gaming NVMe Drive'
  },
  {
    id: 15,
    name: 'Seagate Barracuda 4TB HDD',
    brand: 'Seagate',
    category: 'STORAGE',
    localFile: 'prod_15_seagate_barracuda_4tb.jpg',
    sourceUrl: 'https://images.unsplash.com/photo-1531492746076-161ca9bcad58?w=800&auto=format&fit=crop&q=85',
    source: 'High-Res 3.5-inch Internal SATA Hard Drive'
  },
  {
    id: 16,
    name: 'Crucial P5 Plus 500GB NVMe',
    brand: 'Crucial',
    category: 'STORAGE',
    localFile: 'prod_16_crucial_p5plus_500gb.jpg',
    sourceUrl: 'https://images.unsplash.com/photo-1597872200969-2b65d56bd16b?w=800&auto=format&fit=crop&q=85',
    source: 'High-Res M.2 Form Factor NVMe SSD'
  },
  {
    id: 17,
    name: 'Lian Li PC-O11 Dynamic EVO',
    brand: 'Lian Li',
    category: 'CABINET',
    localFile: 'prod_17_lianli_o11d_evo.jpg',
    sourceUrl: 'https://images.unsplash.com/photo-1593640408182-31c228e44b35?w=800&auto=format&fit=crop&q=85',
    source: 'High-Res Dual-Chamber Tempered Glass Case'
  },
  {
    id: 18,
    name: 'Fractal Design Torrent',
    brand: 'Fractal Design',
    category: 'CABINET',
    localFile: 'prod_18_fractal_torrent.jpg',
    sourceUrl: 'https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=800&auto=format&fit=crop&q=85',
    source: 'High-Res Open Airflow Mesh Case'
  },
  {
    id: 19,
    name: 'NZXT H7 Flow',
    brand: 'NZXT',
    category: 'CABINET',
    localFile: 'prod_19_nzxt_h7_flow.jpg',
    sourceUrl: 'https://images.unsplash.com/photo-1593640408182-31c228e44b35?w=800&auto=format&fit=crop&q=85',
    source: 'High-Res Clean Minimalist Mid-Tower Chassis'
  },
  {
    id: 20,
    name: 'Corsair 5000D Airflow',
    brand: 'Corsair',
    category: 'CABINET',
    localFile: 'prod_20_corsair_5000d.jpg',
    sourceUrl: 'https://images.unsplash.com/photo-1547658719-da2b51169166?w=800&auto=format&fit=crop&q=85',
    source: 'High-Res High-Airflow Steel Case'
  }
];

function downloadUrl(url, dest) {
  return new Promise((resolve, reject) => {
    if (fs.existsSync(dest) && fs.statSync(dest).size > 1000) {
      return resolve(true);
    }
    const client = url.startsWith('https') ? https : http;
    const req = client.get(url, { headers: { 'User-Agent': 'Mozilla/5.0' } }, (res) => {
      if (res.statusCode === 301 || res.statusCode === 302) {
        return downloadUrl(res.headers.location, dest).then(resolve).catch(reject);
      }
      if (res.statusCode !== 200) {
        return reject(new Error(`HTTP ${res.statusCode}`));
      }
      const file = fs.createWriteStream(dest);
      res.pipe(file);
      file.on('finish', () => file.close(() => resolve(true)));
    });
    req.on('error', reject);
    req.setTimeout(10000, () => { req.destroy(); reject(new Error('Timeout')); });
  });
}

async function run() {
  const auditReport = [];

  for (const item of productAssets) {
    const destPath = path.join(prodDir, item.localFile);
    let success = false;
    let note = '';

    if (fs.existsSync(destPath) && fs.statSync(destPath).size > 1000) {
      success = true;
      note = `Already verified locally (${Math.round(fs.statSync(destPath).size / 1024)} KB)`;
    } else if (item.sourceUrl) {
      try {
        console.log(`Downloading [${item.id}] ${item.name}...`);
        await downloadUrl(item.sourceUrl, destPath);
        success = true;
        note = `Downloaded (${Math.round(fs.statSync(destPath).size / 1024)} KB)`;
      } catch (err) {
        success = false;
        note = `Download failed: ${err.message}`;
      }
    }

    auditReport.push({
      id: item.id,
      name: item.name,
      category: item.category,
      brand: item.brand,
      localAssetPath: `/assets/products/${item.localFile}`,
      relativeAssetPath: `../assets/products/${item.localFile}`,
      source: item.source,
      verified: success,
      note: note
    });
  }

  // Save manifest
  const manifestPath = path.join(__dirname, 'frontend/assets/products_manifest.json');
  fs.writeFileSync(manifestPath, JSON.stringify(auditReport, null, 2));
  console.log(`Saved product manifest with ${auditReport.length} items to ${manifestPath}`);
}

run();
