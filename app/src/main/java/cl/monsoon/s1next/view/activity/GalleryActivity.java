package cl.monsoon.s1next.view.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.databinding.ActivityGalleryBinding;
import cl.monsoon.s1next.util.IntentUtil;
import cl.monsoon.s1next.util.TransformationUtil;
import cl.monsoon.s1next.view.internal.ToolbarPresenter;
import cl.monsoon.s1next.viewmodel.ImageViewModel;
import cl.monsoon.s1next.widget.PhotoView;

/**
 * An Activity shows an ImageView that supports multi-touch.
 */
public final class GalleryActivity extends AppCompatActivity {

    private static final String ARG_IMAGE_URL = "image_url";

    private String mImageUrl;

    public static void startGalleryActivity(Context context, String imageUrl) {
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putExtra(GalleryActivity.ARG_IMAGE_URL, imageUrl);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityGalleryBinding binding = DataBindingUtil.setContentView(this,
                R.layout.activity_gallery);

        ToolbarPresenter toolbarPresenter = new ToolbarPresenter(this, binding.toolbar);
        setTitle(null);
        toolbarPresenter.setupNavCrossIcon();

        // set Toolbar's padding because we use `android:windowTranslucentStatus` in this Activity
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar, (v, insets) -> {
            int top = insets.getSystemWindowInsetTop();
            v.setPadding(0, top, 0, 0);
            v.getLayoutParams().height = v.getContext().getResources().getDimensionPixelSize(
                    R.dimen.abc_action_bar_default_height_material) + top;

            return insets.consumeSystemWindowInsets();
        });

        mImageUrl = getIntent().getStringExtra(ARG_IMAGE_URL);
        ImageViewModel viewModel = new ImageViewModel();
        viewModel.imageUrl.set(mImageUrl);
        binding.setImageViewModel(viewModel);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_gallery, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();

                return true;
            case R.id.menu_browser:
                IntentUtil.startViewIntentExcludeOurApp(this, Uri.parse(mImageUrl));

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @BindingAdapter("url")
    public static void loadImage(PhotoView photoView, String url) {
        photoView.setMaxInitialScaleFactor(1);
        photoView.enableImageTransforms(true);

        Glide.with(photoView.getContext())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .transform(new TransformationUtil.GlMaxTextureSizeBitmapTransformation(
                        photoView.getContext()))
                .into(new SimpleTarget<GlideDrawable>() {

                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        photoView.bindDrawable(resource);
                        if (resource.isAnimated()) {
                            resource.setLoopCount(GlideDrawable.LOOP_FOREVER);
                            resource.start();
                        }
                    }
                });
    }
}
