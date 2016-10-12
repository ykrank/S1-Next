package me.ykrank.s1next.view.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import me.ykrank.s1next.R;

/**
 * Created by ykrank on 2016/9/28 0028.
 */

public class SearchActivity extends AppCompatActivity {

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

        setContentView(R.layout.activity_search);
    }
}
