package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;

import java.util.Collection;

/**
 * Created by jukais on 4.3.2014.
 */
@ApiModel(value = "HakukohteenValintakoeDTO", description = "Hakukohteen valintakoe")
public class HakukohteenValintakoeDTO {
    private String tyyppiUri;
    private String oid;

    public String getTyyppiUri() {
        return tyyppiUri;
    }

    public void setTyyppiUri(String tyyppiUri) {
        this.tyyppiUri = tyyppiUri;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getOid() {
        return oid;
    }
}
