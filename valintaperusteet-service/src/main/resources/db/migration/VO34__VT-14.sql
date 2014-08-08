ALTER TABLE hakijaryhma ADD COLUMN tarkkakiintio boolean DEFAULT false;
ALTER TABLE hakijaryhma ADD COLUMN kaytakaikki boolean DEFAULT false;

ALTER TABLE hakijaryhma DROP CONSTRAINT FK340BF187E4C115D4;
ALTER TABLE hakijaryhma DROP CONSTRAINT FK340BF1877C1201F8;
ALTER TABLE hakijaryhma DROP CONSTRAINT FK340BF187B3D288E6;

ALTER TABLE hakijaryhma DROP COLUMN hakukohde_viite_id;
ALTER TABLE hakijaryhma DROP COLUMN edellinen_hakijaryhma_id;
ALTER TABLE hakijaryhma DROP COLUMN master_hakijaryhma_id;

ALTER TABLE hakijaryhma_jono ALTER COLUMN valintatapajono_id DROP NOT NULL;

ALTER TABLE hakijaryhma_jono ADD COLUMN hakukohde_viite_id BIGINT;
alter table hakijaryhma_jono add constraint FK_4objpla3og2wm717mlrbq84xu foreign key (hakukohde_viite_id) references hakukohde_viite;







