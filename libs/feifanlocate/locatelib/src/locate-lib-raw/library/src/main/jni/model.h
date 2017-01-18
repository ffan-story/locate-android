//
// Created by yry on 2016/10/13.
//

#ifndef HELLOJNI_MODEL_H
#define HELLOJNI_MODEL_H

#include "stddef.h"

class BeaconSignal {

private:
    double *mData;
    int mDataLength;

public:

    BeaconSignal(BeaconSignal &other) {
        int otherDataLen = other.getDataLength();
        double *newData = new double[otherDataLen];
        for (int i = 0; i < otherDataLen; ++i) {
            newData[i] = other.getDataAtIndex(i);
        }
        mDataLength = otherDataLen;
        mData = newData;
    }

    BeaconSignal(double *data, int dataLength);

    ~BeaconSignal();

    int getDataLength();

    double getDataAtIndex(int index);

};

class BeaconFingerprint {

private:
    BeaconSignal mSignal;
    double mX;
    double mY;

public:

    BeaconFingerprint(BeaconFingerprint &other): mSignal(other.getSignal()), mX(other.getX()), mY(other.getY()) {};

    BeaconFingerprint(BeaconSignal &signal, double x, double y): mSignal(signal), mX(x), mY(y) {};

    BeaconSignal &getSignal();

    double getX();

    double getY();
};


#endif //HELLOJNI_MODEL_H
