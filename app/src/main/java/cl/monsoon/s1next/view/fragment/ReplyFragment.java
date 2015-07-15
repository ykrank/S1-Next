package cl.monsoon.s1next.view.fragment;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.util.List;

import cl.monsoon.s1next.Api;
import cl.monsoon.s1next.App;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.data.api.model.Quote;
import cl.monsoon.s1next.data.api.model.Result;
import cl.monsoon.s1next.data.api.model.mapper.ResultWrapper;
import cl.monsoon.s1next.data.pref.GeneralPreferencesManager;
import cl.monsoon.s1next.event.EmoticonClickEvent;
import cl.monsoon.s1next.singleton.BusProvider;
import cl.monsoon.s1next.util.DeviceUtil;
import cl.monsoon.s1next.util.ResourceUtil;
import cl.monsoon.s1next.util.ToastUtil;
import cl.monsoon.s1next.util.ViewUtil;
import cl.monsoon.s1next.view.adapter.EmoticonGridRecyclerAdapter;
import cl.monsoon.s1next.widget.AsyncResult;
import cl.monsoon.s1next.widget.EmoticonFactory;
import cl.monsoon.s1next.widget.HttpGetLoader;
import cl.monsoon.s1next.widget.HttpPostLoader;
import cl.monsoon.s1next.widget.ViewPagerTabs;

/**
 * Sends the reply via EditView.
 */
public final class ReplyFragment extends Fragment {

    public static final String TAG = ReplyFragment.class.getName();

    /**
     * The serialization (saved instance state) Bundle key representing the quote POJO.
     */
    private static final String STATE_QUOTE = "quote";

    /**
     * The serialization (saved instance state) Bundle key representing whether emoticon
     * keyboard is showing when configuration changes.
     */
    private static final String STATE_IS_EMOTICON_KEYBOARD_SHOWING = "is_emoticon_keyboard_showing";

    private static final String ARG_THREAD_ID = "thread_id";
    private static final String ARG_QUOTE_POST_ID = "quote_post_id";

    private static final String STATUS_REPLY_SUCCESS = "post_reply_succeed";

    private GeneralPreferencesManager mGeneralPreferencesManager;

    private String mThreadId;
    private String mQuotePostId;

    private Quote mQuote;

    /**
     * The reply we need to send.
     */
    private EditText mReplyView;

    private boolean mIsEmoticonKeyboardShowing;
    private MenuItem mMenuEmoticon;
    private View mEmoticonKeyboard;
    private ViewPagerTabs mEmoticonKeyboardCategoryTabs;
    private final Interpolator mInterpolator = new AccelerateDecelerateInterpolator();

    private MenuItem mMenuSend;

    public static ReplyFragment newInstance(String threadId, String quotePostId) {
        ReplyFragment fragment = new ReplyFragment();

        Bundle bundle = new Bundle();
        bundle.putString(ARG_THREAD_ID, threadId);
        bundle.putString(ARG_QUOTE_POST_ID, quotePostId);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mGeneralPreferencesManager = App.getAppComponent(activity).getGeneralPreferencesManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reply, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mThreadId = getArguments().getString(ARG_THREAD_ID);
        mQuotePostId = getArguments().getString(ARG_QUOTE_POST_ID);

        mReplyView = (EditText) view.findViewById(R.id.reply);
        mReplyView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mMenuSend == null) {
                    return;
                }

