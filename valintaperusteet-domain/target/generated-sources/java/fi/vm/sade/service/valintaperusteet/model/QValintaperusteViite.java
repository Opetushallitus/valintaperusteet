package fi.vm.sade.service.valintaperusteet.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.*;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;


/**
 * QValintaperusteViite is a Querydsl query type for ValintaperusteViite
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QValintaperusteViite extends EntityPathBase<ValintaperusteViite> {

    private static final long serialVersionUID = 1529133519;

    private static final PathInits INITS = PathInits.DIRECT;

    public static final QValintaperusteViite valintaperusteViite = new QValintaperusteViite("valintaperusteViite");

    public final fi.vm.sade.generic.model.QBaseEntity _super = new fi.vm.sade.generic.model.QBaseEntity(this);

    public final QFunktiokutsu funktiokutsu;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final StringPath kuvaus = createString("kuvaus");

    public final EnumPath<Valintaperustelahde> lahde = createEnum("lahde", Valintaperustelahde.class);

    public final BooleanPath onPaasykoe = createBoolean("onPaasykoe");

    public final BooleanPath onPakollinen = createBoolean("onPakollinen");

    public final StringPath tunniste = createString("tunniste");

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QValintaperusteViite(String variable) {
        this(ValintaperusteViite.class, forVariable(variable), INITS);
    }

    public QValintaperusteViite(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QValintaperusteViite(PathMetadata<?> metadata, PathInits inits) {
        this(ValintaperusteViite.class, metadata, inits);
    }

    public QValintaperusteViite(Class<? extends ValintaperusteViite> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.funktiokutsu = inits.isInitialized("funktiokutsu") ? new QFunktiokutsu(forProperty("funktiokutsu"), inits.get("funktiokutsu")) : null;
    }

}

