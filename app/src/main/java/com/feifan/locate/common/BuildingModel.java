package com.feifan.locate.common;

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
    public String code;
    public int minFloor;

    public BuildingModel(Cursor cursor) {
        super(cursor);
        int nameIndex = cursor.getColumnIndexOrThrow(Building.NAME);
        name = cursor.getString(nameIndex);
        int codeIndex = cursor.getColumnIndexOrThrow(Building.CODE);
        code = cursor.getString(codeIndex);
        int minFloorIndex = cursor.getColumnIndexOrThrow(Building.MIN_FLOOR);
        minFloor = cursor.getInt(minFloorIndex);
    }

    protected BuildingModel(Parcel in) {
        name = in.readString();
        code = in.readString();
        minFloor = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(code);
        dest.writeInt(minFloor);
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
