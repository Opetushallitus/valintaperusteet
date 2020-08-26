package fi.vm.sade.service.valintaperusteet.model;

public class Laskentakaava implements FunktionArgumentti {
  private LaskentakaavaId id;

  private long version;

  private boolean onLuonnos;

  private String nimi;

  private String kuvaus;

  private LaskentakaavaId kopioLaskentakaavasta;

  private ValintaryhmaId valintaryhmaId;

  private HakukohdeViiteId hakukohdeViiteId;

  private Funktiokutsu funktiokutsu;

  public Laskentakaava(LaskentakaavaId id,
                       long version,
                       boolean onLuonnos,
                       String nimi,
                       String kuvaus,
                       LaskentakaavaId kopioLaskentakaavasta,
                       ValintaryhmaId valintaryhmaId,
                       HakukohdeViiteId hakukohdeViiteId,
                       Funktiokutsu funktiokutsu) {
    this.id = id;
    this.version = version;
    this.onLuonnos = onLuonnos;
    this.nimi = nimi;
    this.kuvaus = kuvaus;
    this.kopioLaskentakaavasta = kopioLaskentakaavasta;
    this.valintaryhmaId = valintaryhmaId;
    this.hakukohdeViiteId = hakukohdeViiteId;
    this.funktiokutsu = funktiokutsu;
  }

  public LaskentakaavaId getId() {
    return id;
  }

  public String getNimi() {
    return nimi;
  }

  public String getKuvaus() {
    return kuvaus;
  }

  public boolean getOnLuonnos() {
    return onLuonnos;
  }

  public HakukohdeViiteId getHakukohdeViiteId() {
    return hakukohdeViiteId;
  }

  public ValintaryhmaId getValintaryhmaId() {
    return valintaryhmaId;
  }

  public Funktiokutsu getFunktiokutsu() {
    return funktiokutsu;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Laskentakaava that = (Laskentakaava) o;

    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
