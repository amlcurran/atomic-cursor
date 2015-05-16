package uk.co.amlcurran.atomiccursor;

import org.junit.Test;

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

}