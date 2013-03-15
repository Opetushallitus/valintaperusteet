package fi.vm.sade.service.valintaperusteet.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QArvovalikonvertteriparametri is a Querydsl query type for Arvovalikonvertteriparametri
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QArvovalikonvertteriparametri extends EntityPathBase<Arvovalikonvertteriparametri> {

    private static final long serialVersionUID = 845147137;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QArvovalikonvertteriparametri arvovalikonvertteriparametri = new QArvovalikonvertteriparametri("arvovalikonvertteriparametri");

    public final QKonvertteriparametri _super;

    // inherited
    public final QFunktiokutsu funktiokutsu;

    //inherited
    public final BooleanPath hylkaysperuste;

    //inherited
    public final NumberPath<Long> id;

    public final NumberPath<Double> maxValue = createNumber("maxValue", Double.class);

    public final NumberPath<Double> minValue = createNumber("minValue", Double.class);

    public final BooleanPath palautaHaettuArvo = createBoolean("palautaHaettuArvo");

    //inherited
    public final StringPath paluuarvo;

    //inherited
    public final NumberPath<Long> version;

    public QArvovalikonvertteriparametri(String variable) {
        this(Arvovalikonvertteriparametri.class, forVariable(variable), INITS);
    }

    public QArvovalikonvertteriparametri(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QArvovalikonvertteriparametri(PathMetadata<?> metadata, PathInits inits) {
        this(Arvovalikonvertteriparametri.class, metadata, inits);
    }

    public QArvovalikonvertteriparametri(Class<? extends Arvovalikonvertteriparametri> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QKonvertteriparametri(type, metadata, inits);
        this.funktiokutsu = _super.funktiokutsu;
        this.hylkaysperuste = _super.hylkaysperuste;
        this.id = _super.id;
        this.paluuarvo = _super.paluuarvo;
        this.version = _super.version;
    }

}

