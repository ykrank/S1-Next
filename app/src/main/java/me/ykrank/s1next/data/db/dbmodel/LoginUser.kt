package me.ykrank.s1next.data.db.dbmodel

import android.text.TextUtils
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


/**
 * Post read history
 */
@Entity(
    tableName = "LoginUser",
)
class LoginUser {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Long? = null

    @ColumnInfo(name = "Uid")
    var uid = 0

    @ColumnInfo(name = "Name")
    var name: String? = null

    @ColumnInfo(name = "EncryptPassword")
    var encryptPassword: String? = null

    @ColumnInfo(name = "QuestionId")
    var questionId: String? = null

    @ColumnInfo(name = "EncryptAnswer")
    var encryptAnswer: String? = null

    @ColumnInfo(name = "LoginTime")
    var loginTime: Long = 0

    /**
     * 更新时间
     */
    @ColumnInfo(name = "Timestamp")
    var timestamp: Long = 0

    constructor()

    constructor(
        id: Long?,
        uid: Int,
        name: String?,
        encryptPassword: String?,
        questionId: String?,
        encryptAnswer: String?,
        loginTime: Long,
        timestamp: Long
    ) {
        this.id = id
        this.uid = uid
        this.name = name
        this.encryptPassword = encryptPassword
        this.questionId = questionId
        this.encryptAnswer = encryptAnswer
        this.loginTime = loginTime
        this.timestamp = timestamp
    }

    fun mergeFrom(other: LoginUser) {
        uid = other.uid
        name = other.name
        encryptPassword = other.encryptPassword
        questionId = other.questionId
        encryptAnswer = other.encryptAnswer
        loginTime = other.loginTime
        timestamp = other.timestamp
    }
}
