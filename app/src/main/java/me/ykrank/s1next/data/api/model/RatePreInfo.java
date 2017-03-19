package me.ykrank.s1next.data.api.model;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.core.JsonParseException;
import com.google.common.base.Objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import me.ykrank.s1next.util.L;

/**
 * Created by ykrank on 2017/3/19.
 */

public class RatePreInfo {
    private String formHash;
    private String tid;
    private String pid;
    private String refer;
    private String handleKey;
    private int minScore;
    private int maxScore;
    private int totalScore;
    private List<String> reasons;

    @NonNull
    public static RatePreInfo fromHtml(String html) {
        RatePreInfo info = new RatePreInfo();
        //remove html wrap
        html = html.replace("<root><![CDATA[", "").replace("]]></root>", "");
        try {
            Document document = Jsoup.parse(html);
            Elements elements = document.select("#rateform>input");
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
            info.setMinScore(Integer.valueOf(splitResult[0].trim()));
            info.setMaxScore(Integer.valueOf(splitResult[1].trim()));
            info.setTotalScore(Integer.valueOf(scoreElements.get(3).text().trim()));
            //reasons
            List<String> reasons = new ArrayList<>();
            elements = document.select("#reasonselect>li");
            for (Element element : elements) {
                reasons.add(element.text());
            }
            info.setReasons(reasons);
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

    public int getMinScore() {
        return minScore;
    }

    public void setMinScore(int minScore) {
        this.minScore = minScore;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void setReasons(List<String> reasons) {
        this.reasons = reasons;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RatePreInfo that = (RatePreInfo) o;
        return minScore == that.minScore &&
                maxScore == that.maxScore &&
                totalScore == that.totalScore &&
                Objects.equal(formHash, that.formHash) &&
                Objects.equal(tid, that.tid) &&
                Objects.equal(pid, that.pid) &&
                Objects.equal(refer, that.refer) &&
                Objects.equal(handleKey, that.handleKey) &&
                Objects.equal(reasons, that.reasons);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(formHash, tid, pid, refer, handleKey, minScore, maxScore, totalScore, reasons);
    }
}
