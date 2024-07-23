package com.github.ykrank.androidtools.ui.vm

import androidx.lifecycle.ViewModel

/**
 * Created by ykrank on 7/17/24
 * 
 */
class BaseRecycleViewModel<D>: ViewModel() {

    var data: D? = null
    val loading = LoadingViewModel()
}