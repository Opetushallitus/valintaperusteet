package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.service.OidService;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * User: kwuoti
 * Date: 8.2.2013
 * Time: 10.32
 *
 * Tilapäinen OID-palvelu kehitykseen.
 */

@Service
public class DummyOidServiceImpl implements OidService {
    private static Random random = new Random();

    private String generoiOid() {
        return String.valueOf(System.currentTimeMillis()) + String.valueOf(random.nextLong());
    }

    @Override
    public String haeValinnanVaiheOid() {
        return generoiOid();
    }

    @Override
    public String haeValintaryhmaOid() {
        return generoiOid();
    }

    @Override
    public String haeValintatapajonoOid() {
        return generoiOid();
    }

    @Override
    public String haeJarjestyskriteeriOid() {
        return generoiOid();
    }

    @Override
    public String haeValintakoeOid() {
        return generoiOid();
    }
}
