package fi.vm.sade.service.valintaperusteet.service;

import static org.junit.Assert.assertThat;

import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteDTO;
import org.apache.commons.lang3.tuple.Pair;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@ContextConfiguration(locations = "classpath:fi/vm/sade/service/valintaperusteet/service/integration-test-context.xml")
@TestExecutionListeners(listeners = {
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@Ignore
public class LaskentakaavaServiceIntegrationTestToBeRunManually {
    @Autowired
    private LaskentakaavaService laskentakaavaService;

    @Test
    public void testFindAvaimetForHakukohde() {
        HakukohdeWithValintaperusteet hakukohdeWithValintaperusteet =
            new HakukohdeWithValintaperusteet.Builder("1.2.246.562.20.39188224891")
                .addValintaperuste("5454fd76-0e02-e868-4e45-f96061aa0cc0", "Entrance examination: 0-70 points, min. 30 points.")
                .addValintaperuste("6b3c52e0-f9e7-7a75-8b83-05611208fa3f", "Pre-assignment: 1 = delivered, 2 = not delivered.")
                .addValintaperuste("ced128ef-f288-fa6a-af47-3a0fa1c3a887", "Pre-assignment: 0-30 points, min. 10 points")
                .build();

        for (int i = 0; i < 10; i++) {
            assertThat(valintaperusteetFor(hakukohdeWithValintaperusteet.hakukohdeOid), are(hakukohdeWithValintaperusteet));
        }
    }

    private Matcher<List<ValintaperusteDTO>> are(HakukohdeWithValintaperusteet expected) {
        return new TypeSafeMatcher<List<ValintaperusteDTO>>() {
            private String failure;
            @Override
            protected boolean matchesSafely(List<ValintaperusteDTO> item) {
                item.sort(Comparator.comparing(ValintaperusteDTO::getTunniste));
                if (item.size() != expected.tunnnisteetJaKuvaukset.size()) {
                    failure = String.format("expected %d items but had %d",
                        expected.tunnnisteetJaKuvaukset.size(), item.size());
                    return false;
                }
                int i = 0;
                for (ValintaperusteDTO valintaperusteKannasta : item) {
                    Pair<String, String> odotettuTunnisteJaKuvaus = expected.tunnnisteetJaKuvaukset.get(i);
                    if (!odotettuTunnisteJaKuvaus.getLeft().equals(valintaperusteKannasta.getTunniste())) {
                        failure = String.format("tunniste at position %d: expected %s but was %s",
                            i, odotettuTunnisteJaKuvaus.getLeft(), valintaperusteKannasta.getTunniste());
                        return false;
                    }
                    if (!odotettuTunnisteJaKuvaus.getRight().equals(valintaperusteKannasta.getKuvaus())) {
                        failure = String.format("kuvaus at position %d: expected %s but was %s",
                            i, odotettuTunnisteJaKuvaus.getRight(), valintaperusteKannasta.getKuvaus());
                    }
                    i = i + 1;
                }
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(String.format("list with valintaperusteet %s : %s",
                    expected.tunnnisteetJaKuvaukset, failure));
            }
        };
    }

    private List<ValintaperusteDTO> valintaperusteetFor(String hakukohdeOid) {
        return laskentakaavaService.findAvaimetForHakukohde(hakukohdeOid);
    }

    public static class HakukohdeWithValintaperusteet {
        public final String hakukohdeOid;
        public final List<Pair<String,String>> tunnnisteetJaKuvaukset;

        private HakukohdeWithValintaperusteet(String hakukohdeOid, List<Pair<String, String>> tunnnisteetJaKuvaukset) {
            this.hakukohdeOid = hakukohdeOid;
            this.tunnnisteetJaKuvaukset = tunnnisteetJaKuvaukset;
        }

        public static class Builder {
            private final String hakukohdeOid;
            public final List<Pair<String,String>> tunnnisteetJaKuvaukset = new ArrayList<>();

            public Builder(String hakukohdeOid) {
                this.hakukohdeOid = hakukohdeOid;
            }

            public Builder addValintaperuste(String tunniste, String kuvaus) {
                tunnnisteetJaKuvaukset.add(Pair.of(tunniste, kuvaus));
                return this;
            }

            public HakukohdeWithValintaperusteet build() {
                tunnnisteetJaKuvaukset.sort(Comparator.comparing(Pair::getLeft));
                return new HakukohdeWithValintaperusteet(hakukohdeOid, tunnnisteetJaKuvaukset);
            }
        }
    }
}
