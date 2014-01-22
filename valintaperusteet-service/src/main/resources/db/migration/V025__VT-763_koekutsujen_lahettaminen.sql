alter table valintakoe add column lahetetaanko_koekutsut boolean;
update valintakoe set lahetetaanko_koekutsut = true;
alter table valintakoe alter column lahetetaanko_koekutsut set not null;
