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
            walkCursor(currentCursor, cursor);
            currentCursor = cursor;
        }
    }

    private void walkCursor(Cursor currentCursor, Cursor newCursor) {
        int currentIdIndex = currentCursor.getColumnIndex(BaseColumns._ID);
        int newIdIndex = newCursor.getColumnIndex(BaseColumns._ID);
        int offset = 0;
        while (currentCursor.moveToNext()) {
            newCursor.moveToPosition(currentCursor.getPosition() + offset);
            long currentId = currentCursor.getLong(currentIdIndex);
            long newId = newCursor.getLong(newIdIndex);
            if (currentId != newId) {
                offset += checkForAdditions(currentCursor, newCursor, newIdIndex, currentId);
                offset += checkForDeletions(currentCursor, currentIdIndex, newId);
            }
        }
    }

    private int checkForDeletions(Cursor currentCursor, int currentIdIndex, long newId) {
        int offset = 0;
        currentCursor.moveToNext();
        if (newId == currentCursor.getLong(currentIdIndex)) {
            callbacks.deletedAt(currentCursor.getPosition() - 1);
            offset = -1;
        }
        currentCursor.moveToPrevious();
        return offset;
    }

    private static boolean atStart(Cursor currentCursor) {
        return currentCursor.getPosition() == 0;
    }

    private int checkForAdditions(Cursor currentCursor, Cursor newCursor, int newIdIndex, long currentId) {
        int additions = 0;
        newCursor.moveToNext();
        long nextNewId = newCursor.getLong(newIdIndex);
        if (nextNewId == currentId) {
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
