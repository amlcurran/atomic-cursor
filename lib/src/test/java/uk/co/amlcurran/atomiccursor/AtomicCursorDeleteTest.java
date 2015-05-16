package uk.co.amlcurran.atomiccursor;

import org.junit.Test;

public class AtomicCursorDeleteTest {

    @Test
    public void testDeletingAnItem() {
        AssertingCallbacks callbacks = new AssertingCallbacks();
        AtomicCursor atomicCursor = new AtomicCursor();

        atomicCursor.submit(ListCursor.withIds(1, 2, 3));
        atomicCursor.setCallbacks(callbacks);
        atomicCursor.submit(ListCursor.withIds(1, 3));

        callbacks.assertDeletedAt(1);
    }

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

}