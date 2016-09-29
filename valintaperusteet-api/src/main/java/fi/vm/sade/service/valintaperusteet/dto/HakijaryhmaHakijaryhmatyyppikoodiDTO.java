package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

public class HakijaryhmaHakijaryhmatyyppikoodiDTO extends HakijaryhmaDTO {

    @ApiModelProperty(value = "Hakijaryhmatyyppikoodi")
    private List<KoodiDTO> hakijaryhmatyyppikoodit = new ArrayList<KoodiDTO>();

    public List<KoodiDTO> getHakijaryhmatyyppikoodit(){ return hakijaryhmatyyppikoodit; }

    public void setHakijaryhmatyyppikoodit(List<KoodiDTO> hakijaryhmatyyppikoodit){
        this.hakijaryhmatyyppikoodit = hakijaryhmatyyppikoodit;
    }
}
