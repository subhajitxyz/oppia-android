package org.oppia.android.util.parser.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

/** [BitmapTransformation] to blur bitmaps. See [BitmapBlurrer] for specifics. */
class BitmapBlurTransformation(context: Context) : BitmapTransformation() {
  private val bitmapBlurrer by lazy { BitmapBlurrer(context) }

  private companion object {
    // See: https://bumptech.github.io/glide/doc/transformations.html#required-methods.
    private val ID = BitmapBlurTransformation::class.java.name
  }

  override fun transform(
    pool: BitmapPool,
    toTransform: Bitmap,
    outWidth: Int,
    outHeight: Int
  ): Bitmap = bitmapBlurrer.blur(toTransform)

  override fun updateDiskCacheKey(messageDigest: MessageDigest) {
    messageDigest.update(ID.toByteArray())
  }

  override fun hashCode(): Int = ID.hashCode()

  override fun equals(other: Any?): Boolean = other is BitmapBlurTransformation
}


class GreyScaleTransformation : BitmapTransformation() {

  private companion object {
    private val ID = GreyScaleTransformation::class.java.name
  }

  override fun transform(
    pool: BitmapPool,
    toTransform: Bitmap,
    outWidth: Int,
    outHeight: Int
  ): Bitmap {
    val greyBitmap = Bitmap.createBitmap(toTransform.width, toTransform.height, toTransform.config)
    val canvas = Canvas(greyBitmap)
    val paint = Paint()
    val colorMatrix = ColorMatrix().apply { setSaturation(0.3f) } // 0 = full grey, 1 = full color
    paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
    canvas.drawBitmap(toTransform, 0f, 0f, paint)
    return greyBitmap
  }

  override fun updateDiskCacheKey(messageDigest: MessageDigest) {
    messageDigest.update(ID.toByteArray(Charsets.UTF_8))
  }

  override fun hashCode(): Int = ID.hashCode()

  override fun equals(other: Any?): Boolean = other is GreyScaleTransformation
}
