package fi.vm.sade.service.valintaperusteet.model;


import static com.mysema.query.types.PathMetadataFactory.*;
import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QKonvertteriparametri is a Querydsl query type for Konvertteriparametri
 */
@Generated("com.mysema.query.codegen.SupertypeSerializer")
public class QKonvertteriparametri extends EntityPathBase<Konvertteriparametri> {

    private static final long serialVersionUID = -1359606545;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QKonvertteriparametri konvertteriparametri = new QKonvertteriparametri("konvertteriparametri");

    public final fi.vm.sade.generic.model.QBaseEntity _super = new fi.vm.sade.generic.model.QBaseEntity(this);

    public final QFunktiokutsu funktiokutsu;

    public final BooleanPath hylkaysperuste = createBoolean("hylkaysperuste");

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final StringPath paluuarvo = createString("paluuarvo");

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QKonvertteriparametri(String variable) {
        this(Konvertteriparametri.class, forVariable(variable), INITS);
    }

    public QKonvertteriparametri(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QKonvertteriparametri(PathMetadata<?> metadata, PathInits inits) {
        this(Konvertteriparametri.class, metadata, inits);
    }

    public QKonvertteriparametri(Class<? extends Konvertteriparametri> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.funktiokutsu = inits.isInitialized("funktiokutsu") ? new QFunktiokutsu(forProperty("funktiokutsu"), inits.get("funktiokutsu")) : null;
    }

}

