package uk.co.amlcurran;

import org.junit.Test;

import java.util.Arrays;

import static org.fest.assertions.api.Assertions.assertThat;

public class AtomicCursorTest {

    @Test
    public void testSubmittingACursorResultsInDatasetChanged() {
        AssertingCallbacks callbacks = new AssertingCallbacks();
        AtomicCursor atomicCursor = new AtomicCursor(callbacks);

        atomicCursor.submit(new ListCursor(Arrays.asList("1", "2", "3")));

        assertThat(callbacks.hasChanged).isTrue();
    }

    class AssertingCallbacks {
        public boolean hasChanged;

        public void dataChanged() {
            hasChanged = true;
        }
    }

}