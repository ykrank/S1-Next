package me.ykrank.s1next.view.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.transition.TransitionManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.data.api.ApiFlatTransformer;
import me.ykrank.s1next.data.api.S1Service;
import me.ykrank.s1next.data.api.UserValidator;
import me.ykrank.s1next.data.api.model.search.ForumSearchWrapper;
import me.ykrank.s1next.data.api.model.search.SearchResult;
import me.ykrank.s1next.data.api.model.search.UserSearchWrapper;
import me.ykrank.s1next.databinding.ActivitySearchBinding;
import me.ykrank.s1next.util.ImeUtils;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.RxJavaUtil;
import me.ykrank.s1next.util.TransitionUtils;
import me.ykrank.s1next.view.adapter.SearchRecyclerViewAdapter;
import me.ykrank.s1next.view.transition.CircularReveal;
import me.ykrank.s1next.view.transition.TransitionCompatCreator;
import me.ykrank.s1next.widget.track.event.SearchTrackEvent;

/**
 * Created by ykrank on 2016/9/28 0028.
 */

public class SearchActivity extends BaseActivity {
    public static final String TAG = BaseActivity.class.getName();

    @Inject
    UserValidator mUserValidator;
    @Inject
    User mUser;
    @Inject
    S1Service s1Service;

    private ActivitySearchBinding binding;

    private SearchView searchView;
    private RecyclerView recyclerView;
    private TextView noResults;
    private ImageButton searchBack;

    private SearchRecyclerViewAdapter adapter;

    private android.support.transition.Transition autoTransitionCompat;

    private Disposable mDisposable;

    public static void start(Context context) {
        context.startActivity(new Intent(context, SearchActivity.class));
    }

