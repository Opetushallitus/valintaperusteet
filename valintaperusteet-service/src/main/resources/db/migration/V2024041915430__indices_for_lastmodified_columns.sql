CREATE INDEX IF NOT EXISTS hakukohde_viite_last_modified_idx ON hakukohde_viite (last_modified);
CREATE INDEX IF NOT EXISTS valinnan_vaihe_last_modified_idx ON valinnan_vaihe (last_modified);
CREATE INDEX IF NOT EXISTS valintatapajono_last_modified_idx ON valintatapajono (last_modified);
CREATE INDEX IF NOT EXISTS jarjestyskriteeri_last_modified_idx ON jarjestyskriteeri (last_modified);
CREATE INDEX IF NOT EXISTS valintakoe_last_modified_idx ON valintakoe (last_modified);
CREATE INDEX IF NOT EXISTS hakukohteen_valintaperuste_last_modified_idx ON hakukohteen_valintaperuste (last_modified);