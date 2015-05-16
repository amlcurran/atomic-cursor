package uk.co.amlcurran;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class AtomicCursorTest {

    @Test
    public void testSubmittingACursorResultsInDatasetChanged() {
        AssertingCallbacks callbacks = new AssertingCallbacks();
        AtomicCursor atomicCursor = new AtomicCursor(callbacks);

        atomicCursor.submit(ListCursor.withIds(1, 2, 3));

        assertThat(callbacks.hasChanged).isTrue();
    }

    @Test
    public void testSubmittingANullCursorFirstDoesNotChangeData() {
        AssertingCallbacks callbacks = new AssertingCallbacks();
        AtomicCursor atomicCursor = new AtomicCursor(callbacks);

        atomicCursor.submit(null);

        assertThat(callbacks.hasChanged).isFalse();
    }

    @Test
    public void testAddingAnItem() {
        AssertingCallbacks callbacks = new AssertingCallbacks();
        AtomicCursor atomicCursor = new AtomicCursor(callbacks);

        atomicCursor.submit(ListCursor.withIds(1, 2, 3));
        atomicCursor.submit(ListCursor.withIds(1, 2, 4, 3));

        assertThat(callbacks.insertedAt).isEqualTo(2);
    }

    class AssertingCallbacks {
        public boolean hasChanged;
        public int insertedAt;

        public void dataChanged() {
            hasChanged = true;
        }

        public void insertedAt(int position) {
            insertedAt = position;
        }
    }

}