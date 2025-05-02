CREATE TABLE `Warehouse` (
  `Id` int PRIMARY KEY AUTO_INCREMENT,
  `Name` varchar(255),
  `ContactUserId` int,
  `Status` int,
  `CreatedAt` timestamp DEFAULT null,
  `UpdatedAt` timestamp DEFAULT null
);

CREATE TABLE `User` (
  `Id` int PRIMARY KEY AUTO_INCREMENT,
  `Name` varchar(255),
  `DUI` varchar(255),
  `Role` int,
  `CreatedAt` timestamp DEFAULT null,
  `UpdatedAt` timestamp DEFAULT null
);

CREATE TABLE `Product` (
  `Id` int PRIMARY KEY AUTO_INCREMENT,
  `Name` varchar(255),
  `CreatedAt` timestamp DEFAULT null,
  `UpdatedAt` timestamp DEFAULT null
);

CREATE TABLE `Batch` (
  `Id` int PRIMARY KEY AUTO_INCREMENT,
  `ProductId` int,
  `Amount` integer,
  `Date` datetime,
  `SerialNumber` varchar(255),
  `Price` decimal(6,2),
  `CreatedAt` timestamp DEFAULT null,
  `UpdatedAt` timestamp DEFAULT null
);

CREATE TABLE `Client` (
  `Id` int PRIMARY KEY AUTO_INCREMENT,
  `Name` varchar(255),
  `CreatedAt` timestamp DEFAULT null,
  `UpdatedAt` timestamp DEFAULT null
);

CREATE TABLE `Movement` (
  `Id` int PRIMARY KEY AUTO_INCREMENT,
  `Notes` text,
  `Type` int,
  `Status` int,
  `ClientId` int,
  `ResponsibleUserId` int,
  `CreatedAt` timestamp DEFAULT null,
  `UpdatedAt` timestamp DEFAULT null
);

CREATE TABLE `MovementBatch` (
  `Id` int PRIMARY KEY AUTO_INCREMENT,
  `MovementId` int,
  `BatchId` int,
  `CreatedAt` timestamp DEFAULT null,
  `UpdatedAt` timestamp DEFAULT null
);

ALTER TABLE `Warehouse` ADD FOREIGN KEY (`ContactUserId`) REFERENCES `User` (`Id`);

ALTER TABLE `Batch` ADD FOREIGN KEY (`ProductId`) REFERENCES `Product` (`Id`);

ALTER TABLE `Movement` ADD FOREIGN KEY (`ClientId`) REFERENCES `Client` (`Id`);

ALTER TABLE `Movement` ADD FOREIGN KEY (`ResponsibleUserId`) REFERENCES `User` (`Id`);

ALTER TABLE `MovementBatch` ADD FOREIGN KEY (`MovementId`) REFERENCES `Movement` (`Id`);

ALTER TABLE `MovementBatch` ADD FOREIGN KEY (`BatchId`) REFERENCES `Batch` (`Id`);
