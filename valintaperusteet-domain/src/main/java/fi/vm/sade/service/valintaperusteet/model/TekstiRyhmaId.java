package fi.vm.sade.service.valintaperusteet.model;

public class TekstiRyhmaId {
    public final long id;

    public TekstiRyhmaId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TekstiRyhmaId that = (TekstiRyhmaId) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
