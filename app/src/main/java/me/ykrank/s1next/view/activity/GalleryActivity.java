package me.ykrank.s1next.view.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.data.pref.DownloadPreferencesManager;
import me.ykrank.s1next.databinding.ActivityGalleryBinding;
import me.ykrank.s1next.util.FileUtil;
import me.ykrank.s1next.util.IntentUtil;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.LeaksUtil;
import me.ykrank.s1next.util.RxJavaUtil;
import me.ykrank.s1next.view.internal.ToolbarDelegate;
import me.ykrank.s1next.viewmodel.ImageViewModel;
import me.ykrank.s1next.widget.glide.model.ForcePassUrl;
import me.ykrank.s1next.widget.track.DataTrackAgent;
import me.ykrank.s1next.widget.track.event.ViewImageTrackEvent;
import me.ykrank.s1next.widget.track.event.page.ActivityEndEvent;
import me.ykrank.s1next.widget.track.event.page.ActivityStartEvent;
import okhttp3.HttpUrl;

/**
 * An Activity shows an ImageView that supports multi-touch.
 */
public final class GalleryActivity extends OriginActivity {
    public static final String TAG = GalleryActivity.class.getName();

    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 0;

    private static final String ARG_IMAGE_URL = "image_url";
    private static final String ARG_IMAGE_THUMB_URL = "image_thumb_url";

    private String mImageUrl;
    private String mImageThumbUrl;

    private PhotoView mPhotoView;
    private ActivityGalleryBinding binding;

    @Inject
    DataTrackAgent trackAgent;
    @Inject
    DownloadPreferencesManager mDownloadPrefManager;

    public static void startGalleryActivity(Context context, String imageUrl) {
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putExtra(ARG_IMAGE_URL, imageUrl);
        context.startActivity(intent);
    }

    public static void startGalleryActivity(Context context, String imageUrl, String thumbUrl) {
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putExtra(ARG_IMAGE_URL, imageUrl);
        intent.putExtra(ARG_IMAGE_THUMB_URL, thumbUrl);
        context.startActivity(intent);
    }

    public static void startGalleryActivity(Context context, String imageUrl, final View transitionView) {
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putExtra(ARG_IMAGE_URL, imageUrl);
        if (transitionView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // create the transition animation - the images in the layouts
            // of both activities are defined with android:transitionName="gallery_transition"
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation((Activity) context, transitionView,
                            context.getResources().getString(R.string.gallery_transition));
            // start the new activity
            context.startActivity(intent, options.toBundle());
        } else {
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery);
        mPhotoView = binding.photoView;
        mImageUrl = getIntent().getStringExtra(ARG_IMAGE_URL);
        mImageThumbUrl = getIntent().getStringExtra(ARG_IMAGE_THUMB_URL);

        L.leaveMsg("GalleryActivity##url:" + mImageUrl + ",thumb:" + mImageThumbUrl);

        ToolbarDelegate toolbarDelegate = new ToolbarDelegate(this, binding.toolbar);
        setTitle(null);
        toolbarDelegate.setupNavCrossIcon();

        trackAgent.post(new ViewImageTrackEvent(mImageUrl, mImageThumbUrl != null));
        binding.setDownloadPrefManager(mDownloadPrefManager);
        binding.setImageViewModel(new ImageViewModel(mImageUrl, mImageThumbUrl));

        mPhotoView.getAttacher().setScaleType(ImageView.ScaleType.CENTER_INSIDE);
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
                supportFinishAfterTransition();
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
            case R.id.menu_large_image_mode:
                boolean checked = item.isChecked();
                item.setChecked(!checked);
                binding.setLarge(!checked);
                return true;
            case R.id.menu_browser:
                IntentUtil.startViewIntentExcludeOurApp(this, Uri.parse(mImageUrl));

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    downloadImage();
                } catch (SecurityException e) {
                    Toast.makeText(GalleryActivity.this, R.string.message_permission_denied, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private void downloadImage() {
        RequestBuilder<File> builder = Glide.with(this)
                .download(new ForcePassUrl(mImageUrl));
        //avatar signature
        if (Api.isAvatarUrl(mImageUrl)) {
            builder = builder.apply(new RequestOptions()
                    .signature(mDownloadPrefManager.getAvatarCacheInvalidationIntervalSignature()));
        }
        builder.into(new SimpleTarget<File>() {
            @Override
            public void onResourceReady(File resource, Transition<? super File> transition) {
                RxJavaUtil.workWithUiResult(() -> {
                    String name = null;
                    File file;
                    File downloadDir = FileUtil.getDownloadDirectory(GalleryActivity.this);
                    HttpUrl url = HttpUrl.parse(mImageUrl);
                    if (url != null) {
                        List<String> segments = url.encodedPathSegments();
                        if (segments.size() > 0) {
                            name = segments.get(segments.size() - 1);
                        }
                        //sometime url is php
                        if (name != null && name.endsWith(".php")) {
                            name = null;
                        }
                    }

                    ImageHeaderParser.ImageType type = FileUtil.getImageType(GalleryActivity.this, resource);
                    String imageType = FileUtil.getImageTypeSuffix(type);
                    if (imageType == null) {
                        imageType = ".jpg";
                    }

                    if (!TextUtils.isEmpty(name)) {
                        if (!name.endsWith(imageType)) {
                            name += imageType;
                        }
                        file = new File(downloadDir, name);
                    } else {
                        file = FileUtil.newFileInDirectory(downloadDir, imageType);
                    }
                    FileUtil.copyFile(resource, file);
                    return file;
                }, f -> {
                    Snackbar.make(binding.getRoot(), R.string.download_success, Snackbar.LENGTH_SHORT).show();
                    FileUtil.notifyImageInMediaStore(GalleryActivity.this, f);
                }, e -> {
                    L.report(e);
                    Toast.makeText(GalleryActivity.this, R.string.download_unknown_error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        // Clean up views and other components
        super.onDestroy();
        LeaksUtil.releaseGestureBoostManagerLeaks(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        trackAgent.post(new ActivityStartEvent(this));
    }

    @Override
    protected void onPause() {
        trackAgent.post(new ActivityEndEvent(this));
        super.onPause();
    }
}
