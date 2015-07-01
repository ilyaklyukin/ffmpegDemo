LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := ffmpeg
LOCAL_SRC_FILES := ffmpeg.c
LOCAL_LDLIBS := -llog -ljnigraphics -lz -landroid
LOCAL_SHARED_LIBRARIES := libavformat libavcodec libswscale libavutil

include $(BUILD_SHARED_LIBRARY)
$(call import-module,ffmpeg-2.7.1/android/armv7-a)
