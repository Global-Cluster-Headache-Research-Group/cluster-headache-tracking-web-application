-- liquibase formatted sql

--before execution
--create chtracker with password 'chtrackerSecret';
--create database chtracker with owner chtracker;

--\connect chtracker
--CREATE EXTENSION IF NOT EXISTS timescaledb;

--in case if we will have to use UUIDs
--CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


--in order to install postgis
--https://postgis.net/docs/postgis_installation.html
--CREATE EXTENSION IF NOT EXISTS EXTENSION postgis;

-- if you built with raster support and want to install it --
--CREATE EXTENSION postgis_raster;

-- if you want to install topology support --
--CREATE EXTENSION postgis_topology;

-- if you built with sfcgal support and want to install it --
--CREATE EXTENSION postgis_sfcgal;

-- if you want to install tiger geocoder --
--CREATE EXTENSION fuzzystrmatch";
--CREATE EXTENSION postgis_tiger_geocoder;

-- if you installed with pcre
-- you should have address standardizer extension as well
--CREATE EXTENSION address_standardizer;

-- changeset yilativs:1 context:common failOnError: true
CREATE EXTENSION IF NOT EXISTS timescaledb;

--in case if we will have to use UUIDs
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

--in order to install postgis
--https://postgis.net/docs/postgis_installation.html
CREATE EXTENSION IF NOT EXISTS 	postgis;


--DROP SCHEMA IF EXISTS reports CASCADE; -- attacks, treatments
--DROP SCHEMA IF EXISTS metadata CASCADE; -- treatment types
--DROP SCHEMA IF EXISTS profile CASCADE; -- user profile related data: roles, privileges, passwords



CREATE SCHEMA profile;
CREATE SCHEMA metadata;
CREATE SCHEMA report;

CREATE TABLE profile.patient(
	id SERIAL PRIMARY KEY,
	login VARCHAR(255) UNIQUE,
	email VARCHAR(1000) ,--see how validate email https://dba.stackexchange.com/questions/68266/what-is-the-best-way-to-store-an-email-address-in-postgresql
	birthday DATE,
	name VARCHAR(1000) NOT NULL,
	password_hash bytea[],
	gender SMALLINT,
	is_blocked BOOLEAN,
	is_deleted BOOLEAN
);

CREATE TABLE metadata.treatment_type(
	id SERIAL PRIMARY KEY,
    name VARCHAR(1000) NOT NULL,
    units VARCHAR(200) NOT NULL,
    trade_name VARCHAR(200),
    UNIQUE (name, units)
);

CREATE TABLE metadata.treatment_form(
	id SERIAL PRIMARY KEY,
    name VARCHAR(1000) NOT NULL,--pills including brand name,injection,ingalation and so on
    units VARCHAR(200) NOT NULL,--perhaps units should be removed from treatment_type
    trade_name VARCHAR(200),
    UNIQUE (name, units)
);

--allowed methods for a given form
CREATE TABLE metadata.treatment_type_form(
	treatment_type_id INT NOT NULL REFERENCES metadata.treatment_type(id),
   	treatment_form_id INT NOT NULL REFERENCES metadata.treatment_form(id),
    PRIMARY KEY (treatment_type_id, treatment_form_id)
);


CREATE TABLE metadata.hormone(
	id SERIAL PRIMARY KEY,
    name VARCHAR(1000) NOT NULL,
    units VARCHAR(200) NOT NULL
);

CREATE TABLE report.hormone_test(
	started TIMESTAMP NOT NULL,	
	patient_id INT NOT NULL REFERENCES profile.patient(id),
	hormone_id INT NOT NULL REFERENCES metadata.hormone(id),
	value INT,
	PRIMARY KEY (patient_id,hormone_id,started)
);

CREATE TABLE report.blood_oxygen(
	started TIMESTAMP NOT NULL,	
	patient_id INT NOT NULL REFERENCES profile.patient(id),
	value INT,
	PRIMARY KEY (patient_id,started)
);

CREATE TABLE report.sleep(
	started TIMESTAMP NOT NULL,	
	stopped TIMESTAMP NOT NULL CHECK (stopped > started),
	patient_id INT NOT NULL REFERENCES profile.patient(id),
	deep_sleep_seconds INT CHECK (deep_sleep_seconds>=0),
	light_sleep_seconds INT CHECK (light_sleep_seconds>=0),
	rem_sleep_seconds INT CHECK (rem_sleep_seconds>=0),
	time_awake_seconds INT CHECK (time_awake_seconds>=0),
	PRIMARY KEY (patient_id,started)
);

SELECT create_hypertable('report.sleep', 'started');

CREATE TABLE report.heartrate(
	started TIMESTAMP NOT NULL,	
	bpm SMALLINT NOT NULL CHECK (bpm > 0 AND bpm < 1000),--max ever registred heart rate was 480
	patient_id INT NOT NULL REFERENCES profile.patient(id),
	PRIMARY KEY (patient_id,started)
);
--SELECT create_hypertable('report.heartrate', 'started');

CREATE TABLE report.weight(
	started TIMESTAMP NOT NULL,	
	kg SMALLINT NOT NULL CHECK (kg > 0 AND kg < 1000),
	patient_id INT NOT NULL REFERENCES profile.patient(id),
	PRIMARY KEY (patient_id,started)
);
--SELECT create_hypertable('report.weight', 'started');

-- systolic pressure (maximum pressure during one heartbeat) over  diastolic  pressure (minimum pressure between two heartbeats)
CREATE TABLE report.blood_pressure(
	started TIMESTAMP NOT NULL,	
	diastolic SMALLINT NOT NULL CHECK (diastolic > 0 AND diastolic < 1000 AND diastolic<systolic),
	systolic SMALLINT NOT NULL CHECK (systolic > 0 AND systolic < 1000),
	patient_id INT NOT NULL REFERENCES profile.patient(id),
	PRIMARY KEY (patient_id,started)
);
--SELECT create_hypertable('report.blood_pressure', 'started');


