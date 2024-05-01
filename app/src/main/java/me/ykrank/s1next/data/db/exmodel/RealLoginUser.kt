package me.ykrank.s1next.data.db.exmodel

class RealLoginUser {

    constructor()
    constructor(
        id: Long?,
        uid: Int,
        name: String?,
        password: String?,
        questionId: String?,
        answer: String?,
        loginTime: Long,
        timestamp: Long
    ) {
        this.id = id
        this.uid = uid
        this.name = name
        this.password = password
        this.questionId = questionId
        this.answer = answer
        this.loginTime = loginTime
        this.timestamp = timestamp
    }


    var id: Long? = null
    var uid = 0
    var name: String? = null
    var password: String? = null
    var questionId: String? = null
    var answer: String? = null
    var loginTime: Long = 0
    var timestamp: Long = 0

    val invalid: Boolean
        get() {
            return password.isNullOrEmpty()
        }
}