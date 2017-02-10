package me.ykrank.s1next.view.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.databinding.ActivityGalleryBinding;
import me.ykrank.s1next.util.IntentUtil;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.ViewUtil;
import me.ykrank.s1next.view.internal.ToolbarDelegate;
import me.ykrank.s1next.viewmodel.ImageViewModel;
import me.ykrank.s1next.widget.PhotoView;
import me.ykrank.s1next.widget.track.DataTrackAgent;
import me.ykrank.s1next.widget.track.event.ViewImageTrackEvent;
import me.ykrank.s1next.widget.track.event.page.PageEndEvent;
import me.ykrank.s1next.widget.track.event.page.PageStartEvent;

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

    private DataTrackAgent trackAgent;

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
        trackAgent = App.get().getTrackAgent();
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery);
        mPhotoView = binding.photoView;
        mImageUrl = getIntent().getStringExtra(ARG_IMAGE_URL);
        mImageThumbUrl = getIntent().getStringExtra(ARG_IMAGE_THUMB_URL);

        L.leaveMsg("GalleryActivity##url:" + mImageUrl + ",thumb:" + mImageThumbUrl);

        ToolbarDelegate toolbarDelegate = new ToolbarDelegate(this, binding.toolbar);
        setTitle(null);
        toolbarDelegate.setupNavCrossIcon();

        ViewUtil.marginTranslucentToolbar(binding.toolbar);
        //TODO http://stackoverflow.com/questions/31492040/snackbar-and-fitssystemwindow

        trackAgent.post(new ViewImageTrackEvent(mImageUrl, mImageThumbUrl != null));
        binding.setImageViewModel(new ImageViewModel(mImageUrl, mImageThumbUrl));
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
        DownloadManager downloadManager = (DownloadManager)
                getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mImageUrl));
        request.setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                mImageUrl.substring(mImageUrl.lastIndexOf("/") + 1));
        downloadManager.enqueue(request);

        Snackbar.make(findViewById(R.id.coordinator_layout),
                R.string.snackbar_action_downloading, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        // Clean up views and other components
        if (mPhotoView != null) {
            mPhotoView.clear();
            mPhotoView = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        trackAgent.post(new PageStartEvent(this, "图片浏览-GalleryActivity"));
    }

    @Override
    protected void onPause() {
        trackAgent.post(new PageEndEvent(this, "图片浏览-GalleryActivity"));
        super.onPause();
    }
}
