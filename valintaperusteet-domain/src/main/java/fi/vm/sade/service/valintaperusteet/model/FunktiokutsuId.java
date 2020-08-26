package fi.vm.sade.service.valintaperusteet.model;

public class FunktiokutsuId {
    public final long id;

    public FunktiokutsuId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FunktiokutsuId that = (FunktiokutsuId) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
