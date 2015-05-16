package uk.co.amlcurran;

public class IdentifiableObject {

    private final long id;

    public IdentifiableObject(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
