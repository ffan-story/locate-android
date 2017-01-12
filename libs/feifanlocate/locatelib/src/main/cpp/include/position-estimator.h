//
// Created by yry on 2016/10/13.
//

#ifndef HELLOJNI_POSITIONESTIMATOR_H
#define HELLOJNI_POSITIONESTIMATOR_H

#include "model.h"

class PositionEstimator {

public:
    virtual void estimatePosition(BeaconSignal &signal, double *output) = 0;

    void updateFingerprints(BeaconFingerprint **fingerprints, int count);

    virtual ~PositionEstimator() = 0;

protected:
    BeaconFingerprint **mFingerprints;

    int mFingerprintsCount;

private:
    void recycleFingerprintsIfNecessary();
};

#endif //HELLOJNI_POSITIONESTIMATOR_H
