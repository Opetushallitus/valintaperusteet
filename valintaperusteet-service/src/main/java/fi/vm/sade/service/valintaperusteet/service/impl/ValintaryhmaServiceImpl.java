package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.OrganisaatioDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValinnanVaiheDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.dto.OrganisaatioDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.Organisaatio;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.service.HakijaryhmaService;
import fi.vm.sade.service.valintaperusteet.service.OidService;
import fi.vm.sade.service.valintaperusteet.service.ValinnanVaiheService;
import fi.vm.sade.service.valintaperusteet.service.ValintaryhmaService;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintaryhmaEiOleOlemassaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 16.1.2013
 * Time: 10.37
 * To change this template use File | Settings | File Templates.
 */
@Service
@Transactional
public class ValintaryhmaServiceImpl implements ValintaryhmaService {

    @Autowired
    private ValintaryhmaDAO valintaryhmaDAO;

    @Autowired
    private ValinnanVaiheService valinnanVaiheService;

    @Autowired
    private HakijaryhmaService hakijaryhmaService;

    @Autowired
    private ValinnanVaiheDAO valinnanVaiheDAO;

    @Autowired
    private OrganisaatioDAO organisaatioDAO;

    @Autowired
    private OidService oidService;

    @Autowired
    private ValintaperusteetModelMapper modelMapper;


    public List<Valintaryhma> findValintaryhmasByParentOid(String id) {
        return valintaryhmaDAO.findChildrenByParentOid(id);
    }

    @Override
    public Valintaryhma readByOid(String oid) {
        return haeValintaryhma(oid);
    }

    private Valintaryhma haeValintaryhma(String oid) {
        Valintaryhma valintaryhma = valintaryhmaDAO.readByOid(oid);
        if (valintaryhma == null) {
            throw new ValintaryhmaEiOleOlemassaException("Valintaryhma (" + oid + ") ei ole olemassa.", oid);
        }

        return valintaryhma;
    }

    @Override
    public Valintaryhma insert(ValintaryhmaCreateDTO dto, String parentOid) {
        Valintaryhma valintaryhma = modelMapper.map(dto, Valintaryhma.class);

        valintaryhma.setOid(oidService.haeValintaryhmaOid());
        Valintaryhma parent = haeValintaryhma(parentOid);
        valintaryhma.setYlavalintaryhma(parent);

        valintaryhma.setOrganisaatiot(getOrganisaatios(dto.getOrganisaatiot()));

        Valintaryhma inserted = valintaryhmaDAO.insert(valintaryhma);
        valinnanVaiheService.kopioiValinnanVaiheetParentilta(inserted, parent);
        hakijaryhmaService.kopioiHakijaryhmatParentilta(inserted, parent);
        return inserted;
    }

    @Override
    public List<Valintaryhma> findParentHierarchyFromOid(String oid) {
        return valintaryhmaDAO.readHierarchy(oid);
    }


    @Override
    public Valintaryhma update(String oid, ValintaryhmaCreateDTO incoming) {
        Valintaryhma managedObject = haeValintaryhma(oid);
        managedObject.setNimi(incoming.getNimi());
        if(incoming.getKohdejoukko() != null) {
            managedObject.setKohdejoukko(incoming.getKohdejoukko());
        }

        managedObject.setOrganisaatiot(getOrganisaatios(incoming.getOrganisaatiot()));
        return managedObject;
    }

    private Set<Organisaatio> getOrganisaatios(Set<OrganisaatioDTO> incoming) {
        Set<Organisaatio> organisaatiot = new HashSet<Organisaatio>();
        for (OrganisaatioDTO organisaatio : incoming) {
            Organisaatio temp = organisaatioDAO.readByOid(organisaatio.getOid());

            // TODO: OidPath pitäis varmaan päivittää joskus vanoille kanssa.
            if(temp == null) {
                temp = organisaatioDAO.insert(modelMapper.map(organisaatio, Organisaatio.class));
            }
            organisaatiot.add(temp);
        }
        return organisaatiot;
    }

    @Override
    public Valintaryhma insert(ValintaryhmaCreateDTO dto) {
        Valintaryhma entity = modelMapper.map(dto, Valintaryhma.class);

        entity.setOid(oidService.haeValintaryhmaOid());
        entity.setOrganisaatiot(getOrganisaatios(dto.getOrganisaatiot()));
        return valintaryhmaDAO.insert(entity);
    }
}
