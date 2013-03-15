package fi.vm.sade.service.valintaperusteet.service.exception;

/**
 * User: kwuoti
 * Date: 18.2.2013
 * Time: 13.34
 */
public class ValintatapajonoOidListaOnTyhjaException extends RuntimeException {
    public ValintatapajonoOidListaOnTyhjaException() {
    }

    public ValintatapajonoOidListaOnTyhjaException(String message) {
        super(message);
    }

    public ValintatapajonoOidListaOnTyhjaException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValintatapajonoOidListaOnTyhjaException(Throwable cause) {
        super(cause);
    }
}
