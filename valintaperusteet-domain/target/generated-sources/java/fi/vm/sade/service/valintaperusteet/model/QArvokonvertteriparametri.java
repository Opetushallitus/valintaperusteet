package fi.vm.sade.service.valintaperusteet.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QArvokonvertteriparametri is a Querydsl query type for Arvokonvertteriparametri
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QArvokonvertteriparametri extends EntityPathBase<Arvokonvertteriparametri> {

    private static final long serialVersionUID = -1376235751;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QArvokonvertteriparametri arvokonvertteriparametri = new QArvokonvertteriparametri("arvokonvertteriparametri");

    public final QKonvertteriparametri _super;

    public final StringPath arvo = createString("arvo");

    // inherited
    public final QFunktiokutsu funktiokutsu;

    //inherited
    public final BooleanPath hylkaysperuste;

    //inherited
    public final NumberPath<Long> id;

    //inherited
    public final StringPath paluuarvo;

    //inherited
    public final NumberPath<Long> version;

    public QArvokonvertteriparametri(String variable) {
        this(Arvokonvertteriparametri.class, forVariable(variable), INITS);
    }

    public QArvokonvertteriparametri(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QArvokonvertteriparametri(PathMetadata<?> metadata, PathInits inits) {
        this(Arvokonvertteriparametri.class, metadata, inits);
    }

    public QArvokonvertteriparametri(Class<? extends Arvokonvertteriparametri> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QKonvertteriparametri(type, metadata, inits);
        this.funktiokutsu = _super.funktiokutsu;
        this.hylkaysperuste = _super.hylkaysperuste;
        this.id = _super.id;
        this.paluuarvo = _super.paluuarvo;
        this.version = _super.version;
    }

}

