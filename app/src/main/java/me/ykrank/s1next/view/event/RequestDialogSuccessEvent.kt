package me.ykrank.s1next.view.event

import me.ykrank.s1next.view.dialog.ProgressDialogFragment

/**
 * Request dialog run task success.
 */
class RequestDialogSuccessEvent(val dialogFragment: ProgressDialogFragment<*>, val msg: String?)