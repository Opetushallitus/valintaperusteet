package fi.vm.sade.service.valintaperusteet.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QHakijaryhma is a Querydsl query type for Hakijaryhma
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QHakijaryhma extends EntityPathBase<Hakijaryhma> {

    private static final long serialVersionUID = -1934372952;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QHakijaryhma hakijaryhma = new QHakijaryhma("hakijaryhma");

    public final fi.vm.sade.generic.model.QBaseEntity _super = new fi.vm.sade.generic.model.QBaseEntity(this);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final SetPath<Valintatapajono, QValintatapajono> jonot = this.<Valintatapajono, QValintatapajono>createSet("jonot", Valintatapajono.class, QValintatapajono.class);

    public final NumberPath<Integer> kasittelyjarjestys = createNumber("kasittelyjarjestys", Integer.class);

    public final StringPath nimi = createString("nimi");

    public final StringPath oid = createString("oid");

    public final BooleanPath onPoissulkeva = createBoolean("onPoissulkeva");

    public final QValintaryhma valintaryhma;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QHakijaryhma(String variable) {
        this(Hakijaryhma.class, forVariable(variable), INITS);
    }

    public QHakijaryhma(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QHakijaryhma(PathMetadata<?> metadata, PathInits inits) {
        this(Hakijaryhma.class, metadata, inits);
    }

    public QHakijaryhma(Class<? extends Hakijaryhma> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.valintaryhma = inits.isInitialized("valintaryhma") ? new QValintaryhma(forProperty("valintaryhma"), inits.get("valintaryhma")) : null;
    }

}

