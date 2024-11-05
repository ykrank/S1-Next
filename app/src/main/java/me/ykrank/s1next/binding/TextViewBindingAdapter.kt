package me.ykrank.s1next.binding

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.text.LineBreaker
import android.os.Build
import android.text.Html
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.format.DateUtils
import android.text.style.TextAppearanceSpan
import android.view.TouchDelegate
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.github.ykrank.androidtools.util.ResourceUtil
import com.github.ykrank.androidtools.util.ViewUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.ykrank.s1next.R
import me.ykrank.s1next.data.User
import me.ykrank.s1next.data.api.app.model.AppPost
import me.ykrank.s1next.data.api.model.Forum
import me.ykrank.s1next.data.api.model.HomeThread
import me.ykrank.s1next.data.api.model.PmGroup
import me.ykrank.s1next.data.api.model.Post
import me.ykrank.s1next.data.api.model.Thread
import me.ykrank.s1next.data.db.dbmodel.History
import me.ykrank.s1next.data.pref.ThemeManager
import me.ykrank.s1next.widget.span.GlideImageGetter
import me.ykrank.s1next.widget.span.HtmlCompat
import me.ykrank.s1next.widget.span.TagHandler
import me.ykrank.s1next.widget.span.replaceQuoteSpans
import okio.buffer
import okio.source
import java.io.IOException
import java.nio.charset.Charset

object TextViewBindingAdapter {
    private const val defaultTextColor = 0

    @JvmStatic
    @BindingAdapter("increaseClickingArea")
    fun increaseClickingArea(textView: TextView, size: Float) {
        // fork from http://stackoverflow.com/a/1343796
        val parent = textView.parent as View
        // post in the parent's message queue to make sure the parent
        // lays out its children before we call View#getHitRect()
        parent.post {
            val halfSize = (size / 2 + 0.5).toInt()
            val rect = Rect()
            textView.getHitRect(rect)
            rect.top -= halfSize
            rect.right += halfSize
            rect.bottom += halfSize
            rect.left -= halfSize
            // use TouchDelegate to increase count's clicking area
            parent.touchDelegate = TouchDelegate(rect, textView)
        }
    }

    @JvmStatic
    @BindingAdapter("underlineText")
    fun setUnderlineText(textView: TextView, text: String?) {
        textView.paintFlags = textView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        textView.paint.isAntiAlias = true
        textView.text = text
    }

    @JvmStatic
    @BindingAdapter("textPath")
    fun loadTextAsset(textView: TextView, textPath: String?) {
        try {
            if (textPath.isNullOrEmpty()) {
                return
            }
            val inputStream = textView.context.assets.open(textPath)
            val source = inputStream.source()
            val bufferedSource = source.buffer()
            textView.text = bufferedSource.readString(Charset.forName("utf-8"))
        } catch (e: IOException) {
            throw IllegalStateException("Can't find license.", e)
        }
    }

    @JvmStatic
    @BindingAdapter("forum", "gentleAccentColor")
    fun setForum(textView: TextView, forum: Forum, gentleAccentColor: Int) {
        textView.text = forum.name
        // add today's posts count to each forum
        if (forum.todayPosts != 0) {
            ViewUtil.concatWithTwoSpacesForRtlSupport(
                textView, forum.todayPosts.toString(), gentleAccentColor
            )
        }
    }

