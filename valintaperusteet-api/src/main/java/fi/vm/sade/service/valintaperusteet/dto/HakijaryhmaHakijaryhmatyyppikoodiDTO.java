package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

public class HakijaryhmaHakijaryhmatyyppikoodiDTO extends HakijaryhmaDTO {

    @ApiModelProperty(value = "Hakijaryhmatyyppikoodi")
    private KoodiDTO hakijaryhmatyyppikoodi = null;

    public KoodiDTO getHakijaryhmatyyppikoodi(){ return hakijaryhmatyyppikoodi; }

    public void setHakijaryhmatyyppikoodi(KoodiDTO hakijaryhmatyyppikoodi){
        this.hakijaryhmatyyppikoodi = hakijaryhmatyyppikoodi;
    }
}
