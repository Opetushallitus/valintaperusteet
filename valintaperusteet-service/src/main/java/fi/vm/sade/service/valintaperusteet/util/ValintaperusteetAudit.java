package fi.vm.sade.service.valintaperusteet.util;

import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.util.Optional;

/**
 * @author Jussi Jartamo
 */
public class ValintaperusteetAudit {
    //public static final Audit AUDIT = new Audit("valintaperusteet-service", ApplicationType.VIRKAILIJA);

    public static String username() {
        return Optional.ofNullable((Principal) SecurityContextHolder.getContext().getAuthentication()).orElse(
                () -> "Kirjautumaton käyttäjä"
        ).getName();
    }
}
