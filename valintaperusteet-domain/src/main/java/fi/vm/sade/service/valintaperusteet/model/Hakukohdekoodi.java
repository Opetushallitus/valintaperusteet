package fi.vm.sade.service.valintaperusteet.model;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * User: wuoti
 * Date: 7.5.2013
 * Time: 12.55
 */
@Entity
@Table(name = "hakukohdekoodi")
@Cacheable(true)
public class Hakukohdekoodi extends Koodi {
}