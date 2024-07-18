package me.ykrank.s1next.data.api

object DiscuzMessageFormatter {
    private const val FULLWIDTH_COMMA = "，"
    private const val FULLWIDTH_FULL_STOP = "。"

    /**
     * We need to add a full stop after a non-single sentence.
     *
     *
     * see https://www.google.com/design/spec/style/writing.html#writing-capitalization-punctuation
     * Periods: Omit for fragments and single sentences in affordances like toasts,
     * snackbars, butterbars, and labels. Do punctuate full sentences in body text,
     * for example, in the body of dialogs.
     *
     *
     * see https://github.com/Discuz-X/DiscuzX/blob/master/source/language/lang_message.php
     */
    fun addFullStopIfNeeded(text: String?): String? {
        if (text.isNullOrEmpty()) {
            return text
        }
        return if (text.contains(FULLWIDTH_COMMA) && !text.endsWith(
                FULLWIDTH_FULL_STOP
            )
        ) {
            text + FULLWIDTH_FULL_STOP
        } else text
    }
}
