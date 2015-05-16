package uk.co.amlcurran.atomiccursor;

import android.database.Cursor;

public class AtomicCursor {

    private Callbacks callbacks = NULL_SAFE_CALLBACKS;
    private Cursor currentCursor = new NullCursor();

    public void setCallbacks(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    public void submit(Cursor cursor) {
        if (cursor == null) {
            currentCursor = new NullCursor();
        } else {
            if (currentCursor instanceof NullCursor) {
                callbacks.dataChanged();
            }
            walkCursor(new WrappedCursor(currentCursor), new WrappedCursor(cursor));
            currentCursor = cursor;
        }
    }

    private void walkCursor(WrappedCursor currentCursor, WrappedCursor newCursor) {
        int offset = 0;
        while (currentCursor.moveToNext()) {
            newCursor.moveToPosition(currentCursor.getPosition() + offset);
            long currentId = currentCursor.getId();
            long newId = newCursor.getId();
            if (currentId != newId) {
                offset += checkForAdditions(currentCursor, newCursor);
                offset += checkForDeletions(currentCursor, newCursor);
            }
        }
    }

    private int checkForDeletions(WrappedCursor currentCursor, WrappedCursor newCursor) {
        int offset = 0;
        long potentiallyDeletedId = currentCursor.getId();
        if (currentCursor.isOneInFrontOf(newCursor) && !newCursor.containsId(potentiallyDeletedId)) {
            callbacks.deletedAt(currentCursor.getPosition());
            offset = -1;
        }
        return offset;
    }

    private int checkForAdditions(WrappedCursor currentCursor, WrappedCursor newCursor) {
        int additions = 0;
        long potentiallyAddedId = newCursor.getId();
        if (newCursor.isOneInFrontOf(currentCursor) && !currentCursor.containsId(potentiallyAddedId)) {
            callbacks.insertedAt(currentCursor.getPosition());
            additions = 1;
        }
        return additions;
    }

    private static final Callbacks NULL_SAFE_CALLBACKS = new Callbacks() {
        @Override
        public void dataChanged() {

        }

        @Override
        public void insertedAt(int position) {

        }

        @Override
        public void deletedAt(int position) {

        }
    };

    public interface Callbacks {
        void dataChanged();

        void insertedAt(int position);

        void deletedAt(int position);
    }
}
