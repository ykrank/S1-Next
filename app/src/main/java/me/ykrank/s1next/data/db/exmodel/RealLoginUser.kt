package me.ykrank.s1next.data.db.exmodel

data class RealLoginUser(
    var id: Long? = null,
    var uid: Int = 0,
    var name: String? = null,
    var password: String? = null,
    var questionId: String? = null,
    var answer: String? = null,
    var loginTime: Long = 0,
    var timestamp: Long = 0,
) {

    val invalid: Boolean
        get() {
            return password.isNullOrEmpty()
        }


    companion object {
        val EMPTY = RealLoginUser()
    }
}