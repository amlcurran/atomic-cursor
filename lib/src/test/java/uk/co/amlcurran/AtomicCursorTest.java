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

    class AssertingCallbacks {
        public boolean hasChanged;

        public void dataChanged() {
            hasChanged = true;
        }
    }

}