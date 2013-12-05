package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.service.valintaperusteet.model.Abstraktivalidointivirhe;
import fi.vm.sade.service.valintaperusteet.model.Funktionimi;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

import java.util.*;

/**
 * User: wuoti
 * Date: 2.12.2013
 * Time: 9.15
 */
@ApiModel(value = "FunktiokutsuDTO", description = "Funktiokutsu")
public class FunktiokutsuDTO {


    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Funktion nimi", required = true)
    private Funktionimi funktionimi;

    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Arvokonvertteriparametrit")
    private Set<ArvokonvertteriparametriDTO> arvokonvertteriparametrit = new HashSet<ArvokonvertteriparametriDTO>();

    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Arvovälikonvertteriparametrit")
    private Set<ArvovalikonvertteriparametriDTO> arvovalikonvertteriparametrit = new HashSet<ArvovalikonvertteriparametriDTO>();

    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Syöteparametrit")
    private Set<SyoteparametriDTO> syoteparametrit = new HashSet<SyoteparametriDTO>();

    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Funktioargumentit")
    private Set<FunktioargumenttiDTO> funktioargumentit = new TreeSet<FunktioargumenttiDTO>();

    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Valintaperusteviitteet")
    private Set<ValintaperusteViiteDTO> valintaperusteviitteet = new TreeSet<ValintaperusteViiteDTO>();

    @ApiModelProperty(value = "Validointivirheet")
    @JsonView(JsonViews.Basic.class)
    private List<Abstraktivalidointivirhe> validointivirheet = new ArrayList<Abstraktivalidointivirhe>();

    public Funktionimi getFunktionimi() {
        return funktionimi;
    }

    public void setFunktionimi(Funktionimi funktionimi) {
        this.funktionimi = funktionimi;
    }

    public Set<ArvokonvertteriparametriDTO> getArvokonvertteriparametrit() {
        return arvokonvertteriparametrit;
    }

    public void setArvokonvertteriparametrit(Set<ArvokonvertteriparametriDTO> arvokonvertteriparametrit) {
        this.arvokonvertteriparametrit = arvokonvertteriparametrit;
    }

    public Set<ArvovalikonvertteriparametriDTO> getArvovalikonvertteriparametrit() {
        return arvovalikonvertteriparametrit;
    }

    public void setArvovalikonvertteriparametrit(Set<ArvovalikonvertteriparametriDTO> arvovalikonvertteriparametrit) {
        this.arvovalikonvertteriparametrit = arvovalikonvertteriparametrit;
    }

    public Set<SyoteparametriDTO> getSyoteparametrit() {
        return syoteparametrit;
    }

    public void setSyoteparametrit(Set<SyoteparametriDTO> syoteparametrit) {
        this.syoteparametrit = syoteparametrit;
    }

    public Set<FunktioargumenttiDTO> getFunktioargumentit() {
        return funktioargumentit;
    }

    public void setFunktioargumentit(Set<FunktioargumenttiDTO> funktioargumentit) {
        this.funktioargumentit = funktioargumentit;
    }

    public Set<ValintaperusteViiteDTO> getValintaperusteviitteet() {
        return valintaperusteviitteet;
    }

    public void setValintaperusteviitteet(Set<ValintaperusteViiteDTO> valintaperusteviitteet) {
        this.valintaperusteviitteet = valintaperusteviitteet;
    }

    public List<Abstraktivalidointivirhe> getValidointivirheet() {
        return validointivirheet;
    }

    public void setValidointivirheet(List<Abstraktivalidointivirhe> validointivirheet) {
        this.validointivirheet = validointivirheet;
    }
}
