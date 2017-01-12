//
// Created by yry on 2016/10/13.
//

#ifndef HELLOJNI_EUCLID_DISTANCE_H
#define HELLOJNI_EUCLID_DISTANCE_H

#include "position-estimator.h"

class EuclidDistanceEstimator : public PositionEstimator {

public:
    void estimatePosition(BeaconSignal &signal, double *output);

private:
    static double calcEuclidDistance(BeaconSignal &signal, BeaconSignal &beacon);
};


#endif //HELLOJNI_EUCLID_DISTANCE_H
