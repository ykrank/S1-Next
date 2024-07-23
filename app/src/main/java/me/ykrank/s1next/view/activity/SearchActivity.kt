package me.ykrank.s1next.view.activity

import android.app.SearchManager
import android.app.SharedElementCallback
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.StyleSpan
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionSet
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.transition.TransitionManager
import com.github.ykrank.androidautodispose.AndroidRxDispose
import com.github.ykrank.androidlifecycle.event.ActivityEvent
import com.github.ykrank.androidtools.util.ImeUtils
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.RxJavaUtil
import com.github.ykrank.androidtools.util.TransitionUtils
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.ApiFlatTransformer
import me.ykrank.s1next.data.api.S1Service
import me.ykrank.s1next.data.api.UserValidator
import me.ykrank.s1next.data.api.model.search.ForumSearchWrapper
import me.ykrank.s1next.data.api.model.search.SearchResult
import me.ykrank.s1next.data.api.model.search.UserSearchWrapper
import me.ykrank.s1next.databinding.ActivitySearchBinding
import me.ykrank.s1next.databinding.AppBarSearchBinding
import me.ykrank.s1next.util.ErrorUtil
import me.ykrank.s1next.view.adapter.SearchRecyclerViewAdapter
import me.ykrank.s1next.view.dialog.LoginPromptDialogFragment
import me.ykrank.s1next.view.transition.CircularReveal
import me.ykrank.s1next.view.transition.TransitionCompatCreator
import me.ykrank.s1next.widget.track.event.SearchTrackEvent
import javax.inject.Inject

/**
 * Created by ykrank on 2016/9/28 0028.
 *
 * TODO Remove debug message for track error
 */
class SearchActivity : BaseActivity() {

    @Inject
    lateinit var mUserValidator: UserValidator

    @Inject
    lateinit var s1Service: S1Service

    private lateinit var binding: ActivitySearchBinding

    private lateinit var searchView: SearchView
    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var searchBack: ImageButton
    private lateinit var appBar: AppBarSearchBinding
    private var noResults: TextView? = null

    private lateinit var adapter: SearchRecyclerViewAdapter

