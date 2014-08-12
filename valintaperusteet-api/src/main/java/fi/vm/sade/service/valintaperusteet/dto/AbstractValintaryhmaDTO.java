package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * User: wuoti Date: 2.12.2013 Time: 14.08
 */
@ApiModel(value = "AbstractValintaryhmaDTO", description = "Valintaryhmä")
public abstract class AbstractValintaryhmaDTO {
    @ApiModelProperty(value = "Nimi", required = true)
    private String nimi;

    @ApiModelProperty(value = "Haun kohdejoukko", required = false)
    private String kohdejoukko;

    @ApiModelProperty(value = "Haun oid", required = false)
    private String hakuoid;

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public String getKohdejoukko() {
        return kohdejoukko;
    }

    public void setKohdejoukko(String kohdejoukko) {
        this.kohdejoukko = kohdejoukko;
    }

    public String getHakuoid() {
        return hakuoid;
    }

    public void setHakuoid(String hakuoid) {
        this.hakuoid = hakuoid;
    }
}
