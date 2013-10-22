package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.ValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintatapajonoEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintatapajonoOidListaOnTyhjaException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintatapajonoaEiVoiLisataException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintatapajonoaEiVoiPoistaaException;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;
import fi.vm.sade.service.valintaperusteet.util.ValintatapajonoKopioija;
import fi.vm.sade.service.valintaperusteet.util.ValintatapajonoUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 17.1.2013
 * Time: 14.44
 * To change this template use File | Settings | File Templates.
 */
@Service
@Transactional
public class ValintatapajonoServiceImpl extends AbstractCRUDServiceImpl<Valintatapajono, Long, String> implements ValintatapajonoService {

    @Autowired
    private ValintatapajonoDAO valintatapajonoDAO;

    @Autowired
    private ValinnanVaiheService valinnanVaiheService;

    @Autowired
    private OidService oidService;

    @Autowired
    private JarjestyskriteeriService jarjestyskriteeriService;

    @Autowired
    private HakijaryhmaService hakijaryhmaService;

    private static ValintatapajonoKopioija kopioija = new ValintatapajonoKopioija();

    @Autowired
    public ValintatapajonoServiceImpl(ValintatapajonoDAO dao) {
        super(dao);
    }

    @Override
    public List<Valintatapajono> findJonoByValinnanvaihe(String oid) {
        return LinkitettavaJaKopioitavaUtil.jarjesta(valintatapajonoDAO.findByValinnanVaihe(oid));
    }

    private void lisaaValinnanVaiheelleKopioMasterValintatapajonosta(ValinnanVaihe valinnanVaihe,
                                                                     Valintatapajono masterValintatapajono,
                                                                     Valintatapajono edellinenMasterValintatapajono) {
        Valintatapajono kopio = ValintatapajonoUtil.teeKopioMasterista(masterValintatapajono);
        kopio.setValinnanVaihe(valinnanVaihe);
        kopio.setOid(oidService.haeValintatapajonoOid());

        List<Valintatapajono> jonot = LinkitettavaJaKopioitavaUtil.jarjesta(
                valintatapajonoDAO.findByValinnanVaihe(valinnanVaihe.getOid()));

        Valintatapajono edellinenValintatapajono =
                LinkitettavaJaKopioitavaUtil.haeMasterinEdellistaVastaava(edellinenMasterValintatapajono, jonot);

        kopio.setEdellinenValintatapajono(edellinenValintatapajono);
        Valintatapajono lisatty = valintatapajonoDAO.insert(kopio);
        LinkitettavaJaKopioitavaUtil.asetaSeuraava(edellinenValintatapajono, lisatty);

        for (ValinnanVaihe vaihekopio : valinnanVaihe.getKopioValinnanVaiheet()) {
            lisaaValinnanVaiheelleKopioMasterValintatapajonosta(vaihekopio,
                    lisatty, lisatty.getEdellinenValintatapajono());
        }
    }

    @Override
    public Valintatapajono lisaaValintatapajonoValinnanVaiheelle(String valinnanVaiheOid, Valintatapajono jono,
                                                                 String edellinenValintatapajonoOid) {
        ValinnanVaihe valinnanVaihe = valinnanVaiheService.readByOid(valinnanVaiheOid);
        if (!ValinnanVaiheTyyppi.TAVALLINEN.equals(valinnanVaihe.getValinnanVaiheTyyppi())) {
            throw new ValintatapajonoaEiVoiLisataException("Valintatapajonoa ei voi lisätä valinnan vaiheelle, jonka " +
                    "tyyppi on " + valinnanVaihe.getValinnanVaiheTyyppi().name());
        }

        Valintatapajono edellinenValintatapajono = null;
        if (StringUtils.isNotBlank(edellinenValintatapajonoOid)) {
            edellinenValintatapajono = haeValintatapajono(edellinenValintatapajonoOid);
        } else {
            edellinenValintatapajono = valintatapajonoDAO.haeValinnanVaiheenViimeinenValintatapajono(valinnanVaiheOid);
        }


        jono.setOid(oidService.haeValintatapajonoOid());
        jono.setValinnanVaihe(valinnanVaihe);
        jono.setEdellinenValintatapajono(edellinenValintatapajono);

        Valintatapajono lisatty = valintatapajonoDAO.insert(jono);
        LinkitettavaJaKopioitavaUtil.asetaSeuraava(edellinenValintatapajono, lisatty);

        for (ValinnanVaihe kopio : valinnanVaihe.getKopioValinnanVaiheet()) {
            lisaaValinnanVaiheelleKopioMasterValintatapajonosta(kopio, lisatty, edellinenValintatapajono);
        }

        return lisatty;
    }


    @Override
    public void deleteByOid(String oid) {
        Valintatapajono valintatapajono = haeValintatapajono(oid);

        if (valintatapajono.getMasterValintatapajono() != null) {
            throw new ValintatapajonoaEiVoiPoistaaException("valintatapajono on peritty.");
        }

        delete(valintatapajono);
    }

