package fi.vm.sade.service.valintaperusteet.service.impl.actors;

import akka.actor.OneForOneStrategy;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;
import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi;
import fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.service.valintaperusteet.service.impl.LuoValintaperusteetServiceImpl;
import fi.vm.sade.service.valintaperusteet.service.impl.actors.messages.LukionValintaperuste;
import fi.vm.sade.service.valintaperusteet.service.impl.actors.messages.LuoValintaperuste;
import fi.vm.sade.service.valintaperusteet.service.impl.generator.PkJaYoPohjaiset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import scala.concurrent.duration.Duration;

import javax.inject.Named;
import java.util.List;

@Named("LukionValintaperusteetActorBean")
@Component
@org.springframework.context.annotation.Scope(value = "prototype")
public class LukionValintaperusteetActorBean extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    @Autowired
    private HakukohdekoodiService hakukohdekoodiService;

    @Autowired
    private JpaTransactionManager transactionManager;

    public LukionValintaperusteetActorBean() {

    }

    public void onReceive(Object message) throws Exception {
        if (message instanceof LukionValintaperuste) {
            LukionValintaperuste peruste = (LukionValintaperuste) message;
            KoodiDTO hakukohdekoodi = peruste.getHakukohdekoodi();
            ValintaryhmaDTO pkvr = peruste.getPainotettuKeskiarvoVr();
            ValintaryhmaDTO pklsvr = peruste.getPainotettuKeskiarvoJaLisanayttoVr();
            ValintaryhmaDTO pkpsvr = peruste.getPainotettuKeskiarvoJaPaasykoeVr();
            ValintaryhmaDTO pkpslsvr = peruste.getPainotettuKeskiarvoJaPaasykoeJaLisanayttoVr();
            TransactionStatus tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
            hakukohdekoodiService.lisaaHakukohdekoodiValintaryhmalle(pkvr.getOid(), hakukohdekoodi);
            hakukohdekoodiService.lisaaHakukohdekoodiValintaryhmalle(pklsvr.getOid(), hakukohdekoodi);
            hakukohdekoodiService.lisaaHakukohdekoodiValintaryhmalle(pkpsvr.getOid(), hakukohdekoodi);
            hakukohdekoodiService.lisaaHakukohdekoodiValintaryhmalle(pkpslsvr.getOid(), hakukohdekoodi);
            transactionManager.commit(tx);
        } else if (message instanceof Exception) {
            Exception exp = (Exception) message;
            exp.printStackTrace();
            getContext().stop(self());
        } else {
            unhandled(message);
            getContext().stop(getSelf());
        }
    }
}
