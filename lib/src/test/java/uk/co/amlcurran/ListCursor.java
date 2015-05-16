package uk.co.amlcurran;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class ListCursor implements Cursor {

    private final List<IdentifiableObject> list;
    private int currentPosition = -1;

    public static ListCursor withIds(long... ids) {
        List<IdentifiableObject> objects = new ArrayList<>();
        for (long id : ids) {
            objects.add(new IdentifiableObject(id));
        }
        return new ListCursor(objects);
    }

    public ListCursor(List<IdentifiableObject> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public int getPosition() {
        return currentPosition;
    }

    @Override
    public boolean move(int offset) {
        return safeMoveTo(currentPosition + offset);
    }

    private boolean safeMoveTo(int position) {
        if (position >= getCount()) {
            return false;
        } else {
            currentPosition = position;
            return true;
        }
    }

    @Override
    public boolean moveToPosition(int position) {
        return safeMoveTo(position);
    }

    @Override
    public boolean moveToFirst() {
        return safeMoveTo(0);
    }

    @Override
    public boolean moveToLast() {
        return safeMoveTo(getCount() - 1);
    }

    @Override
    public boolean moveToNext() {
        return safeMoveTo(currentPosition + 1);
    }

    @Override
    public boolean moveToPrevious() {
        return safeMoveTo(currentPosition - 1);
    }

    @Override
    public boolean isFirst() {
        return false;
    }

    @Override
    public boolean isLast() {
        return false;
    }

    @Override
    public boolean isBeforeFirst() {
        return false;
    }

    @Override
    public boolean isAfterLast() {
        return false;
    }

    @Override
    public int getColumnIndex(String columnName) {
        return 0;
    }

    @Override
    public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
        return 0;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return null;
    }

    @Override
    public String[] getColumnNames() {
        return new String[0];
    }

    @Override
    public int getColumnCount() {
        return 0;
    }

    @Override
    public byte[] getBlob(int columnIndex) {
        return new byte[0];
    }

    @Override
    public String getString(int columnIndex) {
        return null;
    }

    @Override
    public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {

    }

    @Override
    public short getShort(int columnIndex) {
        return 0;
    }

    @Override
    public int getInt(int columnIndex) {
        return 0;
    }

    @Override
    public long getLong(int columnIndex) {
        return list.get(currentPosition).getId();
    }

    @Override
    public float getFloat(int columnIndex) {
        return 0;
    }

    @Override
    public double getDouble(int columnIndex) {
        return 0;
    }

    @Override
    public int getType(int columnIndex) {
        return 0;
    }

    @Override
    public boolean isNull(int columnIndex) {
        return false;
    }

    @Override
    public void deactivate() {

    }

    @Override
    public boolean requery() {
        return false;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public void registerContentObserver(ContentObserver observer) {

    }

    @Override
    public void unregisterContentObserver(ContentObserver observer) {

    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void setNotificationUri(ContentResolver cr, Uri uri) {

    }

    @Override
    public Uri getNotificationUri() {
        return null;
    }

    @Override
    public boolean getWantsAllOnMoveCalls() {
        return false;
    }

    @Override
    public Bundle getExtras() {
        return null;
    }

    @Override
    public Bundle respond(Bundle extras) {
        return null;
    }
}
