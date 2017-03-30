package me.ykrank.s1next.widget.hostcheck;

import android.accounts.NetworkErrorException;
import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;

import java.util.Arrays;

import io.reactivex.disposables.Disposable;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.RxJavaUtil;
import okhttp3.HttpUrl;

/**
 * Created by ykrank on 2017/3/29.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class HostUrlCheckJobService extends JobService {
    public static final String BUNDLE_CHECKED_BASE_URLS = "bundle_checked_base_urls";

    private HostUrlCheckJobClient jobClient;
    private Disposable mDisposable;

    @Override
    public boolean onStartJob(JobParameters params) {
        HostUrlCheckTask checkTask = HostUrlCheckTask.INSTANCE;
        if (!checkTask.isInit()) {
            L.report(new IllegalStateException("HostUrlCheckTask not init when start job"));
        }
        String[] baseUrls = params.getExtras().getStringArray(BUNDLE_CHECKED_BASE_URLS);
        if (baseUrls == null) {
            return false;
        }
        jobClient = new HostUrlCheckJobClient();
        mDisposable = jobClient.startJob(Arrays.asList(baseUrls))
                .compose(RxJavaUtil.iOSingleTransformer())
                .subscribe(results -> {
                    if (results.size() > 0) {
                        L.leaveMsg("Host check result" + results.toString());
                        checkTask.setBaseHttpUrl(HttpUrl.parse(results.get(0).getBaseUrl()));
                        checkTask.setLastCheckTime(System.currentTimeMillis());
                        jobFinished(params, false);
                    } else {
                        L.report(new NetworkErrorException("Host check result is null"));
                        jobFinished(params, true);
                    }
                }, e -> {
                    L.report(e);
                    jobFinished(params, true);
                });
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        RxJavaUtil.disposeIfNotNull(mDisposable);
        if (jobClient != null) {
            jobClient.stopJob();
        }
        return true;
    }
}
