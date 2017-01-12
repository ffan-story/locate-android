//
// Created by yry on 2016/10/13.
//

#include <math.h>
#include <float.h>
#include "include/euclid-distance-estimator.h"


double EuclidDistanceEstimator::calcEuclidDistance(BeaconSignal &signal,
                                                   BeaconSignal &beacon) {

    double sum = 0;
    int dataLength = signal.getDataLength();
    for (int i = 0; i < dataLength; ++i) {
        double delta = signal.getDataAtIndex(i) - beacon.getDataAtIndex(i);
        sum += delta * delta;
    }
    return sqrt(sum);
}

void EuclidDistanceEstimator::estimatePosition(BeaconSignal &signal, double *output) {

    double minDistance = DBL_MAX;
    int minPos = -1;
    for (int i = 0; i < mFingerprintsCount; ++i) {
        double distance = EuclidDistanceEstimator::calcEuclidDistance(signal, mFingerprints[i]->getSignal());
        if (distance < minDistance) {
            minDistance = distance;
            minPos = i;
        }
    }
    if (minPos < 0) {
        return;
    }
    output[0] = mFingerprints[minPos]->getX();
    output[1] = mFingerprints[minPos]->getY();
}