CREATE TABLE report.attack(
	started TIMESTAMP NOT NULL,	
	stopped TIMESTAMP NOT NULL CHECK (stopped > started),
	patient_id INT NOT NULL REFERENCES profile.patient(id),
	max_pain_level SMALLINT NOT NULL CHECK (max_pain_level > 0 AND max_pain_level < 11),
	while_asleep BOOLEAN,--null means unknown
--	point geometry(Point, 4326),
	comments VARCHAR(4000),
	PRIMARY KEY (patient_id,started)
);
--SELECT create_hypertable('report.attack', 'started');

CREATE TABLE report.abortive_treatment_usage(
	started TIMESTAMP NOT NULL,
	stopped TIMESTAMP CHECK (stopped > started),--some treatments like oxygen or aer
	attack_started TIMESTAMP NOT NULL,
	patient_id INT NOT NULL REFERENCES profile.patient(id),
	treatment_type_id INT NOT NULL REFERENCES metadata.treatment_type(id),
    doze INT NOT NULL, CHECK (doze > 0),
    successful BOOLEAN, --true=worked, false=didn't work, null==unknown
    comments VARCHAR(4000),
	PRIMARY KEY (patient_id,treatment_type_id,started,attack_started)
);
--SELECT create_hypertable('report.abortive_treatment_usage', 'started');

CREATE TABLE report.preventive_treatment_usage(
	started TIMESTAMP NOT NULL,
	patient_id INT NOT NULL REFERENCES profile.patient(id),
	treatment_type_id INT NOT NULL REFERENCES metadata.treatment_type(id),
	doze INT NOT NULL, CHECK (doze > 0),
	comments VARCHAR(4000),
	PRIMARY KEY (patient_id,started,treatment_type_id)
);
--SELECT create_hypertable('report.preventive_treatment_usage', 'started');


INSERT INTO metadata.treatment_type (name,units) VALUES ('100% O2 via nonrebreathing mask','lpm');--lpm stands for litters per minute
INSERT INTO metadata.treatment_type (name,units) VALUES ('100% O2 via demand valve','lpm');--lpm stands for litters per minute
INSERT INTO metadata.treatment_type (name,units) VALUES ('Sumatriptan injection','mg');
INSERT INTO metadata.treatment_type (name,units) VALUES ('Sumatriptan nasal powder','mg');
INSERT INTO metadata.treatment_type (name,units) VALUES ('Sumatriptan nasal spray','mg');
INSERT INTO metadata.treatment_type (name,units) VALUES ('Sumatriptan pills','mg');
INSERT INTO metadata.treatment_type (name,units) VALUES ('Caffeine drink, e.g. coffee or redbull','mg');
INSERT INTO metadata.treatment_type (name,units) VALUES ('Caffeine pills','mg');
INSERT INTO metadata.treatment_type (name,units,trade_name) VALUES ('Galcanezumab','mg','Emgality');
INSERT INTO metadata.treatment_type (name,units,trade_name) VALUES ('Erenumab','mg','Aimovig');
INSERT INTO metadata.treatment_type (name,units,trade_name) VALUES ('Fremanezumab','mg','Ajovy');
INSERT INTO metadata.treatment_type (name,units) VALUES ('Lidocaine drops','mg');
INSERT INTO metadata.treatment_type (name,units) VALUES ('Cardio Workout','bpm');--heart beats per minute
INSERT INTO metadata.treatment_type (name,units) VALUES ('Hyperventilation','bpm');--heart beats per minute
INSERT INTO metadata.treatment_type (name,units) VALUES ('Verapamil','mg');--preventive
INSERT INTO metadata.treatment_type (name,units) VALUES ('Vitamin D3 pills','IU'); --preventive
INSERT INTO metadata.treatment_type (name,units) VALUES ('Vitamin D3 sun exposure','seconds'); --preventive
INSERT INTO metadata.treatment_type (name,units) VALUES ('Vitamin D3 UVB lamp','minutes'); --preventive
INSERT INTO metadata.treatment_type (name,units) VALUES ('Psilocybin mushroom','g');--preventive
INSERT INTO metadata.treatment_type (name,units) VALUES ('LSA','mg');--preventive
INSERT INTO metadata.treatment_type (name,units) VALUES ('LSD','mg');--preventive 
INSERT INTO metadata.treatment_type (name,units) VALUES ('Lithium','mg'); --preventive
INSERT INTO metadata.treatment_type (name,units) VALUES ('Topiramate','mg'); --preventive
INSERT INTO metadata.treatment_type (name,units) VALUES ('Testosterone','mg'); --preventive
INSERT INTO metadata.treatment_type (name,units) VALUES ('Tianeptine','mg'); --preventive
INSERT INTO metadata.treatment_type (name,units) VALUES ('Melatonin','mg'); --preventive
INSERT INTO metadata.treatment_type (name,units) VALUES ('Ergotamine','mg'); --preventive
INSERT INTO metadata.treatment_type (name,units) VALUES ('Valproate','mg'); --preventive
INSERT INTO metadata.treatment_type (name,units) VALUES ('Propranolol','mg'); --preventive



INSERT INTO profile.patient (login, email ,birthday ,name , password_hash , gender , is_blocked) VALUES('yilativs','yilativs@gmail.com','1978-07-05','Semochking Vitaliy Evgenevich',NULL,1,FALSE);
INSERT INTO profile.patient (login, email ,birthday ,name , password_hash , gender , is_blocked) VALUES('john','do@spam.net','1974-12-15','John Dow',NULL,2,TRUE);

