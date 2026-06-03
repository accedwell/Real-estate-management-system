DROP TABLE IF EXISTS Reports CASCADE; 
DROP TABLE IF EXISTS MaintenanceRequests CASCADE; 
DROP TABLE IF EXISTS Applications CASCADE; 
DROP TABLE IF EXISTS Tenants CASCADE; 
DROP TABLE IF EXISTS Properties CASCADE; 
DROP TABLE IF EXISTS Users CASCADE; 

CREATE TABLE Users ( 
userID SERIAL PRIMARY KEY, 
name VARCHAR(100) NOT NULL, 
role VARCHAR(50) CHECK (role IN ('Owner','Tenant','Agent','Manager','Admin','Maintenance')), 
email VARCHAR(100) UNIQUE NOT NULL, 
phone VARCHAR(20), 
passwordHash BYTEA NOT NULL 
); 

CREATE TABLE Properties ( 
propertyID SERIAL PRIMARY KEY, 
ownerID INT NOT NULL, 
address VARCHAR(255) NOT NULL, 
type VARCHAR(50), 
size INT, 
rooms INT CHECK (rooms >= 0), 
status VARCHAR(20) CHECK (status IN ('Available','Rented','Sold')), 
FOREIGN KEY (ownerID) REFERENCES Users(userID) 
); 

CREATE TABLE Tenants ( 
tenantID SERIAL PRIMARY KEY, 
userID INT NOT NULL, 
propertyID INT NOT NULL, 
leaseStart DATE NOT NULL, 
leaseEnd DATE, 
FOREIGN KEY (userID) REFERENCES Users(userID), 
FOREIGN KEY (propertyID) REFERENCES Properties(propertyID) 
); 

CREATE TABLE Applications ( 
applicationID SERIAL PRIMARY KEY, 
tenantID INT NOT NULL,  
propertyID INT NOT NULL, 
date DATE NOT NULL,     
status VARCHAR(20) CHECK (status IN ('Pending','Approved','Rejected')), 
FOREIGN KEY (tenantID) REFERENCES Users(userID), 
FOREIGN KEY (propertyID) REFERENCES Properties(propertyID) 
); 

CREATE TABLE MaintenanceRequests ( 
requestID SERIAL PRIMARY KEY, 
tenantID INT NOT NULL, 
propertyID INT NOT NULL, 
description VARCHAR(255) NOT NULL, 
priority VARCHAR(20) CHECK (priority IN ('Low','Medium','High')), 
status VARCHAR(20) CHECK (status IN ('Pending','InProgress','Resolved')), 
cost NUMERIC(12,2) DEFAULT 0.00, 
resolutionNotes VARCHAR(255), 
FOREIGN KEY (tenantID) REFERENCES Tenants(tenantID), 
FOREIGN KEY (propertyID) REFERENCES Properties(propertyID) 
); 

CREATE TABLE Reports ( 
reportID SERIAL PRIMARY KEY, 
type VARCHAR(50), 
generatedBy INT NOT NULL, 
date DATE NOT NULL, 
filePath VARCHAR(255), 
FOREIGN KEY (generatedBy) REFERENCES Users(userID) 
); 

INSERT INTO Users (name, role, email, phone, passwordHash) VALUES 
('Alice Owner','Owner','alice@demo.com','1234567890','hash1'), 
('Bob Tenant','Tenant','bob@demo.com','1234567891','hash2'), 
('Charlie Agent','Agent','charlie@demo.com','1234567892','hash3'), 
('Diana Manager','Manager','diana@demo.com','1234567893','hash4'), 
('Eddie Maintenance','Maintenance','eddie@demo.com','1234567894','hash5');
 
INSERT INTO Properties (ownerID, address, type, size, rooms, status) VALUES 
(1,'123 Main St','House',120,4,'Available'), 
(1,'456 Oak Ave','Apartment',80,3,'Rented'), 
(1,'789 Pine Rd','Condo',100,2,'Available'), 
(1,'321 Maple Dr','House',150,5,'Sold'), 
(1,'654 Birch Ln','Apartment',60,2,'Available'); 

INSERT INTO Tenants (userID, propertyID, leaseStart, leaseEnd) VALUES 
(2,1,'2025-01-01','2025-12-31'), 
(2,2,'2025-02-01','2025-11-30'), 
(3,3,'2025-03-01','2025-09-30'), 
(4,4,'2025-04-01','2025-10-31'), 
(5,5,'2025-05-01','2025-12-31'); 

INSERT INTO Applications (tenantID, propertyID, date, status) VALUES 
(2, 1, '2026-01-15', 'Pending'), 
(2, 2, '2026-02-10', 'Approved'), 
(3, 3, '2026-03-05', 'Rejected'), 
(3, 4, '2026-04-20', 'Pending'), 
(4, 5, '2026-05-11', 'Approved'); 

INSERT INTO MaintenanceRequests (tenantID, propertyID, description, priority, status, cost, 
resolutionNotes) VALUES 
(1,2,'Leaky faucet','Low','Pending',200.00,''), 
(2,3,'Broken window','High','InProgress',500.00,'Glass ordered'), 
(3,4,'Heating issue','Medium','Resolved',800.00,'Fixed thermostat'), 
(4,5,'Roof leak','High','Pending',1200.00,'Awaiting contractor'), 
(5,1,'Paint peeling','Low','Resolved',150.00,'Repainted'); 

INSERT INTO Reports (type, generatedBy, date, filePath) VALUES 
('Monthly Summary',4,'2025-06-30','/reports/june.pdf'), 
('Tenant Report',2,'2025-07-15','/reports/tenant.pdf'), 
('Property Status',1,'2025-08-01','/reports/property.pdf'), 
('Maintenance Log',5,'2025-09-10','/reports/maintenance.pdf'), 
('Transaction Report',3,'2025-10-20','/reports/transactions.pdf');