    private val autoTransitionCompat by lazy { TransitionCompatCreator.getAutoTransition() }

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)

        appBar = binding.appBar
        searchView = appBar.searchView
        recyclerView = binding.searchResults
        searchBack = appBar.searchback

        appBar.toolbar.navigationIcon = null
        setupWindowAnimations()
        setupTransitions()
        compatBackIcon()

        adapter = SearchRecyclerViewAdapter(this)
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recyclerView.adapter = adapter

        searchBack.setOnClickListener {
            L.leaveMsg("SearchBack click")
            dismiss()
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setupSearchView()
    }

    override fun onResume() {
        super.onResume()
        L.leaveMsg("SearchActivity onResume")
    }

    override fun onPause() {
        super.onPause()
        L.leaveMsg("SearchActivity onPause")
    }

    override fun onBackPressed() {
        L.leaveMsg("SearchActivity onBackPressed")
        super.onBackPressed()
    }

    override fun onDestroy() {
        L.leaveMsg("SearchActivity onDestroy")
        super.onDestroy()
    }

    private fun setupWindowAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val enterTransition =
                TransitionInflater.from(this).inflateTransition(R.transition.search_enter)
            window.enterTransition = enterTransition

            val returnTransition =
                TransitionInflater.from(this).inflateTransition(R.transition.search_return)
            window.returnTransition = returnTransition

            val enterShareTransition =
                TransitionInflater.from(this).inflateTransition(R.transition.search_shared_enter)
            window.sharedElementEnterTransition = enterShareTransition

            val returnShareTransition =
                TransitionInflater.from(this).inflateTransition(R.transition.search_shared_return)
            window.sharedElementReturnTransition = returnShareTransition
        }
    }

    private fun setupTransitions() {
        // grab the position that the search icon transitions in *from*
        // & use it to configure the return transition
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setEnterSharedElementCallback(object : SharedElementCallback() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                override fun onSharedElementStart(
                    sharedElementNames: List<String>,
                    sharedElements: List<View>?,
                    sharedElementSnapshots: List<View>
                ) {
                    if (sharedElements != null && !sharedElements.isEmpty()) {
                        val searchIcon = sharedElements[0]
                        if (searchIcon.id != R.id.searchback) return
                        val centerX = (searchIcon.left + searchIcon.right) / 2
                        val hideResults = TransitionUtils.findTransition(
                            window.returnTransition as TransitionSet,
                            CircularReveal::class.java, R.id.results_container
                        ) as CircularReveal?
                        hideResults?.setCenter(Point(centerX, 0))
                    }
                }
            })
            // focus the search view once the transition finishes
            window.enterTransition.addListener(
                object : TransitionUtils.TransitionListenerAdapter() {
                    override fun onTransitionEnd(transition: Transition) {
                        searchView.requestFocus()
                        ImeUtils.showIme(searchView)
                    }
                })
        } else {
            searchView.requestFocus()
            ImeUtils.showIme(searchView)
        }
    }

    private fun compatBackIcon() {
    }

    private fun setupSearchView() {
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        // hint, inputType & ime options seem to be ignored from XML! Set in code
        searchView.queryHint = getString(R.string.search_hint)
        searchView.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            searchView.imeOptions = searchView.imeOptions or EditorInfo.IME_ACTION_SEARCH or
                    EditorInfo.IME_FLAG_NO_EXTRACT_UI or EditorInfo.IME_FLAG_NO_FULLSCREEN
        } else {
            searchView.imeOptions = EditorInfo.IME_ACTION_SEARCH or
                    EditorInfo.IME_FLAG_NO_EXTRACT_UI or EditorInfo.IME_FLAG_NO_FULLSCREEN
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                L.leaveMsg("onQueryTextSubmit:$query")
                if (query != null) {
                    searchFor(query)
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                return false
            }
        })
    }

    private fun clearResults(transition: Boolean) {
        if (transition) {
            TransitionManager.beginDelayedTransition(
                binding.coordinatorLayout,
                autoTransitionCompat
            )
        }

        recyclerView.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.resultsScrim.visibility = View.GONE
        setNoResultsVisibility(View.GONE, null)
    }

    private fun setResults(data: List<SearchResult>?, errorMsg: String?) {
        L.leaveMsg("setResults error:$errorMsg")
        if (data != null && data.isNotEmpty()) {
            if (recyclerView.visibility != View.VISIBLE) {
                TransitionManager.beginDelayedTransition(
                    binding.resultsContainer,
                    autoTransitionCompat
                )
                binding.progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
            adapter.swapDataSet(data)
        } else {
            TransitionManager.beginDelayedTransition(binding.resultsContainer, autoTransitionCompat)
            binding.progressBar.visibility = View.GONE
            setNoResultsVisibility(View.VISIBLE, errorMsg)
        }
    }

    private fun setNoResultsVisibility(visibility: Int, errorMsg: String?) {
        if (visibility == View.VISIBLE) {
            if (noResults == null) {
                noResults = binding.stubNoSearchResults.viewStub?.inflate() as TextView?
                noResults?.setOnClickListener {
                    searchView.setQuery("", false)
                    searchView.requestFocus()
                    ImeUtils.showIme(searchView)
                }
            }
            if (TextUtils.isEmpty(errorMsg)) {
                val message = String.format(
                    getString(R.string.no_search_results), searchView.query.toString()
                )
                val ssb = SpannableStringBuilder(message)
                ssb.setSpan(
                    StyleSpan(Typeface.ITALIC),
                    message.indexOf('“') + 1,
                    message.length - 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                noResults?.text = ssb
            } else {
                noResults?.text = errorMsg
            }
        }
        if (noResults != null) {
            noResults?.visibility = visibility
        }
    }

    private fun searchFor(query: String) {
        trackAgent.post(SearchTrackEvent(query))

        clearResults(false)
        binding.progressBar.visibility = View.VISIBLE
        ImeUtils.hideIme(searchView)
        searchView.clearFocus()
        //        dataManager.searchFor(query);

        val selected = appBar.spinner.selectedItem as String
        if (TextUtils.equals(getString(R.string.search_type_entry_user), selected)) {
            ApiFlatTransformer.flatMappedWithAuthenticityToken(
                s1Service,
                mUserValidator,
                mUser
            ) { token -> s1Service.searchUser(token, query) }
                .map { UserSearchWrapper.fromSource(it) }
                .compose(RxJavaUtil.iOSingleTransformer())
                .to(AndroidRxDispose.withSingle(this, ActivityEvent.DESTROY))
                .subscribe({ setResults(it.userSearchResults, it.errorMsg) }, {
                    setResults(null, ErrorUtil.parse(this, it))
                })
        } else {
            ApiFlatTransformer.flatMappedWithAuthenticityToken(
                s1Service,
                mUserValidator,
                mUser
            ) { token -> s1Service.searchForum(token, query) }
                .map { ForumSearchWrapper.fromSource(it) }
                .compose(RxJavaUtil.iOSingleTransformer())
                .to(AndroidRxDispose.withSingle(this, ActivityEvent.DESTROY))
                .subscribe({ setResults(it.forumSearchResults, null) }, {
                    setResults(null, ErrorUtil.parse(this, it))
                })
        }
    }

    private fun dismiss() {
        // clear the background else the touch ripple moves with the translation which looks bad
        ViewCompat.setBackground(searchBack, null)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition()
        } else {
            finish()
        }
    }

    companion object {
        val TAG = BaseActivity::class.java.simpleName

        fun start(activity: FragmentActivity, searchIconView: View) {
            if (LoginPromptDialogFragment.showLoginPromptDialogIfNeeded(
                    activity.supportFragmentManager,
                    App.appComponent.user
                )
            ) {
                return
            }
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity, searchIconView,
                activity.getString(R.string.transition_search_back)
            ).toBundle()
            ActivityCompat.startActivity(
                activity,
                Intent(activity, SearchActivity::class.java),
                options
            )
        }
    }
}