    @JvmStatic
    @BindingAdapter("themeManager", "forumId", "thread", "user")
    fun setThread(
        textView: TextView, themeManager: ThemeManager, forumId: String, thread: Thread, user: User
    ) {
        val builder = SpannableStringBuilder(thread.title)
        val hintSpan = TextAppearanceSpan(
            textView.context,
            com.github.ykrank.androidtools.R.style.TextAppearance_ThreadList_Title_Hint
        )
        if (thread.permission != 0) {
            val permSpan: Spannable = SpannableString(
                String.format(
                    "[%s%s]",
                    textView.context.getString(R.string.thread_permission_hint),
                    thread.permission
                )
            )
            permSpan.setSpan(hintSpan, 0, permSpan.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            // add thread's permission hint
            builder.append(permSpan)
        }
        if (thread.isHide) {
            val blacklistSpan: Spannable = SpannableString(
                String.format(
                    "[%s]", textView.context.getString(R.string.user_in_blacklist)
                )
            )
            blacklistSpan.setSpan(
                hintSpan, 0, blacklistSpan.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            // add thread's blacklist hint
            builder.append(blacklistSpan)
            textView.setTextColor(Color.GRAY)
        } else {
            textView.setTextColor(ResourceUtil.getTextColorPrimary(textView.context))
        }
        // disable TextView if user has no permission to access this thread
        val hasPermission = user.permission >= thread.permission
        textView.setEnabled(hasPermission)

        //add typename
        if (!TextUtils.isEmpty(thread.typeName)) {
            val typeSpan: Spannable = SpannableString(String.format("[%s] ", thread.typeName))
            typeSpan.setSpan(hintSpan, 0, typeSpan.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            builder.insert(0, typeSpan)
        } else if (("0" == thread.typeId || "344" == thread.typeId) && "75" == forumId && thread.displayOrder == 0) {
            //add 泥潭
            val typeSpan: Spannable = SpannableString("[泥潭] ")
            typeSpan.setSpan(hintSpan, 0, typeSpan.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            builder.insert(0, typeSpan)
        }
        textView.text = builder

        // add thread's replies count to each thread
        var repliesStr = thread.replies
        if (thread.reliesCount > 0 && thread.lastReplyCount > 0) {
            val addReplies = thread.reliesCount - thread.lastReplyCount
            repliesStr += if (addReplies >= 0) {
                " (+$addReplies)"
            } else {
                //Because cache need invalid
                " (-)"
            }
        }
        ViewUtil.concatWithTwoSpacesForRtlSupport(
            textView,
            repliesStr,
            if (hasPermission) themeManager.gentleAccentColor else themeManager.hintOrDisabledGentleAccentColor
        )
    }

    @JvmStatic
    @BindingAdapter("relativeDateTime")
    fun setRelativeDateTime(textView: TextView, datetime: Long) {
        textView.text = DateUtils.getRelativeDateTimeString(
            textView.context, datetime, DateUtils.MINUTE_IN_MILLIS, DateUtils.DAY_IN_MILLIS, 0
        )
    }

    @JvmStatic
    @BindingAdapter("lifecycleOwner", "reply")
    fun setReply(
        textView: TextView,
        oLifecycleOwner: LifecycleOwner?,
        oPost: AppPost?,
        lifecycleOwner: LifecycleOwner,
        post: AppPost?
    ) {
        if (oPost === post) {
            return
        }
        if (post == null) {
            textView.text = ""
            return
        }
        if (post.hide) {
            textView.text = ""
            var text = "[" + textView.context.getString(R.string.user_in_blacklist) + "]"
            if (!TextUtils.isEmpty(post.remark)) {
                text += "-[" + post.remark + "]"
            }
            // add reply's blacklist hint
            ViewUtil.concatWithTwoSpacesForRtlSupport(textView, text)
            return
        }
        var oMsg: String? = null
        if (oPost != null) {
            oMsg = oPost.message
        }
        setHtmlWithImage(textView, oLifecycleOwner, oMsg, lifecycleOwner, post.message)
    }

    @JvmStatic
    fun buildBlacklistContent(context: Context, @Post.HideFLag hide: Int, remark: String?): String {
        val textHide: String = if (hide == Post.HIDE_USER) {
            context.getString(R.string.user_in_blacklist)
        } else {
            context.getString(R.string.word_in_black_word)
        }
        var text = "[$textHide]"
        if (!remark.isNullOrEmpty()) {
            text += "-[$remark]"
        }
        return text
    }

    private fun concatBlacklist(textView: TextView, @Post.HideFLag hide: Int, remark: String?) {
        if (hide != Post.HIDE_NO) {
            textView.text = ""
            // add reply's blacklist hint
            ViewUtil.concatWithTwoSpacesForRtlSupport(
                textView,
                buildBlacklistContent(textView.context, hide, remark)
            )
        }
    }

    @JvmStatic
    @BindingAdapter("lifecycleOwner", "reply")
    fun setReply(
        textView: TextView,
        oLifecycleOwner: LifecycleOwner?,
        oPost: Post?,
        lifecycleOwner: LifecycleOwner,
        post: Post?
    ) {
        if (oPost === post) {
            return
        }
        if (post == null) {
            textView.text = ""
            return
        }
        if (post.hide != Post.HIDE_NO) {
            concatBlacklist(textView, post.hide, post.remark)
            return
        }
        var oReply: String? = null
        if (post.isTrade) {
            if (oPost != null) {
                oReply = oPost.extraHtml
            }
            setHtmlWithImage(textView, oLifecycleOwner, oReply, lifecycleOwner, post.extraHtml)
        } else {
            if (oPost != null) {
                oReply = oPost.reply
            }
            setHtmlWithImage(textView, oLifecycleOwner, oReply, lifecycleOwner, post.reply)
        }
    }

    @JvmStatic
    @SuppressLint("WrongConstant")
    @BindingAdapter("lifecycleOwner", "imgHtml")
    fun setHtmlWithImage(
        textView: TextView,
        oLifecycleOwner: LifecycleOwner?,
        oHtml: String?,
        lifecycleOwner: LifecycleOwner,
        html: String?
    ) {
        if (TextUtils.equals(oHtml, html)) {
            return
        }
        if (TextUtils.isEmpty(html)) {
            textView.text = null
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                textView.setBreakStrategy(LineBreaker.BREAK_STRATEGY_SIMPLE)
            } else {
                textView.setBreakStrategy(Layout.BREAK_STRATEGY_SIMPLE)
            }
            lifecycleOwner.lifecycleScope.launch {
                val glideImageGetter = GlideImageGetter[textView, lifecycleOwner]
                val span = withContext(Dispatchers.Default) {
                    HtmlCompat.fromHtml(html, glideImageGetter, TagHandler(textView))
                        .replaceQuoteSpans(textView.context)
                }
                textView.text = span
            }
        }
    }

    @JvmStatic
    @BindingAdapter("lifecycleOwner", "html")
    fun setHtml(textView: TextView, lifecycleOwner: LifecycleOwner, html: String?) {
        if (TextUtils.isEmpty(html)) {
            textView.text = null
        } else {
            lifecycleOwner.lifecycleScope.launch {
                val span = withContext(Dispatchers.Default) {
                    HtmlCompat.fromHtml(html).replaceQuoteSpans(textView.context)
                }
                textView.text = span
            }
        }
    }

    @JvmStatic
    @Suppress("deprecation")
    @BindingAdapter("pmAuthorNameDesc", "user")
    fun setPmAuthorNameDesc(textView: TextView, pmGroup: PmGroup, user: User) {
        val context = textView.context
        if (TextUtils.equals(pmGroup.lastAuthorid, user.uid)) {
            textView.text =
                Html.fromHtml(context.getString(R.string.pm_desc_to_other, pmGroup.toUsername))
        } else {
            textView.text =
                Html.fromHtml(context.getString(R.string.pm_desc_to_me, pmGroup.toUsername))
        }
    }

    @JvmStatic
    @BindingAdapter("homeThread")
    fun setHomeThread(textView: TextView, thread: HomeThread?) {
        if (thread == null) {
            textView.text = null
        } else {
            textView.text = thread.title
            ViewUtil.concatWithTwoSpacesForRtlSupport(textView, thread.forum, Color.GRAY)
        }
    }

    @JvmStatic
    @BindingAdapter("history")
    fun setHomeThread(textView: TextView, history: History?) {
        if (history == null) {
            textView.text = null
        } else {
            textView.text = history.title
        }
    }
}
