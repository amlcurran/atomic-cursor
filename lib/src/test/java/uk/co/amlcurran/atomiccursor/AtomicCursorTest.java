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
        atomicCursor.submit(ListCursor.withIds(2, 1, 2, 4, 3));

        callbacks.assertInsertedAt(0);
        callbacks.assertInsertedAt(2);
    }

    static class AssertingCallbacks implements AtomicCursor.Callbacks {
        public boolean hasChanged;
        private List<Integer> insertedAt = new ArrayList<>();

        @Override
        public void dataChanged() {
            hasChanged = true;
        }

        @Override
        public void insertedAt(int position) {
            insertedAt.add(position);
        }

        public boolean wasInsertedAt(int position) {
            return insertedAt.contains(position);
        }

        private void assertInsertedAt(int position) {
            assertThat(wasInsertedAt(position))
                    .overridingErrorMessage("Expected insert at %1d, was inserted at %2$s", position, insertedAt.toString())
                    .isTrue();

        }
    }

}