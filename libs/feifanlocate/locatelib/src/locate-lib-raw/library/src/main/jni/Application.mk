APP_CPPFLAGS += -fexceptions -frtti -Wfatal-errors
APP_PLATFORM := android-14
APP_STL      := stlport_static

ifeq ($(NDK_DEBUG),0)
  APP_CFLAGS += -DNDEBUG=1 -UDEBUG -U_DEBUG
else
  APP_CFLAGS += -DDEBUG=1 -D_DEBUG=1 -UNDEBUG
endif