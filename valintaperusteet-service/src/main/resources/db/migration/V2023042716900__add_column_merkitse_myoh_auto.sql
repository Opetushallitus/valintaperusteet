alter table valintatapajono add column merkitse_myoh_auto boolean;
update valintatapajono set merkitse_myoh_auto = false;
alter table valintatapajono alter column merkitse_myoh_auto set not null;
