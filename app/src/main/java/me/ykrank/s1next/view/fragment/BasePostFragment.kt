package me.ykrank.s1next.view.fragment

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.UiThread
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPropertyAnimatorListener
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import com.github.ykrank.androidautodispose.AndroidRxDispose
import com.github.ykrank.androidlifecycle.event.FragmentEvent
import com.github.ykrank.androidtools.util.ImeUtils
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.ResourceUtil
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
import me.ykrank.s1next.view.adapter.EmoticonPagerAdapter
import me.ykrank.s1next.view.event.EmoticonClickEvent
import me.ykrank.s1next.view.event.RequestDialogSuccessEvent
import javax.inject.Inject

/**
 * Created by ykrank on 2016/7/31 0031.
 */
abstract class BasePostFragment : BaseFragment() {
    private val mInterpolator = FastOutSlowInInterpolator()
    protected lateinit var mFragmentPostBinding: FragmentPostBinding
    protected lateinit var mReplyView: EditText
    /**
     * `mMenuEmoticon` is null before [.onCreateOptionsMenu].
     */
    protected var mMenuEmoticon: MenuItem? = null
    protected lateinit var mEmoticonKeyboard: View
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
    internal var isEmoticonKeyboardShowing: Boolean = false
    private var mCacheDisposable: Disposable? = null
    private var requestDialogDisposable: Disposable? = null
    /**
     * whether already post
     */
    private var post = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mFragmentPostBinding = DataBindingUtil.inflate<FragmentPostBinding>(inflater, R.layout.fragment_post, container,
                false)
        mReplyView = mFragmentPostBinding.reply
        mEmoticonKeyboard = mFragmentPostBinding.emoticonKeyboard
        return mFragmentPostBinding.root
    }

    protected fun initCreateView(fragmentPostBinding: FragmentPostBinding) {
        mFragmentPostBinding = fragmentPostBinding
        mReplyView = mFragmentPostBinding.reply
        mEmoticonKeyboard = mFragmentPostBinding.emoticonKeyboard
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            isEmoticonKeyboardShowing = savedInstanceState.getBoolean(
                    STATE_IS_EMOTICON_KEYBOARD_SHOWING)
        }
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

        setupEmoticonKeyboard()

        if (savedInstanceState != null) {
            if (isEmoticonKeyboardShowing) {
                showEmoticonKeyboard()
            }
        }
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
                .to(AndroidRxDispose.withObservable<EmoticonClickEvent>(this, FragmentEvent.PAUSE))
                .subscribe({ event ->
                    mReplyView.text.replace(mReplyView.selectionStart,
                            mReplyView.selectionEnd, event.emoticonEntity)
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

        mMenuEmoticon = menu.findItem(R.id.menu_emoticon)
        if (isEmoticonKeyboardShowing) {
            setKeyboardIcon()
        }

        mMenuSend = menu.findItem(R.id.menu_send).setEnabled(!TextUtils.isEmpty(mReplyView.text))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_emoticon -> {
                if (isEmoticonKeyboardShowing) {
                    hideEmoticonKeyboard(true)
                } else {
                    showEmoticonKeyboard()
                }

                return true
            }
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState?.putBoolean(STATE_IS_EMOTICON_KEYBOARD_SHOWING, isEmoticonKeyboardShowing)
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

    private fun setupEmoticonKeyboard() {
        val viewPager = mFragmentPostBinding.emoticonKeyboardPager
        viewPager.adapter = EmoticonPagerAdapter(activity)

        val tabLayout = mFragmentPostBinding.emoticonKeyboardTabLayout
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun showEmoticonKeyboard() {
        isEmoticonKeyboardShowing = true

        // hide keyboard
        ImeUtils.setShowSoftInputOnFocus(mReplyView, false)
        ImeUtils.hideIme(mReplyView)
        activity!!.window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        mEmoticonKeyboard.visibility = View.VISIBLE
        // translationYBy(-mEmoticonKeyboard.getHeight())
        // doesn't work when orientation change
        ViewCompat.animate(mEmoticonKeyboard)
                .alpha(1f)
                .translationY(0f)
                .setInterpolator(mInterpolator)
                .withLayer()
                .setListener(EmoticonKeyboardAnimator())

        setKeyboardIcon()
    }

    fun hideEmoticonKeyboard() {
        hideEmoticonKeyboard(false)
    }

    private fun hideEmoticonKeyboard(shouldShowKeyboard: Boolean) {
        ViewCompat.animate(mEmoticonKeyboard)
                .alpha(0f)
                .translationY(mEmoticonKeyboard.height.toFloat())
                .setInterpolator(mInterpolator)
                .withLayer()
                .setListener(object : EmoticonKeyboardAnimator() {

                    override fun onAnimationEnd(view: View) {
                        mEmoticonKeyboard.visibility = View.GONE

                        ImeUtils.setShowSoftInputOnFocus(mReplyView, true)
                        activity!!.window.setSoftInputMode(
                                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

                        if (shouldShowKeyboard) {
                            ImeUtils.showIme(mReplyView)
                        }

                        super.onAnimationEnd(view)
                    }
                })

        isEmoticonKeyboardShowing = false
        setEmoticonIcon()
    }

    private fun setEmoticonIcon() {
        mMenuEmoticon?.setIcon(ResourceUtil.getResourceId(context!!.theme,
                R.attr.iconMenuEmoticon))
        mMenuEmoticon?.setTitle(R.string.menu_emoticon)
    }

    private fun setKeyboardIcon() {
        mMenuEmoticon?.setIcon(ResourceUtil.getResourceId(context!!.theme,
                R.attr.iconMenuKeyboard))
        mMenuEmoticon?.setTitle(R.string.menu_keyboard)
    }

    @CallSuper
    open fun isContentEmpty(): Boolean {
        return mReplyView.text.isNullOrBlank()
    }

    val content: String?
        get() {
            return mReplyView.text.toString()
        }

    private open inner class EmoticonKeyboardAnimator : ViewPropertyAnimatorListener {

        override fun onAnimationStart(view: View) {
            mMenuEmoticon?.isEnabled = false
        }

        override fun onAnimationEnd(view: View) {
            mMenuEmoticon?.isEnabled = true
        }

        override fun onAnimationCancel(view: View) {}
    }

    companion object {
        /**
         * The serialization (saved instance state) Bundle key representing whether emoticon
         * keyboard is showing when configuration changes.
         */
        private val STATE_IS_EMOTICON_KEYBOARD_SHOWING = "is_emoticon_keyboard_showing"
    }
}
