package me.ykrank.s1next.view.dialog.requestdialog

import android.os.Bundle
import me.ykrank.s1next.App
import me.ykrank.s1next.view.dialog.ProgressDialogFragment
import me.ykrank.s1next.view.event.RequestDialogSuccessEvent
import me.ykrank.s1next.widget.RxBus

/**
 * Dialog to send request.
 * Do before dialog dismiss
 */
abstract class BaseRequestDialogFragment<D> : ProgressDialogFragment<D>() {

    protected lateinit var rxBus: RxBus

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rxBus = App.getAppComponent().rxBus
    }

    protected fun onRequestSuccess(msg: String?) {
        rxBus.post(RequestDialogSuccessEvent(this, msg))
    }

    protected fun onRequestError(msg: String?) {
        showShortText(msg)
    }
}