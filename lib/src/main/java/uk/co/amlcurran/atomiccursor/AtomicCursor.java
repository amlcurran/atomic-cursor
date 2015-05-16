package uk.co.amlcurran.atomiccursor;

import android.database.Cursor;
import android.provider.BaseColumns;

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
                offset += checkForAdditions(currentCursor, newCursor, currentId);
                offset += checkForDeletions(currentCursor, newCursor);
            }
        }
    }

    private int checkForDeletions(WrappedCursor currentCursor, WrappedCursor newCursor) {
        int offset = 0;
        int currentPosition = currentCursor.getPosition();
        if (currentCursor.isOneInFrontOf(newCursor)) {
            callbacks.deletedAt(currentCursor.getPosition() - 1);
            offset = -1;
        }
        currentCursor.moveToPosition(currentPosition);
        return offset;
    }

    private int checkForAdditions(WrappedCursor currentCursor, WrappedCursor newCursor, long currentId) {
        int additions = 0;
        long potentiallyAddedId = newCursor.getId();
        if (newCursor.moveToNext()) {
            long nextNewId = newCursor.getId();
            if (nextNewId == currentId && !currentCursor.containsId(potentiallyAddedId)) {
                callbacks.insertedAt(currentCursor.getPosition());
                additions = 1;
            }
            newCursor.moveToPrevious();
        }
        return additions;
    }

    private class WrappedCursor {

        private final Cursor cursor;
        private final int idIndex;

        public WrappedCursor(Cursor cursor) {
            this.cursor = cursor;
            this.idIndex = cursor.getColumnIndexOrThrow(BaseColumns._ID);
        }

        public boolean moveToNext() {
            return cursor.moveToNext();
        }

        public boolean moveToPosition(int position) {
            return cursor.moveToPosition(position);
        }

        public int getPosition() {
            return cursor.getPosition();
        }

        public boolean moveToPrevious() {
            return cursor.moveToPrevious();
        }

        public long getId() {
            return cursor.getLong(idIndex);
        }

        public boolean containsId(long id) {
            int startPosition = getPosition();
            moveToPosition(-1);
            while (moveToNext()) {
                if (getId() == id) {
                    return true;
                }
            }
            moveToPosition(startPosition);
            return false;
        }

        private boolean nextIdMatches(long newId) {
            return moveToNext() && newId == getId();
        }

        private boolean isOneInFrontOf(WrappedCursor newCursor) {
            return nextIdMatches(newCursor.getId());
        }
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
