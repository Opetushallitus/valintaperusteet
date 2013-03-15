package fi.vm.sade.service.valintaperusteet.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QHakukohdeViite is a Querydsl query type for HakukohdeViite
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QHakukohdeViite extends EntityPathBase<HakukohdeViite> {

    private static final long serialVersionUID = 1334184868;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QHakukohdeViite hakukohdeViite = new QHakukohdeViite("hakukohdeViite");

    public final fi.vm.sade.generic.model.QBaseEntity _super = new fi.vm.sade.generic.model.QBaseEntity(this);

    public final StringPath hakuoid = createString("hakuoid");

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final SetPath<Laskentakaava, QLaskentakaava> laskentakaava = this.<Laskentakaava, QLaskentakaava>createSet("laskentakaava", Laskentakaava.class, QLaskentakaava.class);

    public final StringPath nimi = createString("nimi");

    public final StringPath oid = createString("oid");

    public final SetPath<ValinnanVaihe, QValinnanVaihe> valinnanvaiheet = this.<ValinnanVaihe, QValinnanVaihe>createSet("valinnanvaiheet", ValinnanVaihe.class, QValinnanVaihe.class);

    public final QValintaryhma valintaryhma;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QHakukohdeViite(String variable) {
        this(HakukohdeViite.class, forVariable(variable), INITS);
    }

    public QHakukohdeViite(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QHakukohdeViite(PathMetadata<?> metadata, PathInits inits) {
        this(HakukohdeViite.class, metadata, inits);
    }

    public QHakukohdeViite(Class<? extends HakukohdeViite> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.valintaryhma = inits.isInitialized("valintaryhma") ? new QValintaryhma(forProperty("valintaryhma"), inits.get("valintaryhma")) : null;
    }

}

