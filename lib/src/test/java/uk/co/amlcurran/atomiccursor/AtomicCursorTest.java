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

    @Test
    public void testMovingAnItem() {
        AssertingCallbacks callbacks = new AssertingCallbacks();
        AtomicCursor atomicCursor = new AtomicCursor();

        atomicCursor.submit(ListCursor.withIds(1, 2, 3));
        atomicCursor.setCallbacks(callbacks);
        atomicCursor.submit(ListCursor.withIds(1, 3, 2));

        callbacks.assertMoved(1, 2);
        callbacks.assertNoDeletions();
        callbacks.assertNoAdditions();
    }

    @Test
    public void testMovingAnItemFromTheStart() {
        AssertingCallbacks callbacks = new AssertingCallbacks();
        AtomicCursor atomicCursor = new AtomicCursor();

        atomicCursor.submit(ListCursor.withIds(1, 2, 3));
        atomicCursor.setCallbacks(callbacks);
        atomicCursor.submit(ListCursor.withIds(2, 1, 3));

        callbacks.assertMoved(0, 1);
    }

    @Test
    public void testMovingTwoItems() {
        AssertingCallbacks callbacks = new AssertingCallbacks();
        AtomicCursor atomicCursor = new AtomicCursor();

        atomicCursor.submit(ListCursor.withIds(1, 2, 3, 4, 5));
        atomicCursor.setCallbacks(callbacks);
        atomicCursor.submit(ListCursor.withIds(1, 3, 5, 4, 2));

        callbacks.assertMoved(1, 4);
        callbacks.assertMoved(4, 2);
        callbacks.assertMoved(2, 1);
    }

    static class AssertingCallbacks implements AtomicCursor.Callbacks {
        public boolean hasChanged;
        private List<Integer> insertedAt = new ArrayList<>();
        private List<Integer> deletedAt = new ArrayList<>();
        private List<Tuple<Integer, Integer>> moved = new ArrayList<>();

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

        @Override
        public void moved(int from, int to) {
            moved.add(new Tuple<>(from, to));
        }

        private void assertInsertedAt(int position) {
            assertThat(insertedAt.contains(position))
                    .overridingErrorMessage("Expected insert at %1d, was inserted at %2$s", position, insertedAt.toString())
                    .isTrue();

        }

        public void assertDeletedAt(int position) {
            assertThat(deletedAt.contains(position))
                    .overridingErrorMessage("Expected delete at %1d, was deleted at %2$s", position, insertedAt.toString())
                    .isTrue();
        }

        public void assertMoved(int from, int to) {
            assertThat(moved.contains(new Tuple<>(from, to)))
                    .overridingErrorMessage("Expected move from %1d to %2$d, was moved at %3$s", from, to, moved.toString())
                    .isTrue();
        }

        public void assertNoDeletions() {
            assertThat(deletedAt.size())
                    .overridingErrorMessage("Shouldn't have deletions at: " + deletedAt.toString())
                    .isEqualTo(0);
        }

        public void assertNoAdditions() {
            assertThat(insertedAt.size())
                    .overridingErrorMessage("Shouldn't have insertations at: " + insertedAt.toString())
                    .isEqualTo(0);
        }

        private class Tuple<A, B> {

            private final A firstItem;
            private final B secondItem;

            private Tuple(A firstItem, B secondItem) {
                this.firstItem = firstItem;
                this.secondItem = secondItem;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;

                Tuple<?, ?> tuple = (Tuple<?, ?>) o;

                return firstItem.equals(tuple.firstItem) && secondItem.equals(tuple.secondItem);

            }

            @Override
            public int hashCode() {
                int result = firstItem.hashCode();
                result = 31 * result + secondItem.hashCode();
                return result;
            }
        }

    }

}