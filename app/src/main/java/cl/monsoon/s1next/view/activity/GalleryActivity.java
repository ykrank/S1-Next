package cl.monsoon.s1next.view.activity;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Optional;

import java.lang.reflect.Method;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.databinding.ActivityGalleryBinding;
import cl.monsoon.s1next.util.IntentUtil;
import cl.monsoon.s1next.view.internal.CoordinatorLayoutAnchorDelegate;
import cl.monsoon.s1next.view.internal.ToolbarDelegate;
import cl.monsoon.s1next.viewmodel.ImageViewModel;

/**
 * An Activity shows an ImageView that supports multi-touch.
 */
public final class GalleryActivity extends AppCompatActivity
        implements CoordinatorLayoutAnchorDelegate {

    private final static int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 0;

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

        ToolbarDelegate toolbarDelegate = new ToolbarDelegate(this, binding.toolbar);
        setTitle(null);
        toolbarDelegate.setupNavCrossIcon();

        // set Toolbar's top margin because we use `android:windowTranslucentStatus` in this Activity
        // we only use translucent status if API >= 21
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar, (v, insets) -> {
            ((ViewGroup.MarginLayoutParams) v.getLayoutParams()).topMargin =
                    insets.getSystemWindowInsetTop();

            // see CoordinatorLayout#setWindowInsets(WindowInsetsCompat)
            // add CoordinatorLayout's default View.OnApplyWindowInsetsListener implementation
            try {
                Method method = CoordinatorLayout.class.getDeclaredMethod("setWindowInsets",
                        WindowInsetsCompat.class);
                method.setAccessible(true);
                // use 0 px top inset because we want to have translucent status bar
                WindowInsetsCompat insetsWithoutZeroTop = insets.replaceSystemWindowInsets(
                        insets.getSystemWindowInsetLeft(), 0, insets.getSystemWindowInsetRight(),
                        insets.getSystemWindowInsetBottom());
                method.invoke(binding.coordinatorLayout, insetsWithoutZeroTop);
            } catch (Exception e) {
                throw new RuntimeException("Failed to invoke CoordinatorLayout#setWindowInsets(" +
                        "WindowInsetsCompat).", e);
            }
            return insets.consumeSystemWindowInsets();
        });

        mImageUrl = getIntent().getStringExtra(ARG_IMAGE_URL);
        binding.setImageViewModel(new ImageViewModel(mImageUrl));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_gallery, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();

                return true;
            case R.id.menu_download:
                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_WRITE_EXTERNAL_STORAGE);

                    return true;
                }

                downloadImage();

                return true;
            case R.id.menu_browser:
                IntentUtil.startViewIntentExcludeOurApp(this, Uri.parse(mImageUrl));

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadImage();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private void downloadImage() {
        DownloadManager downloadManager = (DownloadManager)
                getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mImageUrl));
        request.setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                mImageUrl.substring(mImageUrl.lastIndexOf("/") + 1));
        downloadManager.enqueue(request);

        showShortSnackbar(R.string.snackbar_action_downloading);
    }

    @Override
    public void setupFloatingActionButton(@DrawableRes int resId, View.OnClickListener onClickListener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void showLongText(CharSequence text) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Snackbar> showLongSnackbarIfVisible(CharSequence text, @StringRes int actionResId, View.OnClickListener onClickListener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void showShortSnackbar(@StringRes int resId) {
        Snackbar.make(findViewById(R.id.coordinator_layout), resId, Snackbar.LENGTH_SHORT).show();
    }
}
