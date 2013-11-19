package fi.vm.sade.service.valintaperusteet.dto;

import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 13.11.2013
 * Time: 12.57
 * To change this template use File | Settings | File Templates.
 */
public class ErrorDTO {
    @JsonView(JsonViews.Basic.class)
    private String message;

    public ErrorDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
