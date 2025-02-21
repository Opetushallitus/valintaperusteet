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
import jakarta.validation.constraints.Min;
import java.io.IOException;

public class Funktioargumentti extends BaseEntity implements Comparable<Funktioargumentti> {

  @JsonBackReference
  private Funktiokutsu parent;

  private Funktiokutsu funktiokutsuChild;

  @JsonSerialize(using = CustomLaskentakaavaSerializer.class)
  @JsonDeserialize(using = CustomLaskentakaavaDeserializer.class)
  private Laskentakaava laskentakaavaChild;

  @Min(1)
  private Integer indeksi;

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