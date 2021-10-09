/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.ykrank.androidtools.widget;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Because we can't retain Fragments that are nested in other fragments.
 * So we adds tag to each Fragments in order to let host Fragment in ViewPager
 * know its target {@link com.github.ykrank.androidtools.ui.internal.DataRetainedFragment}.
 * <p>
 * Forked from {@link FragmentStatePagerAdapter}
 * Version: 25.3.0
 */
@SuppressLint({"CommitTransaction", "unchecked"})
@SuppressWarnings("unchecked")
public abstract class TagFragmentStatePagerAdapter<T extends Fragment> extends PagerAdapter {

    private static final String TAG = "TagFragStatePagerAdapt";
    private static final boolean DEBUG = false;

    private final FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction = null;

    private ArrayList<Fragment.SavedState> mSavedState = new ArrayList<>();
    private ArrayList<T> mFragments = new ArrayList<>();
    private Fragment mCurrentPrimaryItem = null;

    public TagFragmentStatePagerAdapter(FragmentManager fm) {
        mFragmentManager = fm;
    }

    /**
     * Returns the Fragment associated with a specified position.
     */
    public abstract T getItem(int position);

    @Nullable
    public T getCachedFragment(int position) {
        return mFragments.size() > position ? mFragments.get(position) : null;
    }

    @Override
    public void startUpdate(ViewGroup container) {
        if (container.getId() == View.NO_ID) {
            throw new IllegalStateException("ViewPager with adapter " + this
                    + " requires a view id");
        }
    }

    @Override
    public T instantiateItem(ViewGroup container, int position) {
        // If we already have this item instantiated, there is nothing
        // to do.  This can happen when we are restoring the entire pager
        // from its saved state, where the fragment manager has already
        // taken care of restoring the fragments we previously had instantiated.
        if (mFragments.size() > position) {
            T f = mFragments.get(position);
            if (f != null) {
                return f;
            }
        }

        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }

        T fragment = getItem(position);
        if (DEBUG) Log.v(TAG, "Adding item #" + position + ": f=" + fragment);
        if (mSavedState.size() > position) {
            Fragment.SavedState fss = mSavedState.get(position);
            if (fss != null) {
                fragment.setInitialSavedState(fss);
            }
        }
        while (mFragments.size() <= position) {
            mFragments.add(null);
        }
        fragment.setMenuVisibility(false);
        fragment.setUserVisibleHint(false);
        mFragments.set(position, fragment);
        // here we did to add a tag to the Fragment!
        mCurTransaction.add(container.getId(), fragment,
                "android:TagFragmentStatePagerAdapter:" + container.getId() + ":" + position);

        return fragment;
    }

    @Override
    public final void destroyItem(ViewGroup container, int position, Object object) {
        destroyItem(container, position, (T) object);
    }

    public void destroyItem(ViewGroup container, int position, T fragment) {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
        if (DEBUG) Log.v(TAG, "Removing item #" + position + ": f=" + fragment
                + " v=" + fragment.getView());
        while (mSavedState.size() <= position) {
            mSavedState.add(null);
        }
        mSavedState.set(position, fragment.isAdded()
                ? mFragmentManager.saveFragmentInstanceState(fragment) : null);
        mFragments.set(position, null);

        mCurTransaction.remove(fragment);
    }

    @Override
    public final void setPrimaryItem(ViewGroup container, int position, Object object) {
        setPrimaryItem(container, position, (T) object);
    }

    public void setPrimaryItem(ViewGroup container, int position, T fragment) {
        if (fragment != mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem.setMenuVisibility(false);
                mCurrentPrimaryItem.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            mCurrentPrimaryItem = fragment;
        }
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (mCurTransaction != null) {
            mCurTransaction.commitNowAllowingStateLoss();
            mCurTransaction = null;
        }
    }

    @Override
    public final boolean isViewFromObject(View view, Object object) {
        return isViewFromObject(view, (T) object);
    }

    public boolean isViewFromObject(View view, T fragment) {
        return fragment.getView() == view;
    }

    @Override
    public Parcelable saveState() {
        Bundle state = null;
        if (mSavedState.size() > 0) {
            state = new Bundle();
            Fragment.SavedState[] fss = new Fragment.SavedState[mSavedState.size()];
            mSavedState.toArray(fss);
            state.putParcelableArray("states", fss);
        }
        for (int i = 0; i < mFragments.size(); i++) {
            Fragment f = mFragments.get(i);
            if (f != null && f.isAdded()) {
                if (state == null) {
                    state = new Bundle();
                }
                String key = "f" + i;
                mFragmentManager.putFragment(state, key, f);
            }
        }
        return state;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        if (state != null) {
            Bundle bundle = (Bundle) state;
            bundle.setClassLoader(loader);
            Parcelable[] fss = bundle.getParcelableArray("states");
            mSavedState.clear();
            mFragments.clear();
            if (fss != null) {
                for (int i = 0; i < fss.length; i++) {
                    mSavedState.add((Fragment.SavedState) fss[i]);
                }
            }
            Iterable<String> keys = bundle.keySet();
            for (String key : keys) {
                if (key.startsWith("f")) {
                    int index = Integer.parseInt(key.substring(1));
                    Fragment f = mFragmentManager.getFragment(bundle, key);
                    if (f != null) {
                        while (mFragments.size() <= index) {
                            mFragments.add(null);
                        }
                        f.setMenuVisibility(false);
                        mFragments.set(index, (T) f);
                    } else {
                        Log.w(TAG, "Bad fragment at key " + key);
                    }
                }
            }
        }
    }
}
