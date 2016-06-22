package com.mm.beacon.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mengmeng on 15/9/20.
 */
public class DoublePoint implements Parcelable {
        public double x;
        public double y;

        public DoublePoint() {}

        public DoublePoint(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public DoublePoint(DoublePoint src) {
            this.x = src.x;
            this.y = src.y;
        }

        /**
         * Set the point's x and y coordinates
         */
        public void set(double x, double y) {
            this.x = x;
            this.y = y;
        }

        /**
         * Negate the point's coordinates
         */
        public final void negate() {
            x = -x;
            y = -y;
        }

        /**
         * Offset the point's coordinates by dx, dy
         */
        public final void offset(double dx, double dy) {
            x += dx;
            y += dy;
        }

        /**
         * Returns true if the point's coordinates equal (x,y)
         */
        public final boolean equals(double x, double y) {
            return this.x == x && this.y == y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DoublePoint point = (DoublePoint) o;

            if (x != point.x) return false;
            if (y != point.y) return false;

            return true;
        }

        @Override
        public int hashCode() {
            double result = x;
            result = 31 * result + y;
            return (int)result;
        }

        @Override
        public String toString() {
            return "DoublePoint(" + x + ", " + y + ")";
        }

        /**
         * Parcelable interface methods
         */
        @Override
        public int describeContents() {
            return 0;
        }

        /**
         * Write this point to the specified parcel. To restore a point from
         * a parcel, use readFromParcel()
         * @param out The parcel to write the point's coordinates into
         */
        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeDouble(x);
            out.writeDouble(y);
        }

        public static final Parcelable.Creator<DoublePoint> CREATOR = new Parcelable.Creator<DoublePoint>() {
            /**
             * Return a new point from the data in the specified parcel.
             */
            public DoublePoint createFromParcel(Parcel in) {
                DoublePoint r = new DoublePoint();
                r.readFromParcel(in);
                return r;
            }

            /**
             * Return an array of rectangles of the specified size.
             */
            public DoublePoint[] newArray(int size) {
                return new DoublePoint[size];
            }
        };

        /**
         * Set the point's coordinates from the data stored in the specified
         * parcel. To write a point to a parcel, call writeToParcel().
         *
         * @param in The parcel to read the point's coordinates from
         */
        public void readFromParcel(Parcel in) {
            x = in.readInt();
            y = in.readInt();
        }
    }
