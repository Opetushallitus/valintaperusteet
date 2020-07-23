package fi.vm.sade.service.valintaperusteet.model;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "hakukohdekoodi")
@Cacheable(true)
public class Hakukohdekoodi extends Koodi {}
