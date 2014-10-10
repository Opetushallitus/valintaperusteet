package fi.vm.sade.service.valintaperusteet.dto;

/**
 * 
 * @author jussi jartamo
 *
 */
public class HakukohdeJaValintakoeDTO {

	private String hakukohdeOid;
	private ValintakoeDTO valintakoeDTO;

	public HakukohdeJaValintakoeDTO() {

	}

	public HakukohdeJaValintakoeDTO(String hakukohdeOid,
			ValintakoeDTO valintakoeDTO) {
		this.hakukohdeOid = hakukohdeOid;
		this.valintakoeDTO = valintakoeDTO;
	}

	public String getHakukohdeOid() {
		return hakukohdeOid;
	}

	public ValintakoeDTO getValintakoeDTO() {
		return valintakoeDTO;
	}
}
