-- Valintaryhmän kopioinnissa laskentakaavat kopioidaan uusilla tunnisteilla, jolloin kopioidun
-- kaavan funktiokutsu-jsonb:n sisäiset viittaukset toisiin laskentakaaviin täytyy osoittaa
-- kopioihin. Viittaus on muotoa {"laskentakaavaChild": {"id": <laskentakaavan id>}}.
--
-- kartta on jsonb-objekti jossa avain on lähdekaavan id merkkijonona ja arvo kopion id.
-- Kaavat joille ei löydy kartasta vastinetta jätetään ennalleen: ne ovat kopioitavan joukon
-- ulkopuolisia kaavoja joihin kopiokin saa viitata suoraan.
CREATE OR REPLACE FUNCTION kopioi_laskentakaavaviitteet(funktiokutsu jsonb, kartta jsonb)
    RETURNS jsonb
    LANGUAGE plpgsql
    IMMUTABLE
    PARALLEL SAFE
AS
$$
DECLARE
    tulos jsonb;
    avain text;
    arvo  jsonb;
    uusi  jsonb;
BEGIN
    IF funktiokutsu IS NULL THEN
        RETURN NULL;
    END IF;

    CASE jsonb_typeof(funktiokutsu)
        WHEN 'array' THEN
            RETURN (SELECT coalesce(jsonb_agg(kopioi_laskentakaavaviitteet(alkio, kartta)), '[]'::jsonb)
                    FROM jsonb_array_elements(funktiokutsu) AS alkio);
        WHEN 'object' THEN
            tulos := '{}'::jsonb;
            FOR avain, arvo IN SELECT * FROM jsonb_each(funktiokutsu)
                LOOP
                    IF avain = 'laskentakaavaChild'
                        AND jsonb_typeof(arvo) = 'object'
                        AND arvo ? 'id' THEN
                        uusi := kartta -> (arvo ->> 'id');
                        IF uusi IS NULL THEN
                            tulos := tulos || jsonb_build_object(avain, arvo);
                        ELSE
                            tulos := tulos || jsonb_build_object(avain, jsonb_build_object('id', uusi));
                        END IF;
                    ELSE
                        tulos := tulos || jsonb_build_object(avain, kopioi_laskentakaavaviitteet(arvo, kartta));
                    END IF;
                END LOOP;
            RETURN tulos;
        ELSE
            RETURN funktiokutsu;
        END CASE;
END;
$$;

COMMENT ON FUNCTION kopioi_laskentakaavaviitteet(jsonb, jsonb) IS
    'Korvaa funktiokutsu-puun laskentakaavaChild-viittaukset annetun vanha id -> uusi id -kartan mukaan.';

-- Kopioitaville riveille generoitava oid. Muoto vastaa DummyOidServiceImpl:n tuottamaa:
-- millisekunnit epochista ja satunnaisluku peräkkäin.
CREATE OR REPLACE FUNCTION kopioinnin_uusi_oid()
    RETURNS varchar
    LANGUAGE sql
    VOLATILE
AS
$$
SELECT (extract(epoch FROM clock_timestamp()) * 1000)::bigint::text
           || (floor(random() * 9007199254740992))::bigint::text;
$$;

COMMENT ON FUNCTION kopioinnin_uusi_oid() IS
    'Generoi uuden oidin valintaryhmän kopioinnissa syntyville riveille.';
