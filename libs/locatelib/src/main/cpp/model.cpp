//
// Created by yry on 2016/10/13.
//

#include "include/model.h"

BeaconSignal::BeaconSignal(double *data, int dataLength) {
    double *newData = new double[dataLength];
    for (int i = 0; i < dataLength; i++) {
        newData[i] = data[i];
    }
    mData = newData;
    mDataLength = dataLength;
}

int BeaconSignal::getDataLength() {
    return mDataLength;
}

double BeaconSignal::getDataAtIndex(int index) {
    return mData[index];
}

BeaconSignal::~BeaconSignal() {
    delete[] mData;
    mData = NULL;
}

BeaconSignal &BeaconFingerprint::getSignal() {
    return mSignal;
}

double BeaconFingerprint::getX() {
    return mX;
}

double BeaconFingerprint::getY() {
    return mY;
}




