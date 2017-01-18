CUR_PATH := $(call my-dir)

##############################################

include $(CLEAR_VARS)

LOCAL_PATH             := $(CUR_PATH)
LOCAL_MODULE           := wanda-locate
LOCAL_CFLAGS           := -Werror -Wmissing-include-dirs $(HIDE_SYMBOLS)
LOCAL_CPPFLAGS         := -fexceptions -frtti -std=c++11 $(HIDE_SYMBOLS)
LOCAL_CXXFLAGS         := -fexceptions -frtti -std=c++11 $(HIDE_SYMBOLS)
LOCAL_SRC_FILES        := euclid-distance-estimator.cpp \
                          wanda_locate_wrap.cxx \
                          model.cpp \
                          position-estimator.cpp


include $(BUILD_SHARED_LIBRARY)

include ${CLEAR_VARS}

##############################################
