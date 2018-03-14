package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.auditlog.ApplicationType;
import fi.vm.sade.auditlog.Audit;
import fi.vm.sade.sharedutils.AuditLogger;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;

/**
 * @author Jussi Jartamo
 */
public class ValintaperusteetAudit {
    public static final Audit AUDIT = new Audit(new AuditLogger(), "valintaperusteet-service", ApplicationType.VIRKAILIJA);

    public static String toNullsafeString(Collection l) {
        return l == null ? "null" : StringUtils.join(l, ",");
    }
}
