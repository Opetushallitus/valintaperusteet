package fi.vm.sade.service.valintaperusteet.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QValintaryhma is a Querydsl query type for Valintaryhma
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QValintaryhma extends EntityPathBase<Valintaryhma> {

    private static final long serialVersionUID = -654025407;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QValintaryhma valintaryhma = new QValintaryhma("valintaryhma");

    public final fi.vm.sade.generic.model.QBaseEntity _super = new fi.vm.sade.generic.model.QBaseEntity(this);

    public final SetPath<Valintaryhma, QValintaryhma> alavalintaryhmat = this.<Valintaryhma, QValintaryhma>createSet("alavalintaryhmat", Valintaryhma.class, QValintaryhma.class);

    public final SetPath<Hakijaryhma, QHakijaryhma> hakijaryhmat = this.<Hakijaryhma, QHakijaryhma>createSet("hakijaryhmat", Hakijaryhma.class, QHakijaryhma.class);

    public final SetPath<HakukohdeViite, QHakukohdeViite> hakukohdeViitteet = this.<HakukohdeViite, QHakukohdeViite>createSet("hakukohdeViitteet", HakukohdeViite.class, QHakukohdeViite.class);

    public final StringPath hakuOid = createString("hakuOid");

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final SetPath<Laskentakaava, QLaskentakaava> laskentakaava = this.<Laskentakaava, QLaskentakaava>createSet("laskentakaava", Laskentakaava.class, QLaskentakaava.class);

    public final StringPath nimi = createString("nimi");

    public final StringPath oid = createString("oid");

    public final SetPath<ValinnanVaihe, QValinnanVaihe> valinnanvaiheet = this.<ValinnanVaihe, QValinnanVaihe>createSet("valinnanvaiheet", ValinnanVaihe.class, QValinnanVaihe.class);

    //inherited
    public final NumberPath<Long> version = _super.version;

    public final QValintaryhma ylavalintaryhma;

    public QValintaryhma(String variable) {
        this(Valintaryhma.class, forVariable(variable), INITS);
    }

    public QValintaryhma(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QValintaryhma(PathMetadata<?> metadata, PathInits inits) {
        this(Valintaryhma.class, metadata, inits);
    }

    public QValintaryhma(Class<? extends Valintaryhma> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.ylavalintaryhma = inits.isInitialized("ylavalintaryhma") ? new QValintaryhma(forProperty("ylavalintaryhma"), inits.get("ylavalintaryhma")) : null;
    }

}

