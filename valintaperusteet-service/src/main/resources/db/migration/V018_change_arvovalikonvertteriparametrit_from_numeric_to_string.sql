ALTER TABLE arvovalikonvertteriparametri ALTER COLUMN minvalue TYPE varchar(255);
ALTER TABLE arvovalikonvertteriparametri ALTER COLUMN maxvalue TYPE varchar(255);
ALTER TABLE arvovalikonvertteriparametri ALTER COLUMN palauta_haettu_arvo TYPE varchar(255);

ALTER TABLE arvovalikonvertteriparametri DROP COLUMN hylkaysperuste;

ALTER TABLE arvokonvertteriparametri ALTER COLUMN hylkaysperuste TYPE varchar(255);
