package com.github.ykrank.androidtools.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * fixed size and FIFO list
 * Created by ykrank on 2017/2/23.
 */

public class FixedFifoList<T> extends LinkedList<T> {
    private int maxSize = Integer.MAX_VALUE;
    private final Object synObj = new Object();

    public FixedFifoList() {
        super();
    }

    public FixedFifoList(int maxSize) {
        super();
        this.maxSize = maxSize;
    }


    /**
     * 向最后添加一个新的，如果长度超过允许的最大值，则弹出一个 *
     */
    public T addLastSafe(T addLast) {
        synchronized (synObj) {
            T head = null;
            while (size() >= maxSize) {
                head = poll();
            }
            addLast(addLast);
            return head;
        }
    }

    /**
     * 弹出head，如果Size = 0返回null。而不同于pop抛出异常
     *
     * @return
     */
    public T pollSafe() {
        synchronized (synObj) {
            return poll();
        }
    }

    /**
     * 获得最大保存
     *
     * @return
     */
    public int getMaxSize() {
        return this.maxSize;
    }

    /**
     * 设置最大存储范围
     *
     * @return 返回的是，因为改变了队列大小，导致弹出的head
     */
    public List<T> setMaxSize(int maxSize) {
        List<T> list = null;
        if (maxSize < this.maxSize) {
            list = new ArrayList<T>();
            synchronized (synObj) {
                while (size() > maxSize) {
                    list.add(poll());
                }
            }
        }
        this.maxSize = maxSize;
        return list;

    }
}
