package me.ykrank.s1next.view.fragment

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.UiThread
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import com.github.ykrank.androidautodispose.AndroidRxDispose
import com.github.ykrank.androidlifecycle.event.FragmentEvent
import com.github.ykrank.androidtools.util.ImeUtils
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.RxJavaUtil
import com.github.ykrank.androidtools.widget.EditorDiskCache
import com.github.ykrank.androidtools.widget.RxBus
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.pref.GeneralPreferencesManager
import me.ykrank.s1next.databinding.FragmentPostBinding
import me.ykrank.s1next.view.activity.BaseActivity
import me.ykrank.s1next.view.event.EmoticonClickEvent
import me.ykrank.s1next.view.event.PostAddImageEvent
import me.ykrank.s1next.view.event.RequestDialogSuccessEvent
import javax.inject.Inject

/**
 * Created by ykrank on 2016/7/31 0031.
 */
abstract class BasePostFragment : BaseFragment(), PostToolsExtrasFragment.PostToolsExtrasContextProvider {
    protected lateinit var mFragmentPostBinding: FragmentPostBinding
    protected lateinit var mReplyView: EditText

    /**
     * `mMenuSend` is null when configuration changes.
     */
    protected var mMenuSend: MenuItem? = null
    @Inject
    internal lateinit var mRxBus: RxBus
    @Inject
    internal lateinit var mGeneralPreferencesManager: GeneralPreferencesManager
    @Inject
    internal lateinit var editorDiskCache: EditorDiskCache
    internal var isToolsKeyboardShowing: Boolean = false
    private var mCacheDisposable: Disposable? = null
    private var requestDialogDisposable: Disposable? = null
    /**
     * whether already post
     */
    private var post = false

    private val toolsFragments: List<Fragment> by lazy {
        //TODO api=15的适配
        listOf(
                EmotionFragment.newInstance(), ImageUploadFragment.newInstance(), PostToolsExtrasFragment.newInstance()
        )
    }

    override val currentEditText: EditText
        get() = mFragmentPostBinding.reply

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mFragmentPostBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_post, container, false)
        mReplyView = mFragmentPostBinding.reply

        return mFragmentPostBinding.root
    }

    protected fun initCreateView(fragmentPostBinding: FragmentPostBinding) {
        mFragmentPostBinding = fragmentPostBinding
        mReplyView = mFragmentPostBinding.reply
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.appComponent.inject(this)

        mReplyView.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                // disable send menu if the content of reply is empty
                mMenuSend?.isEnabled = !TextUtils.isEmpty(s.toString())
            }
        })

        setupToolsKeyboard()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setHasOptionsMenu(true)
        bindRequestDialog()
    }

    override fun onResume() {
        super.onResume()

        mRxBus.get()
                .ofType(EmoticonClickEvent::class.java)
                .to(AndroidRxDispose.withObservable(this, FragmentEvent.PAUSE))
                .subscribe({ event ->
                    mReplyView.text.replace(mReplyView.selectionStart,
                            mReplyView.selectionEnd, event.emoticonEntity)
                })
        mRxBus.get()
                .ofType(PostAddImageEvent::class.java)
                .to(AndroidRxDispose.withObservable(this, FragmentEvent.PAUSE))
                .subscribe({ event ->
                    mReplyView.text.replace(mReplyView.selectionStart,
                            mReplyView.selectionEnd, "[img]${event.url}[/img]")
                })

        RxJavaUtil.disposeIfNotNull(mCacheDisposable)
        mCacheDisposable = null
        if (!TextUtils.isEmpty(cacheKey) && TextUtils.isEmpty(mReplyView.text)) {
            mCacheDisposable = resumeFromCache(Single.just(cacheKey)
                    .flatMap { key -> RxJavaUtil.neverNull(editorDiskCache.get(key)) })
        }
    }

    override fun onPause() {
        super.onPause()
        RxJavaUtil.disposeIfNotNull(mCacheDisposable)
        mCacheDisposable = null
        if (!post && !cacheKey.isNullOrEmpty() && !isContentEmpty()) {
            val cacheString = buildCacheString()
            val key = cacheKey
            if (!TextUtils.isEmpty(cacheString)) {
                mCacheDisposable = Single.just(cacheString)
                        .map { s ->
                            editorDiskCache.put(key, s)
                            s
                        }
                        .compose(RxJavaUtil.iOSingleTransformer<String>())
                        .subscribe(L::i, L::report)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_reply, menu)

        mMenuSend = menu.findItem(R.id.menu_send).setEnabled(!TextUtils.isEmpty(mReplyView.text))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_send -> return onMenuSendClick()
            else -> return super.onOptionsItemSelected(item)
        }
    }

    protected abstract fun onMenuSendClick(): Boolean

    /**
     * Key of EditorDiskCache cache. not save/restore if return null
     */
    abstract val cacheKey: String?

    abstract fun isRequestDialogAccept(event: RequestDialogSuccessEvent): Boolean

    /**
     * construct string should cached from view
     */
    @CallSuper
    open fun buildCacheString(): String? {
        return content
    }

    @UiThread
    open fun resumeFromCache(cache: Single<String>): Disposable? {
        return cache.compose(RxJavaUtil.iOSingleTransformer<String>())
                .subscribe({ mReplyView.setText(it) }, L::report)
    }

    private fun bindRequestDialog() {
        if (requestDialogDisposable == null) {
            requestDialogDisposable = mRxBus.get()
                    .filter { it is RequestDialogSuccessEvent && isRequestDialogAccept(it) }
                    .map { it as RequestDialogSuccessEvent }
                    .to(AndroidRxDispose.withObservable(this, FragmentEvent.DESTROY))
                    .subscribe({
                        post = true
                        editorDiskCache.remove(cacheKey)
                        BaseActivity.setResultMessage(activity!!, it.msg)
                        activity?.finish()
                    }, L::report)
        }
    }

    private fun setupToolsKeyboard() {
        val tabLayout = mFragmentPostBinding.tabLayoutPostTools
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                if (!isToolsKeyboardShowing) {
                    showHideToolsTab(tab, true)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                showHideToolsTab(tab, false)
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                showHideToolsTab(tab, true)
            }

        })
        childFragmentManager?.beginTransaction()?.also {
            val t = it
            toolsFragments.forEach {
                t.add(R.id.fragment_post_tools, it)
                t.hide(it)
            }
        }?.commit()

        //Check IME show or hide
