package fi.vm.sade.service.valintaperusteet.model;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * User: wuoti
 * Date: 18.6.2013
 * Time: 13.31
 */
@Entity
@Table(name = "valintakoekoodi")
@Cacheable(true)
public class Valintakoekoodi extends Koodi {
}
