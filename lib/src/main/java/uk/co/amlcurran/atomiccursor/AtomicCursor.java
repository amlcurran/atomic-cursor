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
                offset += checkForDeletions(currentCursor, newId);
            }
        }
    }

    private int checkForDeletions(WrappedCursor currentCursor, long newId) {
        int offset = 0;
        currentCursor.moveToNext();
        if (newId == currentCursor.getId()) {
            callbacks.deletedAt(currentCursor.getPosition() - 1);
            offset = -1;
        }
        currentCursor.moveToPrevious();
        return offset;
    }

    private static boolean atStart(Cursor currentCursor) {
        return currentCursor.getPosition() == 0;
    }

    private int checkForAdditions(WrappedCursor currentCursor, WrappedCursor newCursor, long currentId) {
        int additions = 0;
        newCursor.moveToNext();
        long nextNewId = newCursor.getId();
        if (nextNewId == currentId) {
            callbacks.insertedAt(currentCursor.getPosition());
            additions = 1;
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
