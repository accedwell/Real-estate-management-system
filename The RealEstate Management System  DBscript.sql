-- =====================================================================
-- REMS - Real Estate Management System - PostgreSQL schema + demo data
--
-- 1. Create the database once:   CREATE DATABASE "RealEstateManagementSystem";
-- 2. Connect to it and run this whole script (it drops and recreates the tables).
--
-- Every demo account uses the password:  Password123
-- (stored as a SHA-256 hash, exactly as the application checks it)
--
--   admin@rems.co.za        Admin
--   diana@demo.com          Manager
--   charlie@demo.com        Agent
--   alice@demo.com          Owner
--   sipho@demo.com          Owner
--   bob@demo.com            Tenant
--   lerato@demo.com         Tenant
--   thabo@demo.com          Tenant
--   eddie@demo.com          Maintenance
-- =====================================================================

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
    size INT CHECK (size > 0),
    rooms INT CHECK (rooms >= 0),
    status VARCHAR(20) CHECK (status IN ('Available','Rented','Sold')),
    FOREIGN KEY (ownerID) REFERENCES Users(userID)
);

-- One row per lease. tenantID identifies the lease; userID is the tenant's account.
CREATE TABLE Tenants (
    tenantID SERIAL PRIMARY KEY,
    userID INT NOT NULL,
    propertyID INT NOT NULL,
    leaseStart DATE NOT NULL,
    leaseEnd DATE,
    CHECK (leaseEnd IS NULL OR leaseEnd >= leaseStart),
    FOREIGN KEY (userID) REFERENCES Users(userID),
    FOREIGN KEY (propertyID) REFERENCES Properties(propertyID)
);

-- Applications.tenantID is the applicant's Users.userID.
CREATE TABLE Applications (
    applicationID SERIAL PRIMARY KEY,
    tenantID INT NOT NULL,
    propertyID INT NOT NULL,
    date DATE NOT NULL,
    status VARCHAR(20) CHECK (status IN ('Pending','Approved','Rejected')),
    FOREIGN KEY (tenantID) REFERENCES Users(userID),
    FOREIGN KEY (propertyID) REFERENCES Properties(propertyID)
);

