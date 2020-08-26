package fi.vm.sade.service.valintaperusteet.model;

public class ValintaperusteViiteId {
    public final long id;

    public ValintaperusteViiteId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValintaperusteViiteId that = (ValintaperusteViiteId) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
