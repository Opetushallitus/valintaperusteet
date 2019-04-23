package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheJonoillaDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoDTO;

import java.util.LinkedHashSet;
import java.util.List;

public class JononPrioriteettiAsettaja {
    public static void filtteroiJonotIlmanLaskentaaJaAsetaPrioriteetit(List<ValinnanVaiheJonoillaDTO> valinnanVaiheJonoillaDTOs) {
        for (ValinnanVaiheJonoillaDTO vaihe : valinnanVaiheJonoillaDTOs) {
            if (vaihe.getJonot() != null) {
                int i = 0;
                LinkedHashSet<ValintatapajonoDTO> ilmanLaskentaaJonot = new LinkedHashSet<>();
                for (ValintatapajonoDTO jono : vaihe.getJonot()) {
                    jono.setPrioriteetti(i);
                    if (!jono.getKaytetaanValintalaskentaa()) {
                        ilmanLaskentaaJonot.add(jono);
                    }
                    i++;
                }
                vaihe.setJonot(ilmanLaskentaaJonot);
            }
        }
    }
}
