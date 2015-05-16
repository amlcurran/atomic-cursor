package uk.co.amlcurran.atomiccursor;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class WrappedCursorTest {

    @Test
    public void isOneInFrontOfDoesntChangePosition() {
        ListCursor cursor = ListCursor.withIds(1, 2, 3, 4, 5);
        WrappedCursor wrappedCursor = new WrappedCursor(cursor);
        cursor.moveToPosition(3);

        ListCursor cursor1 = ListCursor.withIds(1, 2, 3, 4, 5);
        cursor1.moveToPosition(1);
        wrappedCursor.isOneInFrontOf(new WrappedCursor(cursor1));

        assertThat(cursor.getPosition()).isEqualTo(3);
    }

    @Test
    public void containsIdDoesntChangePosition() {
        ListCursor cursor = ListCursor.withIds(1, 2, 3, 4, 5);
        WrappedCursor wrappedCursor = new WrappedCursor(cursor);
        cursor.moveToPosition(3);

        wrappedCursor.containsId(3);

        assertThat(cursor.getPosition()).isEqualTo(3);
    }

}