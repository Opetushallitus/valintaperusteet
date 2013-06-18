package fi.vm.sade.service.valintaperusteet.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * User: wuoti
 * Date: 18.6.2013
 * Time: 13.06
 */
@Entity
@DiscriminatorValue("opetuskielikoodi")
public class Opetuskielikoodi extends Koodi {
}
