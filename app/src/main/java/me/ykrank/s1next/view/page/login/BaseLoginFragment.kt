package me.ykrank.s1next.view.page.login

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.github.ykrank.androidtools.util.ViewUtil
import me.ykrank.s1next.App.Companion.appComponent
import me.ykrank.s1next.R
import me.ykrank.s1next.data.api.Api
import me.ykrank.s1next.data.db.biz.LoginUserBiz
import me.ykrank.s1next.databinding.FragmentAppLoginBinding
import me.ykrank.s1next.util.IntentUtil
import me.ykrank.s1next.view.fragment.BaseFragment
import javax.inject.Inject

/**
 * A Fragment offers login via username and password.
 */
abstract class BaseLoginFragment : BaseFragment() {

    @Inject
    lateinit var mLoginUserBiz: LoginUserBiz

    private var mUsernameView: EditText? = null
    private var mPasswordView: EditText? = null
    private var mLoginButton: Button? = null
    private var binding: FragmentAppLoginBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        appComponent.inject(this)
        val binding = FragmentAppLoginBinding.inflate(inflater, container, false)
        this.binding = binding
        mUsernameView = binding.username
        mPasswordView = binding.password
        mLoginButton = binding.login

        initView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewUtil.consumeRunnableWhenImeActionPerformed(mPasswordView) { prepareLogin() }
        mLoginButton?.setOnClickListener { v: View? -> prepareLogin() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_login, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_account_new) {
            IntentUtil.startViewIntentExcludeOurApp(
                context, Uri.parse(
                    Api.URL_BROWSER_REGISTER
                )
            )
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initView() {
        val usersView = binding?.users ?: return
        // TODO 使用协程切到异步线程
        val users = mLoginUserBiz.getDecryptUserList()
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.choose_user)
            .setSingleChoiceItems(
                users.mapNotNull { it.name }.toTypedArray(),
                -1,
            ) { dialog: DialogInterface?, which: Int ->
                val user = users[which]
                if (user.invalid) {
                    showShortText(R.string.choose_user_invalid)
                } else {
                    dialog?.dismiss()

                    binding?.apply {
                        username.setText(user.name)
                        password.setText(user.password)
                        questionSpinner.setSelection(user.questionId?.toIntOrNull() ?: 0)
                        answer.setText(user.answer)
                    }
                }
            }
            .create()
        usersView.setOnClickListener {
            dialog.show()
        }
    }

    private fun prepareLogin() {
        // reset errors
        mUsernameView?.error = null
        mPasswordView?.error = null
        val username = mUsernameView?.getText()?.toString()
        val password = mPasswordView?.getText()?.toString()
        val questionId = binding?.questionSpinner?.selectedItemPosition
        val answer = binding?.answer?.getText()?.toString()
        var cancel = false
        var focusView: View? = null
        val error = getText(R.string.error_field_required)
        if (TextUtils.isEmpty(username)) {
            mUsernameView?.error = error
            cancel = true
            focusView = mUsernameView
        }
        if (TextUtils.isEmpty(password)) {
            mPasswordView?.error = error
            cancel = true
            if (focusView == null) {
                focusView = mPasswordView
            }
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {
            // start to log in
            if (username != null && password != null) {
                showLoginDialog(username, password, questionId, answer)
            }
        }
    }

    abstract fun showLoginDialog(
        username: String,
        password: String,
        questionId: Int?,
        answer: String?
    )

    companion object {
        @JvmField
        val TAG = BaseLoginFragment::class.java.getName()
    }
}
