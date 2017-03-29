package me.ykrank.s1next.widget.hostcheck;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.text.TextUtils;

import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.RxJavaUtil;

/**
 * Created by ykrank on 2017/3/29.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class HostUrlCheckJobService extends JobService {
    public static final String BUNDLE_BASE_URL = "bundle_base_url";

    private HostUrlCheckJobClient jobClient;

    @Override
    public boolean onStartJob(JobParameters params) {
        if (!HostUrlCheckTask.INSTANCE.isInit()) {
            L.report(new IllegalStateException("HostUrlCheckTask not init when start job"));
        }
        String baseUrl = params.getExtras().getString(BUNDLE_BASE_URL);
        if (TextUtils.isEmpty(baseUrl)) {
            return false;
        }
        jobClient = new HostUrlCheckJobClient();
        jobClient.startJob(baseUrl)
                .compose(RxJavaUtil.iOSingleTransformer())
                .subscribe(pair -> {
                    // TODO: 2017/3/29 add job here 
                }, e -> {
                    L.report(e);
                    jobFinished(params, true);
                });
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (jobClient != null) {
            jobClient.stopJob();
        }
        return true;
    }
}
