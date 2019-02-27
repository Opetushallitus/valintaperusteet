package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheJonoillaDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoDTO;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JononPrioriteettiAsettaja {
    public static void filtteroiJaJarjestaJonotIlmanLaskentaa(List<ValinnanVaiheJonoillaDTO> valinnanVaiheJonoillaDTOs) {
        for (ValinnanVaiheJonoillaDTO vaihe : valinnanVaiheJonoillaDTOs) {
            if (vaihe.getJonot() != null) {
                int i = 0;
                Set<ValintatapajonoDTO> ilmanLaskentaaJonot = new HashSet<>();
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
