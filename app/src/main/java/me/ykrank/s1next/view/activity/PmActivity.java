package me.ykrank.s1next.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.concurrent.TimeUnit;

import me.ykrank.s1next.R;
import me.ykrank.s1next.data.event.PmGroupClickEvent;
import me.ykrank.s1next.util.RxJavaUtil;
import me.ykrank.s1next.view.fragment.PmFragment;
import me.ykrank.s1next.view.fragment.PmGroupsFragment;
import rx.Subscription;

import static android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN;


public class PmActivity extends BaseActivity {

    private Subscription mSubscription;

    private Fragment fragment;

    public static void startPmActivity(Context context) {
        Intent intent = new Intent(context, PmActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        if (savedInstanceState == null) {
            fragment = PmGroupsFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_layout, fragment, PmGroupsFragment.TAG)
                    .commit();
        }

        mSubscription = mEventBus.get()
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (o instanceof PmGroupClickEvent) {
                        //Fixme subscribe twice
                        PmGroupClickEvent event = (PmGroupClickEvent) o;
                        fragment = PmFragment.newInstance(event.getToUid(), event.getToUsername());
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frame_layout, fragment, PmFragment.TAG)
                                .setTransition(TRANSIT_FRAGMENT_OPEN)
                                .addToBackStack(null)
                                .commit();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        RxJavaUtil.unsubscribeIfNotNull(mSubscription);
        super.onDestroy();
    }
}
