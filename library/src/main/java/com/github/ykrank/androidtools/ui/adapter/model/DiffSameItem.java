package com.github.ykrank.androidtools.ui.adapter.model;

import androidx.annotation.Nullable;

import com.google.common.base.Objects;

/**
 * Created by ykrank on 2016/10/27.
 */

public interface DiffSameItem {

    /**
     * whether two object of this is same item, use in recycleView to show animate
     *
     * @return same or not
     */
    boolean isSameItem(Object other);

    default boolean isSameContent(Object other) {
        return Objects.equal(this, other);
    }

    @Nullable
    default Object getChangePayload(Object other) {
        return null;
    }
}
