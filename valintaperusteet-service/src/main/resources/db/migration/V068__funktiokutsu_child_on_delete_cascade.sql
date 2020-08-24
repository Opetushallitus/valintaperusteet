ALTER TABLE arvovalikonvertteriparametri
DROP CONSTRAINT fk2f41e402655ceaf9,
ADD CONSTRAINT fk2f41e402655ceaf9 FOREIGN KEY (funktiokutsu_id) REFERENCES funktiokutsu(id) ON DELETE CASCADE;

ALTER TABLE arvokonvertteriparametri
DROP CONSTRAINT fkcd06f9a655ceaf9,
ADD CONSTRAINT fkcd06f9a655ceaf9 FOREIGN KEY (funktiokutsu_id) REFERENCES funktiokutsu(id) ON DELETE CASCADE;

ALTER TABLE valintaperuste_viite
DROP CONSTRAINT fk78bbb121655ceaf9,
ADD CONSTRAINT fk78bbb121655ceaf9 FOREIGN KEY (funktiokutsu_id) REFERENCES funktiokutsu(id) ON DELETE CASCADE;

ALTER TABLE syoteparametri
DROP CONSTRAINT fkd5afaf79655ceaf9,
ADD CONSTRAINT fkd5afaf79655ceaf9 FOREIGN KEY (funktiokutsu_id) REFERENCES funktiokutsu(id) ON DELETE CASCADE;

ALTER TABLE funktioargumentti
DROP CONSTRAINT fk589bc2c0ad95a88f,
ADD CONSTRAINT fk589bc2c0ad95a88f FOREIGN KEY (funktiokutsuparent_id) REFERENCES funktiokutsu(id) ON DELETE CASCADE;
