package fi.vm.sade.service.valintaperusteet.service.impl.actors.messages;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kjsaila
 * Date: 17/12/13
 * Time: 13:12
 * To change this template use File | Settings | File Templates.
 */
public class UusiRekursio {

    private Long id;
    private boolean laajennaAlakaavat;
    private Set<Long> laskentakaavaIds;

    public UusiRekursio(Long id, boolean laajennaAlakaavat, Set<Long> laskentakaavaIds) {
        this.id = id;
        this.laajennaAlakaavat = laajennaAlakaavat;
        this.laskentakaavaIds = laskentakaavaIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isLaajennaAlakaavat() {
        return laajennaAlakaavat;
    }

    public void setLaajennaAlakaavat(boolean laajennaAlakaavat) {
        this.laajennaAlakaavat = laajennaAlakaavat;
    }

    public Set<Long> getLaskentakaavaIds() {
        return laskentakaavaIds;
    }

    public void setLaskentakaavaIds(Set<Long> laskentakaavaIds) {
        this.laskentakaavaIds = laskentakaavaIds;
    }
}
