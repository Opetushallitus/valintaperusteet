alter table funktiokutsu add column tallenna_tulos boolean;
update funktiokutsu set tallenna_tulos = false;
alter table funktiokutsu alter column tallenna_tulos set not null;

ALTER TABLE funktiokutsu ADD COLUMN tulos_tunniste character varying(255);

ALTER TABLE funktiokutsu ADD COLUMN tulos_teksti_fi character varying(4096);
ALTER TABLE funktiokutsu ADD COLUMN tulos_teksti_sv character varying(4096);
ALTER TABLE funktiokutsu ADD COLUMN tulos_teksti_en character varying(4096);
