package fi.vm.sade.service.valintaperusteet.service.impl.actors.messages;

import fi.vm.sade.service.valintaperusteet.dto.KoodiDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaryhmaDTO;

/**
 * Created with IntelliJ IDEA.
 * User: kjsaila
 * Date: 17/12/13
 * Time: 13:12
 * To change this template use File | Settings | File Templates.
 */
public class LukionValintaperuste {

    private KoodiDTO hakukohdekoodi;
    private ValintaryhmaDTO painotettuKeskiarvoVr;
    private ValintaryhmaDTO painotettuKeskiarvoJaLisanayttoVr;
    private ValintaryhmaDTO painotettuKeskiarvoJaPaasykoeVr;
    private ValintaryhmaDTO painotettuKeskiarvoJaPaasykoeJaLisanayttoVr;


    public LukionValintaperuste(KoodiDTO hakukohdekoodi,
                                ValintaryhmaDTO painotettuKeskiarvoVr,
                                ValintaryhmaDTO painotettuKeskiarvoJaLisanayttoVr,
                                ValintaryhmaDTO painotettuKeskiarvoJaPaasykoeVr,
                                ValintaryhmaDTO painotettuKeskiarvoJaPaasykoeJaLisanayttoVr) {

        this.hakukohdekoodi = hakukohdekoodi;
        this.painotettuKeskiarvoVr = painotettuKeskiarvoVr;
        this.painotettuKeskiarvoJaLisanayttoVr = painotettuKeskiarvoJaLisanayttoVr;
        this.painotettuKeskiarvoJaPaasykoeVr = painotettuKeskiarvoJaPaasykoeVr;
        this.painotettuKeskiarvoJaPaasykoeJaLisanayttoVr = painotettuKeskiarvoJaPaasykoeJaLisanayttoVr;
    }

    public KoodiDTO getHakukohdekoodi() {
        return hakukohdekoodi;
    }

    public void setHakukohdekoodi(KoodiDTO hakukohdekoodi) {
        this.hakukohdekoodi = hakukohdekoodi;
    }

    public ValintaryhmaDTO getPainotettuKeskiarvoVr() {
        return painotettuKeskiarvoVr;
    }

    public void setPainotettuKeskiarvoVr(ValintaryhmaDTO painotettuKeskiarvoVr) {
        this.painotettuKeskiarvoVr = painotettuKeskiarvoVr;
    }

    public ValintaryhmaDTO getPainotettuKeskiarvoJaLisanayttoVr() {
        return painotettuKeskiarvoJaLisanayttoVr;
    }

    public void setPainotettuKeskiarvoJaLisanayttoVr(ValintaryhmaDTO painotettuKeskiarvoJaLisanayttoVr) {
        this.painotettuKeskiarvoJaLisanayttoVr = painotettuKeskiarvoJaLisanayttoVr;
    }

    public ValintaryhmaDTO getPainotettuKeskiarvoJaPaasykoeVr() {
        return painotettuKeskiarvoJaPaasykoeVr;
    }

    public void setPainotettuKeskiarvoJaPaasykoeVr(ValintaryhmaDTO painotettuKeskiarvoJaPaasykoeVr) {
        this.painotettuKeskiarvoJaPaasykoeVr = painotettuKeskiarvoJaPaasykoeVr;
    }

    public ValintaryhmaDTO getPainotettuKeskiarvoJaPaasykoeJaLisanayttoVr() {
        return painotettuKeskiarvoJaPaasykoeJaLisanayttoVr;
    }

    public void setPainotettuKeskiarvoJaPaasykoeJaLisanayttoVr(ValintaryhmaDTO painotettuKeskiarvoJaPaasykoeJaLisanayttoVr) {
        this.painotettuKeskiarvoJaPaasykoeJaLisanayttoVr = painotettuKeskiarvoJaPaasykoeJaLisanayttoVr;
    }
}
