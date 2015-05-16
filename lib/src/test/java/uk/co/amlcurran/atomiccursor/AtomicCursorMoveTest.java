package uk.co.amlcurran.atomiccursor;

import org.junit.Test;

public class AtomicCursorMoveTest {

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
    public void testAMoveIsntADelete() {
        AssertingCallbacks callbacks = new AssertingCallbacks();
        AtomicCursor atomicCursor = new AtomicCursor();

        atomicCursor.submit(ListCursor.withIds(1, 2, 3));
        atomicCursor.setCallbacks(callbacks);
        atomicCursor.submit(ListCursor.withIds(1, 3, 2));

        callbacks.assertNoDeletions();
    }

}