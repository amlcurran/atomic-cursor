package uk.co.amlcurran.atomiccursor;

import android.database.Cursor;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

public class AtomicCursor {

    private Callbacks callbacks = NULL_SAFE_CALLBACKS;
    private Cursor currentCursor = new NullCursor();
    private List<Integer> shouldIgnoreAsMoved = new ArrayList<>();

    public void setCallbacks(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    public void submit(Cursor cursor) {
        shouldIgnoreAsMoved.clear();
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
                checkForMoves(currentCursor, newCursor, currentIdIndex, newId);
                offset += checkForAdditions(currentCursor, newCursor, newIdIndex, currentId);
                checkForDeletions(currentCursor, currentIdIndex, newId);
            }
        }
    }

    private void checkForMoves(Cursor currentCursor, Cursor newCursor, int currentIdIndex, long newId) {
        int startPosition = currentCursor.getPosition();
        currentCursor.moveToFirst();
        while (currentCursor.moveToNext()) {
            if (currentCursor.getLong(currentIdIndex) == newId) {
                shouldIgnoreAsMoved.add(currentCursor.getPosition());
                callbacks.moved(currentCursor.getPosition(), newCursor.getPosition());
            }
        }
        currentCursor.moveToPosition(startPosition);
    }

    private void checkForDeletions(Cursor currentCursor, int currentIdIndex, long newId) {
        if (currentCursor.moveToNext()) {
            if (newId == currentCursor.getLong(currentIdIndex) && shouldNotIgnorePosition(currentCursor)) {
                callbacks.deletedAt(currentCursor.getPosition() - 1);
            }
            currentCursor.moveToPrevious();
        }
    }

    private boolean shouldNotIgnorePosition(Cursor currentCursor) {
        return !shouldIgnoreAsMoved.contains(currentCursor.getPosition());
    }

    private int checkForAdditions(Cursor currentCursor, Cursor newCursor, int newIdIndex, long currentId) {
        int additions = 0;
        newCursor.moveToNext();
        long nextNewId = newCursor.getLong(newIdIndex);
        if (nextNewId == currentId && shouldNotIgnorePosition(newCursor)) {
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

        @Override
        public void moved(int from, int to) {

        }
    };

    public interface Callbacks {
        void dataChanged();

        void insertedAt(int position);

        void deletedAt(int position);

        void moved(int from, int to);
    }
}
