package me.ykrank.s1next.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;

import me.ykrank.s1next.R;
import me.ykrank.s1next.databinding.ActivitySearchBinding;

/**
 * Created by ykrank on 2016/9/28 0028.
 */

public class SearchActivity extends BaseActivity {
    private ActivitySearchBinding binding;

    public static void start(Context context){
        context.startActivity(new Intent(context, SearchActivity.class));
    }

    public static void start(Activity activity, @NonNull View searchIconView){
        Bundle options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, searchIconView,
                activity.getString(R.string.transition_search_back)).toBundle();
        ActivityCompat.startActivity(activity, new Intent(activity, SearchActivity.class), options);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        
        binding.appBar.toolbar.setNavigationIcon(null);
        setupWindowAnimations();
        compatBackIcon();
    }

    @Override
    public boolean isTranslucent() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setupWindowAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition enterTransition = TransitionInflater.from(this).inflateTransition(R.transition.search_enter);
            getWindow().setEnterTransition(enterTransition);

            Transition returnTransition = TransitionInflater.from(this).inflateTransition(R.transition.search_return);
            getWindow().setReturnTransition(returnTransition);

            Transition enterShareTransition = TransitionInflater.from(this).inflateTransition(R.transition.search_shared_enter);
            getWindow().setSharedElementEnterTransition(enterShareTransition);

            Transition returnShareTransition = TransitionInflater.from(this).inflateTransition(R.transition.search_shared_return);
            getWindow().setSharedElementReturnTransition(returnShareTransition);
        }
    }
    
    private void compatBackIcon(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            StateListDrawable drawable = new StateListDrawable();

            int[] attribute = new int[]{R.attr.colorPrimaryDark, R.attr.colorPrimary};
            TypedArray array = getTheme().obtainStyledAttributes(attribute);
            int colorPrimaryDark = array.getColor(0, Color.TRANSPARENT);
            int colorPrimary = array.getColor(1, Color.TRANSPARENT);
            array.recycle();

            drawable.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(colorPrimaryDark));
            drawable.addState(new int[]{-android.R.attr.state_pressed}, new ColorDrawable(colorPrimary));

            //noinspection deprecation
            binding.appBar.searchback.setBackgroundDrawable(drawable);
        }
    }
}
