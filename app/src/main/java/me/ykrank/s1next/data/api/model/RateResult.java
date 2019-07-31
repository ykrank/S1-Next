package me.ykrank.s1next.data.api.model;

import androidx.annotation.NonNull;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ykrank on 2017/3/24.
 */

public class RateResult {
    private boolean success;
    private String errorMsg;

    @NonNull
    public static RateResult fromHtml(String html) {
        RateResult result = new RateResult();
        if (TextUtils.isEmpty(html)) {
            result.setSuccess(false);
            result.setErrorMsg("返回值为空！");
            return result;
        }
        if (html.contains("succeedhandle_rate")) {
            result.setSuccess(true);
        } else {
            result.setSuccess(false);
            Pattern pattern = Pattern.compile("errorhandle_rate\\('(.+)'");
            Matcher matcher = pattern.matcher(html);
            if (matcher.find()) {
                result.setErrorMsg(matcher.group(1));
            } else {
                result.setErrorMsg(html);
            }
        }
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return "RateResult{" +
                "success=" + success +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }
}
