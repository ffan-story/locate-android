// -*- Mode: c++; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*-

%module "wanda_locate"

%{
#include "model.h"
#include "position-estimator.h"
#include "euclid-distance-estimator.h"
%}

%typemap(jni) (BeaconFingerprint **fingerprints, int count) "jobjectArray"
%typemap(jtype) (BeaconFingerprint **fingerprints, int count) "BeaconFingerprint[]"
%typemap(jstype) (BeaconFingerprint **fingerprints, int count) "BeaconFingerprint[]"
%typemap(javain) (BeaconFingerprint **fingerprints, int count) "$javainput"
%typemap(in) (BeaconFingerprint **fingerprints, int count) {
    jint count = jenv->GetArrayLength($input);
    BeaconFingerprint **fps = new BeaconFingerprint*[count];
    for (jint i = 0; i < count; i++) {
        // Get instance of BeaconFingerprint
        jobject fpObject = jenv->GetObjectArrayElement($input, i);
        jclass clazz = jenv->GetObjectClass(fpObject);
        jfieldID cPtrFieldId = jenv->GetFieldID(clazz, "swigCPtr", "J");
        jlong cPtr = jenv->GetLongField(fpObject, cPtrFieldId);
        BeaconFingerprint *fp = *(BeaconFingerprint**)&cPtr;
        BeaconFingerprint *fingerprint = new BeaconFingerprint(*fp);
        // Get fingerprint from one of the elements from java
        fps[i] = fingerprint;
    }
    $1 = fps;
    $2 = count;
}

%typemap(jni) double *output "jdoubleArray"
%typemap(jtype) double *output "double[]"
%typemap(jstype) double *output "double[]"
%typemap(javain) double *output "$javainput"
%typemap(in) double *output {
    $1 = jenv->GetDoubleArrayElements($input, NULL);
}
%typemap(freearg) double *output {
    jenv->ReleaseDoubleArrayElements($input, $1, 0);
}

%typemap(jni) (double *data, int dataLength) "jdoubleArray"
%typemap(jtype) (double *data, int dataLength) "double[]"
%typemap(jstype) (double *data, int dataLength) "double[]"
%typemap(javain) (double *data, int dataLength) "$javainput"
%typemap(in) (double *data, int dataLength) {
    $1 = jenv->GetDoubleArrayElements($input, NULL);
    $2 = jenv->GetArrayLength($input);
}
%typemap(freearg) (double *data, int dataLength) {
    jenv->ReleaseDoubleArrayElements($input, $1, 0);
}

%include "model.h"
%include "position-estimator.h"
%include "euclid-distance-estimator.h"
%apply (BeaconFingerprint **fingerprints, int count) {(BeaconFingerprint **fingerprints, int count)}
%apply (double *data, int dataLength) {(double *data, int dataLength)}
