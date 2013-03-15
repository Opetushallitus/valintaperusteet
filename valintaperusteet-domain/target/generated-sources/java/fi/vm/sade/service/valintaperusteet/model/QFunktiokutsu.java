package fi.vm.sade.service.valintaperusteet.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QFunktiokutsu is a Querydsl query type for Funktiokutsu
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QFunktiokutsu extends EntityPathBase<Funktiokutsu> {

    private static final long serialVersionUID = 1371918493;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QFunktiokutsu funktiokutsu = new QFunktiokutsu("funktiokutsu");

    public final fi.vm.sade.generic.model.QBaseEntity _super = new fi.vm.sade.generic.model.QBaseEntity(this);

    public final SetPath<Arvokonvertteriparametri, QArvokonvertteriparametri> arvokonvertteriparametrit = this.<Arvokonvertteriparametri, QArvokonvertteriparametri>createSet("arvokonvertteriparametrit", Arvokonvertteriparametri.class, QArvokonvertteriparametri.class);

    public final SetPath<Arvovalikonvertteriparametri, QArvovalikonvertteriparametri> arvovalikonvertteriparametrit = this.<Arvovalikonvertteriparametri, QArvovalikonvertteriparametri>createSet("arvovalikonvertteriparametrit", Arvovalikonvertteriparametri.class, QArvovalikonvertteriparametri.class);

    public final SetPath<Funktioargumentti, QFunktioargumentti> funktioargumentit = this.<Funktioargumentti, QFunktioargumentti>createSet("funktioargumentit", Funktioargumentti.class, QFunktioargumentti.class);

    public final EnumPath<Funktionimi> funktionimi = createEnum("funktionimi", Funktionimi.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final SetPath<Syoteparametri, QSyoteparametri> syoteparametrit = this.<Syoteparametri, QSyoteparametri>createSet("syoteparametrit", Syoteparametri.class, QSyoteparametri.class);

    public final QValintaperusteViite valintaperuste;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QFunktiokutsu(String variable) {
        this(Funktiokutsu.class, forVariable(variable), INITS);
    }

    public QFunktiokutsu(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QFunktiokutsu(PathMetadata<?> metadata, PathInits inits) {
        this(Funktiokutsu.class, metadata, inits);
    }

    public QFunktiokutsu(Class<? extends Funktiokutsu> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.valintaperuste = inits.isInitialized("valintaperuste") ? new QValintaperusteViite(forProperty("valintaperuste"), inits.get("valintaperuste")) : null;
    }

}

