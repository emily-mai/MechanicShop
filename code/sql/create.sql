DROP TABLE IF EXISTS Customer CASCADE;--OK
DROP TABLE IF EXISTS Mechanic CASCADE;--OK
DROP TABLE IF EXISTS Car CASCADE;--OK
DROP TABLE IF EXISTS Owns CASCADE;--OK
DROP TABLE IF EXISTS Service_Request CASCADE;--OK
DROP TABLE IF EXISTS Closed_Request CASCADE;--OK

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO postgres;

-- added below CREATE SEQUENCE STATEMENTS to create sequence object that autogenerate the cid and mid in the table when
-- inserting
CREATE SEQUENCE customer_id_seq AS integer START 500;
CREATE SEQUENCE mechanic_id_seq AS integer START 250;


-------------
---DOMAINS---
-------------
CREATE DOMAIN us_postal_code AS TEXT CHECK(VALUE ~ '^\d{5}$' OR VALUE ~ '^\d{5}-\d{4}$');
CREATE DOMAIN _STATUS CHAR(1) CHECK (value IN ( 'W' , 'C', 'R' ) );
CREATE DOMAIN _GENDER CHAR(1) CHECK (value IN ( 'F' , 'M' ) );
CREATE DOMAIN _CODE CHAR(2) CHECK (value IN ( 'MJ' , 'MN', 'SV' ) ); --Major, Minimum, Service
CREATE DOMAIN _PINTEGER AS int4 CHECK(VALUE > 0);
CREATE DOMAIN _PZEROINTEGER AS int4 CHECK(VALUE >= 0);
CREATE DOMAIN _YEARS AS int4 CHECK(VALUE >= 0 AND VALUE < 100);
CREATE DOMAIN _YEAR AS int4 CHECK(VALUE >= 1970);

------------
---TABLES---
------------

-- made changes to id (in Customer, Mechanic) so that id would be auto-generated and incremented with each valid insert and used
-- alter sequence statement so that we can change id in the respective tables
CREATE TABLE Customer
(
    id integer NOT NULL DEFAULT nextval('customer_id_seq'),
	fname CHAR(32) NOT NULL,
	lname CHAR(32) NOT NULL,
	phone CHAR(13) NOT NULL,
	address CHAR(256) NOT NULL,
	PRIMARY KEY (id)
);
ALTER SEQUENCE customer_id_seq OWNED BY Customer.id;

CREATE TABLE Mechanic
(
    id integer NOT NULL DEFAULT nextval('mechanic_id_seq'),
	fname CHAR(32) NOT NULL,
	lname CHAR(32) NOT NULL,
	experience _YEARS NOT NULL,
	PRIMARY KEY (id) 
);
ALTER SEQUENCE mechanic_id_seq OWNED BY Mechanic.id;

CREATE TABLE Car
(
	vin VARCHAR(16) NOT NULL,
	make VARCHAR(32) NOT NULL,
	model VARCHAR(32) NOT NULL,
	year _YEAR NOT NULL,
	PRIMARY KEY (vin)
);
---------------
---RELATIONS---
---------------
CREATE TABLE Owns
(
	ownership_id INTEGER NOT NULL,
	customer_id INTEGER NOT NULL,
	car_vin VARCHAR(16) NOT NULL,
	PRIMARY KEY (ownership_id),
	FOREIGN KEY (customer_id) REFERENCES Customer(id),
	FOREIGN KEY (car_vin) REFERENCES Car(vin)
);

CREATE TABLE Service_Request
(
	rid INTEGER NOT NULL,
	customer_id INTEGER NOT NULL,
	car_vin VARCHAR(16) NOT NULL,
	date TIMESTAMP NOT NULL,
	odometer _PINTEGER NOT NULL,
	complain TEXT,
	PRIMARY KEY (rid),
	FOREIGN KEY (customer_id) REFERENCES Customer(id),
	FOREIGN KEY (car_vin) REFERENCES Car(vin)
);

CREATE TABLE Closed_Request
(
--     wid integer NOT NULL DEFAULT nextval('closed_request_seq'),
	wid INTEGER NOT NULL,
	rid INTEGER NOT NULL,
	mid INTEGER NOT NULL,
	date TIMESTAMP NOT NULL,
	comment TEXT,
	bill _PINTEGER NOT NULL,
	PRIMARY KEY (wid),
	FOREIGN KEY (rid) REFERENCES Service_Request(rid),
	FOREIGN KEY (mid) REFERENCES Mechanic(id)
);
-- ALTER SEQUENCE closed_request_seq OWNED BY Closed_Request.wid;


----------------------------
-- INSERT DATA STATEMENTS --
----------------------------

COPY Customer (
	id,
	fname,
	lname,
	phone,
	address
)
FROM 'C:\Users\Emily Mai\Documents\CS166\Project\Phase_3\code\data\customer.csv'
WITH DELIMITER ',';

COPY Mechanic (
	id,
	fname,
	lname,
	experience
)
FROM 'C:\Users\Emily Mai\Documents\CS166\Project\Phase_3\code\data\mechanic.csv'
WITH DELIMITER ',';

COPY Car (
	vin,
	make,
	model,
	year
)
FROM 'C:\Users\Emily Mai\Documents\CS166\Project\Phase_3\code\data\car.csv'
WITH DELIMITER ',';

COPY Owns (
	ownership_id,
	customer_id,
	car_vin
)
FROM 'C:\Users\Emily Mai\Documents\CS166\Project\Phase_3\code\data\owns.csv'
WITH DELIMITER ',';

COPY Service_Request (
	rid,
	customer_id,
	car_vin,
	date,
	odometer,
	complain
)
FROM 'C:\Users\Emily Mai\Documents\CS166\Project\Phase_3\code\data\service_request.csv'
WITH DELIMITER ',';

COPY Closed_Request (
	wid,
	rid,
	mid,
	date,
	comment,
	bill
)
FROM 'C:\Users\Emily Mai\Documents\CS166\Project\Phase_3\code\data\closed_request.csv'
WITH DELIMITER ',';
