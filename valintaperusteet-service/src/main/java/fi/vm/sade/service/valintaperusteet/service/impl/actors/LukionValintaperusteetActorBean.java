package fi.vm.sade.service.valintaperusteet.service.impl.actors;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.service.valintaperusteet.service.impl.actors.messages.LukionValintaperuste;
import javax.inject.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Named("LukionValintaperusteetActorBean")
@Component
@org.springframework.context.annotation.Scope(value = "prototype")
public class LukionValintaperusteetActorBean extends UntypedActor {
  LoggingAdapter log = Logging.getLogger(getContext().system(), this);

  @Autowired private HakukohdekoodiService hakukohdekoodiService;

  @Autowired private JpaTransactionManager transactionManager;

  public LukionValintaperusteetActorBean() {}

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
      log.error("LukionValintaperusteetActorBean virhetilanne", exp);
      getContext().stop(self());
    } else {
      unhandled(message);
      getContext().stop(getSelf());
    }
  }
}