    public static void start(Activity activity, @NonNull View searchIconView) {
        Bundle options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, searchIconView,
                activity.getString(R.string.transition_search_back)).toBundle();
        ActivityCompat.startActivity(activity, new Intent(activity, SearchActivity.class), options);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.getPrefComponent().inject(this);
        super.onCreate(savedInstanceState);
        L.leaveMsg("SearchActivity");
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);

        searchView = binding.appBar.searchView;
        recyclerView = binding.searchResults;
        searchBack = binding.appBar.searchback;

        binding.appBar.toolbar.setNavigationIcon(null);
        setupWindowAnimations();
        setupTransitions();
        compatBackIcon();

        adapter = new SearchRecyclerViewAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        searchBack.setOnClickListener(v -> dismiss());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupSearchView();
    }

    @Override
    protected void onDestroy() {
        RxJavaUtil.disposeIfNotNull(mDisposable);
        super.onDestroy();
    }

    private void setupWindowAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition enterTransition = TransitionInflater.from(this).inflateTransition(R.transition.search_enter);
            getWindow().setEnterTransition(enterTransition);

            Transition returnTransition = TransitionInflater.from(this).inflateTransition(R.transition.search_return);
            getWindow().setReturnTransition(returnTransition);

            Transition enterShareTransition = TransitionInflater.from(this).inflateTransition(R.transition.search_shared_enter);
            getWindow().setSharedElementEnterTransition(enterShareTransition);

            Transition returnShareTransition = TransitionInflater.from(this).inflateTransition(R.transition.search_shared_return);
            getWindow().setSharedElementReturnTransition(returnShareTransition);
        }
    }

    private void setupTransitions() {
        // grab the position that the search icon transitions in *from*
        // & use it to configure the return transition
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setEnterSharedElementCallback(new SharedElementCallback() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onSharedElementStart(
                        List<String> sharedElementNames,
                        List<View> sharedElements,
                        List<View> sharedElementSnapshots) {
                    if (sharedElements != null && !sharedElements.isEmpty()) {
                        View searchIcon = sharedElements.get(0);
                        if (searchIcon.getId() != R.id.searchback) return;
                        int centerX = (searchIcon.getLeft() + searchIcon.getRight()) / 2;
                        CircularReveal hideResults = (CircularReveal) TransitionUtils.findTransition(
                                (TransitionSet) getWindow().getReturnTransition(),
                                CircularReveal.class, R.id.results_container);
                        if (hideResults != null) {
                            hideResults.setCenter(new Point(centerX, 0));
                        }
                    }
                }
            });
            // focus the search view once the transition finishes
            getWindow().getEnterTransition().addListener(
                    new TransitionUtils.TransitionListenerAdapter() {
                        @Override
                        public void onTransitionEnd(Transition transition) {
                            searchView.requestFocus();
                            ImeUtils.showIme(searchView);
                        }
                    });
        } else {
            searchView.requestFocus();
            ImeUtils.showIme(searchView);
        }
    }

    private void compatBackIcon() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            StateListDrawable drawable = new StateListDrawable();

            int[] attribute = new int[]{R.attr.colorPrimaryDark, R.attr.colorPrimary};
            TypedArray array = getTheme().obtainStyledAttributes(attribute);
            int colorPrimaryDark = array.getColor(0, Color.TRANSPARENT);
            int colorPrimary = array.getColor(1, Color.TRANSPARENT);
            array.recycle();

            drawable.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(colorPrimaryDark));
            drawable.addState(new int[]{-android.R.attr.state_pressed}, new ColorDrawable(colorPrimary));

            //noinspection deprecation
            ViewCompat.setBackground(binding.appBar.searchback, drawable);
        }
    }

    private void setupSearchView() {
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        // hint, inputType & ime options seem to be ignored from XML! Set in code
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            searchView.setImeOptions(searchView.getImeOptions() | EditorInfo.IME_ACTION_SEARCH |
                    EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NO_FULLSCREEN);
        } else {
            searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH |
                    EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NO_FULLSCREEN);
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchFor(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (TextUtils.isEmpty(query)) {
                    clearResults(true);
                }
                return true;
            }
        });
    }

    private void clearResults(boolean transition) {
        if (transition) {
            TransitionManager.beginDelayedTransition(binding.coordinatorLayout, getAutoTransition());
        }

        recyclerView.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.GONE);
        binding.resultsScrim.setVisibility(View.GONE);
        setNoResultsVisibility(View.GONE, null);
    }

    private void setResults(List<? extends SearchResult> data, @Nullable String errorMsg) {
        if (data != null && data.size() > 0) {
            if (recyclerView.getVisibility() != View.VISIBLE) {
                TransitionManager.beginDelayedTransition(binding.resultsContainer,
                        getAutoTransition());
                binding.progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
            adapter.diffNewDataSet(data, true);
        } else {
            TransitionManager.beginDelayedTransition(
                    binding.resultsContainer, getAutoTransition());
            binding.progressBar.setVisibility(View.GONE);
            setNoResultsVisibility(View.VISIBLE, errorMsg);
        }
    }

    private void setNoResultsVisibility(int visibility, @Nullable String errorMsg) {
        if (visibility == View.VISIBLE) {
            if (noResults == null) {

                noResults = (TextView) binding.stubNoSearchResults.getViewStub().inflate();
                noResults.setOnClickListener(v -> {
                    searchView.setQuery("", false);
                    searchView.requestFocus();
                    ImeUtils.showIme(searchView);
                });
            }
            if (TextUtils.isEmpty(errorMsg)) {
                String message = String.format(
                        getString(R.string.no_search_results), searchView.getQuery().toString());
                SpannableStringBuilder ssb = new SpannableStringBuilder(message);
                ssb.setSpan(new StyleSpan(Typeface.ITALIC),
                        message.indexOf('“') + 1,
                        message.length() - 1,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                noResults.setText(ssb);
            } else {
                noResults.setText(errorMsg);
            }
        }
        if (noResults != null) {
            noResults.setVisibility(visibility);
        }
    }

    private void searchFor(String query) {
        trackAgent.post(new SearchTrackEvent(query));

        clearResults(false);
        binding.progressBar.setVisibility(View.VISIBLE);
        ImeUtils.hideIme(searchView);
        searchView.clearFocus();
//        dataManager.searchFor(query);

        String selected = (String) binding.appBar.spinner.getSelectedItem();
        if (TextUtils.equals(getString(R.string.search_type_entry_user), selected)) {
            mDisposable = ApiFlatTransformer.flatMappedWithAuthenticityToken(s1Service, mUserValidator, mUser,
                    token -> s1Service.searchUser(token, query))
                    .map(UserSearchWrapper::fromSource)
                    .compose(RxJavaUtil.iOTransformer())
                    .subscribe(wrapper -> setResults(wrapper.getUserSearchResults(), wrapper.getErrorMsg()), L::e);
        } else {
            mDisposable = ApiFlatTransformer.flatMappedWithAuthenticityToken(s1Service, mUserValidator, mUser,
                    token -> s1Service.searchForum(token, query))
                    .map(ForumSearchWrapper::fromSource)
                    .compose(RxJavaUtil.iOTransformer())
                    .subscribe(wrapper -> setResults(wrapper.getForumSearchResults(), null), L::e);
        }
    }

    private android.support.transition.Transition getAutoTransition() {
        if (autoTransitionCompat == null) {
            autoTransitionCompat = TransitionCompatCreator.getAutoTransition();
        }
        return autoTransitionCompat;
    }

    private void dismiss() {
        // clear the background else the touch ripple moves with the translation which looks bad
        ViewCompat.setBackground(searchBack, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }
    }
}
