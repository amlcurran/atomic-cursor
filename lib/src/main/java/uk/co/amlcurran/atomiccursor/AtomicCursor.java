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
            cursor = new NullCursor();
        }
        if (currentCursor instanceof NullCursor) {
            callbacks.dataChanged();
        } else {
            walkCursor(new WrappedCursor(currentCursor), new WrappedCursor(cursor));
        }
        currentCursor = cursor;
    }

    private void walkCursor(WrappedCursor currentCursor, WrappedCursor newCursor) {
        int offset = 0;
        while (currentCursor.moveToNext()) {
            newCursor.moveToPosition(currentCursor.getPosition() + offset);
            long currentId = currentCursor.getId();
            long newId = newCursor.getId();
            if (currentId != newId) {
                Action addAction = checkForAdditions(currentCursor, newCursor);
                offset += addAction.offset();
                Action deleteAction = checkForDeletions(currentCursor, newCursor);
                offset += deleteAction.offset();
                if (!addAction.isHandled() || !deleteAction.isHandled()) {
                    callbacks.dataChanged();
                    return;
                }
            }
        }
    }

    private Action checkForDeletions(WrappedCursor currentCursor, WrappedCursor newCursor) {
        long potentiallyDeletedId = currentCursor.getId();
        if (currentCursor.isOneInFrontOf(newCursor)) {
            if (!newCursor.containsId(potentiallyDeletedId)) {
                DeleteAction deleteAction = new DeleteAction(currentCursor.getPosition());
                deleteAction.act(callbacks);
                return deleteAction;
            } else {
                return new UnhandledAction();
            }
        }
        return new NoChangeAction();
    }

    private Action checkForAdditions(WrappedCursor currentCursor, WrappedCursor newCursor) {
        long potentiallyAddedId = newCursor.getId();
        if (newCursor.isOneInFrontOf(currentCursor)) {
            if (!currentCursor.containsId(potentiallyAddedId)) {
                AddAction addAction = new AddAction(currentCursor.getPosition());
                addAction.act(callbacks);
                return addAction;
            } else {
                return new UnhandledAction();
            }
        }
        return new NoChangeAction();
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

    private interface Action {
        void act(Callbacks callbacks);

        boolean isHandled();

        int offset();
    }

    public interface Callbacks {
        void dataChanged();

        void insertedAt(int position);

        void deletedAt(int position);
    }

    private class DeleteAction implements Action {
        private final int position;

        public DeleteAction(int position) {
            this.position = position;
        }

        @Override
        public void act(Callbacks callbacks) {
            callbacks.deletedAt(position);
        }

        @Override
        public boolean isHandled() {
            return true;
        }

        @Override
        public int offset() {
            return -1;
        }
    }

    private class AddAction implements Action {
        private final int position;

        public AddAction(int position) {
            this.position = position;
        }

        @Override
        public void act(Callbacks callbacks) {
            callbacks.insertedAt(position);
        }

        @Override
        public boolean isHandled() {
            return true;
        }

        @Override
        public int offset() {
            return 1;
        }
    }

    private class UnhandledAction implements Action {
        @Override
        public void act(Callbacks callbacks) {

        }

        @Override
        public boolean isHandled() {
            return false;
        }

        @Override
        public int offset() {
            return 0;
        }
    }

    private class NoChangeAction implements Action {
        @Override
        public void act(Callbacks callbacks) {

        }

        @Override
        public boolean isHandled() {
            return true;
        }

        @Override
        public int offset() {
            return 0;
        }
    }
}
