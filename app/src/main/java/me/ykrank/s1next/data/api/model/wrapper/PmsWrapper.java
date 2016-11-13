package me.ykrank.s1next.data.api.model.wrapper;

import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import java.util.List;

import me.ykrank.s1next.data.User;
import me.ykrank.s1next.data.api.model.Pm;
import me.ykrank.s1next.data.api.model.collection.Pms;
import me.ykrank.s1next.util.LooperUtil;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PmsWrapper {
    @JsonProperty("Variables")
    private Pms pms;

    public Pms getPms() {
        return pms;
    }

    public void setPms(Pms pms) {
        this.pms = pms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PmsWrapper that = (PmsWrapper) o;
        return Objects.equal(pms, that.pms);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pms);
    }

    /**
     * 完善每条私信的收信人学校
     * @param me 自己
     * @param toUsername 对方用户名
     */
    public PmsWrapper setMsgToUsername(User me, String toUsername){
        LooperUtil.enforceOnWorkThread();
        List<Pm> pmList= getPms().getPmList();
        if (pmList == null || pmList.isEmpty()){
            return this;
        }
        for (Pm pm: pmList) {
            if (TextUtils.equals(pm.getMsgToId(), me.getUid())){
                pm.setMsgTo(me.getName());
            } else {
                pm.setMsgTo(toUsername);
            }
        }
        return this;
    }
}
