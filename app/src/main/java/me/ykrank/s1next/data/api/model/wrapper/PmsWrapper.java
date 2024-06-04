package me.ykrank.s1next.data.api.model.wrapper;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.ykrank.androidtools.util.LooperUtil;

import java.util.List;

import me.ykrank.s1next.data.User;
import me.ykrank.s1next.data.api.model.Pm;
import me.ykrank.s1next.data.api.model.collection.Pms;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PmsWrapper extends BaseDataWrapper<Pms> {

    /**
     * 完善每条私信的收信人
     *
     * @param me         自己
     * @param toUsername 对方用户名
     */
    @NonNull
    public PmsWrapper setMsgToUsername(User me, String toUsername) {
        LooperUtil.enforceOnWorkThread();
        List<Pm> pmList = getData().getList();
        if (pmList == null || pmList.isEmpty()) {
            return this;
        }
        for (Pm pm : pmList) {
            if (TextUtils.equals(pm.getMsgToId(), me.getUid())) {
                pm.setMsgTo(me.getName());
            } else {
                pm.setMsgTo(toUsername);
            }
        }
        return this;
    }
}
