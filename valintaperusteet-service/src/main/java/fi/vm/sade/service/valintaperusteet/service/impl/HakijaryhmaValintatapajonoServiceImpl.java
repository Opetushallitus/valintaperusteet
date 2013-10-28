package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.HakijaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.dao.HakijaryhmaValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.service.valintaperusteet.service.exception.*;
import fi.vm.sade.service.valintaperusteet.util.HakijaryhmaKopioija;
import fi.vm.sade.service.valintaperusteet.util.HakijaryhmaUtil;
import fi.vm.sade.service.valintaperusteet.util.HakijaryhmaValintatapajonoKopioija;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 1.10.2013
 * Time: 16.23
 * To change this template use File | Settings | File Templates.
 */
@Service
@Transactional
public class HakijaryhmaValintatapajonoServiceImpl extends AbstractCRUDServiceImpl<HakijaryhmaValintatapajono, Long, String> implements HakijaryhmaValintatapajonoService {

    @Autowired
    private HakijaryhmaDAO hakijaryhmaDAO;

    @Autowired
    private HakijaryhmaValintatapajonoDAO hakijaryhmaValintatapajonoDAO;

    @Autowired
    private OidService oidService;

    @Autowired
    private ValintaryhmaService valintaryhmaService;

    @Autowired
    private HakukohdeService hakukohdeService;

    @Autowired
    private ValintatapajonoService valintapajonoService;

    @Autowired
    private LaskentakaavaService laskentakaavaService;

    private static HakijaryhmaValintatapajonoKopioija kopioija = new HakijaryhmaValintatapajonoKopioija();

    @Autowired
    public HakijaryhmaValintatapajonoServiceImpl(HakijaryhmaValintatapajonoDAO dao) {
        super(dao);
    }

    private HakijaryhmaValintatapajono haeHakijaryhmaValintatapajono(String oid) {
        HakijaryhmaValintatapajono hakijaryhma = hakijaryhmaValintatapajonoDAO.readByOid(oid);

        if (hakijaryhma == null) {
            throw new HakijaryhmaEiOleOlemassaException("Hakijaryhmavalintatapajono (" + oid + ") ei ole olemassa", oid);
        }

        return hakijaryhma;
    }

    @Override
    public List<HakijaryhmaValintatapajono> findHakijaryhmaByJono(String oid) {
        return hakijaryhmaValintatapajonoDAO.findByValintatapajono(oid);
    }

    @Override
    public HakijaryhmaValintatapajono readByOid(String oid) {
        return haeHakijaryhmaValintatapajono(oid);
    }


    @Override
    public void deleteByOid(String oid, boolean skipInheritedCheck) {

        HakijaryhmaValintatapajono hakijaryhmaValintatapajono = hakijaryhmaValintatapajonoDAO.readByOid(oid);


        if (!skipInheritedCheck && hakijaryhmaValintatapajono.getMaster() != null) {
            throw new HakijaryhmaaEiVoiPoistaaException("hakijaryhma on peritty.");
        }

        delete(hakijaryhmaValintatapajono);
    }


    // CRUD
    @Override
    public HakijaryhmaValintatapajono update(String oid, HakijaryhmaValintatapajono entity) {
        HakijaryhmaValintatapajono managedObject = haeHakijaryhmaValintatapajono(oid);
        return LinkitettavaJaKopioitavaUtil.paivita(managedObject, entity, kopioija);
    }

    @Override
    public HakijaryhmaValintatapajono insert(HakijaryhmaValintatapajono entity) {
        return hakijaryhmaValintatapajonoDAO.insert(entity);
    }

    @Override

    public void delete(HakijaryhmaValintatapajono entity) {
        for (HakijaryhmaValintatapajono hakijaryhma : entity.getKopiot()) {
            delete(hakijaryhma);
        }

        if (entity.getSeuraava() != null) {
            HakijaryhmaValintatapajono seuraava = entity.getSeuraava();
            seuraava.setEdellinen(entity.getEdellinen());
        }

        hakijaryhmaValintatapajonoDAO.remove(entity);
    }

    @Override
    public void deleteById(Long aLong) {
        throw new RuntimeException("not implemented");
    }
}