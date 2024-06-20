package com.github.ykrank.androidtools.util

object StringUtils {
    const val SPACE = " "
    const val TWO_SPACES = "  "
    private const val NON_BREAKING_SPACE_ENTITY_NAME = "&nbsp;"

    /**
     * Concatenates `first` and `second` with [.TWO_SPACES].
     *
     *
     * <pre>
     * StringUtil.concatWithTwoSpaces("a", 1) = "a  1"
    </pre> *
     *
     * @return A new string which is the concatenation of this string, two spaces
     * and the specified string.
     */
    fun concatWithTwoSpaces(first: CharSequence?, last: Int): String {
        return concatWithTwoSpaces(first, last.toString())
    }

    /**
     * <pre>
     * StringUtil.concatWithTwoSpaces(1, "a") = "1  a"
    </pre> *
     *
     * @see .concatWithTwoSpaces
     */
    fun concatWithTwoSpaces(first: Int, last: CharSequence): String {
        return concatWithTwoSpaces(first.toString(), last)
    }

    /**
     * <pre>
     * StringUtil.concatWithTwoSpaces("a", "b") = "a  b"
    </pre> *
     *
     * @see .concatWithTwoSpaces
     */
    fun concatWithTwoSpaces(first: CharSequence?, last: CharSequence): String {
        return if (first == null) {
            last.toString()
        } else first.toString() + TWO_SPACES + last
    }

    /**
     * Replaces all occurrences of the [.NON_BREAKING_SPACE_ENTITY_NAME]
     * within the space.
     *
     * @param text The text to search and replace in.
     * @return The text with any replacements processed.
     */
    fun unescapeNonBreakingSpace(text: String?): String {
        return text?.replace(NON_BREAKING_SPACE_ENTITY_NAME, SPACE) ?: ""
    }

    /**
     * decode like \u8652
     */
    fun uniDecode(s: String): String {
        val sb = StringBuilder(s.length)
        val chars = s.toCharArray()
        var i = 0
        while (i < chars.size) {
            val c = chars[i]
            if (c == '\\') {
                if (i + 1 < chars.size && chars[i + 1] == '\\') {
                    sb.append("\\\\")
                    i += 2
                    continue
                } else if (i + 5 < chars.size && chars[i + 1] == 'u') {
                    var cc: Char = 0.toChar()
                    for (j in 2..5) {
                        val ch = chars[i + j].lowercaseChar()
                        if (ch in '0'..'9' || ch in 'a'..'f') {
                            cc = (cc.code shl 4 or Character.digit(ch, 16)).toChar()
                        } else {
                            cc = 0.toChar()
                            break
                        }
                    }
                    if (cc > 0.toChar()) {
                        sb.append(cc)
                        i += 6
                        continue
                    }
                }
            }
            sb.append(c)
            i++
        }
        return sb.toString()
    }

    /**
     *   StringUtils.abbreviate(null, *)      = null
     *   StringUtils.abbreviate("", 4)        = ""
     *   StringUtils.abbreviate("abcdefg", 6) = "abc..."
     *   StringUtils.abbreviate("abcdefg", 7) = "abcdefg"
     *   StringUtils.abbreviate("abcdefg", 8) = "abcdefg"
     *   StringUtils.abbreviate("abcdefg", 4) = "a..."
     *   StringUtils.abbreviate("abcdefg", 3) = IllegalArgumentException
     */
    fun abbreviate(str: String?, maxWidth: Int, suffix: String = "..."): String {
        if (str == null) return ""
        if (str.length <= maxWidth) {
            return str
        }
        return "${str.substring(0, (maxWidth - suffix.length))}$suffix"
    }
}
