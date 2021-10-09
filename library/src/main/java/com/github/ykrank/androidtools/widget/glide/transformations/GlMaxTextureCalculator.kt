package com.github.ykrank.androidtools.widget.glide.transformations

import android.annotation.TargetApi
import android.opengl.*
import android.os.Build
import com.github.ykrank.androidtools.util.L
import javax.microedition.khronos.opengles.GL10


/**
 * A calculator for getting OpenGL texture size limit.
 */
internal class GlMaxTextureCalculator {

    var glMaxTextureSize: Int = 0
        private set

    private val gl10MaxTextureSize: Int
        get() {
            val maxTextureSize = IntArray(1)
            GLES10.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0)

            return maxTextureSize[0]
        }

    /**
     * Forked from https://github.com/android/platform_frameworks_base/blob/master/services/core/java/com/android/server/display/ColorFade.java
     * and http://stackoverflow.com/q/26985858
     */
    private val gl20MaxTextureSize: Int
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        get() {
            var eglDisplay: EGLDisplay? = null
            var eglContext: EGLContext? = null
            var eglSurface: EGLSurface? = null
            try {
                eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
                if (eglDisplay === EGL14.EGL_NO_DISPLAY) {
                    return -1
                }

                val version = IntArray(2)
                if (!EGL14.eglInitialize(eglDisplay, version, 0, version, 1)) {
                    return -1
                }

                val eglConfigAttribList = intArrayOf(EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT, EGL14.EGL_RED_SIZE, 8, EGL14.EGL_GREEN_SIZE, 8, EGL14.EGL_BLUE_SIZE, 8, EGL14.EGL_ALPHA_SIZE, 8, EGL14.EGL_NONE)

                val eglConfigs = arrayOfNulls<EGLConfig>(1)
                val numEglConfigs = IntArray(1)
                if (!EGL14.eglChooseConfig(eglDisplay, eglConfigAttribList, 0, eglConfigs, 0,
                        eglConfigs.size, numEglConfigs, 0)) {
                    return -1
                }
                if (numEglConfigs[0] <= 0) {
                    return -1
                }
                val eglConfig = eglConfigs[0]

                val eglContextAttribList = intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE)
                eglContext = EGL14.eglCreateContext(eglDisplay, eglConfig, EGL14.EGL_NO_CONTEXT,
                        eglContextAttribList, 0)
                if (eglContext == null) {
                    return -1
                }
                val eglSurfaceAttribList = intArrayOf(EGL14.EGL_WIDTH, 64, EGL14.EGL_HEIGHT, 64, EGL14.EGL_NONE)
                eglSurface = EGL14.eglCreatePbufferSurface(eglDisplay, eglConfig, eglSurfaceAttribList, 0)
                if (eglSurface == null) {
                    return -1
                }
                if (!EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
                    return -1
                }

                val maxTextureSize = IntArray(1)
                GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0)

                return maxTextureSize[0]
            } catch (e: Exception) {
                L.report(e)
                return -1
            } finally {
                if (eglDisplay != null) {
                    EGL14.eglMakeCurrent(eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
                            EGL14.EGL_NO_CONTEXT)
                    if (eglSurface != null) {
                        EGL14.eglDestroySurface(eglDisplay, eglSurface)
                    }
                    if (eglContext != null) {
                        EGL14.eglDestroyContext(eglDisplay, eglContext)
                    }
                    EGL14.eglTerminate(eglDisplay)
                }
            }
        }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            glMaxTextureSize = gl20MaxTextureSize
        } else {
            glMaxTextureSize = gl10MaxTextureSize
        }
        if (glMaxTextureSize <= 0) {
            glMaxTextureSize = GL_TEXTURE_SIZE_MINIMUM
        }
    }

    companion object {
        private val GL_TEXTURE_SIZE_MINIMUM = 2048

        val instance: GlMaxTextureCalculator by lazy {
            GlMaxTextureCalculator()
        }
    }
}
