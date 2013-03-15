package fi.vm.sade.service.valintaperusteet.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QLaskentakaava is a Querydsl query type for Laskentakaava
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QLaskentakaava extends EntityPathBase<Laskentakaava> {

    private static final long serialVersionUID = 450444436;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QLaskentakaava laskentakaava = new QLaskentakaava("laskentakaava");

    public final fi.vm.sade.generic.model.QBaseEntity _super = new fi.vm.sade.generic.model.QBaseEntity(this);

    public final QFunktiokutsu funktiokutsu;

    public final QHakukohdeViite hakukohde;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final SetPath<Jarjestyskriteeri, QJarjestyskriteeri> jarjestyskriteerit = this.<Jarjestyskriteeri, QJarjestyskriteeri>createSet("jarjestyskriteerit", Jarjestyskriteeri.class, QJarjestyskriteeri.class);

    public final StringPath kuvaus = createString("kuvaus");

    public final StringPath nimi = createString("nimi");

    public final BooleanPath onLuonnos = createBoolean("onLuonnos");

    public final EnumPath<Funktiotyyppi> tyyppi = createEnum("tyyppi", Funktiotyyppi.class);

    public final QValintaryhma valintaryhma;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QLaskentakaava(String variable) {
        this(Laskentakaava.class, forVariable(variable), INITS);
    }

    public QLaskentakaava(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QLaskentakaava(PathMetadata<?> metadata, PathInits inits) {
        this(Laskentakaava.class, metadata, inits);
    }

    public QLaskentakaava(Class<? extends Laskentakaava> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.funktiokutsu = inits.isInitialized("funktiokutsu") ? new QFunktiokutsu(forProperty("funktiokutsu"), inits.get("funktiokutsu")) : null;
        this.hakukohde = inits.isInitialized("hakukohde") ? new QHakukohdeViite(forProperty("hakukohde"), inits.get("hakukohde")) : null;
        this.valintaryhma = inits.isInitialized("valintaryhma") ? new QValintaryhma(forProperty("valintaryhma"), inits.get("valintaryhma")) : null;
    }

}

