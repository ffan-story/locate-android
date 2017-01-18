package com.feifan.sensorlib.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xuchunlei on 2016/12/13.
 */

public class SensorData implements Parcelable {

    public Acceleration acceleration;
    public LinearAcceleration linearAcceleration;
    public Orientation orientation;

    public SensorData() {
        acceleration = new Acceleration();
        linearAcceleration = new LinearAcceleration();
        orientation = new Orientation();
    }

    @Override
    public String toString() {
//        return acceleration.toString() + "," + linearAcceleration.toString() + "," + orientation.toString();
        return acceleration.toString() + "," + orientation.toString();
    }

    public static class Acceleration implements Parcelable {
        /** X轴加速度 */
        public volatile float x;
        /** Y轴加速度 */
        public volatile float y;
        /** Z轴加速度,包含重力加速度 */
        public volatile float z;

        private Acceleration() {

        }

        @Override
        public String toString() {
            return x + "," + y + "," + z;
        }

        protected Acceleration(Parcel in) {
            x = in.readFloat();
            y = in.readFloat();
            z = in.readFloat();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeFloat(x);
            dest.writeFloat(y);
            dest.writeFloat(z);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Acceleration> CREATOR = new Creator<Acceleration>() {
            @Override
            public Acceleration createFromParcel(Parcel in) {
                return new Acceleration(in);
            }

            @Override
            public Acceleration[] newArray(int size) {
                return new Acceleration[size];
            }
        };
    }

    public static class LinearAcceleration implements Parcelable {
        /** x轴加速度 */
        public float x;
        /** y轴加速度 */
        public float y;
        /** z轴加速度，不包含重力加速度 */
        public float z;

        private LinearAcceleration() {

        }

        @Override
        public String toString() {
            return x + "," + y + "," + z;
        }

        protected LinearAcceleration(Parcel in) {
            x = in.readFloat();
            y = in.readFloat();
            z = in.readFloat();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeFloat(x);
            dest.writeFloat(y);
            dest.writeFloat(z);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<LinearAcceleration> CREATOR = new Creator<LinearAcceleration>() {
            @Override
            public LinearAcceleration createFromParcel(Parcel in) {
                return new LinearAcceleration(in);
            }

            @Override
            public LinearAcceleration[] newArray(int size) {
                return new LinearAcceleration[size];
            }
        };
    }

    public static class Orientation implements Parcelable {
        /** 磁北极和Y轴方位角 -π ～ +π */
        public volatile float azimuth;
        /** X轴和水平面的夹角 -π ~ +π */
        public volatile float pitch;
        /** Y轴和水平面的夹脚 -π/2 ~ +π/2 */
        public volatile float roll;

        private Orientation(){

        }

        @Override
        public String toString() {
            return azimuth + "," + pitch + "," + roll;
        }

        protected Orientation(Parcel in) {
            azimuth = in.readFloat();
            pitch = in.readFloat();
            roll = in.readFloat();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeFloat(azimuth);
            dest.writeFloat(pitch);
            dest.writeFloat(roll);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Orientation> CREATOR = new Creator<Orientation>() {
            @Override
            public Orientation createFromParcel(Parcel in) {
                return new Orientation(in);
            }

            @Override
            public Orientation[] newArray(int size) {
                return new Orientation[size];
            }
        };
    }

    protected SensorData(Parcel in) {
        acceleration = in.readParcelable(Acceleration.class.getClassLoader());
        linearAcceleration = in.readParcelable(LinearAcceleration.class.getClassLoader());
        orientation = in.readParcelable(Orientation.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(acceleration, flags);
        dest.writeParcelable(linearAcceleration, flags);
        dest.writeParcelable(orientation, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SensorData> CREATOR = new Creator<SensorData>() {
        @Override
        public SensorData createFromParcel(Parcel in) {
            return new SensorData(in);
        }

        @Override
        public SensorData[] newArray(int size) {
            return new SensorData[size];
        }
    };
}