-- MaintenanceRequests.tenantID is the lease (Tenants.tenantID) the request belongs to.
CREATE TABLE MaintenanceRequests (
    requestID SERIAL PRIMARY KEY,
    tenantID INT NOT NULL,
    propertyID INT NOT NULL,
    description VARCHAR(255) NOT NULL,
    priority VARCHAR(20) CHECK (priority IN ('Low','Medium','High')),
    status VARCHAR(20) CHECK (status IN ('Pending','InProgress','Resolved')),
    cost NUMERIC(12,2) DEFAULT 0.00 CHECK (cost >= 0),
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

-- ---------------------------------------------------------------- Demo users (password: Password123)
INSERT INTO Users (name, role, email, phone, passwordHash) VALUES
('System Administrator', 'Admin',       'admin@rems.co.za', '+27 12 000 0000', decode('008c70392e3abfbd0fa47bbc2ed96aa99bd49e159727fcba0f2e6abeb3a9d601', 'hex')), -- 1
('Alice Naidoo',         'Owner',       'alice@demo.com',   '+27 82 123 4567', decode('008c70392e3abfbd0fa47bbc2ed96aa99bd49e159727fcba0f2e6abeb3a9d601', 'hex')), -- 2
('Bob Mokoena',          'Tenant',      'bob@demo.com',     '+27 83 234 5678', decode('008c70392e3abfbd0fa47bbc2ed96aa99bd49e159727fcba0f2e6abeb3a9d601', 'hex')), -- 3
('Charlie van Wyk',      'Agent',       'charlie@demo.com', '+27 84 345 6789', decode('008c70392e3abfbd0fa47bbc2ed96aa99bd49e159727fcba0f2e6abeb3a9d601', 'hex')), -- 4
('Diana Khumalo',        'Manager',     'diana@demo.com',   '+27 71 456 7890', decode('008c70392e3abfbd0fa47bbc2ed96aa99bd49e159727fcba0f2e6abeb3a9d601', 'hex')), -- 5
('Eddie Dlamini',        'Maintenance', 'eddie@demo.com',   '+27 72 567 8901', decode('008c70392e3abfbd0fa47bbc2ed96aa99bd49e159727fcba0f2e6abeb3a9d601', 'hex')), -- 6
('Lerato Mabena',        'Tenant',      'lerato@demo.com',  '+27 73 678 9012', decode('008c70392e3abfbd0fa47bbc2ed96aa99bd49e159727fcba0f2e6abeb3a9d601', 'hex')), -- 7
('Sipho Ndlovu',         'Owner',       'sipho@demo.com',   '+27 74 789 0123', decode('008c70392e3abfbd0fa47bbc2ed96aa99bd49e159727fcba0f2e6abeb3a9d601', 'hex')), -- 8
('Thabo Sithole',        'Tenant',      'thabo@demo.com',   '+27 76 890 1234', decode('008c70392e3abfbd0fa47bbc2ed96aa99bd49e159727fcba0f2e6abeb3a9d601', 'hex')); -- 9

-- ---------------------------------------------------------------- Properties
INSERT INTO Properties (ownerID, address, type, size, rooms, status) VALUES
(2, '12 Jacaranda Street, Hatfield, Pretoria',      'House',     180, 4, 'Rented'),     -- 1
(2, '45 Mandela Drive, Witbank (eMalahleni)',       'Apartment',  85, 2, 'Rented'),     -- 2
(2, '7 Protea Close, Sandton, Johannesburg',        'Townhouse', 140, 3, 'Available'),  -- 3
(8, '221 Long Street, Cape Town City Centre',       'Apartment',  65, 1, 'Available'),  -- 4
(8, '9 Marula Avenue, Mbombela',                    'House',     220, 5, 'Sold'),       -- 5
(8, '30 Church Street, Polokwane',                  'Office',    300, 8, 'Available'),  -- 6
(2, '18 Baobab Crescent, Centurion',                'Condo',     110, 3, 'Rented');     -- 7

-- ---------------------------------------------------------------- Leases (tenant users only)
INSERT INTO Tenants (userID, propertyID, leaseStart, leaseEnd) VALUES
(3, 1, '2026-01-01', '2026-12-31'),   -- 1 Bob    -> Hatfield house
(7, 2, '2026-02-01', '2027-01-31'),   -- 2 Lerato -> Witbank apartment
(9, 7, '2026-03-01', NULL),           -- 3 Thabo  -> Centurion condo (open-ended)
(3, 4, '2025-01-01', '2025-12-31');   -- 4 Bob    -> Cape Town (ended)

-- ---------------------------------------------------------------- Applications (tenantID = applicant userID)
INSERT INTO Applications (tenantID, propertyID, date, status) VALUES
(3, 3, '2026-10-02', 'Pending'),
(7, 4, '2026-09-15', 'Pending'),
(9, 7, '2026-02-10', 'Approved'),
(7, 5, '2026-04-20', 'Rejected'),
(3, 1, '2025-12-01', 'Approved');

-- ---------------------------------------------------------------- Maintenance (tenantID = lease id)
INSERT INTO MaintenanceRequests (tenantID, propertyID, description, priority, status, cost, resolutionNotes) VALUES
(1, 1, 'Kitchen tap leaking under the sink',          'Low',    'Pending',     0.00,    ''),
(2, 2, 'Bedroom window cracked after storm',          'High',   'InProgress',  850.00,  'Glass ordered from supplier'),
(3, 7, 'Geyser not heating water',                    'High',   'Resolved',    3200.00, 'Element and thermostat replaced'),
(1, 1, 'Garage door remote not working',              'Medium', 'Pending',     0.00,    ''),
(4, 4, 'Paint peeling in bathroom',                   'Low',    'Resolved',    450.00,  'Repainted with damp-proof paint');

-- ---------------------------------------------------------------- Report log
INSERT INTO Reports (type, generatedBy, date, filePath) VALUES
('Tenant Report (PDF)',      5, '2026-06-30', 'exports/Tenant_Leases_2026-06-30.pdf'),
('Maintenance Report (CSV)', 1, '2026-07-15', 'exports/Maintenance_Report_2026-07-15.csv'),
('Property Report (TXT)',    5, '2026-08-01', 'exports/Applications_2026-08-01.txt');
