alter table valintatapajono add column ei_varasijatayttoa boolean;
alter table valintatapajono alter column ei_varasijatayttoa set not null;