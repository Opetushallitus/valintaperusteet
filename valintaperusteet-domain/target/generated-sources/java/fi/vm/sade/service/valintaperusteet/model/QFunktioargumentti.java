package fi.vm.sade.service.valintaperusteet.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QFunktioargumentti is a Querydsl query type for Funktioargumentti
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QFunktioargumentti extends EntityPathBase<Funktioargumentti> {

    private static final long serialVersionUID = -787299295;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QFunktioargumentti funktioargumentti = new QFunktioargumentti("funktioargumentti");

    public final fi.vm.sade.generic.model.QBaseEntity _super = new fi.vm.sade.generic.model.QBaseEntity(this);

    public final QFunktiokutsu funktiokutsuChild;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final NumberPath<Integer> indeksi = createNumber("indeksi", Integer.class);

    public final QLaskentakaava laskentakaavaChild;

    public final QFunktiokutsu parent;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QFunktioargumentti(String variable) {
        this(Funktioargumentti.class, forVariable(variable), INITS);
    }

    public QFunktioargumentti(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QFunktioargumentti(PathMetadata<?> metadata, PathInits inits) {
        this(Funktioargumentti.class, metadata, inits);
    }

    public QFunktioargumentti(Class<? extends Funktioargumentti> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.funktiokutsuChild = inits.isInitialized("funktiokutsuChild") ? new QFunktiokutsu(forProperty("funktiokutsuChild"), inits.get("funktiokutsuChild")) : null;
        this.laskentakaavaChild = inits.isInitialized("laskentakaavaChild") ? new QLaskentakaava(forProperty("laskentakaavaChild"), inits.get("laskentakaavaChild")) : null;
        this.parent = inits.isInitialized("parent") ? new QFunktiokutsu(forProperty("parent"), inits.get("parent")) : null;
    }

}

