CREATE TABLE `warehouse` (
    `id` int PRIMARY KEY AUTO_INCREMENT,
    `name` varchar(255),
    `contactUserId` int UNIQUE,
    `status` int,
    `location` varchar(255),
    `createdAt` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updatedAt` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `user` (
    `id` int PRIMARY KEY AUTO_INCREMENT,
    `username` varchar(255) UNIQUE,
    `password` varchar(255),
    `name` varchar(255),
    `dui` varchar(255) UNIQUE,
    `role` int,
    `createdAt` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updatedAt` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `product` (
    `id` int PRIMARY KEY AUTO_INCREMENT,
    `name` varchar(255),
    `createdAt` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updatedAt` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `batch` (
    `id` int PRIMARY KEY AUTO_INCREMENT,
    `productId` int,
    `amount` int,
    `expirationDate` datetime DEFAULT null,
    `serialNumber` varchar(255),
    `price` decimal(6, 2),
    `createdAt` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updatedAt` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `client` (
    `id` int PRIMARY KEY AUTO_INCREMENT,
    `name` varchar(255),
    `address` varchar(255),
    `createdAt` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updatedAt` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `movement` (
    `id` int PRIMARY KEY AUTO_INCREMENT,
    `notes` text,
    `type` int,
    `status` int,
    `clientId` int,
    `responsibleUserId` int,
    `createdByUserId` int,
    `createdAt` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updatedAt` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `movementBatch` (
    `id` int PRIMARY KEY AUTO_INCREMENT,
    `movementId` int,
    `batchId` int,
    `createdAt` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updatedAt` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

ALTER TABLE
    `warehouse`
ADD
    FOREIGN KEY (`contactUserId`) REFERENCES `user` (`id`);

ALTER TABLE
    `batch`
ADD
    FOREIGN KEY (`productId`) REFERENCES `product` (`id`);

ALTER TABLE
    `movement`
ADD
    FOREIGN KEY (`clientId`) REFERENCES `client` (`id`);

ALTER TABLE
    `movement`
ADD
    FOREIGN KEY (`responsibleUserId`) REFERENCES `user` (`id`);

ALTER TABLE
    `movement`
ADD
    FOREIGN KEY (`createdByUserId`) REFERENCES `user` (`id`);

ALTER TABLE
    `movementBatch`
ADD
    FOREIGN KEY (`movementId`) REFERENCES `movement` (`id`);

ALTER TABLE
    `movementBatch`
ADD
    FOREIGN KEY (`batchId`) REFERENCES `batch` (`id`);