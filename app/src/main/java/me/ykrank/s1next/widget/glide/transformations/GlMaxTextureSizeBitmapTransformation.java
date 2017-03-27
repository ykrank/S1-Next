package me.ykrank.s1next.widget.glide.transformations;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.os.Build;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import javax.microedition.khronos.opengles.GL10;

/**
 * A {@link com.bumptech.glide.load.Transformation} for transforming {@link android.graphics.Bitmap}'s
 * size not to exceed the OpenGl texture size limit.
 */
public final class GlMaxTextureSizeBitmapTransformation extends BitmapTransformation {

    public GlMaxTextureSizeBitmapTransformation(Context context) {
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        final int resWidth = toTransform.getWidth();
        final int resHeight = toTransform.getHeight();
        float maxTextureSize = GlMaxTextureCalculator.INSTANCE.glMaxTextureSize;

        float sizeMultiplier = Math.min(maxTextureSize / resWidth, maxTextureSize / resHeight);
        if (sizeMultiplier < 1) {
            return TransformationUtil.sizeMultiplier(pool, toTransform, sizeMultiplier);
        } else {
            return toTransform;
        }
    }

    @Override
    public String getId() {
        return "TransformationUtil.GlMaxTextureSizeBitmapTransformation";
    }

    /**
     * A calculator for getting OpenGL texture size limit.
     */
    private enum GlMaxTextureCalculator {
        INSTANCE;

        private static final int GL_TEXTURE_SIZE_MINIMUM = 2048;

        private int glMaxTextureSize;

        GlMaxTextureCalculator() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                glMaxTextureSize = getGl20MaxTextureSize();
            } else {
                glMaxTextureSize = getGl10MaxTextureSize();
            }
            // FIXME: 2017/3/27 something wrong in huawei emui
            if (glMaxTextureSize <= 0) {
                glMaxTextureSize = GL_TEXTURE_SIZE_MINIMUM;
            }
        }

        private int getGl10MaxTextureSize() {
            int[] maxTextureSize = new int[1];
            GLES10.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);

            return maxTextureSize[0];
        }

        /**
         * Forked from https://github.com/android/platform_frameworks_base/blob/master/services/core/java/com/android/server/display/ColorFade.java
         * and http://stackoverflow.com/q/26985858
         */
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        private int getGl20MaxTextureSize() {
            EGLDisplay eglDisplay = null;
            EGLContext eglContext = null;
            EGLSurface eglSurface = null;
            try {
                // create EglContext
                eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
                if (eglDisplay == EGL14.EGL_NO_DISPLAY) {
                    return -1;
                }

                int[] version = new int[2];
                if (!EGL14.eglInitialize(eglDisplay, version, 0, version, 1)) {
                    return -1;
                }

                int[] eglConfigAttribList = new int[]{
                        EGL14.EGL_RENDERABLE_TYPE,
                        EGL14.EGL_OPENGL_ES2_BIT,
                        EGL14.EGL_RED_SIZE, 8,
                        EGL14.EGL_GREEN_SIZE, 8,
                        EGL14.EGL_BLUE_SIZE, 8,
                        EGL14.EGL_ALPHA_SIZE, 8,
                        EGL14.EGL_NONE
                };

                EGLConfig[] eglConfigs = new EGLConfig[1];
                int[] numEglConfigs = new int[1];
                if (!EGL14.eglChooseConfig(eglDisplay, eglConfigAttribList, 0, eglConfigs, 0,
                        eglConfigs.length, numEglConfigs, 0)) {
                    return -1;
                }
                EGLConfig eglConfig = eglConfigs[0];

                int[] eglContextAttribList = new int[]{
                        EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                        EGL14.EGL_NONE
                };
                eglContext = EGL14.eglCreateContext(eglDisplay, eglConfig, EGL14.EGL_NO_CONTEXT,
                        eglContextAttribList, 0);
                if (eglContext == null) {
                    return -1;
                }

                // create EglSurface
                int[] eglSurfaceAttribList = new int[]{
                        EGL14.EGL_WIDTH, 64,
                        EGL14.EGL_HEIGHT, 64,
                        EGL14.EGL_NONE
                };
                eglSurface = EGL14.eglCreatePbufferSurface(eglDisplay, eglConfig, eglSurfaceAttribList, 0);
                if (eglSurface == null) {
                    return -1;
                }

                // attach EglContext
                if (!EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
                    return -1;
                }

                int[] maxTextureSize = new int[1];
                GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);

                return maxTextureSize[0];
            } finally {
                // tear down
                if (eglDisplay != null) {
                    // detach EglContext
                    EGL14.eglMakeCurrent(eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
                            EGL14.EGL_NO_CONTEXT);
                    if (eglSurface != null) {
                        EGL14.eglDestroySurface(eglDisplay, eglSurface);
                    }
                    if (eglContext != null) {
                        EGL14.eglDestroyContext(eglDisplay, eglContext);
                    }
                    EGL14.eglTerminate(eglDisplay);
                }
            }
        }
    }
}
