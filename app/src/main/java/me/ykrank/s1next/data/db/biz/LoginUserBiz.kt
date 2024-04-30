package me.ykrank.s1next.data.db.biz

import me.ykrank.s1next.App
import me.ykrank.s1next.data.db.AppDatabase
import me.ykrank.s1next.data.db.AppDatabaseManager
import me.ykrank.s1next.data.db.dbmodel.LoginUser
import me.ykrank.s1next.data.db.exmodel.RealLoginUser
import me.ykrank.s1next.widget.encrypt.Encryption

class LoginUserBiz(private val manager: AppDatabaseManager, private val encryption: Encryption) {

    private val loginUserDao
        get() = session.loginUser()

    private val session: AppDatabase
        get() = manager.getOrBuildDb()

    fun encryptUser(user: RealLoginUser): LoginUser {
        return LoginUser(
            id = user.id,
            uid = user.uid,
            name = user.name,
            encryptPassword = user.password?.let { encryption.encryptText(it) },
            questionId = user.questionId,
            encryptAnswer = user.answer?.let { encryption.encryptText(it) },
            loginTime = user.loginTime,
            timestamp = user.timestamp,
        )
    }

    fun decryptUser(user: LoginUser): RealLoginUser {
        return RealLoginUser(
            id = user.id,
            uid = user.uid,
            name = user.name,
            password = user.encryptPassword?.let {
                try {
                    return@let encryption.decryptText(it)
                } catch (e: Exception) {
                    return@let null
                }
            },
            questionId = user.questionId,
            answer = user.encryptAnswer?.let {
                try {
                    return@let encryption.decryptText(it)
                } catch (e: Exception) {
                    return@let null
                }
            },
            loginTime = user.loginTime,
            timestamp = user.timestamp,
        )
    }

    fun getEncryptUserList(): List<LoginUser> {
        return loginUserDao.loadAll()
    }

    fun getDecryptUserList(): List<RealLoginUser> {
        return loginUserDao.loadAll().map {
            decryptUser(it)
        }
    }

    fun saveUser(loginUser: RealLoginUser) {
        val encryptLoginUser = encryptUser(loginUser)
        val oldUser = loginUserDao.getByUid(loginUser.uid)
        if (oldUser == null) {
            loginUserDao.insert(encryptLoginUser)
        } else {
            oldUser.mergeFrom(encryptLoginUser)
            loginUserDao.update(oldUser)
        }
    }

    companion object {

        val instance: LoginUserBiz
            get() = App.appComponent.loginUserBiz
    }
}