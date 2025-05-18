-- Usuarios
INSERT INTO `user` (`username`, `password`, `name`, `dui`, `role`, `createdAt`)
VALUES ('admin', '$2y$10$EkCyKANTqo6wLYouGU0OsuglSlafKmmim5E8zpizAGWyexvt2/EAy', 'Carlos Meléndez', '04567891-3', 1,
        NOW()),
       ('operator1', '$2y$10$fLsU7QEirM1q49Y7eMzXpOZgQ3QzD4Tk/gVzvLb/lMrBSvf.OneX.', 'Marta González', '03124567-2', 2,
        NOW()),
       ('operator2', '$2y$10$3rLd3JMnr3.U9jypBySQx.sRHxcO1Q6lkM2G8LK9giJxtZiRP1TKa', 'José Ramírez', '06789123-9', 2,
        NOW()),
       ('bodeguero1', '$2y$10$Um8vnS9wMWScSV6m9onVbeUJ7EicamKbjHFTRlDmTAjTCY31.PuHe', 'Luis Vásquez', '05432198-7', 3,
        NOW());

-- Almacenes
INSERT INTO `warehouse` (`name`, `contactUserId`, `status`, `location`, `createdAt`)
VALUES ('Bodega Central', 1, 1, 'San Salvador', NOW()),
       ('Bodega Occidente', 2, 1, 'Santa Ana', NOW()),
       ('Bodega Oriente', 3, 1, 'San Miguel', NOW());

-- Productos (Pan Dulce Industrial)
INSERT INTO `product` (`name`, `createdAt`)
VALUES ('Pan Dulce Tradicional', NOW()),
       ('Semita de Piña', NOW()),
       ('Pico de Gallo', NOW()),
       ('Pan de Manteca', NOW()),
       ('Cuernitos de Azúcar', NOW()),
       ('Pan Francés', NOW()),
       ('Pan de Coco', NOW()),
       ('Pan de Queso', NOW()),
       ('Pan de Yema', NOW());

-- Lotes
INSERT INTO `batch` (`productId`, `amount`, `expirationDate`, `serialNumber`, `price`, `createdAt`)
VALUES (1, 500, '2025-06-01 00:00:00', 'PDT-001', 0.15, NOW()),
       (2, 300, '2025-06-05 00:00:00', 'SDP-001', 0.25, NOW()),
       (3, 400, '2025-06-30 00:00:00', 'PDG-001', 0.20, NOW()),
       (4, 600, '2025-07-10 00:00:00', 'PMT-001', 0.10, NOW()),
       (5, 350, '2025-07-15 00:00:00', 'CDA-001', 0.18, NOW()),
       (6, 700, '2025-07-20 00:00:00', 'PFR-001', 0.12, NOW()),
       (7, 300, '2025-06-28 00:00:00', 'PDC-001', 0.22, NOW()),
       (8, 200, '2025-08-02 00:00:00', 'PDQ-001', 0.30, NOW()),
       (9, 250, '2025-07-08 00:00:00', 'PDY-001', 0.16, NOW());

-- Clientes (Supermercados, tiendas y cafeterías)
INSERT INTO `client` (`name`, `address`, `createdAt`)
VALUES ('Super Selectos Escalón', 'Colonia Escalón, San Salvador', NOW()),
       ('Tiendona La Esperanza', 'Soyapango', NOW()),
       ('Supermercado El Baratón', 'Santa Ana', NOW()),
       ('Panadería Delicias del Sur', 'San Miguel', NOW()),
       ('Café La Cosecha', 'Ahuachapán', NOW());

-- Movimientos (1 = salida, 2 = entrada)
INSERT INTO `movement` (`notes`, `type`, `status`, `clientId`, `createdByUserId`, `responsibleUserId`, `createdAt`)
VALUES ('Entrega semanal de pan dulce', 1, 1, 1, 1, 2, NOW()),
       ('Reposición urgente de pan francés', 1, 1, 2, 1, 2, NOW()),
       ('Nuevo lote de pan de coco', 2, 1, 2, 1, 2, NOW()),
       ('Entrega de semitas de piña', 1, 1, 3, 1, 3, NOW()),
       ('Despacho a cafetería', 1, 1, 5, 1, 4, NOW());

-- Movimientos - Lotes
INSERT INTO `movementBatch` (`movementId`, `batchId`, `createdAt`)
VALUES (1, 1, NOW()),
       (1, 5, NOW()),
       (2, 6, NOW()),
       (3, 7, NOW()),
       (4, 2, NOW()),
       (5, 8, NOW());
