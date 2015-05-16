package uk.co.amlcurran.atomiccursor;

import android.database.Cursor;
import android.provider.BaseColumns;

class WrappedCursor {

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
                moveToPosition(startPosition);
                return true;
            }
        }
        moveToPosition(startPosition);
        return false;
    }

    private boolean nextIdMatches(long newId) {
        return peekAtId(getPosition() + 1) == newId;
    }

    private long peekAtId(int position) {
        long id = -1;
        int currentPosition = getPosition();
        if (moveToPosition(position)) {
            id = getId();
            moveToPosition(currentPosition);
        }
        return id;
    }

    public boolean isOneInFrontOf(WrappedCursor newCursor) {
        return nextIdMatches(newCursor.getId());
    }
}
