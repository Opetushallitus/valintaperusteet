package fi.vm.sade.service.valintaperusteet.model;

public class ArvovalikonvertteriparametriId {
    private final long id;

    public ArvovalikonvertteriparametriId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArvovalikonvertteriparametriId that = (ArvovalikonvertteriparametriId) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
