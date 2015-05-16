package uk.co.amlcurran.atomiccursor;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class AtomicCursorAddTest {

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
        atomicCursor.submit(ListCursor.withIds(5, 1, 2, 4, 3));

        callbacks.assertInsertedAt(0);
        callbacks.assertInsertedAt(2);
    }

}