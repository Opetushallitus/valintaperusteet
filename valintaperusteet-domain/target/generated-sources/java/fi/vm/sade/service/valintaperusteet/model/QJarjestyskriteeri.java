package fi.vm.sade.service.valintaperusteet.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QJarjestyskriteeri is a Querydsl query type for Jarjestyskriteeri
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QJarjestyskriteeri extends EntityPathBase<Jarjestyskriteeri> {

    private static final long serialVersionUID = -1416359461;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QJarjestyskriteeri jarjestyskriteeri = new QJarjestyskriteeri("jarjestyskriteeri");

    public final fi.vm.sade.generic.model.QBaseEntity _super = new fi.vm.sade.generic.model.QBaseEntity(this);

    public final BooleanPath aktiivinen = createBoolean("aktiivinen");

    public final QJarjestyskriteeri edellinen;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final SetPath<Jarjestyskriteeri, QJarjestyskriteeri> kopiot = this.<Jarjestyskriteeri, QJarjestyskriteeri>createSet("kopiot", Jarjestyskriteeri.class, QJarjestyskriteeri.class);

    public final QLaskentakaava laskentakaava;

    public final QJarjestyskriteeri master;

    public final StringPath metatiedot = createString("metatiedot");

    public final StringPath oid = createString("oid");

    public final QJarjestyskriteeri seuraava;

    public final QValintatapajono valintatapajono;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QJarjestyskriteeri(String variable) {
        this(Jarjestyskriteeri.class, forVariable(variable), INITS);
    }

    public QJarjestyskriteeri(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QJarjestyskriteeri(PathMetadata<?> metadata, PathInits inits) {
        this(Jarjestyskriteeri.class, metadata, inits);
    }

    public QJarjestyskriteeri(Class<? extends Jarjestyskriteeri> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.edellinen = inits.isInitialized("edellinen") ? new QJarjestyskriteeri(forProperty("edellinen"), inits.get("edellinen")) : null;
        this.laskentakaava = inits.isInitialized("laskentakaava") ? new QLaskentakaava(forProperty("laskentakaava"), inits.get("laskentakaava")) : null;
        this.master = inits.isInitialized("master") ? new QJarjestyskriteeri(forProperty("master"), inits.get("master")) : null;
        this.seuraava = inits.isInitialized("seuraava") ? new QJarjestyskriteeri(forProperty("seuraava"), inits.get("seuraava")) : null;
        this.valintatapajono = inits.isInitialized("valintatapajono") ? new QValintatapajono(forProperty("valintatapajono"), inits.get("valintatapajono")) : null;
    }

}

