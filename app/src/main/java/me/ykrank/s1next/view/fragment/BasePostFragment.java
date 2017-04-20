package me.ykrank.s1next.view.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.event.EmoticonClickEvent;
import me.ykrank.s1next.data.pref.GeneralPreferencesManager;
import me.ykrank.s1next.databinding.FragmentPostBinding;
import me.ykrank.s1next.util.ImeUtils;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.ResourceUtil;
import me.ykrank.s1next.util.RxJavaUtil;
import me.ykrank.s1next.view.adapter.EmoticonPagerAdapter;
import me.ykrank.s1next.widget.EditorDiskCache;
import me.ykrank.s1next.widget.EventBus;

/**
 * Created by ykrank on 2016/7/31 0031.
 */
public abstract class BasePostFragment extends BaseFragment {
    /**
     * The serialization (saved instance state) Bundle key representing whether emoticon
     * keyboard is showing when configuration changes.
     */
    private static final String STATE_IS_EMOTICON_KEYBOARD_SHOWING = "is_emoticon_keyboard_showing";
    private final Interpolator mInterpolator = new FastOutSlowInInterpolator();
    protected FragmentPostBinding mFragmentPostBinding;
    protected EditText mReplyView;
    /**
     * {@code mMenuEmoticon} is null before {@link #onCreateOptionsMenu(Menu, MenuInflater)}.
     */
    @Nullable
    protected MenuItem mMenuEmoticon;
    protected View mEmoticonKeyboard;
    /**
     * {@code mMenuSend} is null when configuration changes.
     */
    @Nullable
    protected MenuItem mMenuSend;
    @Inject
    EventBus mEventBus;
    @Inject
    GeneralPreferencesManager mGeneralPreferencesManager;
    @Inject
    EditorDiskCache editorDiskCache;
    private boolean mIsEmoticonKeyboardShowing;
    private Disposable mEmotionClickDisposable;
    private Disposable mCacheDisposable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentPostBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_post, container,
                false);
        mReplyView = mFragmentPostBinding.reply;
        mEmoticonKeyboard = mFragmentPostBinding.emoticonKeyboard;
        return mFragmentPostBinding.getRoot();
    }

    protected void initCreateView(@NonNull FragmentPostBinding fragmentPostBinding) {
        mFragmentPostBinding = fragmentPostBinding;
        mReplyView = mFragmentPostBinding.reply;
        mEmoticonKeyboard = mFragmentPostBinding.emoticonKeyboard;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mIsEmoticonKeyboardShowing = savedInstanceState.getBoolean(
                    STATE_IS_EMOTICON_KEYBOARD_SHOWING);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        App.getAppComponent().inject(this);

        mReplyView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mMenuSend != null) {
                    // disable send menu if the content of reply is empty
                    mMenuSend.setEnabled(!TextUtils.isEmpty(s.toString()));
                }
            }
        });

        setupEmoticonKeyboard();

        if (savedInstanceState != null) {
            if (mIsEmoticonKeyboardShowing) {
                showEmoticonKeyboard();
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        mEmotionClickDisposable = mEventBus.get()
                .ofType(EmoticonClickEvent.class)
                .subscribe(event -> {
                    mReplyView.getText().replace(mReplyView.getSelectionStart(),
                            mReplyView.getSelectionEnd(), event.getEmoticonEntity());
                });

        RxJavaUtil.disposeIfNotNull(mCacheDisposable);
        mCacheDisposable = null;
        if (!TextUtils.isEmpty(getCacheKey()) && TextUtils.isEmpty(mReplyView.getText())) {
            mCacheDisposable = resumeFromCache(Single.just(getCacheKey())
                    .map(editorDiskCache::get));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        RxJavaUtil.disposeIfNotNull(mEmotionClickDisposable);
        RxJavaUtil.disposeIfNotNull(mCacheDisposable);
        mCacheDisposable = null;
        if (!TextUtils.isEmpty(getCacheKey()) && !isContentEmpty()) {
            final String cacheString = buildCacheString();
            final String key = getCacheKey();
            if (!TextUtils.isEmpty(cacheString)) {
                mCacheDisposable = Single.just(cacheString)
                        .map(s -> {
                            editorDiskCache.put(key, s);
                            return s;
                        })
                        .compose(RxJavaUtil.iOSingleTransformer())
                        .subscribe(L::i, L::report);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_reply, menu);

        mMenuEmoticon = menu.findItem(R.id.menu_emoticon);
        if (mIsEmoticonKeyboardShowing) {
            setKeyboardIcon();
        }

        mMenuSend = menu.findItem(R.id.menu_send).setEnabled(!TextUtils.isEmpty(mReplyView.getText()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_emoticon:
                if (mIsEmoticonKeyboardShowing) {
                    hideEmoticonKeyboard(true);
                } else {
                    showEmoticonKeyboard();
                }

                return true;
            case R.id.menu_send:
                return OnMenuSendClick();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected abstract boolean OnMenuSendClick();

    /**
     * Key of EditorDiskCache cache. not save/restore if return null
     */
    @Nullable
    public abstract String getCacheKey();

    /**
     * construct string should cached from view
     */
    @CallSuper
    @Nullable
    public String buildCacheString() {
        return getContent();
    }

    @UiThread
    @Nullable
    public Disposable resumeFromCache(Single<String> cache) {
        return cache.compose(RxJavaUtil.iOSingleTransformer())
                .subscribe(mReplyView::setText, L::report);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(STATE_IS_EMOTICON_KEYBOARD_SHOWING, mIsEmoticonKeyboardShowing);
    }

    private void setupEmoticonKeyboard() {
        ViewPager viewPager = mFragmentPostBinding.emoticonKeyboardPager;
        viewPager.setAdapter(new EmoticonPagerAdapter(getActivity()));

        TabLayout tabLayout = mFragmentPostBinding.emoticonKeyboardTabLayout;
        tabLayout.setupWithViewPager(viewPager);
    }

    private void showEmoticonKeyboard() {
        mIsEmoticonKeyboardShowing = true;

        // hide keyboard
        ImeUtils.setShowSoftInputOnFocus(mReplyView, false);
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mReplyView.getWindowToken(), 0);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mEmoticonKeyboard.setVisibility(View.VISIBLE);
        // translationYBy(-mEmoticonKeyboard.getHeight())
        // doesn't work when orientation change
        ViewCompat.animate(mEmoticonKeyboard)
                .alpha(1)
                .translationY(0)
                .setInterpolator(mInterpolator)
                .withLayer()
                .setListener(new EmoticonKeyboardAnimator());

        setKeyboardIcon();
    }

    public void hideEmoticonKeyboard() {
        hideEmoticonKeyboard(false);
    }

    private void hideEmoticonKeyboard(boolean shouldShowKeyboard) {
        ViewCompat.animate(mEmoticonKeyboard)
                .alpha(0)
                .translationY(mEmoticonKeyboard.getHeight())
                .setInterpolator(mInterpolator)
                .withLayer()
                .setListener(new EmoticonKeyboardAnimator() {

                    @Override
                    public void onAnimationEnd(View view) {
                        mEmoticonKeyboard.setVisibility(View.GONE);

                        ImeUtils.setShowSoftInputOnFocus(mReplyView, true);
                        getActivity().getWindow().setSoftInputMode(
                                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

                        if (shouldShowKeyboard) {
                            InputMethodManager inputMethodManager = (InputMethodManager)
                                    getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.showSoftInput(mReplyView,
                                    InputMethodManager.SHOW_IMPLICIT);
                        }

                        super.onAnimationEnd(view);
                    }
                });

        mIsEmoticonKeyboardShowing = false;
        setEmoticonIcon();
    }

    private void setEmoticonIcon() {
        if (mMenuEmoticon != null) {
            mMenuEmoticon.setIcon(ResourceUtil.getResourceId(getContext().getTheme(),
                    R.attr.iconMenuEmoticon));
            mMenuEmoticon.setTitle(R.string.menu_emoticon);
        }
    }

    private void setKeyboardIcon() {
        if (mMenuEmoticon != null) {
            mMenuEmoticon.setIcon(ResourceUtil.getResourceId(getContext().getTheme(),
                    R.attr.iconMenuKeyboard));
            mMenuEmoticon.setTitle(R.string.menu_keyboard);
        }
    }

    public boolean isEmoticonKeyboardShowing() {
        return mIsEmoticonKeyboardShowing;
    }

    @CallSuper
    public boolean isContentEmpty() {
        return mReplyView == null || TextUtils.isEmpty(mReplyView.getText());
    }

    @Nullable
    public String getContent() {
        if (mReplyView != null) {
            return mReplyView.getText().toString();
        }
        return null;
    }

    private class EmoticonKeyboardAnimator implements ViewPropertyAnimatorListener {

        @Override
        public void onAnimationStart(View view) {
            if (mMenuEmoticon != null) {
                mMenuEmoticon.setEnabled(false);
            }
        }

        @Override
        public void onAnimationEnd(View view) {
            if (mMenuEmoticon != null) {
                mMenuEmoticon.setEnabled(true);
            }
        }

        @Override
        public void onAnimationCancel(View view) {
        }
    }
}
