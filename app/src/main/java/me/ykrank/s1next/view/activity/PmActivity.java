package me.ykrank.s1next.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import io.reactivex.disposables.Disposable;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.api.model.PmGroup;
import me.ykrank.s1next.data.event.PmGroupClickEvent;
import me.ykrank.s1next.util.RxJavaUtil;
import me.ykrank.s1next.view.fragment.PmFragment;
import me.ykrank.s1next.view.fragment.PmGroupsFragment;
import me.ykrank.s1next.view.internal.RequestCode;


public class PmActivity extends BaseActivity {

    private Disposable mDisposable;

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
            fragment = PmGroupsFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_layout, fragment, PmGroupsFragment.TAG)
                    .commit();
        }

        mDisposable = mEventBus.get()
                .ofType(PmGroupClickEvent.class)
                .subscribe(event -> {
                    PmGroup pmGroup = event.getPmGroup();
                    fragment = PmFragment.newInstance(pmGroup.getToUid(), pmGroup.getToUsername());
                    replaceFragmentWithBackStack(fragment, PmFragment.TAG);
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
    protected void onDestroy() {
        RxJavaUtil.disposeIfNotNull(mDisposable);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.REQUEST_CODE_MESSAGE_IF_SUCCESS) {
            if (resultCode == Activity.RESULT_OK) {
                PmFragment pmFragment = (PmFragment) getSupportFragmentManager().findFragmentByTag(PmFragment.TAG);
                if (pmFragment != null) {
                    pmFragment.startSwipeRefresh();
                }
            }
        }
    }
}
