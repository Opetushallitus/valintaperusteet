package fi.vm.sade.service.valintaperusteet.model;

public class Syotettavanarvontyyppi {
    private SyotettavanarvontyyppiId id;

    private long version;

    private String uri;

    private String nimiFi;

    private String nimiSv;

    private String nimiEn;

    private String arvo;

    public Syotettavanarvontyyppi(SyotettavanarvontyyppiId id,
                                  long version,
                                  String uri,
                                  String nimiFi,
                                  String nimiSv,
                                  String nimiEn,
                                  String arvo) {
        this.id = id;
        this.version = version;
        this.uri = uri;
        this.nimiFi = nimiFi;
        this.nimiSv = nimiSv;
        this.nimiEn = nimiEn;
        this.arvo = arvo;
    }

    public SyotettavanarvontyyppiId getId() {
        return id;
    }

    public String getUri() {
        return uri;
    }

    public String getNimiFi() {
        return nimiFi;
    }

    public String getNimiSv() {
        return nimiSv;
    }

    public String getNimiEn() {
        return nimiEn;
    }

    public String getArvo() {
        return arvo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Syotettavanarvontyyppi that = (Syotettavanarvontyyppi) o;

        return uri.equals(that.uri);
    }

    @Override
    public int hashCode() {
        return uri.hashCode();
    }
}
