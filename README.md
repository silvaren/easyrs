# easyRS

easyRS is a set of convenience RenderScript tools for processing common Android formats such as Bitmap and NV21. Currently it supports the following operations (leveraging RenderScript Intrinsics):

* Blend
* Blur
* ColorMatrix
* Convolve
* Histogram
* LUT
* LUT3D
* Resize
* YuvToRGB

It also supports NV21 Image conversion to/from Bitmap:
* NV21 to Bitmap
* Bitmap to NV21

When applying operations to NV21 images, beware that conversions to/from Bitmap format are part of the processing pipeline so they have an overhead to consider.

### Download ###

You can add it to your Android project by following the example below in your app's build.gradle:

```groovy
android {
    ...
    defaultConfig {
        ...
        renderscriptTargetApi 16
        renderscriptSupportModeEnabled true
    }
    ...
}

dependencies {
    ...
    compile 'io.github.silvaren:easyrs:0.5.2'
}
```

Make sure jcenter repository is available in your top-level or app-level build.gradle:
```groovy
buildscript {
    repositories {
        ...
        jcenter()
    }
}
```
