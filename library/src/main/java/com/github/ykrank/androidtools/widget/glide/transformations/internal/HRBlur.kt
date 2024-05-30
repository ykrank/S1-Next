package com.github.ykrank.androidtools.widget.glide.transformations.internal

import android.graphics.Bitmap
import android.graphics.HardwareRenderer
import android.graphics.PixelFormat
import android.graphics.RenderEffect
import android.graphics.RenderNode
import android.graphics.Shader
import android.hardware.HardwareBuffer
import android.media.Image
import android.media.ImageReader
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * 支持使用由 HardwareBuffer 支持的 HardwareRenderer 进行加速渲染
 *
 * Created by yuanke on 5/30/24
 * @author yuanke.ykrank@bytedance.com
 */
object HRBlur {

    @JvmStatic
    @Throws(RuntimeException::class)
    @RequiresApi(Build.VERSION_CODES.S)
    fun blur(bitmap: Bitmap, radius: Float): Bitmap {
        var imageReaderM: ImageReader? = null
        var hardwareBufferM: HardwareBuffer? = null
        var imageM: Image? = null
        var renderNodeM: RenderNode? = null
        var hardwareRendererM: HardwareRenderer? = null
        try {
            val imageReader = ImageReader.newInstance(
                bitmap.width, bitmap.height,
                PixelFormat.RGBA_8888, 1,
                HardwareBuffer.USAGE_GPU_SAMPLED_IMAGE or HardwareBuffer.USAGE_GPU_COLOR_OUTPUT
            )
            imageReaderM = imageReader
            val renderNode = RenderNode("BlurEffect")
            renderNodeM = renderNode
            val hardwareRenderer = HardwareRenderer()
            hardwareRendererM = hardwareRenderer

            hardwareRenderer.setSurface(imageReader.surface)
            hardwareRenderer.setContentRoot(renderNode)
            renderNode.setPosition(0, 0, imageReader.width, imageReader.height)
            val blurRenderEffect = RenderEffect.createBlurEffect(
                radius, radius,
                Shader.TileMode.MIRROR
            )
            renderNode.setRenderEffect(blurRenderEffect)

            val renderCanvas = renderNode.beginRecording()
            renderCanvas.drawBitmap(bitmap, 0f, 0f, null)
            renderNode.endRecording()
            hardwareRenderer.createRenderRequest()
                .setWaitForPresent(true)
                .syncAndDraw()

            val image = imageReader.acquireNextImage() ?: throw RuntimeException("No Image")
            imageM = image
            val hardwareBuffer = image.hardwareBuffer ?: throw RuntimeException("No HardwareBuffer")
            hardwareBufferM = hardwareBuffer
            return Bitmap.wrapHardwareBuffer(hardwareBuffer, null)
                ?: throw RuntimeException("Create Bitmap Failed")
        } finally {
            hardwareBufferM?.close()
            imageM?.close()
            imageReaderM?.close()
            renderNodeM?.discardDisplayList()
            hardwareRendererM?.destroy()
        }
    }
}