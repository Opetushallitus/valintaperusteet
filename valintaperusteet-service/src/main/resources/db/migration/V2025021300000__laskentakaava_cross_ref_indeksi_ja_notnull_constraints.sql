ALTER TABLE laskentakaava ALTER COLUMN funktiokutsu_id DROP NOT NULL;
ALTER TABLE laskentakaava_history ALTER COLUMN funktiokutsu_id DROP NOT NULL;

ALTER TABLE laskentakaava ALTER COLUMN funktiokutsu SET NOT NULL;
ALTER TABLE laskentakaava_history ALTER COLUMN funktiokutsu SET NOT NULL;

-- Lisätään indeksi jota käytetään kun haetaan kaavoja joihin on viitattu toisissa kaavoissa, sisältää kaavan viittaukset toisiin kaavoihin
--  esimerkkihaku: SELECT id FROM laskentakaava WHERE jsonb_path_query_array(funktiokutsu, 'strict $.**.laskentakaavaChild.id') @@ '$[*]==<laskentakaavan id>';
CREATE INDEX IF NOT EXISTS laskentakaava_cross_reference_idx ON laskentakaava USING GIN (jsonb_path_query_array(funktiokutsu, 'strict $.**.laskentakaavaChild.id'));