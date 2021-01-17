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
--CREATE EXTENSION IF NOT EXISTS timescaledb;

--in case if we will have to use UUIDs
--CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

--in order to install postgis
--https://postgis.net/docs/postgis_installation.html
--CREATE EXTENSION IF NOT EXISTS 	postgis;


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
	is_blocked BOOLEAN DEFAULT FALSE,
	is_deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE metadata.treatment_type(
	id SERIAL PRIMARY KEY,
    name VARCHAR(1000) NOT NULL,
    units VARCHAR(200) NOT NULL,
    trade_name VARCHAR(200),
    is_abortive BOOLEAN DEFAULT FALSE,
    is_preventive BOOLEAN  DEFAULT FALSE,
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

--SELECT create_hypertable('report.sleep', 'started');

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

CREATE TABLE report.abortive_treatment(
	attack_started TIMESTAMP NOT NULL,
	started TIMESTAMP NOT NULL,
	stopped TIMESTAMP CHECK (stopped > started),--some treatments like oxygen or aer
	patient_id INT NOT NULL REFERENCES profile.patient(id),
	treatment_type_id INT NOT NULL REFERENCES metadata.treatment_type(id),
    doze INT NOT NULL, CHECK (doze > 0),
    successful BOOLEAN, --true=worked, false=didn't work, null==unknown
    comments VARCHAR(4000),
	PRIMARY KEY (patient_id,treatment_type_id,started,attack_started)
);
--SELECT create_hypertable('report.abortive_treatment', 'started');

CREATE TABLE report.preventive_treatment(
	started TIMESTAMP NOT NULL,
	patient_id INT NOT NULL REFERENCES profile.patient(id),
	treatment_type_id INT NOT NULL REFERENCES metadata.treatment_type(id),
	doze INT NOT NULL, CHECK (doze > 0),
	comments VARCHAR(4000),
	PRIMARY KEY (patient_id,started,treatment_type_id)
);
--SELECT create_hypertable('report.preventive_treatment', 'started');


INSERT INTO metadata.treatment_type (name,units,is_abortive) VALUES ('100% oxygen via nonrebreathing mask','lpm',TRUE);--lpm stands for litters per minute
INSERT INTO metadata.treatment_type (name,units,is_abortive) VALUES ('100% oxygen via demand valve','lpm',TRUE);--lpm stands for litters per minute
INSERT INTO metadata.treatment_type (name,units,is_abortive) VALUES ('Sumatriptan injection','mg',TRUE);
INSERT INTO metadata.treatment_type (name,units,is_abortive) VALUES ('Sumatriptan nasal powder','mg',TRUE);
INSERT INTO metadata.treatment_type (name,units,is_abortive) VALUES ('Sumatriptan nasal spray','mg',TRUE);
INSERT INTO metadata.treatment_type (name,units,is_abortive) VALUES ('Sumatriptan pills','mg',TRUE);
INSERT INTO metadata.treatment_type (name,units,is_abortive) VALUES ('Caffeine in a drink e.g. coffee, redbull or pepsi','mg',TRUE);
INSERT INTO metadata.treatment_type (name,units,is_abortive) VALUES ('Caffeine pills','mg',TRUE);
INSERT INTO metadata.treatment_type (name,units,trade_name,is_abortive) VALUES ('Galcanezumab','mg','Emgality',TRUE);
INSERT INTO metadata.treatment_type (name,units,trade_name,is_abortive) VALUES ('Erenumab','mg','Aimovig',TRUE);
INSERT INTO metadata.treatment_type (name,units,trade_name,is_abortive) VALUES ('Fremanezumab','mg','Ajovy',TRUE);
INSERT INTO metadata.treatment_type (name,units,is_abortive) VALUES ('Lidocaine drops 4%','mg',TRUE);
INSERT INTO metadata.treatment_type (name,units,trade_name,is_abortive) VALUES ('Acetylsalicylic  acid','mg','Aspirin',TRUE);
INSERT INTO metadata.treatment_type (name,units,is_abortive) VALUES ('SPG Neurostimulator' ,'seconds',TRUE);
INSERT INTO metadata.treatment_type (name,units,is_abortive) VALUES ('Gammacore Neurostimulator' ,'seconds',TRUE);
INSERT INTO metadata.treatment_type (name,units,is_abortive,is_preventive) VALUES ('Cannabis inhalation','mg',TRUE,TRUE);--considered as both, abortive and preventive 
INSERT INTO metadata.treatment_type (name,units,is_abortive) VALUES ('Cardio Workout','bpm',TRUE);--heart beats per minute
INSERT INTO metadata.treatment_type (name,units,is_abortive) VALUES ('Hyperventilation','bpm',TRUE);--heart beats per minute
INSERT INTO metadata.treatment_type (name,units,is_preventive) VALUES ('Verapamil','mg',TRUE);--preventive
INSERT INTO metadata.treatment_type (name,units,trade_name,is_preventive) VALUES ('Cholecalciferol pills 1mcg=40IU','IU','Vitamin D3',TRUE); --preventive
INSERT INTO metadata.treatment_type (name,units,is_preventive) VALUES ('Vitamin D3 sun exposure','seconds',TRUE); --preventive
INSERT INTO metadata.treatment_type (name,units,is_preventive) VALUES ('Vitamin D3 UVB lamp','seconds',TRUE); --preventive
INSERT INTO metadata.treatment_type (name,units,is_preventive) VALUES ('Psilocybin mushroom','g',TRUE);--preventive
INSERT INTO metadata.treatment_type (name,units,is_preventive) VALUES ('LSA','mg',TRUE);--preventive
INSERT INTO metadata.treatment_type (name,units,is_preventive) VALUES ('LSD','mg',TRUE);--preventive 
INSERT INTO metadata.treatment_type (name,units,is_preventive) VALUES ('Lithium','mg',TRUE); --preventive
INSERT INTO metadata.treatment_type (name,units,is_preventive) VALUES ('Topiramate','mg',TRUE); --preventive
INSERT INTO metadata.treatment_type (name,units,is_preventive) VALUES ('Testosterone','mg',TRUE); --preventive
INSERT INTO metadata.treatment_type (name,units,is_preventive) VALUES ('Tianeptine','mg',TRUE); --preventive
INSERT INTO metadata.treatment_type (name,units,is_preventive) VALUES ('Melatonin','mg',TRUE); --preventive
INSERT INTO metadata.treatment_type (name,units,is_preventive) VALUES ('Ergotamine','mg',TRUE); --preventive
INSERT INTO metadata.treatment_type (name,units,is_preventive) VALUES ('Valproate','mg',TRUE); --preventive
INSERT INTO metadata.treatment_type (name,units,is_preventive) VALUES ('Propranolol','mg',TRUE); --preventive
INSERT INTO metadata.treatment_type (name,units,is_preventive) VALUES ('Tetrahydrocannabinol inhalant','mg',TRUE); --preventive, not yet listed as an official treatment
INSERT INTO metadata.treatment_type (name,units,is_preventive) VALUES ('Cannabidiol inhalant','mg',TRUE); --preventive, not yet listed as an official treatment
INSERT INTO metadata.treatment_type (name,units,is_preventive) VALUES ('Betamethasone injection','mcg',TRUE); --preventive, not yet listed as an official treatment (careful mcg=1000mg


INSERT INTO profile.patient (login, email ,birthday ,name , password_hash , gender , is_blocked,is_deleted) VALUES('yilativs','yilativs@somemail.com','1978-01-01','Vitaliy Semochkin',NULL,1,FALSE,FALSE);
INSERT INTO profile.patient (login, email ,birthday ,name , password_hash , gender , is_blocked,is_deleted) VALUES('pavias','pavia@somemail.com','1974-12-15','Pavia Anderson',NULL,2,FALSE,FALSE);

