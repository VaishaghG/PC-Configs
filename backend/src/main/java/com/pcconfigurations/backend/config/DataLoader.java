package com.pcconfigurations.backend.config;

import com.pcconfigurations.backend.entity.Product;
import com.pcconfigurations.backend.entity.User;
import com.pcconfigurations.backend.repository.ProductRepository;
import com.pcconfigurations.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        loadSampleProducts();
        loadAdminUser();
    }

    private void loadAdminUser() {
        if (!userRepository.existsByEmail("admin@pcconfigurations.com")) {
            User admin = new User();
            admin.setFullName("Admin User");
            admin.setEmail("admin@pcconfigurations.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
            logger.info("Admin user created: admin@pcconfigurations.com / admin123");
        }
    }

    private void loadSampleProducts() {
        if (productRepository.count() > 0) {
            logger.info("Products already exist, skipping sample data load.");
            return;
        }

        logger.info("Loading sample products...");

        List<Product> products = List.of(
            // CPUs
            createProduct("Intel Core i9-13900K", "Intel", Product.Category.CPU,
                new BigDecimal("49999"),
                "24-core (8P+16E), 5.8GHz Boost, 125W TDP, LGA1700 socket. Flagship 13th gen Raptor Lake CPU for extreme performance.",
                "Cores: 24 | Threads: 32 | Base: 3.0GHz | Boost: 5.8GHz | Cache: 36MB L3 | TDP: 125W | Socket: LGA1700",
                "/assets/products/prod_1_intel_i9_13900k.jpg"),

            createProduct("AMD Ryzen 9 7950X", "AMD", Product.Category.CPU,
                new BigDecimal("59999"),
                "16-core Zen 4, 5.7GHz Boost, AM5 socket. Workstation and creator powerhouse.",
                "Cores: 16 | Threads: 32 | Base: 4.5GHz | Boost: 5.7GHz | Cache: 64MB L3 | TDP: 170W | Socket: AM5",
                "/assets/products/prod_2_amd_ryzen9_7950x.jpg"),

            createProduct("Intel Core i7-13700K", "Intel", Product.Category.CPU,
                new BigDecimal("35999"),
                "16-core (8P+8E), 5.4GHz Boost, LGA1700. Excellent price-to-performance.",
                "Cores: 16 | Threads: 24 | Base: 3.4GHz | Boost: 5.4GHz | Cache: 30MB L3 | TDP: 125W | Socket: LGA1700",
                "/assets/products/prod_3_intel_i7_13700k.jpg"),

            createProduct("AMD Ryzen 5 7600X", "AMD", Product.Category.CPU,
                new BigDecimal("24999"),
                "6-core Zen 4, 5.3GHz Boost, AM5 socket. Best mid-range gaming CPU.",
                "Cores: 6 | Threads: 12 | Base: 4.7GHz | Boost: 5.3GHz | Cache: 32MB L3 | TDP: 105W | Socket: AM5",
                "/assets/products/prod_4_amd_ryzen5_7600x.jpg"),

            // GPUs
            createProduct("NVIDIA RTX 4090", "NVIDIA", Product.Category.GPU,
                new BigDecimal("159999"),
                "Ada Lovelace, 16384 CUDA cores, 24GB GDDR6X. Flagship GPU for 4K and beyond.",
                "CUDA Cores: 16384 | VRAM: 24GB GDDR6X | Boost: 2520MHz | TDP: 450W | Interface: PCIe 4.0 x16",
                "/assets/products/prod_5_nvidia_rtx_4090.jpg"),

            createProduct("NVIDIA RTX 4080 Super", "NVIDIA", Product.Category.GPU,
                new BigDecimal("109999"),
                "Ada Lovelace, 10240 CUDA cores, 16GB GDDR6X. High-end 4K gaming.",
                "CUDA Cores: 10240 | VRAM: 16GB GDDR6X | Boost: 2550MHz | TDP: 320W | Interface: PCIe 4.0 x16",
                "/assets/products/prod_6_nvidia_rtx_4080super.jpg"),

            createProduct("AMD Radeon RX 7900 XTX", "AMD", Product.Category.GPU,
                new BigDecimal("89999"),
                "RDNA 3, 6144 shaders, 24GB GDDR6. AMD flagship with ray tracing.",
                "Shaders: 6144 | VRAM: 24GB GDDR6 | Boost: 2500MHz | TDP: 355W | Interface: PCIe 4.0 x16",
                "/assets/products/prod_7_amd_rx7900xtx.jpg"),

            createProduct("NVIDIA RTX 4070 Ti", "NVIDIA", Product.Category.GPU,
                new BigDecimal("74999"),
                "Ada Lovelace, 7680 CUDA cores, 12GB GDDR6X. Excellent 1440p and entry-level 4K.",
                "CUDA Cores: 7680 | VRAM: 12GB GDDR6X | Boost: 2610MHz | TDP: 285W | Interface: PCIe 4.0 x16",
                "/assets/products/prod_8_nvidia_rtx_4070ti.jpg"),

            // RAM
            createProduct("Corsair Dominator Platinum 32GB DDR5", "Corsair", Product.Category.RAM,
                new BigDecimal("14999"),
                "32GB (2x16GB) DDR5-6000 CL30. Premium RGB memory for enthusiast builds.",
                "Capacity: 32GB (2x16GB) | Speed: DDR5-6000 | Latency: CL30 | Voltage: 1.35V | RGB: Yes",
                "/assets/products/prod_9_corsair_dominator_32gb.jpg"),

            createProduct("G.Skill Trident Z5 32GB DDR5", "G.Skill", Product.Category.RAM,
                new BigDecimal("12999"),
                "32GB (2x16GB) DDR5-6400 CL32. High-frequency DDR5 with RGB.",
                "Capacity: 32GB (2x16GB) | Speed: DDR5-6400 | Latency: CL32 | Voltage: 1.4V | RGB: Yes",
                "/assets/products/prod_10_gskill_trident_32gb.jpg"),

            createProduct("Kingston Fury Beast 16GB DDR4", "Kingston", Product.Category.RAM,
                new BigDecimal("5499"),
                "16GB (2x8GB) DDR4-3600 CL17. Reliable budget to mid-range RAM.",
                "Capacity: 16GB (2x8GB) | Speed: DDR4-3600 | Latency: CL17 | Voltage: 1.35V | RGB: No",
                "/assets/products/prod_11_kingston_fury_16gb.jpg"),

            createProduct("TeamGroup T-Force 64GB DDR5", "TeamGroup", Product.Category.RAM,
                new BigDecimal("22999"),
                "64GB (2x32GB) DDR5-5600 CL36. Massive capacity for workstations and creators.",
                "Capacity: 64GB (2x32GB) | Speed: DDR5-5600 | Latency: CL36 | Voltage: 1.25V | RGB: Yes",
                "/assets/products/prod_12_teamgroup_tforce_64gb.jpg"),

            // Storage
            createProduct("Samsung 990 Pro 2TB NVMe", "Samsung", Product.Category.STORAGE,
                new BigDecimal("14999"),
                "PCIe 4.0 NVMe M.2 SSD, 7450MB/s read, 6900MB/s write. Premium storage for demanding workloads.",
                "Capacity: 2TB | Interface: PCIe 4.0 NVMe | Read: 7450MB/s | Write: 6900MB/s | Form: M.2 2280",
                "/assets/products/prod_13_samsung_990pro_2tb.jpg"),

            createProduct("WD Black SN850X 1TB NVMe", "Western Digital", Product.Category.STORAGE,
                new BigDecimal("8999"),
                "PCIe 4.0 NVMe gaming SSD, 7300MB/s read. PS5 compatible with heatsink option.",
                "Capacity: 1TB | Interface: PCIe 4.0 NVMe | Read: 7300MB/s | Write: 6300MB/s | Form: M.2 2280",
                "/assets/products/prod_14_wd_black_sn850x.jpg"),

            createProduct("Seagate Barracuda 4TB HDD", "Seagate", Product.Category.STORAGE,
                new BigDecimal("6999"),
                "3.5\" SATA HDD 7200RPM, 256MB cache. Budget-friendly mass storage.",
                "Capacity: 4TB | Interface: SATA 6Gb/s | RPM: 7200 | Cache: 256MB | Form: 3.5 inch",
                "/assets/products/prod_15_seagate_barracuda_4tb.jpg"),

            createProduct("Crucial P5 Plus 500GB NVMe", "Crucial", Product.Category.STORAGE,
                new BigDecimal("4999"),
                "PCIe 4.0 NVMe SSD, 6600MB/s read. Compact and fast storage solution.",
                "Capacity: 500GB | Interface: PCIe 4.0 NVMe | Read: 6600MB/s | Write: 4000MB/s | Form: M.2 2280",
                "/assets/products/prod_16_crucial_p5plus_500gb.jpg"),

            // Cabinets
            createProduct("Lian Li PC-O11 Dynamic EVO", "Lian Li", Product.Category.CABINET,
                new BigDecimal("12999"),
                "Mid-tower ATX case with dual-chamber design. Supports 360mm radiators and up to 7 fans.",
                "Form Factor: Mid Tower ATX | Tempered Glass: Dual Panel | Fan Support: Up to 7x 120mm | Radiator: 360mm | USB: Type-C",
                "/assets/products/prod_17_lianli_o11d_evo.jpg"),

            createProduct("Fractal Design Torrent", "Fractal Design", Product.Category.CABINET,
                new BigDecimal("14499"),
                "E-ATX mid-tower with massive airflow design. Front intake fan included.",
                "Form Factor: Mid Tower E-ATX | Tempered Glass: Side | Fan Support: Up to 9x 120mm | Radiator: 360mm | USB: Type-C",
                "/assets/products/prod_18_fractal_torrent.jpg"),

            createProduct("NZXT H7 Flow", "NZXT", Product.Category.CABINET,
                new BigDecimal("9999"),
                "Mid-tower ATX with excellent airflow and clean aesthetic. Two pre-installed 120mm fans.",
                "Form Factor: Mid Tower ATX | Tempered Glass: Side | Fan Support: Up to 6x 120mm | Radiator: 280mm | USB: Type-C",
                "/assets/products/prod_19_nzxt_h7_flow.jpg"),

            createProduct("Corsair 5000D Airflow", "Corsair", Product.Category.CABINET,
                new BigDecimal("10499"),
                "Mid-tower ATX with high airflow mesh front. 2x120mm fans included.",
                "Form Factor: Mid Tower ATX | Tempered Glass: Side | Fan Support: Up to 10x 120mm | Radiator: 360mm | USB: Type-C",
                "/assets/products/prod_20_corsair_5000d.jpg")
        );

        productRepository.saveAll(products);
        logger.info("Loaded {} sample products.", products.size());
    }

    private Product createProduct(String name, String brand, Product.Category category,
                                   BigDecimal price, String description, String specs, String imageUrl) {
        Product p = new Product();
        p.setName(name);
        p.setBrand(brand);
        p.setCategory(category);
        p.setPrice(price);
        p.setDescription(description);
        p.setSpecifications(specs);
        p.setImageUrl(imageUrl);
        p.setStockQuantity(25);
        return p;
    }
}
