package uk.co.amlcurran.atomiccursor;

import org.junit.Ignore;
import org.junit.Test;

public class AtomicCursorComboTest {

    @Test
    public void testAddingAndDeletingAnItem() {
        AssertingCallbacks callbacks = new AssertingCallbacks();
        AtomicCursor atomicCursor = new AtomicCursor();

        atomicCursor.submit(ListCursor.withIds(1, 2, 3));
        atomicCursor.setCallbacks(callbacks);
        atomicCursor.submit(ListCursor.withIds(2, 4, 3));

        callbacks.assertInsertedAt(2);
        callbacks.assertDeletedAt(0);
    }

    @Test
    @Ignore(value = "should handle in future")
    public void testUnhandledChangesResultInFullChange() {
        AssertingCallbacks callbacks = new AssertingCallbacks();
        AtomicCursor atomicCursor = new AtomicCursor();

        atomicCursor.submit(ListCursor.withIds(1, 3, 2));
        atomicCursor.setCallbacks(callbacks);
        atomicCursor.submit(ListCursor.withIds(1, 2, 3));

        callbacks.assertNoAdditions();
        callbacks.assertNoDeletions();
        callbacks.assertChanged();
    }

}