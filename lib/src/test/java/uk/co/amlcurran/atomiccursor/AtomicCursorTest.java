package uk.co.amlcurran.atomiccursor;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class AtomicCursorTest {

    @Test
    public void testSubmittingACursorResultsInDatasetChanged() {
        AssertingCallbacks callbacks = new AssertingCallbacks();
        AtomicCursor atomicCursor = new AtomicCursor();
        atomicCursor.setCallbacks(callbacks);

        atomicCursor.submit(ListCursor.withIds(1, 2, 3));

        assertThat(callbacks.hasChanged).isTrue();
    }

    @Test
    public void testSubmittingANullCursorFirstDoesNotChangeData() {
        AssertingCallbacks callbacks = new AssertingCallbacks();
        AtomicCursor atomicCursor = new AtomicCursor();
        atomicCursor.setCallbacks(callbacks);

        atomicCursor.submit(null);

        assertThat(callbacks.hasChanged).isFalse();
    }

    @Test
    public void testAddingAnItem() {
        AssertingCallbacks callbacks = new AssertingCallbacks();
        AtomicCursor atomicCursor = new AtomicCursor();

        atomicCursor.submit(ListCursor.withIds(1, 2, 3));
        atomicCursor.setCallbacks(callbacks);
        atomicCursor.submit(ListCursor.withIds(1, 2, 4, 3));

        callbacks.assertInsertedAt(2);
    }

    @Test
    public void testAMoveIsntAnAdd() {
        AssertingCallbacks callbacks = new AssertingCallbacks();
        AtomicCursor atomicCursor = new AtomicCursor();

        atomicCursor.submit(ListCursor.withIds(1, 2, 3));
        atomicCursor.setCallbacks(callbacks);
        atomicCursor.submit(ListCursor.withIds(1, 3, 2));

        callbacks.assertNoAdditions();
    }

    @Test
    public void testDeletingAnItem() {
        AssertingCallbacks callbacks = new AssertingCallbacks();
        AtomicCursor atomicCursor = new AtomicCursor();

        atomicCursor.submit(ListCursor.withIds(1, 2, 3));
        atomicCursor.setCallbacks(callbacks);
        atomicCursor.submit(ListCursor.withIds(1, 3));

        callbacks.assertDeletedAt(1);
    }

//    @Test
//    public void testAMoveIsntADelete() {
//        AssertingCallbacks callbacks = new AssertingCallbacks();
//        AtomicCursor atomicCursor = new AtomicCursor();
//
//        atomicCursor.submit(ListCursor.withIds(1, 2, 3));
//        atomicCursor.setCallbacks(callbacks);
//        atomicCursor.submit(ListCursor.withIds(1, 3, 2));
//
//        callbacks.assertNoDeletions();
//    }

    @Test
    public void testDeletingAnItemAtTheStart() {
        AssertingCallbacks callbacks = new AssertingCallbacks();
        AtomicCursor atomicCursor = new AtomicCursor();

        atomicCursor.submit(ListCursor.withIds(1, 2, 3));
        atomicCursor.setCallbacks(callbacks);
        atomicCursor.submit(ListCursor.withIds(2, 3));

        callbacks.assertDeletedAt(0);
    }

    @Test
    public void testDeletingTwoItems() {
        AssertingCallbacks callbacks = new AssertingCallbacks();
        AtomicCursor atomicCursor = new AtomicCursor();

        atomicCursor.submit(ListCursor.withIds(1, 2, 3, 4, 5));
        atomicCursor.setCallbacks(callbacks);
        atomicCursor.submit(ListCursor.withIds(1, 3, 5));

        callbacks.assertDeletedAt(1);
        callbacks.assertDeletedAt(3);
    }

    @Test
    public void testAddingAnItemDoesntNotifyGenericChange() {
        AssertingCallbacks callbacks = new AssertingCallbacks();
        AtomicCursor atomicCursor = new AtomicCursor();

        atomicCursor.submit(ListCursor.withIds(1, 2, 3));
        atomicCursor.setCallbacks(callbacks);
        atomicCursor.submit(ListCursor.withIds(1, 2, 4, 3));

        assertThat(callbacks.hasChanged).isFalse();
    }

    @Test
    public void testAddingTwoItems() {
        AssertingCallbacks callbacks = new AssertingCallbacks();
        AtomicCursor atomicCursor = new AtomicCursor();

        atomicCursor.submit(ListCursor.withIds(1, 2, 3));
        atomicCursor.setCallbacks(callbacks);
        atomicCursor.submit(ListCursor.withIds(5, 1, 2, 4, 3));

        callbacks.assertInsertedAt(0);
        callbacks.assertInsertedAt(2);
    }

    static class AssertingCallbacks implements AtomicCursor.Callbacks {
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

        private void assertInsertedAt(int position) {
            assertThat(insertedAt.contains(position))
                    .overridingErrorMessage("Expected insert at %1d, was inserted at %2$s", position, insertedAt.toString())
                    .isTrue();

        }

        public void assertDeletedAt(int position) {
            assertThat(deletedAt.contains(position))
                    .overridingErrorMessage("Expected delete at %1d, was deleted at %2$s", position, deletedAt.toString())
                    .isTrue();
        }

        public void assertNoDeletions() {
            assertThat(deletedAt.size())
                    .overridingErrorMessage("Expected no deletes, was deleted at " + deletedAt.toString())
                    .isEqualTo(0);
        }

        public void assertNoAdditions() {
            assertThat(insertedAt.size())
                    .overridingErrorMessage("Expected no inserts, was inserted at " + insertedAt.toString())
                    .isEqualTo(0);
        }
    }

}