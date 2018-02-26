package fi.vm.sade.service.valintaperusteet.dto;

public class HakukohdeJaValintaryhmaDTO {
    private String hakukohdeOid;
    private ValintaryhmaDTO valintaryhma;

    public HakukohdeJaValintaryhmaDTO(String hakukohdeOid, ValintaryhmaDTO valintaryhma) {
        this.hakukohdeOid = hakukohdeOid;
        this.valintaryhma = valintaryhma;
    }

    public String getHakukohdeOid() {
        return hakukohdeOid;
    }

    public ValintaryhmaDTO getValintaryhma() {
        return valintaryhma;
    }
}
