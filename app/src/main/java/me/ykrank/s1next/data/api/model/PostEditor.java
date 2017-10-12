package me.ykrank.s1next.data.api.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import me.ykrank.s1next.data.api.model.wrapper.HtmlDataWrapper;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.LooperUtil;

/**
 * Model for edit post
 * Created by ykrank on 2017/4/12.
 */

public class PostEditor {
    /**
     * if no element "#typeid>option", this is null
     */
    @Nullable
    private List<ThreadType> threadTypes;
    private int typeIndex;
    private String subject;
    private String message;

    @NonNull
    public static PostEditor fromHtml(String html) {
        LooperUtil.enforceOnWorkThread();
        PostEditor editor = new PostEditor();
        try {
            Document document = Jsoup.parse(html);
            HtmlDataWrapper.Companion.preTreatHtml(document);
            //thread types
            Elements typeIdElements = document.select("#typeid>option");
            if (typeIdElements.size() > 0) {
                List<ThreadType> threadTypes = new ArrayList<>();
                for (int i = 0; i < typeIdElements.size(); i++) {
                    Element element = typeIdElements.get(i);
                    String typeId = element.attr("value").trim();
                    String typeName = element.text();
                    if ("selected".equals(element.attr("selected").trim())) {
                        editor.setTypeIndex(i);
                    }
                    threadTypes.add(new ThreadType(typeId, typeName));
                }
                editor.setThreadTypes(threadTypes);
            } else {
                editor.setThreadTypes(null);
            }
            //subject
            Elements subjectElements = document.select("input#subject");
            if (subjectElements.size() > 0) {
                editor.setSubject(subjectElements.get(0).attr("value"));
            }
            //message
            Elements messageElements = document.select("textarea#e_textarea");
            if (messageElements.size() > 0) {
                editor.setMessage(messageElements.get(0).text());
            }
        } catch (Exception e) {
            L.leaveMsg("Source:" + html);
            L.report(e);
        }
        return editor;
    }

    @Nullable
    public List<ThreadType> getThreadTypes() {
        return threadTypes;
    }

    public void setThreadTypes(@Nullable List<ThreadType> threadTypes) {
        this.threadTypes = threadTypes;
    }

    public int getTypeIndex() {
        return typeIndex;
    }

    public void setTypeIndex(int typeIndex) {
        this.typeIndex = typeIndex;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
