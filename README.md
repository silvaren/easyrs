# easyRS

easyRS is a set of convenience RenderScript tools for processing common Android formats such as Bitmap and NV21. Currently it supports most RenderScript Intrinsics operations plus Bitmap/NV21 format conversions.

First you need to create a RenderScript context (from the support library class android.support.v8.renderscript.RenderScript):

```Java
RenderScript rs = RenderScript.create(context); // where context can be your activity, application, etc.
```

#### NV21 to Bitmap  
```Java
Bitmap outputBitmap = Nv21Image.nv21ToBitmap(rs, nv21ByteArray, width, height); // where nv21ByteArray contains
                                                                                // the NV21 image data
```
#### Bitmap to NV21  
Converts an Android Bitmap image to a NV21 image. Please notice that because of the NV21 format the resulting image will be resized to the nearest even sized dimensions (eg. 501x499 -> 500x498).
```Java
Nv21Image nv21Image = Nv21Image.bitmapToNV21(rs, inputBitmap);
```
#### Blend
Blends two images, with operations such as add, clear, dst, dstAtop, dstIn, dstOut, dstOver, multiply, src, srcAtop, srcIn, srcOut, srcOver, subtract, xor [(see reference)](https://developer.android.com/reference/android/renderscript/ScriptIntrinsicBlend.html).
```Java
Blend.add(rs, inputBitmap, inputBitmap2); // result is written to inputBitmap2
```
#### Blur  
```Java
float radius = 25.f; // where radius can be any float from 0.0f to 25.0f
Bitmap outputBitmap = Blur.blur(rs, inputBitmap, radius);
```
#### ColorMatrix  
```Java
Bitmap outputBitmap = ColorMatrix.applyMatrix(rs, inputBitmap, matrix3f); // where matrix3f is a 3x3 Matrix3f
                                                                          // from the RenderScript package
```
#### Convolve  
```Java
Bitmap outputBitmap = Convolve.convolve3x3(rs, inputBitmap, coefficients); // where coefficients is a 3x3 float
                                                                           // array convolve kernel
```
#### Histogram  
```Java
int[] histograms = Histogram.rgbaHistograms(rs, inputBitmap); // where histograms will contain the histograms
                                                              // of each RGBA channels
```
#### LUT  
```Java
Bitmap outputBitmap = Lut.applyLut(rs, inputBitmap, rgbaLut); // where rgbaLut is the Lookup Table to be applied
```
#### LUT3D  
```Java
Bitmap outputBitmap = Lut3D.apply3dLut(rs, inputBitmap, cube); // where cube is the 3D Lookup Table to be applied
```
#### Resize  
```Java
Bitmap outputBitmap = Resize.resize(rs, inputBitmap, targetWidth, targetHeight);
```

When applying operations to NV21 images, beware that conversions to/from Bitmap format are part of the processing pipeline so they have an overhead to consider and that the image will be rounded down to the nearest even integer in each dimension.

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
    compile 'io.github.silvaren:easyrs:0.5.3'
}
```

Make sure jcenter repository is available in your top-level or app-level build.gradle:
```groovy
allprojects {
    repositories {
        ...
        jcenter()
    }
}
```

### License ###

The MIT License (MIT)
Copyright (c) 2016 Renato Oliveira da Silva

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
