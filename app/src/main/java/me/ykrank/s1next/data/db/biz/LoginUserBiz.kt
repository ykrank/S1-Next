package me.ykrank.s1next.data.db.biz

import androidx.annotation.WorkerThread
import me.ykrank.s1next.App
import me.ykrank.s1next.data.db.AppDatabase
import me.ykrank.s1next.data.db.AppDatabaseManager
import me.ykrank.s1next.data.db.dbmodel.LoginUser
import me.ykrank.s1next.data.db.exmodel.RealLoginUser
import me.ykrank.s1next.widget.encrypt.Encryption
import java.security.GeneralSecurityException

class LoginUserBiz(private val manager: AppDatabaseManager, private val encryption: Encryption) {

    private val loginUserDao
        get() = session.loginUser()

    private val session: AppDatabase
        get() = manager.getOrBuildDb()

    @WorkerThread
    private fun encryptUser(user: RealLoginUser): LoginUser {
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

    @WorkerThread
    @Throws(GeneralSecurityException::class)
    private fun decryptUser(user: LoginUser): RealLoginUser {
        return RealLoginUser(
            id = user.id,
            uid = user.uid,
            name = user.name,
            password = user.encryptPassword?.let {
                encryption.decryptText(it)
            },
            questionId = user.questionId,
            answer = user.encryptAnswer?.let {
                encryption.decryptText(it)
            },
            loginTime = user.loginTime,
            timestamp = user.timestamp,
        )
    }

    @WorkerThread
    fun getEncryptUserList(): List<LoginUser> {
        return loginUserDao.loadAll()
    }

    @WorkerThread
    fun getDecryptUserList(): List<RealLoginUser> {
        return loginUserDao.loadAll().mapNotNull {
            try {
                decryptUser(it)
            } catch (e: Exception) {
                null
            }
        }
    }

    @WorkerThread
    fun getUserByUid(uid: Int): RealLoginUser? {
        return loginUserDao.getByUid(uid)?.let {
            try {
                decryptUser(it)
            } catch (e: Exception) {
                null
            }
        }
    }

    @WorkerThread
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