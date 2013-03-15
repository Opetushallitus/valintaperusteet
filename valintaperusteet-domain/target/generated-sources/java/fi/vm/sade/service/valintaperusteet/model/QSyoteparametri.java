package fi.vm.sade.service.valintaperusteet.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QSyoteparametri is a Querydsl query type for Syoteparametri
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QSyoteparametri extends EntityPathBase<Syoteparametri> {

    private static final long serialVersionUID = -861793864;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QSyoteparametri syoteparametri = new QSyoteparametri("syoteparametri");

    public final fi.vm.sade.generic.model.QBaseEntity _super = new fi.vm.sade.generic.model.QBaseEntity(this);

    public final StringPath arvo = createString("arvo");

    public final StringPath avain = createString("avain");

    public final QFunktiokutsu funktiokutsu;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QSyoteparametri(String variable) {
        this(Syoteparametri.class, forVariable(variable), INITS);
    }

    public QSyoteparametri(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QSyoteparametri(PathMetadata<?> metadata, PathInits inits) {
        this(Syoteparametri.class, metadata, inits);
    }

    public QSyoteparametri(Class<? extends Syoteparametri> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.funktiokutsu = inits.isInitialized("funktiokutsu") ? new QFunktiokutsu(forProperty("funktiokutsu"), inits.get("funktiokutsu")) : null;
    }

}

