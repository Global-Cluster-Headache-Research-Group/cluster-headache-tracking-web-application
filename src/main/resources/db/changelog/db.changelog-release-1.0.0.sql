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

--CREATE EXTENSION IF NOT EXISTS timescaledb;

--in case if we will have to use UUIDs
--CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

--in order to install postgis
--https://postgis.net/docs/postgis_installation.html
--CREATE EXTENSION IF NOT EXISTS 	postgis;

-- changeset yilativs:1 context:common failOnError: true

CREATE SCHEMA profile; -- attacks, treatments, hormone test results, heart and motion activity
CREATE SCHEMA metadata; -- treatment types
CREATE SCHEMA report; -- user profile related data: roles, privileges, passwords

CREATE TABLE profile.patient(
	id SERIAL PRIMARY KEY,
	login VARCHAR(255) UNIQUE,
	email VARCHAR(1000) ,--see how validate email https://dba.stackexchange.com/questions/68266/what-is-the-best-way-to-store-an-email-address-in-postgresql
	birthday DATE,
	name VARCHAR(1000) NOT NULL,
	password_hash bytea,
	gender SMALLINT,
	is_blocked BOOLEAN DEFAULT FALSE,
	is_deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE report.attack (
    id SERIAL PRIMARY KEY,
    comments character varying(1000),
    max_pain_level integer NOT NULL,
    started timestamp without time zone NOT NULL,
    stopped timestamp without time zone,
    while_asleep boolean,
    patient_id integer NOT NULL REFERENCES profile.patient(id),
    CONSTRAINT attack_max_pain_level_check CHECK (((max_pain_level >= 1) AND (max_pain_level <= 10)))
);

CREATE TABLE metadata.abortive_treatment_type(
	id SERIAL PRIMARY KEY,
    name VARCHAR(1000) NOT NULL,
    units VARCHAR(200) NOT NULL,
    trade_name VARCHAR(200),
    UNIQUE (name, units)
);

CREATE TABLE report.abortive_treatment (
    id SERIAL PRIMARY KEY,
    comments character varying(1000),
    doze integer NOT NULL,
    started timestamp without time zone NOT NULL,
    stopped timestamp without time zone,
    successful boolean,
    patient_id integer NOT NULL REFERENCES profile.patient(id),
    abortive_treatment_type_id integer NOT NULL REFERENCES metadata.abortive_treatment_type(id),
    attack_id integer NOT NULL REFERENCES report.attack(id)
);

CREATE TABLE metadata.preventive_treatment_type(
	id SERIAL PRIMARY KEY,
    name VARCHAR(1000) NOT NULL,
    units VARCHAR(200) NOT NULL,
    trade_name VARCHAR(200),
    UNIQUE (name, units)
);

CREATE TABLE report.preventive_treatment (
    id SERIAL PRIMARY KEY,
    comments character varying(1000),
    doze integer NOT NULL,
    started timestamp without time zone NOT NULL,
    stopped timestamp without time zone,
    patient_id integer NOT NULL REFERENCES profile.patient(id),
    preventive_treatment_type_id integer NOT NULL REFERENCES metadata.preventive_treatment_type(id)
);

INSERT INTO metadata.abortive_treatment_type (name,units) VALUES ('100% oxygen via nonrebreathing mask','lpm');--lpm stands for litters per minute
INSERT INTO metadata.abortive_treatment_type (name,units) VALUES ('100% oxygen via demand valve','lpm');--lpm stands for litters per minute
INSERT INTO metadata.abortive_treatment_type (name,units) VALUES ('Sumatriptan injection','mg');
INSERT INTO metadata.abortive_treatment_type (name,units) VALUES ('Sumatriptan nasal powder','mg');
INSERT INTO metadata.abortive_treatment_type (name,units) VALUES ('Sumatriptan nasal spray','mg');
INSERT INTO metadata.abortive_treatment_type (name,units) VALUES ('Sumatriptan pills','mg');
INSERT INTO metadata.abortive_treatment_type (name,units) VALUES ('Caffeine in a drink e.g. coffee, redbull or pepsi','mg');
INSERT INTO metadata.abortive_treatment_type (name,units) VALUES ('Caffeine pills','mg');
INSERT INTO metadata.abortive_treatment_type (name,units,trade_name) VALUES ('Galcanezumab','mg','Emgality');
INSERT INTO metadata.abortive_treatment_type (name,units,trade_name) VALUES ('Erenumab','mg','Aimovig');
INSERT INTO metadata.abortive_treatment_type (name,units,trade_name) VALUES ('Fremanezumab','mg','Ajovy');
INSERT INTO metadata.abortive_treatment_type (name,units) VALUES ('Lidocaine drops 4%','mg');
INSERT INTO metadata.abortive_treatment_type (name,units,trade_name) VALUES ('Acetylsalicylic  acid','mg','Aspirin');
INSERT INTO metadata.abortive_treatment_type (name,units) VALUES ('SPG Neurostimulator' ,'seconds');
INSERT INTO metadata.abortive_treatment_type (name,units) VALUES ('Gammacore Neurostimulator' ,'seconds');
INSERT INTO metadata.abortive_treatment_type (name,units) VALUES ('Cannabis inhalation','mg');--considered as both, abortive and preventive 
INSERT INTO metadata.abortive_treatment_type (name,units) VALUES ('Cardio Workout','bpm');--heart beats per minute
INSERT INTO metadata.abortive_treatment_type (name,units) VALUES ('Hyperventilation','bpm');--heart beats per minute
INSERT INTO metadata.abortive_treatment_type (name,units) VALUES ('Tetrahydrocannabinol inhalant','mg'); -- not yet listed as an official treatment
INSERT INTO metadata.abortive_treatment_type (name,units) VALUES ('Cannabidiol inhalant','mg'); -- not yet listed as an official treatment
INSERT INTO metadata.abortive_treatment_type (name,units) VALUES ('Betamethasone injection','mcg'); -- not yet listed as an official treatment (careful mcg=1000mg)

INSERT INTO metadata.preventive_treatment_type (name,units) VALUES ('Cannabis inhalation','mg');--considered as both, abortive and preventive
INSERT INTO metadata.preventive_treatment_type (name,units) VALUES ('Verapamil','mg');--preventive
INSERT INTO metadata.preventive_treatment_type (name,units,trade_name) VALUES ('Cholecalciferol pills 1mcg=40IU','IU','Vitamin D3'); --preventive
INSERT INTO metadata.preventive_treatment_type (name,units) VALUES ('Vitamin D3 sun exposure','seconds'); --preventive
INSERT INTO metadata.preventive_treatment_type (name,units) VALUES ('Vitamin D3 UVB lamp','seconds'); --preventive
INSERT INTO metadata.preventive_treatment_type (name,units) VALUES ('Psilocybin mushroom','g');--preventive
INSERT INTO metadata.preventive_treatment_type (name,units) VALUES ('LSA','mg');--preventive
INSERT INTO metadata.preventive_treatment_type (name,units) VALUES ('LSD','mg');--preventive 
INSERT INTO metadata.preventive_treatment_type (name,units) VALUES ('Lithium','mg'); --preventive
INSERT INTO metadata.preventive_treatment_type (name,units) VALUES ('Topiramate','mg'); --preventive
INSERT INTO metadata.preventive_treatment_type (name,units) VALUES ('Testosterone','mg'); --preventive
INSERT INTO metadata.preventive_treatment_type (name,units) VALUES ('Tianeptine','mg'); --preventive
INSERT INTO metadata.preventive_treatment_type (name,units) VALUES ('Melatonin','mg'); --preventive
INSERT INTO metadata.preventive_treatment_type (name,units) VALUES ('Ergotamine','mg'); --preventive
INSERT INTO metadata.preventive_treatment_type (name,units) VALUES ('Valproate','mg'); --preventive
INSERT INTO metadata.preventive_treatment_type (name,units) VALUES ('Propranolol','mg'); --preventive
INSERT INTO metadata.preventive_treatment_type (name,units) VALUES ('Tetrahydrocannabinol inhalant','mg'); --preventive, not yet listed as an official treatment
INSERT INTO metadata.preventive_treatment_type (name,units) VALUES ('Cannabidiol inhalant','mg'); --preventive, not yet listed as an official treatment
INSERT INTO metadata.preventive_treatment_type (name,units) VALUES ('Betamethasone injection','mcg'); --preventive, not yet listed as an official treatment (careful mcg=1000mg)


INSERT INTO profile.patient (login, email ,birthday ,name , password_hash , gender , is_blocked,is_deleted) VALUES('yilativs','yilativs@somemail.com','1978-01-01','Vitaliy Semochkin',NULL,1,FALSE,FALSE);
INSERT INTO profile.patient (login, email ,birthday ,name , password_hash , gender , is_blocked,is_deleted) VALUES('pavias','pavia@somemail.com','1974-12-15','Pavia Anderson',NULL,2,FALSE,FALSE);


