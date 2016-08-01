package me.ykrank.s1next.data.api.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.LooperUtil;

/**
 * Created by ykrank on 2016/7/31 0031.
 */
public final class ThreadType {
    private String typeId;
    private String typeName;

    public ThreadType(String typeId, String typeName) {
        this.typeId = typeId;
        this.typeName = typeName;
    }

    /**
     * Extracts {@link Quote} from XML string.
     */
    @NonNull
    public static List<ThreadType> fromXmlString(String xmlString) {
        LooperUtil.enforceOnWorkThread();
        List<ThreadType> types = new ArrayList<>();
        // example: <select name="typeid" id="typeid" width="80"> <option value="0">选择主题分类</option> </select>
        Pattern pattern = Pattern.compile("<select name=\"typeid\" id=\"typeid\"[\\s\\S]+?</select>");
        Matcher matcher = pattern.matcher(xmlString);

        if (matcher.find()) {
            // example: <option value="290">漫画</option>
            pattern = Pattern.compile("<option value=\"([0-9]+)\">([^\\x00-\\xff]+)</option>");
            Matcher matcher2 = pattern.matcher(matcher.group(0));
            while (matcher2.find()) {
                try {
                    ThreadType threadType = new ThreadType(matcher2.group(1), matcher2.group(2));
                    types.add(threadType);
                } catch (IllegalStateException e) {
                    L.e(e);
                }
            }
        }

        return types;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ThreadType that = (ThreadType) o;

        if (typeId != null ? !typeId.equals(that.typeId) : that.typeId != null) return false;
        return typeName != null ? typeName.equals(that.typeName) : that.typeName == null;

    }

    @Override
    public int hashCode() {
        int result = typeId != null ? typeId.hashCode() : 0;
        result = 31 * result + (typeName != null ? typeName.hashCode() : 0);
        return result;
    }
}
