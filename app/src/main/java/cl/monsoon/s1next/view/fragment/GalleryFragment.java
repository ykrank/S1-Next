package cl.monsoon.s1next.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import cl.monsoon.s1next.R;
import cl.monsoon.s1next.util.IntentUtil;
import cl.monsoon.s1next.widget.PhotoView;
import cl.monsoon.s1next.widget.TransformationUtil;

public final class GalleryFragment extends Fragment {

    public static final String TAG = GalleryFragment.class.getSimpleName();

    private static final String ARG_IMAGE_URL = "image_url";

    private String mUrl;

    public static GalleryFragment newInstance(String imageUrl) {
        GalleryFragment fragment = new GalleryFragment();

        Bundle bundle = new Bundle();
        bundle.putString(ARG_IMAGE_URL, imageUrl);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUrl = getArguments().getString(ARG_IMAGE_URL);

        PhotoView photoView = (PhotoView) view.findViewById(R.id.photo_view);
        photoView.setMaxInitialScaleFactor(1);
        photoView.enableImageTransforms(true);

        Glide.with(getActivity())
                .load(mUrl)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .transform(new TransformationUtil.GlMaxTextureSizeBitmapTransformation(getActivity()))
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_gallery, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_browser:
                IntentUtil.startViewIntentExcludeOurApp(getActivity(), Uri.parse(mUrl));

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
