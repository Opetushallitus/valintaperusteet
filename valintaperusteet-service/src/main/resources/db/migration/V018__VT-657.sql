alter table valintatapajono add column poissa_oleva_taytto boolean;
update valintatapajono set poissa_oleva_taytto = false;
alter table valintatapajono alter column poissa_oleva_taytto set not null;

alter table valintatapajono add column varasijat int4;
update valintatapajono set varasijat = 0;
alter table valintatapajono alter column varasijat set not null;

alter table valintatapajono add column varasija_taytto_paivat int4;
update valintatapajono set varasija_taytto_paivat = 0;
alter table valintatapajono alter column varasija_taytto_paivat set not null;
