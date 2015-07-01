1. Download latest NDK
http://developer.android.com/ndk/downloads/index.html#download
Linux 64-bit (x86)

2. decompress archive
7za x android-ndk-r10e-linux-x86_64.bin

3. Download ffmpeg
http://ffmpeg.org/download.html

4. tar -xvf ffmpeg-2.7.1.tar.bz2 -C /opt/android-ndk/android-ndk-r10e/sources

5.  Update configure file
Open ffmpeg-2.7.1/configure file with a text editor, and locate the following lines.
***************************************************************
SLIBNAME_WITH_MAJOR='$(SLIBNAME).$(LIBMAJOR)'
LIB_INSTALL_EXTRA_CMD='$$(RANLIB) "$(LIBDIR)/$(LIBNAME)"'
SLIB_INSTALL_NAME='$(SLIBNAME_WITH_VERSION)'
SLIB_INSTALL_LINKS='$(SLIBNAME_WITH_MAJOR) $(SLIBNAME)'

This cause ffmpeg shared libraries to be compiled to libavcodec.so.<version> (e.g. libavcodec.so.55), which is not compatible with Android build system. Therefore we’ll need to replace the above lines with the following lines.

SLIBNAME_WITH_MAJOR='$(SLIBPREF)$(FULLNAME)-$(LIBMAJOR)$(SLIBSUF)'
LIB_INSTALL_EXTRA_CMD='$$(RANLIB) "$(LIBDIR)/$(LIBNAME)"'
SLIB_INSTALL_NAME='$(SLIBNAME_WITH_MAJOR)'
SLIB_INSTALL_LINKS='$(SLIBNAME)'
***************************************************************


6. create build_android.sh
***************************************************************
#!/bin/bash
NDK=/opt/android-ndk/android-ndk-r10e
SYSROOT=$NDK/platforms/android-16/arch-arm/
TOOLCHAIN=$NDK/toolchains/arm-linux-androideabi-4.9/prebuilt/linux-x86_64
function build_one
{
./configure \
    --prefix=$PREFIX \
    --enable-shared \
    --disable-static \
    --disable-doc \
    --disable-ffmpeg \
    --disable-ffplay \
    --disable-ffprobe \
    --disable-ffserver \
    --disable-avdevice \
    --disable-doc \
    --disable-symver \
    --cross-prefix=$TOOLCHAIN/bin/arm-linux-androideabi- \
    --target-os=linux \
    --arch=arm \
    --enable-cross-compile \
    --sysroot=$SYSROOT \
    --extra-cflags="-Os -fpic $ADDI_CFLAGS" \
    --extra-ldflags="$ADDI_LDFLAGS" \
    $ADDITIONAL_CONFIGURE_FLAG
make clean
make
make install
}
#arm v7vfpv3
CPU=armv7-a
OPTIMIZE_CFLAGS="-mfloat-abi=softfp -mfpu=vfpv3-d16 -marm -march=$CPU "
PREFIX=./android/$CPU
ADDITIONAL_CONFIGURE_FLAG=
build_one
***************************************************************

7. copy this file to /opt/android-ndk/android-ndk-r10e/sources/ffmpeg-2.7.1

8. make it executable and run it
sudo chmod +x build_android.sh
./build_android.sh

9. The /opt/android-ndk/android-ndk-r10e/sources/ffmpeg-2.7.1/android/armv7-a/lib folder contains the shared libraries, while <...>/armv7-a/include folder contains the header files for libavcodec, libavformat, libavfilter, libavutil, libswscale etc.

10. Developing android project in Windows:
Download and extract the same NDK for windows.

11. copy ffmpeg directory with built libararies to the folder
<NDK_DIR>/sources/ffmpeg-2.7.1

11. Create file Andoid.mk in folder
<NDK_DIR>/sources/ffmpeg-2.7.1/android/armv7-a
(write the built libraries versions here)
***************************************************************
LOCAL_PATH:= $(call my-dir)
 
include $(CLEAR_VARS)
LOCAL_MODULE:= libavcodec
LOCAL_SRC_FILES:= lib/libavcodec-56.so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
include $(PREBUILT_SHARED_LIBRARY)
 
include $(CLEAR_VARS)
LOCAL_MODULE:= libavformat
LOCAL_SRC_FILES:= lib/libavformat-56.so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
include $(PREBUILT_SHARED_LIBRARY)
 
include $(CLEAR_VARS)
LOCAL_MODULE:= libswscale
LOCAL_SRC_FILES:= lib/libswscale-3.so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
include $(PREBUILT_SHARED_LIBRARY)
 
include $(CLEAR_VARS)
LOCAL_MODULE:= libavutil
LOCAL_SRC_FILES:= lib/libavutil-54.so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
include $(PREBUILT_SHARED_LIBRARY)
 
include $(CLEAR_VARS)
LOCAL_MODULE:= libavfilter
LOCAL_SRC_FILES:= lib/libavfilter-5.so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
include $(PREBUILT_SHARED_LIBRARY)
 
include $(CLEAR_VARS)
LOCAL_MODULE:= libswresample
LOCAL_SRC_FILES:= lib/libswresample-1.so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
include $(PREBUILT_SHARED_LIBRARY)
***************************************************************

12. to compile *.c file in Android Studio:
run ndk-build.cmd from jni folder:
<PROJECT_DIR>\app\src\main\jni> <NDK_DIR>\ndk-build

13. Copu compiled libraries to the folder 
<PROJECT_DIR>\app\src\main\jniLibs\armeabi-v7a

14. Import static libraries and JNI methods in your java code (see MainActivity.java)
