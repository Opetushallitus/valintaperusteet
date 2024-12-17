package fi.vm.sade.service.valintaperusteet.model;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "hakukohdekoodi")
@Cacheable(true)
public class Hakukohdekoodi extends Koodi {}
