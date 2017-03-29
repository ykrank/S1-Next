package me.ykrank.s1next.widget.hostcheck;

/**
 * Check result
 * Created by ykrank on 2017/3/29.
 */

public class HostUrlCheckResult {
    private String baseUrl;
    private boolean success;
    private int time;

    public HostUrlCheckResult(String baseUrl, boolean success, int time) {
        this.baseUrl = baseUrl;
        this.success = success;
        this.time = time;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "HostUrlCheckResult{" +
                "baseUrl='" + baseUrl + '\'' +
                ", success=" + success +
                ", time=" + time +
                '}';
    }
}
