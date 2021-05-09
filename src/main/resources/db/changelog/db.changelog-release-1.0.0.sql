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

CREATE TABLE metadata.abortive_treatment_type(
	id SERIAL PRIMARY KEY,
    name VARCHAR(1000) NOT NULL,
    units VARCHAR(200) NOT NULL,
    trade_name VARCHAR(200),
    comments VARCHAR(1000),
    UNIQUE (name, units)
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

CREATE TABLE metadata.preventive_treatment_type(
	id SERIAL PRIMARY KEY,
    name VARCHAR(1000) NOT NULL,
    units VARCHAR(200) NOT NULL,
    trade_name VARCHAR(200),
    comments VARCHAR(10000),
    UNIQUE (name, units)
);
--ALTER TABLE ONLY metadata.preventive_treatment_type ADD CONSTRAINT preventive_treatment_type_pkey PRIMARY KEY (id);

INSERT INTO metadata.preventive_treatment_type (name,units) VALUES ('Cannabis inhalation','mg');--considered as both, abortive and preventive
INSERT INTO metadata.preventive_treatment_type (name,units) VALUES ('Verapamil','mg');
INSERT INTO metadata.preventive_treatment_type (name,units,trade_name,comments) VALUES ('Cholecalciferol','IU','Vitamin D3','1mcg=40IU. 100g of cod liver contains may contain up to 5000 IU. Vitamin D amount produced by UVB exposure depends on skin type, age, bulbs total power and in case of Sun on latitude, season and weather conditions.');
INSERT INTO metadata.preventive_treatment_type (name,units,trade_name,comments) VALUES ('LSA','mcg','Psilocybin mushroom','Illegal drug, LSA has been reported to abort attacks, to decrease frequency and intensity of attacks, and to induce remission in patients suffering from cluster headache. Official research is still in progress. 1 shroom ~ 0.5g 1g of shrooms ~ 10mg LSA');
INSERT INTO metadata.preventive_treatment_type (name,units,comments) VALUES ('LSD','mcg','Illegal drug, LSD has been reported to abort attacks, to decrease frequency and intensity of attacks, and to induce remission in patients suffering from cluster headache. Official research is still in progress'); 
INSERT INTO metadata.preventive_treatment_type (name,units) VALUES ('Lithium','mg');
INSERT INTO metadata.preventive_treatment_type (name,units) VALUES ('Topiramate','mg');
INSERT INTO metadata.preventive_treatment_type (name,units) VALUES ('Testosterone','mg');
INSERT INTO metadata.preventive_treatment_type (name,units) VALUES ('Tianeptine','mg');
INSERT INTO metadata.preventive_treatment_type (name,units) VALUES ('Melatonin','mg');
INSERT INTO metadata.preventive_treatment_type (name,units) VALUES ('Ergotamine','mg');
INSERT INTO metadata.preventive_treatment_type (name,units) VALUES ('Valproate','mg');
INSERT INTO metadata.preventive_treatment_type (name,units) VALUES ('Propranolol','mg');
INSERT INTO metadata.preventive_treatment_type (name,units,trade_name) VALUES ('Triamcinolone injection','mg','Kenalog');
INSERT INTO metadata.preventive_treatment_type (name,units,comments) VALUES ('Tetrahydrocannabinol inhalant','mg','not yet listed as an official treatment');
INSERT INTO metadata.preventive_treatment_type (name,units,comments) VALUES ('Cannabidiol inhalant','mg','not yet listed as an official treatment');
INSERT INTO metadata.preventive_treatment_type (name,units,comments) VALUES ('Betamethasone injection','mcg','not yet listed as an official treatment (careful mcg=1000mg)');
INSERT INTO metadata.preventive_treatment_type (name,units,comments) VALUES ('Indomethacin','mg','Should not be used for cluster headaches, often given to patients in order to exclude other headache types. Indomethacin-responsive headaches include a subset of trigeminal autonomic cephalalgias (paroxysmal hemicrania and hemicrania continua), Valsalva-induced headaches (cough headache, exercise headache, and sex headache), primary stabbing headache, and hypnic headache.'); 

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
CREATE INDEX patient__login_idx ON profile.patient USING btree (login);

CREATE TABLE report.attack (
    id integer PRIMARY KEY,
    comments character varying(1000),
    max_pain_level smallint NOT NULL,
    started timestamp without time zone NOT NULL,
    stopped timestamp without time zone,
    while_asleep boolean,
    patient_id integer NOT NULL,
    UNIQUE(patient_id, started),
    CONSTRAINT attack_max_pain_level_check CHECK (((max_pain_level >= 1) AND (max_pain_level <= 10)))
);
ALTER TABLE ONLY report.attack ADD CONSTRAINT attack__patient_fk FOREIGN KEY (patient_id) REFERENCES profile.patient(id);
ALTER TABLE ONLY report.attack ADD CONSTRAINT attack_uniq UNIQUE (patient_id, started);

CREATE SEQUENCE report.attack_seq START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 100;

CREATE TABLE report.abortive_treatment (
    id integer PRIMARY KEY,
    comments character varying(1000),
    doze integer NOT NULL,
    started timestamp without time zone NOT NULL,
    stopped timestamp without time zone,
    successful boolean,
    abortive_treatment_type_id integer NOT NULL,
    attack_id integer NOT NULL,
    UNIQUE (attack_id, started, abortive_treatment_type_id)
);

ALTER TABLE ONLY report.abortive_treatment ADD CONSTRAINT abortive_treatment__abortive_treatment_fk FOREIGN KEY (abortive_treatment_type_id) REFERENCES metadata.abortive_treatment_type(id);
ALTER TABLE ONLY report.abortive_treatment ADD CONSTRAINT abortive_treatment__attack_fk FOREIGN KEY (attack_id) REFERENCES report.attack(id);
CREATE INDEX abortive_treatment__abortive_treatment_type_idx ON report.abortive_treatment USING btree (abortive_treatment_type_id);
CREATE INDEX abortive_treatment__attack_id_idx ON report.abortive_treatment USING btree (attack_id);

CREATE SEQUENCE report.abortive_treatment_seq START WITH 1 INCREMENT BY 50 NO MINVALUE  NO MAXVALUE CACHE 100;

CREATE TABLE report.preventive_treatment (
    id integer PRIMARY KEY,
    comments character varying(1000),
    doze integer NOT NULL,
    started timestamp without time zone NOT NULL,
    stopped timestamp without time zone,
    patient_id integer NOT NULL,
    preventive_treatment_type_id integer NOT NULL,
    UNIQUE (patient_id, started, preventive_treatment_type_id)
);

ALTER TABLE ONLY report.preventive_treatment ADD CONSTRAINT preventive_treatment__patient_fk FOREIGN KEY (patient_id) REFERENCES profile.patient(id);
ALTER TABLE ONLY report.preventive_treatment ADD CONSTRAINT preventive_treatment__preventive_treatment_fk FOREIGN KEY (preventive_treatment_type_id) REFERENCES metadata.preventive_treatment_type(id);
CREATE INDEX preventive_treatment__patient_id_preventive_treatment_type_id_i ON report.preventive_treatment USING btree (patient_id, preventive_treatment_type_id);
CREATE INDEX preventive_treatment__patient_id_started_idx ON report.preventive_treatment USING btree (patient_id, started);

CREATE SEQUENCE report.preventive_treatment_seq START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 100;

INSERT INTO profile.patient (login, email ,birthday ,name , password_hash , gender , is_blocked,is_deleted) VALUES('yilativs','yilativs@somemail.com','1978-01-01','Vitaliy Semochkin',NULL,1,FALSE,FALSE);
INSERT INTO profile.patient (login, email ,birthday ,name , password_hash , gender , is_blocked,is_deleted) VALUES('pavias','pavia@somemail.com','1974-12-15','Pavia Anderson',NULL,2,FALSE,FALSE);

