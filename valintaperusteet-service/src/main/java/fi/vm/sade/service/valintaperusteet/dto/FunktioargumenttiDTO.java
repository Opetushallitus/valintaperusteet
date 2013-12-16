package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: wuoti
 * Date: 2.12.2013
 * Time: 9.54
 */
@ApiModel(value = "FunktioargumenttiDTO", description = "Funktioargumentti")
public class FunktioargumenttiDTO implements Comparable<FunktioargumenttiDTO> {

    @ApiModelProperty(value = "Lapsifunktiokutsu")
    private FunktiokutsuDTO funktiokutsuChild;

    @ApiModelProperty(value = "Lapsilaskentakaava")
    private LaskentakaavaListDTO laskentakaavaChild;

    @ApiModelProperty(value = "Indeksi", required = true)
    private Integer indeksi;

    public FunktiokutsuDTO getFunktiokutsuChild() {
        return funktiokutsuChild;
    }

    public void setFunktiokutsuChild(FunktiokutsuDTO funktiokutsuChild) {
        this.funktiokutsuChild = funktiokutsuChild;
    }

    public LaskentakaavaListDTO getLaskentakaavaChild() {
        return laskentakaavaChild;
    }

    public void setLaskentakaavaChild(LaskentakaavaListDTO laskentakaavaChild) {
        this.laskentakaavaChild = laskentakaavaChild;
    }

    public Integer getIndeksi() {
        return indeksi;
    }

    public void setIndeksi(Integer indeksi) {
        this.indeksi = indeksi;
    }

    @Override
    public int compareTo(FunktioargumenttiDTO o) {
        return indeksi - o.indeksi;
    }
}
