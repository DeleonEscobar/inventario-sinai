-- Primero limpiamos las tablas para evitar conflictos si ya hay datos
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE movementBatch;
TRUNCATE TABLE movement;
TRUNCATE TABLE batch;
TRUNCATE TABLE product;
TRUNCATE TABLE warehouse;
TRUNCATE TABLE client;
TRUNCATE TABLE user;

SET FOREIGN_KEY_CHECKS = 1;

-- Usuarios
INSERT INTO `user` (`username`, `password`, `name`, `dui`, `role`, `createdAt`)
VALUES ('admin', '$2y$10$QnCebVSweHGhk6Qq.M04oufs2ha0lTBUAFEV6FIige3osI0mMoniq', 'Juan Pérez', '01234567-8', 1, NOW()),
       ('operator', '$2y$10$mbruBSadRSEsW5Lw5TYKc.i5SRmWTsGsGQo7dCPj1w.UJZ4EYoH1.', 'Ana López', '87654321-0', 2, NOW());

-- Almacenes
INSERT INTO `warehouse` (`name`, `contactUserId`, `status`, `location`, `createdAt`)
VALUES ('Bodega Central', 1, 1, 'San Salvador', NOW());

-- Productos
INSERT INTO `product` (`name`, `createdAt`)
VALUES ('Alcohol Gel', NOW()),
       ('Mascarilla N95', NOW());

-- Lotes
INSERT INTO `batch` (`productId`, `amount`, `expirationDate`, `serialNumber`, `price`, `createdAt`)
VALUES (1, 100, '2025-12-31 00:00:00', 'AG-001', 2.50, NOW()),
       (2, 200, '2026-01-15 00:00:00', 'MN95-001', 5.00, NOW());

-- Clientes
INSERT INTO `client` (`name`, `address`, `createdAt`)
VALUES ('Clínica Vida', 'Santa Tecla', NOW()),
       ('Hospital San Juan', 'Soyapango', NOW());

-- Movimientos
INSERT INTO `movement` (`notes`, `type`, `status`, `clientId`, `responsibleUserId`, `createdAt`)
VALUES ('Entrega inicial de productos', 1, 1, 1, 2, NOW()),
       ('Reposición de stock', 1, 1, 2, 2, NOW());

-- Movimientos - Lotes
INSERT INTO `movementBatch` (`movementId`, `batchId`, `createdAt`)
VALUES (1, 1, NOW()),
       (2, 2, NOW());
