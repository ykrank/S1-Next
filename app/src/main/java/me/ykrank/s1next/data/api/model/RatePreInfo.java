package me.ykrank.s1next.data.api.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.core.JsonParseException;
import com.github.ykrank.androidtools.util.L;
import com.google.common.base.Objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import me.ykrank.s1next.data.api.ApiUtil;

/**
 * Created by ykrank on 2017/3/19.
 */

public class RatePreInfo implements Parcelable {
    private String formHash;
    private String tid;
    private String pid;
    private String refer;
    private String handleKey;
    private String minScore;
    private String maxScore;
    private String totalScore;
    private List<String> reasons;
    private boolean checked;
    private boolean disabled;
    private List<String> scoreChoices;
    private String alertError;

    public RatePreInfo() {

    }

    protected RatePreInfo(Parcel in) {
        formHash = in.readString();
        tid = in.readString();
        pid = in.readString();
        refer = in.readString();
        handleKey = in.readString();
        minScore = in.readString();
        maxScore = in.readString();
        totalScore = in.readString();
        reasons = in.createStringArrayList();
        checked = in.readByte() != 0;
        disabled = in.readByte() != 0;
        scoreChoices = in.createStringArrayList();
        alertError = in.readString();
    }

    public static final Creator<RatePreInfo> CREATOR = new Creator<RatePreInfo>() {
        @Override
        public RatePreInfo createFromParcel(Parcel in) {
            return new RatePreInfo(in);
        }

        @Override
        public RatePreInfo[] newArray(int size) {
            return new RatePreInfo[size];
        }
    };

    @NonNull
    public static RatePreInfo fromHtml(@NonNull String html) {
        RatePreInfo info = new RatePreInfo();
        //remove html wrap
        html = ApiUtil.replaceAjaxHeader(html);
        try {
            Document document = Jsoup.parse(html);
            //alert error
            Elements elements = document.select("div.alert_error");
            if (!elements.isEmpty()) {
                info.setAlertError(elements.get(0).text());
                return info;
            }

            elements = document.select("#rateform>input");
            if (elements.size() != 5) {
                throw new JsonParseException(null, "#rateform>input size is " + elements.size());
            }
            info.setFormHash(elements.get(0).attr("value"));
            info.setTid(elements.get(1).attr("value"));
            info.setPid(elements.get(2).attr("value"));
            info.setRefer(elements.get(3).attr("value"));
            info.setHandleKey(elements.get(4).attr("value"));
            //score
            elements = document.select(".dt.mbm>tbody>tr");
            if (elements.size() != 2) {
                throw new JsonParseException(null, ".dt.mbm>tbody>tr size is " + elements.size());
            }
            Elements scoreElements = elements.get(1).children();
            String minMaxScoreString = scoreElements.get(2).text().trim();
            String[] splitResult = minMaxScoreString.split("~");
            info.setMinScore(splitResult[0].trim());
            info.setMaxScore(splitResult[1].trim());
            info.setTotalScore(scoreElements.get(3).text().trim());
            //reasons
            List<String> reasons = new ArrayList<>();
            elements = document.select("#reasonselect>li");
            for (Element element : elements) {
                reasons.add(element.text());
            }
            info.setReasons(reasons);
            //checkbox
            elements = document.select("#sendreasonpm");
            if (elements.size() != 1) {
                throw new JsonParseException(null, "#sendreasonpm size is " + elements.size());
            }
            Element checkBoxElement = elements.get(0);
            info.setChecked("checked".equalsIgnoreCase(checkBoxElement.attr("checked")));
            info.setDisabled("disabled".equalsIgnoreCase(checkBoxElement.attr("disabled")));

            info.setScoreChoices();
        } catch (Exception e) {
            L.report(e);
        }
        return info;
    }

    public String getFormHash() {
        return formHash;
    }

    public void setFormHash(String formHash) {
        this.formHash = formHash;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getRefer() {
        return refer;
    }

    public void setRefer(String refer) {
        this.refer = refer;
    }

    public String getHandleKey() {
        return handleKey;
    }

    public void setHandleKey(String handleKey) {
        this.handleKey = handleKey;
    }

    public String getMinScore() {
        return minScore;
    }

    public void setMinScore(String minScore) {
        this.minScore = minScore;
    }

    public String getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(String maxScore) {
        this.maxScore = maxScore;
    }

    public String getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(String totalScore) {
        this.totalScore = totalScore;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void setReasons(List<String> reasons) {
        this.reasons = reasons;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public List<String> getScoreChoices() {
        return scoreChoices;
    }

    public void setScoreChoices() {
        List<String> list = new ArrayList<>();
        for (int i = Float.valueOf(minScore).intValue(); i <= Float.valueOf(maxScore); i++) {
            if (i != 0) {
                list.add(String.valueOf(i));
            }
        }
        this.scoreChoices = list;
    }

    public String getAlertError() {
        return alertError;
    }

    public void setAlertError(String alertError) {
        this.alertError = alertError;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RatePreInfo)) return false;
        RatePreInfo that = (RatePreInfo) o;
        return checked == that.checked &&
                disabled == that.disabled &&
                Objects.equal(formHash, that.formHash) &&
                Objects.equal(tid, that.tid) &&
                Objects.equal(pid, that.pid) &&
                Objects.equal(refer, that.refer) &&
                Objects.equal(handleKey, that.handleKey) &&
                Objects.equal(minScore, that.minScore) &&
                Objects.equal(maxScore, that.maxScore) &&
                Objects.equal(totalScore, that.totalScore) &&
                Objects.equal(reasons, that.reasons) &&
                Objects.equal(scoreChoices, that.scoreChoices) &&
                Objects.equal(alertError, that.alertError);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(formHash, tid, pid, refer, handleKey, minScore, maxScore, totalScore, reasons, checked, disabled, scoreChoices, alertError);
    }

    @Override
    public String toString() {
        return "RatePreInfo{" +
                "formHash='" + formHash + '\'' +
                ", tid='" + tid + '\'' +
                ", pid='" + pid + '\'' +
                ", refer='" + refer + '\'' +
                ", handleKey='" + handleKey + '\'' +
                ", minScore=" + minScore +
                ", maxScore=" + maxScore +
                ", totalScore=" + totalScore +
                ", reasons=" + reasons +
                ", checked=" + checked +
                ", disabled=" + disabled +
                ", scoreChoices=" + scoreChoices +
                ", alertError='" + alertError + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(formHash);
        dest.writeString(tid);
        dest.writeString(pid);
        dest.writeString(refer);
        dest.writeString(handleKey);
        dest.writeString(minScore);
        dest.writeString(maxScore);
        dest.writeString(totalScore);
        dest.writeStringList(reasons);
        dest.writeByte((byte) (checked ? 1 : 0));
        dest.writeByte((byte) (disabled ? 1 : 0));
        dest.writeStringList(scoreChoices);
        dest.writeString(alertError);
    }
}
