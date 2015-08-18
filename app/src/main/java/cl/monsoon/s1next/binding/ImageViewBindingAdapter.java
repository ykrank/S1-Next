package cl.monsoon.s1next.binding;

import android.content.res.ColorStateList;
import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.util.Pair;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;

import cl.monsoon.s1next.R;

public final class ImageViewBindingAdapter {

    private ImageViewBindingAdapter() {}

    @BindingAdapter("imageDrawable")
    public static void setImageDrawable(ImageView imageView, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            @ColorInt int rippleColor = ContextCompat.getColor(imageView.getContext(),
                    R.color.ripple_material_dark);
            // add ripple effect
            RippleDrawable rippleDrawable = new RippleDrawable(ColorStateList.valueOf(rippleColor),
                    drawable, null);
            imageView.setImageDrawable(rippleDrawable);
        } else {
            imageView.setImageDrawable(drawable);
        }
    }

    @BindingAdapter({"emoticon", "emoticonDrawableRequestBuilder"})
    public static void loadEmoticon(ImageView imageView, Pair<String, String> emoticon,
                                    DrawableRequestBuilder<Uri> emoticonDrawableRequestBuilder) {
        emoticonDrawableRequestBuilder.load(Uri.parse(emoticon.first)).into(imageView);
    }
}
