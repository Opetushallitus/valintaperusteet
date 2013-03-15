package fi.vm.sade.service.valintaperusteet.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QValinnanVaihe is a Querydsl query type for ValinnanVaihe
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QValinnanVaihe extends EntityPathBase<ValinnanVaihe> {

    private static final long serialVersionUID = -813930457;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QValinnanVaihe valinnanVaihe = new QValinnanVaihe("valinnanVaihe");

    public final fi.vm.sade.generic.model.QBaseEntity _super = new fi.vm.sade.generic.model.QBaseEntity(this);

    public final BooleanPath aktiivinen = createBoolean("aktiivinen");

    public final QValinnanVaihe edellinenValinnanVaihe;

    public final QHakukohdeViite hakukohdeViite;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final SetPath<Valintatapajono, QValintatapajono> jonot = this.<Valintatapajono, QValintatapajono>createSet("jonot", Valintatapajono.class, QValintatapajono.class);

    public final SetPath<ValinnanVaihe, QValinnanVaihe> kopioValinnanVaiheet = this.<ValinnanVaihe, QValinnanVaihe>createSet("kopioValinnanVaiheet", ValinnanVaihe.class, QValinnanVaihe.class);

    public final StringPath kuvaus = createString("kuvaus");

    public final QValinnanVaihe masterValinnanVaihe;

    public final StringPath nimi = createString("nimi");

    public final StringPath oid = createString("oid");

    public final QValinnanVaihe seuraavaValinnanVaihe;

    public final QValintaryhma valintaryhma;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QValinnanVaihe(String variable) {
        this(ValinnanVaihe.class, forVariable(variable), INITS);
    }

    public QValinnanVaihe(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QValinnanVaihe(PathMetadata<?> metadata, PathInits inits) {
        this(ValinnanVaihe.class, metadata, inits);
    }

    public QValinnanVaihe(Class<? extends ValinnanVaihe> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.edellinenValinnanVaihe = inits.isInitialized("edellinenValinnanVaihe") ? new QValinnanVaihe(forProperty("edellinenValinnanVaihe"), inits.get("edellinenValinnanVaihe")) : null;
        this.hakukohdeViite = inits.isInitialized("hakukohdeViite") ? new QHakukohdeViite(forProperty("hakukohdeViite"), inits.get("hakukohdeViite")) : null;
        this.masterValinnanVaihe = inits.isInitialized("masterValinnanVaihe") ? new QValinnanVaihe(forProperty("masterValinnanVaihe"), inits.get("masterValinnanVaihe")) : null;
        this.seuraavaValinnanVaihe = inits.isInitialized("seuraavaValinnanVaihe") ? new QValinnanVaihe(forProperty("seuraavaValinnanVaihe"), inits.get("seuraavaValinnanVaihe")) : null;
        this.valintaryhma = inits.isInitialized("valintaryhma") ? new QValintaryhma(forProperty("valintaryhma"), inits.get("valintaryhma")) : null;
    }

}

