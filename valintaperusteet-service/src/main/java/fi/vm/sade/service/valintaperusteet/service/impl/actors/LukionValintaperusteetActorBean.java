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
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintakoe;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import fi.vm.sade.service.valintaperusteet.service.*;
import fi.vm.sade.service.valintaperusteet.service.impl.LuoValintaperusteetServiceImpl;
import fi.vm.sade.service.valintaperusteet.service.impl.actors.messages.LukionValintaperuste;
import fi.vm.sade.service.valintaperusteet.service.impl.actors.messages.LuoValintaperuste;
import fi.vm.sade.service.valintaperusteet.service.impl.generator.PkJaYoPohjaiset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import scala.concurrent.duration.Duration;

import javax.inject.Named;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kjsaila
 * Date: 17/12/13
 * Time: 13:10
 * To change this template use File | Settings | File Templates.
 */

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

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return new OneForOneStrategy(5, Duration.create("20 seconds"),
                new Function<Throwable, SupervisorStrategy.Directive>() {
                    public SupervisorStrategy.Directive apply(Throwable cause) {
                        log.error("Virhe valintaperusteiden luonnissa (LukionValintaperusteetActorBean). Syy: {}, viesti:{}", cause.getCause(), cause.getMessage());
                        return SupervisorStrategy.restart();
                    }
                });
    }

    public void onReceive(Object message) throws Exception {

        if (message instanceof LukionValintaperuste) {
            LukionValintaperuste peruste = (LukionValintaperuste)message;

            TransactionStatus tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
            transactionManager.commit(tx);
            tx = transactionManager.getTransaction(new DefaultTransactionDefinition());

            hakukohdekoodiService
                    .lisaaHakukohdekoodiValintaryhmalle(peruste.getPainotettuKeskiarvoVr().getOid(), peruste.getHakukohdekoodi());
            hakukohdekoodiService.lisaaHakukohdekoodiValintaryhmalle(peruste.getPainotettuKeskiarvoJaLisanayttoVr().getOid(),
                    peruste.getHakukohdekoodi());
            hakukohdekoodiService.lisaaHakukohdekoodiValintaryhmalle(peruste.getPainotettuKeskiarvoJaPaasykoeVr().getOid(),
                    peruste.getHakukohdekoodi());
            hakukohdekoodiService.lisaaHakukohdekoodiValintaryhmalle(
                    peruste.getPainotettuKeskiarvoJaPaasykoeJaLisanayttoVr().getOid(), peruste.getHakukohdekoodi());

            transactionManager.commit(tx);


        }else if(message instanceof Exception) {
            Exception exp = (Exception)message;
            exp.printStackTrace();
            getContext().stop(self());
        } else {
            unhandled(message);
            getContext().stop(getSelf());
        }

    }


}
