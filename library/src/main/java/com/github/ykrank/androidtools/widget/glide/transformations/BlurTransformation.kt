package com.github.ykrank.androidtools.widget.glide.transformations

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.renderscript.RSRuntimeException
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.widget.glide.transformations.internal.FastBlur
import com.github.ykrank.androidtools.widget.glide.transformations.internal.HRBlur.blur
import com.github.ykrank.androidtools.widget.glide.transformations.internal.RSBlur
import java.security.MessageDigest
import kotlin.math.min

/**
 * Fork from https://github.com/wasabeef/glide-transformations
 * See https://github.com/wasabeef/glide-transformations/pull/45
 *
 *
 * Copyright (C) 2015 Wasabeef
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class BlurTransformation @JvmOverloads constructor(
    context: Context,
    private val mBitmapPool: BitmapPool = Glide.get(context).bitmapPool,
    private val mRadius: Int = MAX_RADIUS,
    private val mSampling: Int = DEFAULT_DOWN_SAMPLING
) : Transformation<Bitmap?> {
    private val mContext: Context
    var targetSize = 0

    constructor(context: Context, radius: Int) : this(
        context,
        Glide.get(context).bitmapPool,
        radius,
        DEFAULT_DOWN_SAMPLING
    )

    constructor(context: Context, radius: Int, sampling: Int) : this(
        context,
        Glide.get(context).bitmapPool,
        radius,
        sampling
    )

    init {
        mContext = context.applicationContext
    }

    override fun transform(
        context: Context,
        resource: Resource<Bitmap?>,
        outWidth: Int,
        outHeight: Int
    ): Resource<Bitmap?> {
        val source = resource.get()
        val width = source.getWidth()
        val height = source.getHeight()
        var sampling = mSampling.toFloat()
        var radius = mRadius
        if (targetSize > 0) {
            sampling = min(
                (width.toFloat() / targetSize).toDouble(),
                (height.toFloat() / targetSize).toDouble()
            )
                .toFloat()
            if (sampling < 1) {
                //targetSize bigger than resource width or height, decrease radius, not sampling
                radius = (mRadius * sampling).toInt()
                sampling = 1f
            }
        }
        val scaledWidth = (width / sampling).toInt()
        val scaledHeight = (height / sampling).toInt()
        var bitmap: Bitmap = mBitmapPool[scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888]
        val canvas = Canvas(bitmap)
        canvas.scale(1 / sampling, 1 / sampling)
        val paint = Paint()
        paint.flags = Paint.FILTER_BITMAP_FLAG
        canvas.drawBitmap(source, 0f, 0f, paint)
        var blur = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                bitmap = blur(bitmap, radius.toFloat())
                blur = true
            } catch (e: RuntimeException) {
                L.report(e)
            }
        }
        if (!blur) {
            try {
                RSBlur.blur(mContext, bitmap, radius)
                blur = true
            } catch (e: RSRuntimeException) {
                L.report(e)
            }
        }
        if (!blur) {
            bitmap = FastBlur.blur(bitmap, radius, true)
        }
        return BitmapResource(bitmap, mBitmapPool)
    }

    val id: String
        get() = "BlurTransformation(radius=$mRadius, sampling=$mSampling)"

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(id.toByteArray())
    }

    companion object {
        private const val MAX_RADIUS = 25
        private const val DEFAULT_DOWN_SAMPLING = 1
    }
}
