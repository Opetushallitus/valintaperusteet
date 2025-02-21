ALTER TABLE laskentakaava ADD COLUMN IF NOT EXISTS funktiokutsu jsonb;
ALTER TABLE laskentakaava_history ADD COLUMN IF NOT EXISTS funktiokutsu jsonb;

-- päivitetään laskentakaavataulun historian päivitys koska kenttä lisätty
create or replace function update_laskentakaava_history() returns trigger
    language plpgsql
as $$
begin
insert into laskentakaava_history (
    id,
    version,
    kuvaus,
    nimi,
    on_luonnos,
    tyyppi,
    funktiokutsu_id,
    funktiokutsu,
    hakukohdeviite,
    valintaryhmaviite,
    kopio_laskentakaavasta_id,
    transaction_id,
    system_time
) values (
             old.id,
             old.version,
             old.kuvaus,
             old.nimi,
             old.on_luonnos,
             old.tyyppi,
             old.funktiokutsu_id,
             old.funktiokutsu,
             old.hakukohdeviite,
             old.valintaryhmaviite,
             old.kopio_laskentakaavasta_id,
             old.transaction_id,
             tstzrange(old.last_modified, now(), '[)')
         );
return null;
end;
$$;