package fi.vm.sade.service.valintaperusteet.model;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Entity
@Table(name = "funktioargumentti")
@Cacheable(true)
public class Funktioargumentti extends BaseEntity implements Comparable<Funktioargumentti> {

    @JoinColumn(name = "funktiokutsuparent_id", nullable = false)
    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    private Funktiokutsu parent;

    @JoinColumn(name = "funktiokutsuchild_id", nullable = true)
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Funktiokutsu funktiokutsuChild;

    @JoinColumn(name = "laskentakaavachild_id", nullable = true)
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Laskentakaava laskentakaavaChild;

    @Min(1)
    @Column(name = "indeksi", nullable = false)
    private Integer indeksi;

    @Transient
    private Funktiokutsu laajennettuKaava;

    public Funktiokutsu getParent() {
        return parent;
    }

    public void setParent(Funktiokutsu parent) {
        this.parent = parent;
    }

    public Funktiokutsu getFunktiokutsuChild() {
        return funktiokutsuChild;
    }

    public void setFunktiokutsuChild(Funktiokutsu funktiokutsuChild) {
        this.funktiokutsuChild = funktiokutsuChild;
    }

    public Laskentakaava getLaskentakaavaChild() {
        return laskentakaavaChild;
    }

    public void setLaskentakaavaChild(Laskentakaava laskentakaavaChild) {
        this.laskentakaavaChild = laskentakaavaChild;
    }

    public Integer getIndeksi() {
        return indeksi;
    }

    public void setIndeksi(Integer indeksi) {
        this.indeksi = indeksi;
    }

    public Funktiokutsu getLaajennettuKaava() {
        return laajennettuKaava;
    }

    public void setLaajennettuKaava(Funktiokutsu laajennettuKaava) {
        this.laajennettuKaava = laajennettuKaava;
    }

    @Override
    public int compareTo(Funktioargumentti o) {
        return indeksi - o.indeksi;
    }
}
