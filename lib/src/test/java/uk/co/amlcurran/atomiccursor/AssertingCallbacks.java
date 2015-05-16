package uk.co.amlcurran.atomiccursor;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

class AssertingCallbacks implements AtomicCursor.Callbacks {
    public boolean hasChanged;
    private List<Integer> insertedAt = new ArrayList<>();
    private List<Integer> deletedAt = new ArrayList<>();

    @Override
    public void dataChanged() {
        hasChanged = true;
    }

    @Override
    public void insertedAt(int position) {
        insertedAt.add(position);
    }

    @Override
    public void deletedAt(int position) {
        deletedAt.add(position);
    }

    void assertInsertedAt(int position) {
        assertThat(insertedAt.contains(position))
                .overridingErrorMessage("Expected insert at %1d, was inserted at %2$s", position, insertedAt.toString())
                .isTrue();

    }

    void assertDeletedAt(int position) {
        assertThat(deletedAt.contains(position))
                .overridingErrorMessage("Expected delete at %1d, was deleted at %2$s", position, deletedAt.toString())
                .isTrue();
    }

    void assertNoDeletions() {
        assertThat(deletedAt.size())
                .overridingErrorMessage("Expected no deletes, was deleted at " + deletedAt.toString())
                .isEqualTo(0);
    }

    void assertNoAdditions() {
        assertThat(insertedAt.size())
                .overridingErrorMessage("Expected no inserts, was inserted at " + insertedAt.toString())
                .isEqualTo(0);
    }

    public void assertChanged() {
        assertThat(hasChanged).isTrue();
    }
}
