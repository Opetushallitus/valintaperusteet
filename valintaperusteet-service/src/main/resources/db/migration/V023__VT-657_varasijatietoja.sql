alter table valintatapajono add column varasijan_tayttojono_id int8;
alter table valintatapajono add column varasijoja_kaytetaan_alkaen timestamp;
alter table valintatapajono add column varasijoja_taytetaan_asti timestamp;

alter table valintatapajono add column kaytetaan_valintalaskentaa boolean;
update valintatapajono set kaytetaan_valintalaskentaa = true;
alter table valintatapajono alter column kaytetaan_valintalaskentaa set not null;

alter table valintatapajono drop column varasija_taytto_paivat;

alter table valintatapajono
  add constraint FK82B665D7394BE571
  foreign key (varasijan_tayttojono_id)
  references valintatapajono;