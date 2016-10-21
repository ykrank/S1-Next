package me.ykrank.s1next.view.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.TransitionRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.SparseArray;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;

import java.util.List;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.R;
import me.ykrank.s1next.data.User;
import me.ykrank.s1next.data.api.ApiFlatTransformer;
import me.ykrank.s1next.data.api.S1Service;
import me.ykrank.s1next.data.api.UserValidator;
import me.ykrank.s1next.data.api.model.Search;
import me.ykrank.s1next.data.api.model.wrapper.SearchWrapper;
import me.ykrank.s1next.databinding.ActivitySearchBinding;
import me.ykrank.s1next.util.ImeUtils;
import me.ykrank.s1next.util.L;
import me.ykrank.s1next.util.RxJavaUtil;
import me.ykrank.s1next.view.adapter.SearchRecyclerViewAdapter;

/**
 * Created by ykrank on 2016/9/28 0028.
 */

public class SearchActivity extends BaseActivity {
    @Inject
    S1Service mS1Service;
    @Inject
    UserValidator mUserValidator;
    @Inject
    User mUser;
    @Inject
    S1Service s1Service;
    
    private ActivitySearchBinding binding;
    
    private SearchView searchView;
    private RecyclerView recyclerView;

    private SparseArray<Transition> transitions = new SparseArray<>();
    
    private SearchWrapper searchWrapper;
    private SearchRecyclerViewAdapter adapter;

    public static void start(Context context){
        context.startActivity(new Intent(context, SearchActivity.class));
    }

    public static void start(Activity activity, @NonNull View searchIconView){
        Bundle options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, searchIconView,
                activity.getString(R.string.transition_search_back)).toBundle();
        ActivityCompat.startActivity(activity, new Intent(activity, SearchActivity.class), options);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.getAppComponent(this).inject(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        
        searchView = binding.appBar.searchView;
        recyclerView = binding.searchResults;
        
        binding.appBar.toolbar.setNavigationIcon(null);
        setupWindowAnimations();
        compatBackIcon();
        
        setupSearchView();
    }

    @Override
    public boolean isTranslucent() {
        return true;
    }

    @Override
    protected void onDestroy() {
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
    
    private void compatBackIcon(){
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
            binding.appBar.searchback.setBackgroundDrawable(drawable);
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
        }else {
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
                    clearResults();
                }
                return true;
            }
        });
    }

    private void clearResults() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(binding.coordinatorLayout, getTransition(R.transition.auto));
        }
//        adapter.clear();
//        dataManager.clear();
//        results.setVisibility(View.GONE);
//        progress.setVisibility(View.GONE);
//        fab.setVisibility(View.GONE);
//        confirmSaveContainer.setVisibility(View.GONE);
//        resultsScrim.setVisibility(View.GONE);
//        setNoResultsVisibility(View.GONE);
    }

    private void setResults(List<Search> data) {
        if (data != null && data.size() > 0) {
            if (recyclerView.getVisibility() != View.VISIBLE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(binding.resultsContainer,
                            getTransition(R.transition.auto));
                }
                binding.progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
            adapter.setDataSet(data);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(
                        binding.resultsContainer, getTransition(R.transition.auto));
            }
            binding.progressBar.setVisibility(View.GONE);
            setNoResultsVisibility(View.VISIBLE);
        }
    }

    private void searchFor(String query) {
        clearResults();
        binding.progressBar.setVisibility(View.VISIBLE);
        ImeUtils.hideIme(searchView);
        searchView.clearFocus();
//        dataManager.searchFor(query);

        s1Service.searchForum(mUser.getAuthenticityToken(), "yes", query)
                .compose(ApiFlatTransformer.AuthenticityTokenTransformer(mS1Service, mUserValidator))
                .compose(RxJavaUtil.iOTransformer())
                .subscribe(t->{
                    searchWrapper = SearchWrapper.fromSource(t);
                    setResults(searchWrapper.getSearches());
                }, L::e);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Transition getTransition(@TransitionRes int transitionId) {
        Transition transition = transitions.get(transitionId);
        if (transition == null) {
            transition = TransitionInflater.from(this).inflateTransition(transitionId);
            transitions.put(transitionId, transition);
        }
        return transition;
    }
}
