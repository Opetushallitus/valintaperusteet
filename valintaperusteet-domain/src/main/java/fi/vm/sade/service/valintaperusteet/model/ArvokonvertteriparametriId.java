package fi.vm.sade.service.valintaperusteet.model;

public class ArvokonvertteriparametriId {
    private final long id;

    public ArvokonvertteriparametriId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArvokonvertteriparametriId that = (ArvokonvertteriparametriId) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
