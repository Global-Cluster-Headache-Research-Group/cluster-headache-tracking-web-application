--days between clusters of a patient
--find those whos remissions become longer and figure out why?
SELECT * FROM (
	SELECT patient_id, LAG(started,1) OVER (ORDER BY  patient_id,started), (LEAD(started,1) OVER (ORDER BY  patient_id,started))::date-started::date as pain_free_days 
	FROM report.attack )s
WHERE pain_free_days>30 AND pain_free_days<10000;

--avg cluster length
SELECT * FROM   
   (SELECT 
           patient_id, 
           LAG(started, 1)  OVER (ORDER BY patient_id, started) AS stopped_previous_cluster,
           started AS cluster_start_date,
           started::date - ( LAG(started, 1) OVER (ORDER BY patient_id, started) )::date  AS  days_from_previous_cluster
        FROM   report.attack
        ORDER  BY started) s
WHERE  ( days_from_previous_cluster > 30  AND days_from_previous_cluster < 10000 ); 


SELECT  patient_id, cluster_number, cluster_start_date, cluster_end_date,  cluster_end_date::date-cluster_start_date::date as cluster_length_days,  days_from_previous_cluster FROM(
	SELECT 
		patient_id, 
	    rank() OVER (PARTITION BY patient_id ORDER BY cluster_start_date) as cluster_number,
		cluster_start_date, 
		LEAD(stopped_previous_cluster, 1) OVER (PARTITION BY patient_id ORDER BY cluster_start_date) AS cluster_end_date,
		days_from_previous_cluster
		FROM(
			SELECT 
           		patient_id, 
           		LAG(started, 1)  OVER (ORDER BY patient_id, started) AS stopped_previous_cluster,
                started AS cluster_start_date,
                started :: DATE - ( LAG(started, 1) OVER  (PARTITION BY patient_id  ORDER BY started)):: date  AS  days_from_previous_cluster
        	FROM   report.attack
        	ORDER  BY started) s
	WHERE  ( days_from_previous_cluster > 30  AND days_from_previous_cluster < 10000 ))ss 
WHERE cluster_end_date IS NOT NULL
ORDER BY patient_id, cluster_start_date ;

-- find attacks per cluster, avg attacks per day, total time in pain,  avg pain level
--use NOW() to identify the distance from last cluster
SELECT a.patient_id, cluster_number,cluster_start_date, cluster_end_date, cluster_length_days, days_from_previous_cluster, COUNT(*)AS attacks_per_cluster, ROUND(COUNT(*)::numeric/cluster_length_days,2) AS avg_attacks_per_day,  ROUND(avg(max_pain_level)) AS avg_pain_level  FROM (
	SELECT  patient_id, cluster_number, cluster_start_date, cluster_end_date,  cluster_end_date::date-cluster_start_date::date AS cluster_length_days,  days_from_previous_cluster FROM(
		SELECT 
			patient_id, 
	    	rank() OVER (PARTITION BY patient_id ORDER BY cluster_start_date) as cluster_number,
			cluster_start_date, 
			LEAD(stopped_previous_cluster, 1) OVER (PARTITION BY patient_id ORDER BY cluster_start_date) AS cluster_end_date,
			days_from_previous_cluster
			FROM(
				SELECT 
           			patient_id, 
           			LAG(started, 1)  OVER (ORDER BY patient_id, started) AS stopped_previous_cluster,
                	started AS cluster_start_date,
                	started :: DATE - ( LAG(started, 1) OVER  (PARTITION BY patient_id  ORDER BY started))::date  AS  days_from_previous_cluster
        		FROM   report.attack
        		ORDER  BY started) s
		WHERE  ( days_from_previous_cluster > 30  AND days_from_previous_cluster < 10000 ))ss 
	WHERE cluster_end_date IS NOT NULL
	ORDER BY patient_id, cluster_start_date 
	) c INNER JOIN report.attack a on  (a.started BETWEEN c.cluster_start_date AND c.cluster_end_date)
GROUP BY a.patient_id, cluster_number,cluster_start_date, cluster_end_date, cluster_length_days, days_from_previous_cluster
ORDER BY patient_id, cluster_start_date;


--

SELECT patient_id, started::date, count(started), round(avg(max_pain_level),2) 
FROM report.attack
GROUP BY patient_id, started::date
ORDER BY patient_id, started;


--cluster lengths
--find which treatment causes shorter clusters (correlation with preventive drugs such as verapamil)
-- check individuals and find out which treatments  made their clusters shorter, if same true for majority with same treatment - thats the answer
--alternative approach take treatment, check if people having it had clusters shorter than without it (need a query - average cluster length with preventive treatment) 

--avg cluster length per treatment


--attacks frequencies per day
--find which treatment cause reduction of attack frequencies the most
--avg attacks frequencies per day per treatment (some treatments are supposed to last long, and some must be taken daily, 
--for each treatment there must be a way to validate, if the treatment is correctly used, on the other hand we don't know
--what is correct, e.g. verapamil could be given in 160 and do nothing for someone heavy, but for a small lady it can be more than enough,
--should we consider dose per patient weight? )

 
--populate attack table for performance testing
INSERT INTO profile.patient (gender,is_blocked,is_deleted,login,name)  SELECT random()*4, FALSE,FALSE,'t'||random()*1000000000,'tester-name'  FROM  generate_series(0, 10000);


INSERT INTO report.attack (id,max_pain_level,started,while_asleep,patient_id)  SELECT nextval('report.attack_seq'), 1+random()*4, timestamp '2014-01-10' + random() * (timestamp '2030-01-01'-timestamp '2014-01-10') ,random() > 0.5, 1+10000*random() FROM  generate_series(0, 10000000);

INSERT INTO report.abortive_treatment (id,doze,started,patient_id,abortive_treatment_type_id,attack_id)  SELECT nextval('report.abortive_treatment_seq'),random()*10, timestamp '2014-01-10' + random() * (timestamp '2030-01-01'-timestamp '2014-01-10') ,10000*random(), 10*random() ,100000*random() FROM  generate_series(0, 10000000);


--sample query
explain analyze  SELECT * FROM report.attack WHERE patient_id=1 AND max_pain_level>3 AND while_asleep=TRUE  ORDER BY started DESC LIMIT 1000;