-- jarjestyskriteeri

drop index jarjestyskriteeri_oid_idx;
drop index jarjestyskriteeri_edellinen_jarjestyskriteeri_id_idx;
drop index jarjestyskriteeri_valintatapajono_id_idx;

alter table jarjestyskriteeri
  add unique (edellinen_jarjestyskriteeri_id),
  add unique (valintatapajono_id, id),
  add foreign key (valintatapajono_id, edellinen_jarjestyskriteeri_id) references jarjestyskriteeri (valintatapajono_id, id);

create unique index jarjestyskriteeri_ensimmainen_key on jarjestyskriteeri (valintatapajono_id) where edellinen_jarjestyskriteeri_id is null;

-- valintatapajono

drop index valintatapajono_oid_idx;
drop index valintatapajono_edellinen_valintatapajono_id_idx;
drop index valintatapajono_valinnan_vaihe_id_idx;

alter table valintatapajono
  add unique (edellinen_valintatapajono_id),
  add unique (valinnan_vaihe_id, id),
  add foreign key (valinnan_vaihe_id, edellinen_valintatapajono_id) references valintatapajono (valinnan_vaihe_id, id);

create unique index valintatapajono_ensimmainen_key on valintatapajono (valinnan_vaihe_id) where edellinen_valintatapajono_id is null;

-- hakijaryhma_jono

drop index hakijaryhma_jono_oid_idx;
drop index hakijaryhma_jono_edellinen_hakijaryhma_jono_id_idx;

alter table hakijaryhma_jono
  add unique (edellinen_hakijaryhma_jono_id),
  add unique (hakukohde_viite_id, id),
  add unique (valintatapajono_id, id),
  add foreign key (hakukohde_viite_id, edellinen_hakijaryhma_jono_id) references hakijaryhma_jono (hakukohde_viite_id, id),
  add foreign key (valintatapajono_id, edellinen_hakijaryhma_jono_id) references hakijaryhma_jono (valintatapajono_id, id);

create unique index hakijaryhma_jono_hakukohde_ensimmainen_key on hakijaryhma_jono (hakukohde_viite_id) where (hakukohde_viite_id is not null and edellinen_hakijaryhma_jono_id is null);
create unique index hakijaryhma_jono_valintatapajono_ensimmainen_key on hakijaryhma_jono (valintatapajono_id) where (valintatapajono_id is not null and edellinen_hakijaryhma_jono_id is null);

-- hakijaryhma

drop index hakijaryhma_oid_idx;
drop index hakijaryhma_edellinen_hakijaryhma_id;

alter table hakijaryhma
  add unique (edellinen_hakijaryhma_id),
  add unique (valintaryhma_id, id),
  add foreign key (valintaryhma_id, edellinen_hakijaryhma_id) references hakijaryhma (valintaryhma_id, id);

create unique index hakijaryhma_ensimmainen_key on hakijaryhma (valintaryhma_id) where (valintaryhma_id is not null and edellinen_hakijaryhma_id is null);

-- valinnan_vaihe

drop index valinnan_vaihe_oid_idx;
drop index if exists valinnan_vaihe_evvi_idx;
drop index valinnan_vaihe_hakukohde_viite_id_idx;

alter table valinnan_vaihe
  add unique (edellinen_valinnan_vaihe_id),
  add unique (hakukohde_viite_id, id),
  add unique (valintaryhma_id, id),
  add foreign key (hakukohde_viite_id, edellinen_valinnan_vaihe_id) references valinnan_vaihe (hakukohde_viite_id, id),
  add foreign key (valintaryhma_id, edellinen_valinnan_vaihe_id) references valinnan_vaihe (valintaryhma_id, id);

create unique index valinnan_vaihe_hakukohde_ensimmainen_key on valinnan_vaihe (hakukohde_viite_id) where (hakukohde_viite_id is not null and edellinen_valinnan_vaihe_id is null);
create unique index valinnan_vaihe_valintaryhma_ensimmainen_key on valinnan_vaihe (valintaryhma_id) where (valintaryhma_id is not null and edellinen_valinnan_vaihe_id is null);