//
// Created by yry on 2016/10/13.
//

#include "include/model.h"
#include "include/position-estimator.h"

void PositionEstimator::updateFingerprints(BeaconFingerprint **fingerprints, int count) {
    recycleFingerprintsIfNecessary();
    BeaconFingerprint **newFingerprints = new BeaconFingerprint*[count];
    for (int i = 0; i < count; ++i) {
        newFingerprints[i] = new BeaconFingerprint(*fingerprints[i]);
    }
    mFingerprints = newFingerprints;
    mFingerprintsCount = count;
}

void PositionEstimator::recycleFingerprintsIfNecessary() {
    BeaconFingerprint **fingerprints = mFingerprints;
    if (fingerprints != NULL) {
        for (int i = 0; i < mFingerprintsCount; ++i) {
            delete fingerprints[i];
        }
        delete[] mFingerprints;
        mFingerprints = NULL;
    }
}

PositionEstimator::~PositionEstimator() {
    recycleFingerprintsIfNecessary();
}



