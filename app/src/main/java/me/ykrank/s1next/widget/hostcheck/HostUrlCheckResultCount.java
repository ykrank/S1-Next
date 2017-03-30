package me.ykrank.s1next.widget.hostcheck;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.List;

import me.ykrank.s1next.util.L;

/**
 * Check result
 * Created by ykrank on 2017/3/29.
 */

public class HostUrlCheckResultCount implements Comparable<HostUrlCheckResultCount> {
    private String baseUrl;
    private int total;
    private int success;
    private int meanTime;

    public HostUrlCheckResultCount() {
    }

    public HostUrlCheckResultCount(List<HostUrlCheckResult> results) {
        if (results != null && !results.isEmpty()) {
            long totalTime = 0;
            for (int i = 0; i < results.size(); i++) {
                HostUrlCheckResult result = results.get(i);
                if (TextUtils.isEmpty(baseUrl)) {
                    baseUrl = result.getBaseUrl();
                } else if (!TextUtils.equals(baseUrl, result.getBaseUrl())) {
                    L.report(new IllegalStateException(String.format("BaseUrl not same when count. countUrl:%s, newUrl: %s", baseUrl, result.getBaseUrl())));
                    continue;
                }

                total++;
                if (result.isSuccess()) {
                    success++;
                    totalTime += result.getTime();
                }
            }
            if (success > 0) {
                meanTime = (int) (totalTime / success);
            }
        }
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getMeanTime() {
        return meanTime;
    }

    public void setMeanTime(int meanTime) {
        this.meanTime = meanTime;
    }

    public double getAccuracy() {
        if (total <= 0) {
            return 0;
        }
        return success / total;
    }

    public double getWeighted() {
        return getAccuracy() * 100 - Math.pow(meanTime, 1.0 / 3);
    }

    @Override
    public String toString() {
        return "HostUrlCheckResultCount{" +
                "baseUrl='" + baseUrl + '\'' +
                ", total=" + total +
                ", success=" + success +
                ", meanTime=" + meanTime +
                '}';
    }

    @Override
    public int compareTo(@NonNull HostUrlCheckResultCount o) {
        double accuracy = getAccuracy() - o.getAccuracy();
        if (Math.abs(accuracy) >= 0.1) {
            return accuracy > 0 ? -1 : 1;
        }
        double weight = getWeighted() - o.getWeighted();
        if (weight > 0) {
            return -1;
        } else if (weight == 0) {
            return 0;
        }
        return 1;
    }
}
