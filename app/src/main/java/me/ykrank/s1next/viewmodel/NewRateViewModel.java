package me.ykrank.s1next.viewmodel;

import android.databinding.ObservableField;

import me.ykrank.s1next.data.api.model.RatePreInfo;

/**
 * Created by ykrank on 2017/3/20.
 */

public class NewRateViewModel {
    public final ObservableField<RatePreInfo> info = new ObservableField<>();
}
