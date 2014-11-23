package cl.monsoon.s1next.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import cl.monsoon.s1next.R;

public final class GalleryFragment extends Fragment {

    public static final String TAG = "gallery_fragment";

    private static final String ARG_IMAGE_URL = "image_url";

    private String mUrl;

    public static GalleryFragment newInstance(String imageUrl) {
        GalleryFragment fragment = new GalleryFragment();

        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URL, imageUrl);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        mUrl = getArguments().getString(ARG_IMAGE_URL);
        Glide.with(getActivity())
                .load(mUrl)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into((ImageView) view.findViewById(R.id.picture));

        return view;
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
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mUrl));

                startActivity(intent);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