    @Override
    public void delete(Valintatapajono valintatapajono) {
        for (Valintatapajono valintatapajono1 : valintatapajono.getKopiot()) {
            delete(valintatapajono1);
        }

        if (valintatapajono.getSeuraava() != null) {
            Valintatapajono seuraava = valintatapajono.getSeuraava();
            seuraava.setEdellinen(valintatapajono.getEdellinen());
        }

        for (Jarjestyskriteeri jarjestyskriteeri : valintatapajono.getJarjestyskriteerit()) {
            jarjestyskriteeriService.delete(jarjestyskriteeri);
        }

        valintatapajonoDAO.remove(valintatapajono);
    }

    private Valintatapajono haeValintatapajono(String oid) {
        Valintatapajono valintatapajono = valintatapajonoDAO.readByOid(oid);
        if (valintatapajono == null) {
            throw new ValintatapajonoEiOleOlemassaException("Valintatapajono (" + oid + ") ei ole olemassa", oid);
        }

        return valintatapajono;
    }

    @Override
    public Valintatapajono readByOid(String oid) {
        return haeValintatapajono(oid);
    }

    @Override
    public List<Valintatapajono> findAll() {
        return valintatapajonoDAO.findAll();
    }

    @Override
    public Valintatapajono update(String oid, Valintatapajono incoming) {
        Valintatapajono managedObject = haeValintatapajono(oid);
        return LinkitettavaJaKopioitavaUtil.paivita(managedObject, incoming, kopioija);
    }

    @Override
    public Valintatapajono insert(Valintatapajono entity) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Valintatapajono> jarjestaValintatapajonot(List<String> valintatapajonoOidit) {
        if (valintatapajonoOidit.isEmpty()) {
            throw new ValintatapajonoOidListaOnTyhjaException("Valintatapajonojen OID-lista on tyhjä");
        }

        Valintatapajono ensimmainen = haeValintatapajono(valintatapajonoOidit.get(0));
        return jarjestaValintatapajonot(ensimmainen.getValinnanVaihe(), valintatapajonoOidit);
    }

    private Valintatapajono kopioiValintatapajonotRekursiivisesti(ValinnanVaihe valinnanVaihe,
                                                                  Valintatapajono master) {
        if (master == null) {
            return null;
        }

        Valintatapajono kopio = ValintatapajonoUtil.teeKopioMasterista(master);
        kopio.setOid(oidService.haeValintatapajonoOid());
        valinnanVaihe.addJono(kopio);
        Valintatapajono edellinen = kopioiValintatapajonotRekursiivisesti(valinnanVaihe,
                master.getEdellinenValintatapajono());
        if (edellinen != null) {
            kopio.setEdellinenValintatapajono(edellinen);
            edellinen.setSeuraava(kopio);
        }

        Valintatapajono lisatty = valintatapajonoDAO.insert(kopio);
        jarjestyskriteeriService.kopioiJarjestyskriteeritMasterValintatapajonoltaKopiolle(lisatty, master);

        return lisatty;
    }

    @Override
    public void kopioiValintatapajonotMasterValinnanVaiheeltaKopiolle(ValinnanVaihe valinnanVaihe,
                                                                      ValinnanVaihe masterValinnanVaihe) {
        Valintatapajono vv = valintatapajonoDAO.haeValinnanVaiheenViimeinenValintatapajono(masterValinnanVaihe.getOid());
        kopioiValintatapajonotRekursiivisesti(valinnanVaihe, vv);
    }


    private List<Valintatapajono> jarjestaValintatapajonot(ValinnanVaihe vaihe, List<String> valintatapajonoOidit) {
        LinkedHashMap<String, Valintatapajono> alkuperainenJarjestys = LinkitettavaJaKopioitavaUtil.teeMappiOidienMukaan(
                LinkitettavaJaKopioitavaUtil.jarjesta(valintatapajonoDAO.findByValinnanVaihe(vaihe.getOid())));

        LinkedHashMap<String, Valintatapajono> jarjestetty =
                LinkitettavaJaKopioitavaUtil.jarjestaOidListanMukaan(alkuperainenJarjestys, valintatapajonoOidit);


        for (ValinnanVaihe kopio : vaihe.getKopiot()) {
            jarjestaKopioValinnanVaiheenValintatapajonot(kopio, jarjestetty);
        }

        return new ArrayList<Valintatapajono>(jarjestetty.values());
    }

    private void jarjestaKopioValinnanVaiheenValintatapajonot(ValinnanVaihe vaihe,
                                                              LinkedHashMap<String, Valintatapajono> uusiMasterJarjestys) {
        LinkedHashMap<String, Valintatapajono> alkuperainenJarjestys = LinkitettavaJaKopioitavaUtil.teeMappiOidienMukaan(
                LinkitettavaJaKopioitavaUtil.jarjesta(valintatapajonoDAO.findByValinnanVaihe(vaihe.getOid())));

        LinkedHashMap<String, Valintatapajono> jarjestetty =
                LinkitettavaJaKopioitavaUtil.jarjestaKopiotMasterJarjestyksenMukaan(alkuperainenJarjestys,
                        uusiMasterJarjestys);

        for (ValinnanVaihe kopio : vaihe.getKopiot()) {
            jarjestaKopioValinnanVaiheenValintatapajonot(kopio, jarjestetty);
        }
    }
}