                // disable send menu if the content of reply is empty
                mMenuSend.setEnabled(!TextUtils.isEmpty(s.toString()));
            }
        });

        setupEmoticonKeyboard();

        if (savedInstanceState != null) {
            mQuote = savedInstanceState.getParcelable(STATE_QUOTE);

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

        BusProvider.get().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        BusProvider.get().unregister(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_reply, menu);

        mMenuEmoticon = menu.findItem(R.id.menu_emoticon);
        if (mIsEmoticonKeyboardShowing) {
            setKeyboardIcon();
        }

        mMenuSend = menu.findItem(R.id.menu_send);
        mMenuSend.setEnabled(!TextUtils.isEmpty(mReplyView.getText().toString()));
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
                    stringBuilder.append("\n\n").append(DeviceUtil.getSignature());
                }

                ReplyLoaderDialogFragment.newInstance(
                        mThreadId, mQuotePostId, mQuote, stringBuilder.toString()).show(
                        getChildFragmentManager(), ReplyLoaderDialogFragment.TAG);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(STATE_QUOTE, mQuote);
        outState.putBoolean(STATE_IS_EMOTICON_KEYBOARD_SHOWING, mIsEmoticonKeyboardShowing);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void appendEmoticonEntity(EmoticonClickEvent event) {
        mReplyView.getText().replace(mReplyView.getSelectionStart(), mReplyView.getSelectionEnd(),
                event.getEmoticonEntity());
    }

    private void setupEmoticonKeyboard() {
        //noinspection ConstantConditions
        mEmoticonKeyboard = getView().findViewById(R.id.emoticon_keyboard);
        ViewPager viewPager = (ViewPager) mEmoticonKeyboard.findViewById(
                R.id.emoticon_keyboard_pager);
        viewPager.setAdapter(new EmoticonPagerAdapter(getActivity()));
        viewPager.addOnPageChangeListener(new EmoticonKeyboardTabsPagerListener());

        mEmoticonKeyboardCategoryTabs = (ViewPagerTabs) mEmoticonKeyboard.findViewById(
                R.id.emoticon_keyboard_category_tabs);
        mEmoticonKeyboardCategoryTabs.setViewPager(viewPager);
    }

    private void showEmoticonKeyboard() {
        mIsEmoticonKeyboardShowing = true;

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
        mIsEmoticonKeyboardShowing = false;

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
        return mReplyView == null || TextUtils.isEmpty(mReplyView.getText().toString());
    }

    private class EmoticonKeyboardTabsPagerListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int state) {
            mEmoticonKeyboardCategoryTabs.onPageScrollStateChanged(state);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            mEmoticonKeyboardCategoryTabs.onPageScrolled(position, positionOffset,
                    positionOffsetPixels);
        }

        @Override
        public void onPageSelected(int position) {
            mEmoticonKeyboardCategoryTabs.onPageSelected(position);
        }
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

    private static class EmoticonPagerAdapter extends PagerAdapter {

        private final Context mContext;

        private final float mEmoticonWidth;
        private final int mEmoticonGridPadding;

        private final EmoticonFactory mEmoticonFactory;
        private final List<String> mEmoticonTypeTitles;

        public EmoticonPagerAdapter(Context context) {
            this.mContext = context;

            Resources resources = context.getResources();
            mEmoticonWidth = resources.getDimension(R.dimen.minimum_touch_target_size);
            mEmoticonGridPadding = resources.getDimensionPixelSize(R.dimen.emoticon_padding);

            mEmoticonFactory = new EmoticonFactory(context);
            mEmoticonTypeTitles = mEmoticonFactory.getEmotionTypeTitles();
        }

        @Override
        public int getCount() {
            return mEmoticonTypeTitles.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mEmoticonTypeTitles.get(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            RecyclerView recyclerView = new RecyclerView(mContext);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 1);
            recyclerView.setLayoutManager(gridLayoutManager);
            RecyclerView.Adapter recyclerAdapter = new EmoticonGridRecyclerAdapter(mContext,
                    mEmoticonFactory.getEmoticonsByIndex(position));
            recyclerView.setAdapter(recyclerAdapter);
            recyclerView.setHasFixedSize(true);
            recyclerView.setPadding(0, mEmoticonGridPadding, 0, mEmoticonGridPadding);
            recyclerView.setClipToPadding(false);

            // auto fit span
            recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {

                        @Override
                        @SuppressWarnings("deprecation")
                        public void onGlobalLayout() {
                            recyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            int measuredWidth = recyclerView.getMeasuredWidth();
                            int spanCount = (int) Math.floor(measuredWidth / mEmoticonWidth);
                            gridLayoutManager.setSpanCount(spanCount);
                            gridLayoutManager.requestLayout();
                        }
                    });

            container.addView(recyclerView);

            return recyclerView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    public static class ReplyLoaderDialogFragment extends LoaderDialogFragment {

        private static final String TAG = ReplyLoaderDialogFragment.class.getName();

        private static final String ARG_QUOTE = "quote";
        private static final String ARG_REPLY = "reply";

        private Quote mQuote;

        public static ReplyLoaderDialogFragment newInstance(String threadId, String quotePostId, Quote quote, String reply) {
            ReplyLoaderDialogFragment fragment = new ReplyLoaderDialogFragment();

            Bundle bundle = new Bundle();
            bundle.putString(ARG_THREAD_ID, threadId);
            bundle.putString(ARG_QUOTE_POST_ID, quotePostId);
            bundle.putParcelable(ARG_QUOTE, quote);
            bundle.putString(ARG_REPLY, reply);
            fragment.setArguments(bundle);

            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if (savedInstanceState == null) {
                mQuote = getArguments().getParcelable(ARG_QUOTE);
            } else {
                mQuote = savedInstanceState.getParcelable(STATE_QUOTE);
            }
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);

            outState.putParcelable(STATE_QUOTE, mQuote);
        }

        @Override
        protected CharSequence getProgressMessage() {
            return getText(R.string.dialog_progress_message_reply);
        }

        @Override
        @LoaderId
        protected int getStartLoaderId() {
            int loaderId;
            final boolean hasAuthenticityToken = !TextUtils.isEmpty(App.getAppComponent(
                    getActivity()).getUser().getAuthenticityToken());
            if (hasAuthenticityToken) {
                if (TextUtils.isEmpty(getArguments().getString(ARG_QUOTE_POST_ID))) {
                    loaderId = ID_LOADER_POST_REPLY;
                } else {
                    if (mQuote == null) {
                        // We need to get extra information for quote.
                        // see cl.monsoon.s1next.Api#URL_QUOTE_HELPER
                        loaderId = ID_LOADER_GET_QUOTE_EXTRA_INFO;
                    } else {
                        loaderId = ID_LOADER_POST_QUOTE;
                    }
                }
            } else {
                // We need to get authenticity token (formhash) if we haven't.
                // Then posts the rely.
                // see cl.monsoon.s1next.Api#URL_AUTHENTICITY_TOKEN_HELPER
                loaderId = ID_LOADER_GET_AUTHENTICITY_TOKEN;
            }

            return loaderId;
        }

        @Override
        public Loader onCreateLoader(@LoaderId int id, Bundle args) {
            if (id == ID_LOADER_GET_AUTHENTICITY_TOKEN) {
                return new HttpGetLoader<>(getActivity(), Api.URL_AUTHENTICITY_TOKEN_HELPER,
                        ResultWrapper.class);
            } else if (id == ID_LOADER_GET_QUOTE_EXTRA_INFO) {
                return new HttpGetLoader<>(getActivity(),
                        Api.getQuoteHelperUrl(getArguments().getString(ARG_THREAD_ID),
                                getArguments().getString(ARG_QUOTE_POST_ID)),
                        Quote.class);
            } else if (id == ID_LOADER_POST_REPLY) {
                return new HttpPostLoader<>(getActivity(),
                        Api.getPostRelyUrl(getArguments().getString(ARG_THREAD_ID)),
                        ResultWrapper.class,
                        Api.getReplyPostBuilder(getArguments().getString(ARG_REPLY)));
            } else if (id == ID_LOADER_POST_QUOTE) {
                return new HttpPostLoader<>(getActivity(),
                        Api.getPostRelyUrl(getArguments().getString(ARG_THREAD_ID)),
                        ResultWrapper.class,
                        Api.getQuotePostBuilder(mQuote, getArguments().getString(ARG_REPLY)));
            }

            return super.onCreateLoader(id, args);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onLoadFinished(Loader loader, Object data) {
            AsyncResult asyncResult = (AsyncResult) data;
            if (asyncResult.exception != null) {
                ToastUtil.showByResId(asyncResult.getExceptionStringRes(), Toast.LENGTH_SHORT);
            } else {
                int id = loader.getId();
                if (id == ID_LOADER_GET_AUTHENTICITY_TOKEN) {
                    if (TextUtils.isEmpty(getArguments().getString(ARG_QUOTE_POST_ID))) {
                        getLoaderManager().initLoader(ID_LOADER_POST_REPLY, null, this);
                    } else {
                        getLoaderManager().initLoader(ID_LOADER_GET_QUOTE_EXTRA_INFO, null, this);
                    }

                    return;
                } else if (id == ID_LOADER_GET_QUOTE_EXTRA_INFO) {
                    mQuote = (Quote) asyncResult.data;
                    ((ReplyFragment) getParentFragment()).mQuote = this.mQuote;

                    getLoaderManager().initLoader(ID_LOADER_POST_QUOTE, null, this);

                    return;
                } else if (id == ID_LOADER_POST_REPLY || id == ID_LOADER_POST_QUOTE) {
                    ResultWrapper wrapper = (ResultWrapper) asyncResult.data;
                    Result result = wrapper.getResult();

                    ToastUtil.showByText(result.getMessage(), Toast.LENGTH_LONG);

                    if (result.getStatus().equals(STATUS_REPLY_SUCCESS)) {
                        getActivity().finish();
                    }
                } else {
                    super.onLoadFinished(loader, asyncResult);
                }
            }

            new Handler().post(this::dismiss);
        }
    }
}
