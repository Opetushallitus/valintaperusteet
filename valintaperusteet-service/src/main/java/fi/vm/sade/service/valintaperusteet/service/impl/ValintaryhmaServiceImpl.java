package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.LaskentakaavaDAO;
import fi.vm.sade.service.valintaperusteet.dao.OrganisaatioDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.dto.OrganisaatioDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.Organisaatio;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.service.OidService;
import fi.vm.sade.service.valintaperusteet.service.ValinnanVaiheService;
import fi.vm.sade.service.valintaperusteet.service.ValintaryhmaService;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintaryhmaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintaryhmaaEiVoidaKopioida;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class ValintaryhmaServiceImpl implements ValintaryhmaService {
    @Autowired
    private ValintaryhmaDAO valintaryhmaDAO;

    @Autowired
    private ValinnanVaiheService valinnanVaiheService;

    @Autowired
    private OrganisaatioDAO organisaatioDAO;

    @Autowired
    private LaskentakaavaDAO laskentakaavaDAO;

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
        managedObject.setKohdejoukko(incoming.getKohdejoukko());
        managedObject.setHakuoid(incoming.getHakuoid());
        if (managedObject.getHakuvuosi() != incoming.getHakuvuosi()) {
            managedObject.setHakuvuosi(incoming.getHakuvuosi());
            asetaHakuvuosiAlaryhmille(managedObject.getOid(), incoming.getHakuvuosi());
        }
        managedObject.setOrganisaatiot(getOrganisaatios(incoming.getOrganisaatiot()));
        return managedObject;
    }

    private void asetaHakuvuosiAlaryhmille(String oid, String hakuvuosi) {
        final List<Valintaryhma> children = valintaryhmaDAO.findChildrenByParentOidPlain(oid);
        children.forEach(v -> {
            v.setHakuvuosi(hakuvuosi);
            asetaHakuvuosiAlaryhmille(v.getOid(), hakuvuosi);
        });
    }

    private Set<Organisaatio> getOrganisaatios(Set<OrganisaatioDTO> incoming) {
        Set<Organisaatio> organisaatiot = new HashSet<Organisaatio>();
        for (OrganisaatioDTO organisaatio : incoming) {
            Organisaatio temp = organisaatioDAO.readByOid(organisaatio.getOid());
            // TODO: OidPath pitäis varmaan päivittää joskus vanoille kanssa.
            if (temp == null) {
                temp = organisaatioDAO.insert(modelMapper.map(organisaatio, Organisaatio.class));
            }
            organisaatiot.add(temp);
        }
        return organisaatiot;
    }

    private boolean isChildOf(String childOid, String parentOid) {
        return valintaryhmaDAO.readHierarchy(childOid).stream().anyMatch(vr -> vr.getOid().equals(parentOid));
    }

    private Valintaryhma copyAsChild(Valintaryhma source, Valintaryhma parent, String name) {
        Valintaryhma copy = new Valintaryhma();
        copy.setYlavalintaryhma(parent);
        copy.setNimi(name);
        copy.setOid(oidService.haeValintaryhmaOid());
        Valintaryhma inserted = valintaryhmaDAO.insert(copy);
        valinnanVaiheService.kopioiValinnanVaiheetParentilta(inserted, parent);
        List<Valintaryhma> children = valintaryhmaDAO.findChildrenByParentOid(source.getOid());
        children.stream().forEach((child -> copyAsChild(child, inserted, child.getNimi())));
        return inserted;
    }

    public Valintaryhma copyAsChild(String sourceOid, String parentOid, String name) {
        // Tarkistetaan, että parent ei ole sourcen jälkeläinen
        if (isChildOf(parentOid, sourceOid)) {
            throw new ValintaryhmaaEiVoidaKopioida("Valintaryhmä (" + parentOid + ") on kohderyhmän (" + sourceOid + ") lapsi", sourceOid, parentOid);
        }
        // Tarkistetaan sisarusten nimet
        List<Valintaryhma> children = valintaryhmaDAO.findChildrenByParentOid(parentOid);
        if (children.stream().anyMatch(vr -> vr.getNimi().equals(name))) {
            throw new ValintaryhmaaEiVoidaKopioida("Valintaryhmällä (" + parentOid + ") on jo \"" + name + "\" niminen lapsi", sourceOid, parentOid);
        }
        Valintaryhma source = valintaryhmaDAO.readByOid(sourceOid);
        Valintaryhma parent = valintaryhmaDAO.readByOid(parentOid);
        return copyAsChild(source, parent, name);
    }

    @Override
    public Valintaryhma insert(ValintaryhmaCreateDTO dto) {
        Valintaryhma entity = modelMapper.map(dto, Valintaryhma.class);
        entity.setOid(oidService.haeValintaryhmaOid());
        entity.setOrganisaatiot(getOrganisaatios(dto.getOrganisaatiot()));
        return valintaryhmaDAO.insert(entity);
    }

    @Override
    public void delete(String oid) {
        Optional<Valintaryhma> managedObject = Optional.ofNullable(haeValintaryhma(oid));
        if (managedObject.isPresent()) {
            for (ValinnanVaihe valinnanVaihe : managedObject.get().getValinnanvaiheet()) {
                valinnanVaiheService.delete(valinnanVaihe);
            }
            for (Laskentakaava laskentakaava : managedObject.get().getLaskentakaava()) {
                laskentakaavaDAO.remove(laskentakaava);
            }
            valintaryhmaDAO.remove(managedObject.get());
        }
    }
}
