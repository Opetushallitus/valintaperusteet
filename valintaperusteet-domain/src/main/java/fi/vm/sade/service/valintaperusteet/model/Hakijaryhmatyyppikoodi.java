package fi.vm.sade.service.valintaperusteet.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hakijaryhmatyyppikoodi")
@Cacheable(true)
public class Hakijaryhmatyyppikoodi extends Koodi{

    @OneToMany(mappedBy="hakijaryhmatyyppikoodi")
    private List<Hakijaryhma> hakijaryhmas = new ArrayList<Hakijaryhma>();

    @OneToMany(mappedBy="hakijaryhmatyyppikoodi")
    private List<HakijaryhmaValintatapajono> hakijaryhmaJonos = new ArrayList<HakijaryhmaValintatapajono>();
}
