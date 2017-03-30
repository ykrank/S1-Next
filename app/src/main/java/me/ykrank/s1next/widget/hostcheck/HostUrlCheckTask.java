package me.ykrank.s1next.widget.hostcheck;

import android.accounts.NetworkErrorException;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import me.ykrank.s1next.data.api.Api;
import me.ykrank.s1next.data.pref.GeneralPreferencesManager;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.RxJavaUtil;
import okhttp3.HttpUrl;

/**
 * Created by ykrank on 2017/3/29.
 */

public enum HostUrlCheckTask {
    INSTANCE;

    public static final int periodic = 600_000;

    private GeneralPreferencesManager prefManager;
    private HttpUrl baseHttpUrl;
    private List<String> toBeCheckedBaseUrls;
    private int hostUrlCheckJobId;
    /**
     * only when JobScheduler invalid
     */
    private long initTime, lastCheckTime;
    private volatile boolean delayChecking = false;

    public static void init(GeneralPreferencesManager prefManager) {
        INSTANCE.prefManager = prefManager;
        String url = prefManager.getBaseUrl();
        if (TextUtils.isEmpty(url)) {
            url = Api.BASE_URL;
        }
        Preconditions.checkArgument(url.endsWith("/"));
        INSTANCE.baseHttpUrl = HttpUrl.parse(url);
    }

    public GeneralPreferencesManager getPrefManager() {
        return prefManager;
    }

    public boolean isInit() {
        return prefManager != null;
    }

    /**
     * always end with / if not null
     *
     * @return base HttpUrl
     */
    @Nullable
    public HttpUrl getBaseHttpUrl() {
        return baseHttpUrl;
    }

    public void setBaseHttpUrl(HttpUrl baseHttpUrl) {
        if (baseHttpUrl == null) {
            return;
        }
        this.baseHttpUrl = baseHttpUrl;
        if (!isInit()) {
            L.report(new IllegalStateException("HostUrlCheckTask not init when set base url"));
            return;
        }
        prefManager.invalidateBaseUrl(baseHttpUrl.toString());
    }

    public long getLastCheckTime() {
        return lastCheckTime;
    }

    public void setLastCheckTime(long lastCheckTime) {
        this.lastCheckTime = lastCheckTime;
    }

    public List<String> getToBeCheckedBaseUrls() {
        ArrayList<String> baseUrls = new ArrayList<>();
        for (String baseUrl : Api.HOST_LIST) {
            baseUrls.add("http://" + baseUrl + "/");
            baseUrls.add("https://" + baseUrl + "/");
            baseUrls.add("http://" + baseUrl + "/2b/");
            baseUrls.add("https://" + baseUrl + "/2b/");
        }
        return baseUrls;
    }

    public Single<List<HostUrlCheckResultCount>> forceCheckHost() {
        HostUrlCheckJobClient jobClient = new HostUrlCheckJobClient();
        return jobClient.startJob(getToBeCheckedBaseUrls())
                .compose(RxJavaUtil.iOSingleTransformer())
                .doOnDispose(() -> delayChecking = false)
                .doOnEvent((t1, t2) -> delayChecking = false)
                .doOnSuccess(results -> {
                    if (results.size() > 0) {
                        L.leaveMsg("Host check result" + results.toString());
                        setBaseHttpUrl(HttpUrl.parse(results.get(0).getBaseUrl()));
                        setLastCheckTime(System.currentTimeMillis());
                    } else {
                        L.report(new NetworkErrorException("Host check result is null"));
                    }
                });
    }

    public Single<List<HostUrlCheckResultCount>> forceCheckHost(int delay) {
        delayChecking = true;
        return Single.timer(delay, TimeUnit.MILLISECONDS)
                .flatMap(l -> forceCheckHost());
    }

    public void forceCheckHostSilent() {
        forceCheckHost().subscribe(r -> {
        }, L::report);
    }

    public void forceCheckHostSilentDelay() {
        forceCheckHost(5000).subscribe(r -> {
        }, L::report);
    }

    public void startCheckHostTask(@NonNull Context context) {
        initTime = System.currentTimeMillis();
        lastCheckTime = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startCheckHostJobScheduler(context);
        }
        forceCheckHostSilentDelay();
    }

    public void stopCheckHostTask(@NonNull Context context) {
        initTime = 0;
        lastCheckTime = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            stopCheckHostJobScheduler(context, hostUrlCheckJobId);
        }
    }

    public void inspectCheckHostTask() {
        if (delayChecking) {
            return;
        }
        if (lastCheckTime == 0) {
            if (System.currentTimeMillis() - initTime > 2 * periodic) {
                forceCheckHostSilentDelay();
            }
        } else if (System.currentTimeMillis() - lastCheckTime > 2 * periodic) {
            forceCheckHostSilentDelay();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public int startCheckHostJobScheduler(@NonNull Context context) {
        JobScheduler mJobScheduler = (JobScheduler)
                context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(1,
                new ComponentName(context.getPackageName(), HostUrlCheckJobService.class.getName()));
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        // 10 min
        builder.setPeriodic(periodic);
        PersistableBundle bundle = new PersistableBundle();

        List<String> baseUrls = getToBeCheckedBaseUrls();
        String[] baseUrlArray = new String[baseUrls.size()];
        bundle.putStringArray(HostUrlCheckJobService.BUNDLE_CHECKED_BASE_URLS, baseUrls.toArray(baseUrlArray));
        builder.setExtras(bundle);

        hostUrlCheckJobId = mJobScheduler.schedule(builder.build());
        return hostUrlCheckJobId;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void stopCheckHostJobScheduler(@NonNull Context context, int jobId) {
        JobScheduler mJobScheduler = (JobScheduler)
                context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        mJobScheduler.cancel(jobId);
    }
}
