package me.ykrank.s1next.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.github.ykrank.androidautodispose.AndroidRxDispose;
import com.github.ykrank.androidlifecycle.event.ActivityEvent;

import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.PmGroup;
import me.ykrank.s1next.view.event.PmGroupClickEvent;
import me.ykrank.s1next.view.fragment.PmFragment;
import me.ykrank.s1next.view.fragment.PmGroupsFragment;
import me.ykrank.s1next.view.internal.RequestCode;


public class PmActivity extends BaseActivity {

    private Fragment fragment;

    public static void startPmActivity(Context context) {
        Intent intent = new Intent(context, PmActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_without_drawer_and_scrolling_effect);

        if (savedInstanceState == null) {
            fragment = PmGroupsFragment.Companion.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_layout, fragment, PmGroupsFragment.Companion.getTAG())
                    .commit();
        }

        mRxBus.get()
                .ofType(PmGroupClickEvent.class)
                .to(AndroidRxDispose.withObservable(this, ActivityEvent.DESTROY))
                .subscribe(event -> {
                    PmGroup pmGroup = event.getPmGroup();
                    fragment = PmFragment.Companion.newInstance(pmGroup.getToUid(), pmGroup.getToUsername());
                    replaceFragmentWithBackStack(fragment, PmFragment.Companion.getTAG());
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.REQUEST_CODE_MESSAGE_IF_SUCCESS) {
            if (resultCode == Activity.RESULT_OK) {
                PmFragment pmFragment = (PmFragment) getSupportFragmentManager().findFragmentByTag(PmFragment.Companion.getTAG());
                if (pmFragment != null) {
                    pmFragment.startSwipeRefresh();
                }
            }
        }
    }
}
