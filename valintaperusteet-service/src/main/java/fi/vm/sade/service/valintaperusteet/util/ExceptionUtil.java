package fi.vm.sade.service.valintaperusteet.util;

public class ExceptionUtil {
    public static <T extends RuntimeException> RuntimeException rethrow(Throwable t) throws T {
        throw (T) t;
    }
}
