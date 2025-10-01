package fi.vm.sade.service.valintaperusteet.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import java.io.IOException;

@Entity
@Table(name = "funktioargumentti")
@Cacheable(true)
public class Funktioargumentti extends BaseEntity implements Comparable<Funktioargumentti> {

  @JoinColumn(name = "funktiokutsuparent_id", nullable = false)
  @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
  @JsonBackReference
  private Funktiokutsu parent;

  @JoinColumn(name = "funktiokutsuchild_id", nullable = true)
  @ManyToOne(cascade = CascadeType.PERSIST)
  private Funktiokutsu funktiokutsuChild;

  @JoinColumn(name = "laskentakaavachild_id", nullable = true)
  @ManyToOne(cascade = CascadeType.PERSIST)
  @JsonSerialize(using = CustomLaskentakaavaSerializer.class)
  @JsonDeserialize(using = CustomLaskentakaavaDeserializer.class)
  private Laskentakaava laskentakaavaChild;

  @Min(1)
  @Column(name = "indeksi", nullable = false)
  private Integer indeksi;

  @Transient private Funktiokutsu laajennettuKaava;

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

  static class LaskentaKaavaProxy {
    public long id;

    public LaskentaKaavaProxy() {}

    public LaskentaKaavaProxy(long id) {
      this.id = id;
    }
  }

  static class CustomLaskentakaavaSerializer extends JsonSerializer<Laskentakaava> {

    @Override
    public void serialize(Laskentakaava value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
      gen.writeObject(new LaskentaKaavaProxy(value.getId()));
    }
  }

  static class CustomLaskentakaavaDeserializer extends JsonDeserializer<Laskentakaava> {

    @Override
    public Laskentakaava deserialize(JsonParser p, DeserializationContext ctxt) {
      try {
        LaskentaKaavaProxy proxy = p.readValueAs(LaskentaKaavaProxy.class);
        return EntityManagerUtils.getEntityManager().find(Laskentakaava.class, proxy.id);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
}
