create index if not exists valintatapajono_valinnan_vaihe_id_idx on valintatapajono (valinnan_vaihe_id);
create index if not exists valintatapajono_edellinen_valintatapajono_id_idx on valintatapajono (edellinen_valintatapajono_id);
create index if not exists valintatapajono_master_valintatapajono_id_idx on valintatapajono (master_valintatapajono_id);
create index if not exists hakijaryhma_jono_edellinen_hakijaryhma_jono_id_idx on hakijaryhma_jono (edellinen_hakijaryhma_jono_id);
create index if not exists jarjestyskriteeri_edellinen_jarjestyskriteeri_id_idx on jarjestyskriteeri (edellinen_jarjestyskriteeri_id);
create index if not exists jarjestyskriteeri_valintatapajono_id_idx on jarjestyskriteeri (valintatapajono_id);
create index if not exists valinnan_vaihe_hakukohde_viite_id_idx on valinnan_vaihe (hakukohde_viite_id);
