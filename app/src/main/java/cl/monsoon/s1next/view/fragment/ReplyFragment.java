package cl.monsoon.s1next.view.fragment;

import android.animation.Animator;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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

import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.event.EmoticonClickEvent;
import cl.monsoon.s1next.data.pref.GeneralPreferencesManager;
import cl.monsoon.s1next.databinding.FragmentReplyBinding;
import cl.monsoon.s1next.util.DeviceUtil;
import cl.monsoon.s1next.util.ResourceUtil;
import cl.monsoon.s1next.util.ViewUtil;
import cl.monsoon.s1next.view.adapter.EmoticonPagerAdapter;
import cl.monsoon.s1next.view.dialog.ReplyRequestDialogFragment;
import cl.monsoon.s1next.widget.EventBus;
import rx.Subscription;

/**
 * A Fragment shows {@link EditText} to let the user enter reply.
 */
public final class ReplyFragment extends Fragment {

    public static final String TAG = ReplyFragment.class.getName();

    private static final String ARG_THREAD_ID = "thread_id";
    private static final String ARG_QUOTE_POST_ID = "quote_post_id";

    /**
     * The serialization (saved instance state) Bundle key representing whether emoticon
     * keyboard is showing when configuration changes.
     */
    private static final String STATE_IS_EMOTICON_KEYBOARD_SHOWING = "is_emoticon_keyboard_showing";

    @Inject
    EventBus mEventBus;

    @Inject
    GeneralPreferencesManager mGeneralPreferencesManager;

    private String mThreadId;
    private String mQuotePostId;

    private FragmentReplyBinding mFragmentReplyBinding;
    private EditText mReplyView;

    private boolean mIsEmoticonKeyboardShowing;
    /**
     * {@code mMenuEmoticon} is null before {@link #onCreateOptionsMenu(Menu, MenuInflater)}.
     */
    @Nullable
    private MenuItem mMenuEmoticon;
    private View mEmoticonKeyboard;
    private final Interpolator mInterpolator = new FastOutSlowInInterpolator();

    /**
     * {@code mMenuSend} is null when configuration changes.
     */
    @Nullable
    private MenuItem mMenuSend;

    private Subscription mEventBusSubscription;

    public static ReplyFragment newInstance(String threadId, @Nullable String quotePostId) {
        ReplyFragment fragment = new ReplyFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_THREAD_ID, threadId);
        bundle.putString(ARG_QUOTE_POST_ID, quotePostId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentReplyBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reply, container,
                false);
        mReplyView = mFragmentReplyBinding.reply;
        mEmoticonKeyboard = mFragmentReplyBinding.emoticonKeyboard;
        return mFragmentReplyBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        App.getAppComponent(getActivity()).inject(this);

        mThreadId = getArguments().getString(ARG_THREAD_ID);
        mQuotePostId = getArguments().getString(ARG_QUOTE_POST_ID);

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
            mIsEmoticonKeyboardShowing = savedInstanceState.getBoolean(
                    STATE_IS_EMOTICON_KEYBOARD_SHOWING);
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

        mEventBusSubscription = mEventBus.get().subscribe(o -> {
            if (o instanceof EmoticonClickEvent) {
                mReplyView.getText().replace(mReplyView.getSelectionStart(),
                        mReplyView.getSelectionEnd(), ((EmoticonClickEvent) o).getEmoticonEntity());
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        mEventBusSubscription.unsubscribe();
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
                StringBuilder stringBuilder = new StringBuilder(mReplyView.getText());
                if (mGeneralPreferencesManager.isSignatureEnabled()) {
                    stringBuilder.append("\n\n").append(DeviceUtil.getSignature(getActivity()));
                }

                ReplyRequestDialogFragment.newInstance(mThreadId, mQuotePostId,
                        stringBuilder.toString()).show(getFragmentManager(),
                        ReplyRequestDialogFragment.TAG);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(STATE_IS_EMOTICON_KEYBOARD_SHOWING, mIsEmoticonKeyboardShowing);
    }

    private void setupEmoticonKeyboard() {
        ViewPager viewPager = mFragmentReplyBinding.emoticonKeyboardPager;
        viewPager.setAdapter(new EmoticonPagerAdapter(getActivity()));

        TabLayout tabLayout = mFragmentReplyBinding.emoticonKeyboardTabLayout;
        tabLayout.setupWithViewPager(viewPager);
    }

    private void showEmoticonKeyboard() {
        mIsEmoticonKeyboardShowing = true;

        // hide keyboard
        ViewUtil.setShowSoftInputOnFocus(mReplyView, false);
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mReplyView.getWindowToken(), 0);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mEmoticonKeyboard.setVisibility(View.VISIBLE);
        // translationYBy(-mEmoticonKeyboard.getHeight())
        // doesn't work when orientation change
        mEmoticonKeyboard.animate()
                .alpha(1)
                .translationYBy(-mEmoticonKeyboard.getTranslationY())
                .setInterpolator(mInterpolator)
                .setListener(new EmoticonKeyboardAnimator());

        setKeyboardIcon();
    }

    public void hideEmoticonKeyboard() {
        hideEmoticonKeyboard(false);
    }

    private void hideEmoticonKeyboard(boolean shouldShowKeyboard) {
        mEmoticonKeyboard.animate()
                .alpha(0)
                .translationYBy(mEmoticonKeyboard.getHeight())
                .setInterpolator(mInterpolator)
                .setListener(new EmoticonKeyboardAnimator() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mEmoticonKeyboard.setVisibility(View.GONE);

                        ViewUtil.setShowSoftInputOnFocus(mReplyView, true);
                        getActivity().getWindow().setSoftInputMode(
                                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

                        if (shouldShowKeyboard) {
                            InputMethodManager inputMethodManager = (InputMethodManager)
                                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.showSoftInput(mReplyView,
                                    InputMethodManager.SHOW_IMPLICIT);
                        }

                        super.onAnimationEnd(animation);
                    }
                });

        mIsEmoticonKeyboardShowing = false;
        setEmoticonIcon();
    }

    private void setEmoticonIcon() {
        if (mMenuEmoticon != null) {
            mMenuEmoticon.setIcon(ResourceUtil.getResourceId(getActivity().getTheme(),
                    R.attr.menuEmoticon));
            mMenuEmoticon.setTitle(R.string.menu_emoticon);
        }
    }

    private void setKeyboardIcon() {
        if (mMenuEmoticon != null) {
            mMenuEmoticon.setIcon(ResourceUtil.getResourceId(getActivity().getTheme(),
                    R.attr.menuKeyboard));
            mMenuEmoticon.setTitle(R.string.menu_keyboard);
        }
    }

    public boolean isEmoticonKeyboardShowing() {
        return mIsEmoticonKeyboardShowing;
    }

    public boolean isReplyEmpty() {
        return mReplyView == null || TextUtils.isEmpty(mReplyView.getText());
    }

    private class EmoticonKeyboardAnimator implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {
            if (mMenuEmoticon != null) {
                mMenuEmoticon.setEnabled(false);
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (mMenuEmoticon != null) {
                mMenuEmoticon.setEnabled(true);
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }
}
