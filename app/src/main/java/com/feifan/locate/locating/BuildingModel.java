package com.feifan.locate.locating;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.feifan.locate.provider.LocateData.Building;
import com.feifan.locate.widget.cursorwork.CursorModel;

/**
 * Created by xuchunlei on 16/9/19.
 */
public class BuildingModel extends CursorModel implements Parcelable {

    public String name;

    public BuildingModel(Cursor cursor) {
        int idIndex = cursor.getColumnIndexOrThrow(Building._ID);
        id = cursor.getInt(idIndex);
        int nameIndex = cursor.getColumnIndexOrThrow(Building.NAME);
        name = cursor.getString(nameIndex);
    }

    protected BuildingModel(Parcel in) {
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BuildingModel> CREATOR = new Creator<BuildingModel>() {
        @Override
        public BuildingModel createFromParcel(Parcel in) {
            return new BuildingModel(in);
        }

        @Override
        public BuildingModel[] newArray(int size) {
            return new BuildingModel[size];
        }
    };

    @Override
    public String toString() {
        return name;
    }
}
