DROP TABLE IF EXISTS Customer CASCADE;
DROP TABLE IF EXISTS Cars CASCADE;
DROP TABLE IF EXISTS ServiceRequest CASCADE;
DROP TABLE IF EXISTS Mechanic CASCADE;

--Tables

CREATE TABLE Customer
(
	C_id 	INTEGER NOT NULL,
	fname 	CHAR(32) NOT NULL,
	lname 	CHAR(32) NOT NULL,
	phone 	CHAR(13) NOT NULL,
	address CHAR(256) NOT NULL,
	PRIMARY KEY (C_id)
);

CREATE TABLE Cars
(
	Vin 	VARCHAR(16) NOT NULL,
	make 	VARCHAR(32) NOT NULL,
	model 	VARCHAR(32) NOT NULL,
	year  	INTEGER NOT NULL,
	PRIMARY KEY(Vin)
);

CREATE TABLE  Mechanic
(
	first_name 	CHAR(32) NOT NULL,
	last_name 	CHAR(32) NOT NULL,
	employee_id 	INTEGER NOT NULL,
	years_of_exp 	INTEGER NOT NULL,
	PRIMARY KEY(employee_id)
);

CREATE TABLE ServiceRequest
(
	open_date 	DATE  NOT NULL,
	close_date 	DATE  NOT NULL,
	status 	
	comments 	TEXT,
	odometer_reading
	bill	INTEGER,
	problem	TEXT,
	Sr_id	INTEGER NOT NULL,
	PRIMARY KEY(Sr_id)
);

--Relationships

CREATE TABLE Owns
(
	C_id	INTEGER NOT NULL,
	Vin	VARCHAR(16) NOT NULL,

	PRIMARY KEY(C_id, Vin),
	FOREIGN KEY(C_id) REFERENCES Customer,
	FOREIGN KEY(Vin) REFERENCES Cars
)

CREATE TABLE Works_on
(
	employee_id	INTEGER NOT NULL,
	Vin		VARCHAR(16) NOT NULL,

	PRIMARY KEY(employee_id, Vin),
	FOREIGN KEY(employee_id) REFERENCES Mechanic,
	FOREIGN KEY(Vin) REFERENCES Cars
)

CREATE TABLE Creates
(
	employee_id	INTEGER NOT NULL,
	Sr_id		INTEGER NOT NULL,
	
	PRIMARY KEY(employee_id, Sr_id),
	FOREIGN KEY(employee_id) REFERENCES Mechanic,
	FOREIGN KEY(Sr_id) REFERENCES ServiceRequest
)

CREATE TABLE Checks
(
	drop_off	BOOL,
	Sr_id		INTEGER NOT NULL,
	Vin		VARCHAR(16) NOT NULL,

	PRIMARY KEY(Sr_id, Vin),
	FOREIGN KEY(Sr_id) REFERENCES ServiceRequest,
	FOREIGN KEY(Vin) REFERENCES Cars
)
