package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModelProperty;

public class ValinnanVaiheJaPrioriteettiDTO extends ValinnanVaiheDTO {

    @ApiModelProperty(value = "Prioriteetti", required = true)
    private int prioriteetti;

    public int getPrioriteetti() {
        return prioriteetti;
    }

    public void setPrioriteetti(int prioriteetti) {
        this.prioriteetti = prioriteetti;
    }
}