//        view?.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
//            L.d("Bottom:$bottom, $oldBottom")
//        }
        view?.also {
            it.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                var imeShowHeight = -1
                var imeHideHeight = -1
                var oldHeight = -1

                override fun onGlobalLayout() {
                    val heightDiff = it.rootView.height - it.height
                    if (heightDiff != oldHeight) {
                        if (oldHeight == -1) {
                            //Not init
                            imeHideHeight = heightDiff
                        } else {
                            if (imeShowHeight == -1) {
                                //Not save show height
                                if (heightDiff > oldHeight) {
                                    imeShowHeight = heightDiff
                                } else {
                                    imeShowHeight = oldHeight
                                    imeHideHeight = heightDiff
                                }
                                afterImeHeightInit(imeShowHeight - imeHideHeight)
                            }
                            afterImeStateChanged(heightDiff > oldHeight)
                        }

                        oldHeight = heightDiff
                    }
                }
            })
        }
    }

    private fun afterImeHeightInit(height: Int) {
//        L.d("Ime init:$height")
        val layoutParams = mFragmentPostBinding.fragmentPostTools.layoutParams
        mFragmentPostBinding.fragmentPostTools.layoutParams = layoutParams.apply {
            //            L.d("fragmentPostTools height: ${this.height}")
            this.height = height
        }
    }

    private fun afterImeStateChanged(show: Boolean) {
//        L.d("Ime changed:$show")
        if (show) {
            hideToolsKeyboard()
        }
    }

    private fun showHideToolsTab(tab: TabLayout.Tab?, show: Boolean) {
        val pos = tab?.position ?: -1
        if (pos >= 0 && pos < toolsFragments.size) {
            if (show) {
                showToolsKeyboard()
                childFragmentManager.beginTransaction()?.show(toolsFragments[pos])?.commit()
            } else {
                childFragmentManager.beginTransaction()?.hide(toolsFragments[pos])?.commit()
            }
        } else {
            L.report(IllegalStateException("Illegal TabLayout pos: $pos, ${toolsFragments.size}"))
        }
    }

    private fun showToolsKeyboard() {
        isToolsKeyboardShowing = true
        ImeUtils.hideIme(mReplyView)
        mFragmentPostBinding.fragmentPostTools.visibility = View.VISIBLE
    }

    fun hideToolsKeyboard() {
        isToolsKeyboardShowing = false
        mFragmentPostBinding.fragmentPostTools.visibility = View.GONE
    }

    @CallSuper
    open fun isContentEmpty(): Boolean {
        return mReplyView.text.isNullOrBlank()
    }

    val content: String?
        get() {
            return mReplyView.text.toString()
        }

}
