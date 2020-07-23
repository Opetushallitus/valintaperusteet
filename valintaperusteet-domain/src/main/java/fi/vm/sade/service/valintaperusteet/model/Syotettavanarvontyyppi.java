package fi.vm.sade.service.valintaperusteet.model;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "syotettavanarvonkoodi")
@Cacheable(true)
public class Syotettavanarvontyyppi extends Koodi {}
