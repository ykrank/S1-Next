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
import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil
import cn.dreamtobe.kpswitch.util.KeyboardUtil
import cn.dreamtobe.kpswitch.widget.KPSwitchPanelFrameLayout
import com.github.ykrank.androidautodispose.AndroidRxDispose
import com.github.ykrank.androidlifecycle.event.FragmentEvent
import com.github.ykrank.androidtools.util.L
import com.github.ykrank.androidtools.util.RxJavaUtil
import com.github.ykrank.androidtools.widget.EditorDiskCache
import com.github.ykrank.androidtools.widget.RxBus
import com.github.ykrank.androidtools.widget.uploadimg.ModelImageUpload
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import me.ykrank.s1next.App
import me.ykrank.s1next.R
import me.ykrank.s1next.data.pref.GeneralPreferencesManager
import me.ykrank.s1next.databinding.FragmentPostBinding
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
    protected lateinit var mImePanelView: KPSwitchPanelFrameLayout

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
    private var mCacheDisposable: Disposable? = null
    private var requestDialogDisposable: Disposable? = null
    /**
     * whether already post
     */
    private var post = false

    private lateinit var toolsFragments: List<Pair<String, Fragment>>
    //Init onCreate
    private var toolsFirstInit = false

    private val addImages: HashSet<String> = hashSetOf()

    override val currentEditText: EditText
        get() = mFragmentPostBinding.reply

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            toolsFirstInit = false
            //Find tools fragment from childFragmentManager
            val fragments = listOf<Fragment>(
                    childFragmentManager.findFragmentByTag(EmotionFragment.TAG)!!,
                    childFragmentManager.findFragmentByTag(ImageUploadFragment.TAG)!!,
                    childFragmentManager.findFragmentByTag(PostToolsExtrasFragment.TAG)!!
            )
            toolsFragments = listOf(
                    Pair(EmotionFragment.TAG, fragments[0]),
                    Pair(ImageUploadFragment.TAG, fragments[1]),
                    Pair(PostToolsExtrasFragment.TAG, fragments[2])
            )
        } else {
            toolsFirstInit = true
            toolsFragments = listOf(
                    Pair(EmotionFragment.TAG, EmotionFragment.newInstance()),
                    Pair(ImageUploadFragment.TAG, ImageUploadFragment.newInstance()),
                    Pair(PostToolsExtrasFragment.TAG, PostToolsExtrasFragment.newInstance())
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        initCreateView(DataBindingUtil.inflate(inflater, R.layout.fragment_post, container, false))

        return mFragmentPostBinding.root
    }

    protected fun initCreateView(fragmentPostBinding: FragmentPostBinding) {
        mFragmentPostBinding = fragmentPostBinding
        mReplyView = mFragmentPostBinding.reply
        mImePanelView = mFragmentPostBinding.fragmentPostTools
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
                .subscribe { event ->
                    mReplyView.text.replace(mReplyView.selectionStart,
                            mReplyView.selectionEnd, event.emoticonEntity)
                }
        mRxBus.get()
                .ofType(PostAddImageEvent::class.java)
                .to(AndroidRxDispose.withObservable(this, FragmentEvent.PAUSE))
                .subscribe { event ->
                    addImages.add(event.url)
                    mReplyView.text.replace(mReplyView.selectionStart,
                            mReplyView.selectionEnd, "[img]${event.url}[/img]")
                }

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
            R.id.menu_send -> {
                //Check selected image added?
                for (image in selectImages) {
                    if (image.url.isNullOrEmpty()) {
                        showShortSnackbar("请先等待图片上传完成")
                        return false
                    }
                    if (!addImages.contains(image.url)) {
                        showShortSnackbar("点击上传完成的图片，才能插入到帖子中")
                        return false
                    }
                }
                return onMenuSendClick()
            }
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
                        showShortTextAndFinishCurrentActivity(it.msg)
                    }, L::report)
        }
    }

    private fun setupToolsKeyboard() {
        val tabLayout = mFragmentPostBinding.tabLayoutPostTools
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                if (!isToolsKeyboardShowing) {
                    showToolsTab(tab)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                showToolsTab(tab)
            }

        })

        //Add all tools fragment to childFragmentManager if first init, or it had add
        if (toolsFirstInit) {
            childFragmentManager.beginTransaction()?.also { t ->
                toolsFragments.forEach {
                    t.add(R.id.fragment_post_tools, it.second, it.first)
                    t.hide(it.second)
                }
            }?.commit()
        }

        KeyboardUtil.attach(activity, mImePanelView, { if (it) isToolsKeyboardShowing = false })
        KPSwitchConflictUtil.attach(mImePanelView, mReplyView)
    }

    internal var isToolsKeyboardShowing: Boolean = false

    private fun showToolsTab(tab: TabLayout.Tab?) {
        val pos = tab?.position ?: -1
        if (pos >= 0 && pos < toolsFragments.size) {
            childFragmentManager.beginTransaction()?.also { t ->
                toolsFragments.forEachIndexed { index, pair ->
                    if (index == pos) {
                        t.show(pair.second)
                    } else {
                        t.hide(pair.second)
                    }
                }
            }?.commit()
        } else {
            L.report(IllegalStateException("Illegal TabLayout pos: $pos, ${toolsFragments.size}"))
        }
        if (!isToolsKeyboardShowing) {
            showToolsKeyboard()
        }
    }

    private fun showToolsKeyboard() {
        isToolsKeyboardShowing = true
        KPSwitchConflictUtil.showPanel(mImePanelView)

    }

    fun hideToolsKeyboard() {
        isToolsKeyboardShowing = false
        KPSwitchConflictUtil.hidePanelAndKeyboard(mImePanelView)
    }

    @CallSuper
    open fun isContentEmpty(): Boolean {
        return mReplyView.text.isNullOrBlank()
    }

    val content: String?
        get() {
            return mReplyView.text.toString()
        }

    private val selectImages: List<ModelImageUpload>
        get() {
            return (toolsFragments[1].second as ImageUploadFragment).images
        }
}
