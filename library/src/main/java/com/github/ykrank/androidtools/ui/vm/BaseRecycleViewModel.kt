package com.github.ykrank.androidtools.ui.vm

import androidx.lifecycle.ViewModel

/**
 * Created by yuanke on 7/17/24
 * @author yuanke.ykrank@bytedance.com
 */
class BaseRecycleViewModel<D>: ViewModel() {

    var data: D? = null
    val loading = LoadingViewModel()
}