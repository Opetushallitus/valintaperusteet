package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;

/**
 * Created by jukais on 4.3.2014.
 */
@ApiModel(value = "AvainArvoDTO", description = "")
public class AvainArvoDTO {
    private String avain;
    private String arvo;

    public String getAvain() {
        return avain;
    }

    public void setAvain(String avain) {
        this.avain = avain;
    }

    public String getArvo() {
        return arvo;
    }

    public void setArvo(String arvo) {
        this.arvo = arvo;
    }
}
