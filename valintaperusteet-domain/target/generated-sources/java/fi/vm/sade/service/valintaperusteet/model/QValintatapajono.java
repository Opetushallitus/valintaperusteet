package fi.vm.sade.service.valintaperusteet.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QValintatapajono is a Querydsl query type for Valintatapajono
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QValintatapajono extends EntityPathBase<Valintatapajono> {

    private static final long serialVersionUID = 1779452792;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QValintatapajono valintatapajono = new QValintatapajono("valintatapajono");

    public final fi.vm.sade.generic.model.QBaseEntity _super = new fi.vm.sade.generic.model.QBaseEntity(this);

    public final BooleanPath aktiivinen = createBoolean("aktiivinen");

    public final NumberPath<Integer> aloituspaikat = createNumber("aloituspaikat", Integer.class);

    public final QValintatapajono edellinenValintatapajono;

    public final ListPath<Long, NumberPath<Long>> hakijaryhmaId = this.<Long, NumberPath<Long>>createList("hakijaryhmaId", Long.class, NumberPath.class);

    public final SetPath<Hakijaryhma, QHakijaryhma> hakijaryhmat = this.<Hakijaryhma, QHakijaryhma>createSet("hakijaryhmat", Hakijaryhma.class, QHakijaryhma.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final SetPath<Jarjestyskriteeri, QJarjestyskriteeri> jarjestyskriteerit = this.<Jarjestyskriteeri, QJarjestyskriteeri>createSet("jarjestyskriteerit", Jarjestyskriteeri.class, QJarjestyskriteeri.class);

    public final SetPath<Valintatapajono, QValintatapajono> kopioValintatapajonot = this.<Valintatapajono, QValintatapajono>createSet("kopioValintatapajonot", Valintatapajono.class, QValintatapajono.class);

    public final StringPath kuvaus = createString("kuvaus");

    public final QValintatapajono masterValintatapajono;

    public final StringPath nimi = createString("nimi");

    public final StringPath oid = createString("oid");

    public final QValintatapajono seuraavaValintatapajono;

    public final BooleanPath siirretaanSijoitteluun = createBoolean("siirretaanSijoitteluun");

    public final EnumPath<Tasapistesaanto> tasapistesaanto = createEnum("tasapistesaanto", Tasapistesaanto.class);

    public final QValinnanVaihe valinnanVaihe;

    public final NumberPath<Long> valinnanVaiheId = createNumber("valinnanVaiheId", Long.class);

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QValintatapajono(String variable) {
        this(Valintatapajono.class, forVariable(variable), INITS);
    }

    public QValintatapajono(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QValintatapajono(PathMetadata<?> metadata, PathInits inits) {
        this(Valintatapajono.class, metadata, inits);
    }

    public QValintatapajono(Class<? extends Valintatapajono> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.edellinenValintatapajono = inits.isInitialized("edellinenValintatapajono") ? new QValintatapajono(forProperty("edellinenValintatapajono"), inits.get("edellinenValintatapajono")) : null;
        this.masterValintatapajono = inits.isInitialized("masterValintatapajono") ? new QValintatapajono(forProperty("masterValintatapajono"), inits.get("masterValintatapajono")) : null;
        this.seuraavaValintatapajono = inits.isInitialized("seuraavaValintatapajono") ? new QValintatapajono(forProperty("seuraavaValintatapajono"), inits.get("seuraavaValintatapajono")) : null;
        this.valinnanVaihe = inits.isInitialized("valinnanVaihe") ? new QValinnanVaihe(forProperty("valinnanVaihe"), inits.get("valinnanVaihe")) : null;
    }

}

