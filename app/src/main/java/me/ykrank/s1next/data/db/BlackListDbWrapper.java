package me.ykrank.s1next.data.db;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.List;

import io.reactivex.Single;
import me.ykrank.s1next.App;
import me.ykrank.s1next.data.db.dbmodel.BlackList;
import me.ykrank.s1next.data.db.dbmodel.BlackListDao;
import me.ykrank.s1next.data.db.dbmodel.BlackListDao.Properties;
import me.ykrank.s1next.data.db.dbmodel.DaoSession;

/**
 * 对黑名单数据库的操作包装
 * Created by AdminYkrank on 2016/2/23.
 */
public class BlackListDbWrapper {
    private final AppDaoSessionManager appDaoSessionManager;

    BlackListDbWrapper(AppDaoSessionManager appDaoSessionManager) {
        this.appDaoSessionManager = appDaoSessionManager;
    }

    public static BlackListDbWrapper getInstance() {
        return App.Companion.getAppComponent().getBlackListDbWrapper();
    }

    private BlackListDao getBlackListDao() {
        return getSession().getBlackListDao();
    }

    private DaoSession getSession() {
        return appDaoSessionManager.getDaoSession();
    }

    @NonNull
    public List<BlackList> getAllBlackList(int limit, int offset) {
        return getBlackListDao().queryBuilder()
                .limit(limit)
                .offset(offset)
                .list();
    }

    public Single<Cursor> getBlackListCursor() {
        return Single.just(getBlackListDao().queryBuilder())
                .map(builder -> builder.buildCursor().query());
    }

    @NonNull
    public BlackList fromCursor(@NonNull Cursor cursor) {
        return getBlackListDao().readEntity(cursor, 0);
    }

    /**
     * 根据用户id查找记录。有效的只保留一条
     *
     * @param id
     * @return
     */
    @Nullable
    public BlackList getBlackListWithAuthorId(int id) {
        if (id <= 0) {
            return null;
        }
        List<BlackList> results = getBlackListDao().queryBuilder()
                .where(Properties.AuthorId.eq(id))
                .list();

        if (results.isEmpty()) {
            return null;
        }

        BlackList result = null;
        for (int i = 0; i < results.size(); i++) {
            if (!TextUtils.isEmpty(results.get(i).getAuthor())) {
                result = results.remove(i);
                break;
            }
        }
        if (result == null) {
            result = results.remove(0);
        }

        if (!results.isEmpty()) {
            getBlackListDao().deleteInTx(results);
        }
        return result;
    }

    /**
     * 根据用户名查找记录。有效的只保留一条
     *
     * @param name
     * @return
     */
    @Nullable
    public BlackList getBlackListWithAuthorName(String name) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        List<BlackList> results = getBlackListDao().queryBuilder()
                .where(Properties.Author.eq(name))
                .list();
        if (results.isEmpty()) {
            return null;
        }

        BlackList result = null;
        for (int i = 0; i < results.size(); i++) {
            if (results.get(i).getAuthorId() > 0) {
                result = results.remove(i);
                break;
            }
        }
        if (result == null) {
            result = results.remove(0);
        }

        if (!results.isEmpty()) {
            getBlackListDao().deleteInTx(results);
        }
        return result;
    }

    /**
     * 合并关联的ID和用户名记录
     * @param id
     * @param name
     * @return
     */
    @Nullable
    public BlackList getMergedBlackList(int id, String name) {
        if (id <= 0) {
            return getBlackListWithAuthorName(name);
        }
        if (TextUtils.isEmpty(name)) {
            return getBlackListWithAuthorId(id);
        }

        List<BlackList> results = getBlackListDao().queryBuilder()
                .whereOr(Properties.AuthorId.eq(id), Properties.Author.eq(name))
                .list();
        if (results.isEmpty()) {
            return null;
        }

        BlackList result = null;
        BlackList tempResult;
        for (int i = 0; i < results.size(); i++) {
            tempResult = results.get(i);
            if (tempResult.getAuthorId() == id && TextUtils.equals(tempResult.getAuthor(), name)) {
                result = results.remove(i);
                break;
            }
        }
        if (result == null) {
            result = results.remove(0);
            result.setAuthorId(id);
            result.setAuthor(name);
            getBlackListDao().update(result);
        }

        if (!results.isEmpty()) {
            getBlackListDao().deleteInTx(results);
        }
        return result;
    }

    @BlackList.ForumFLag
    public int getForumFlag(int id, String name) {
        BlackList oBlackList = getMergedBlackList(id, name);
        if (oBlackList != null) return oBlackList.getForum();
        return BlackList.NORMAL;
    }

    @BlackList.PostFLag
    public int getPostFlag(int id, String name) {
        BlackList oBlackList = getMergedBlackList(id, name);
        if (oBlackList != null) return oBlackList.getPost();
        return BlackList.NORMAL;
    }

    public void saveBlackList(@NonNull BlackList blackList) {
        if (blackList.getAuthorId() <= 0 && TextUtils.isEmpty(blackList.getAuthor())) {
            return;
        }
        BlackList oBlackList = getMergedBlackList(blackList.getAuthorId(), blackList.getAuthor());
        if (oBlackList == null) {
            getBlackListDao().insert(blackList);
        } else {
            oBlackList.mergeFrom(blackList);
            getBlackListDao().update(oBlackList);
        }
    }

    public void delBlackList(@NonNull BlackList blackList) {
        BlackList oBlackList = getMergedBlackList(blackList.getAuthorId(), blackList.getAuthor());
        if (oBlackList != null) {
            getBlackListDao().delete(oBlackList);
        }
    }

    public void delBlackLists(List<BlackList> blackLists) {
        getBlackListDao().deleteInTx(blackLists);
    }

    public void saveDefaultBlackList(int authorid, String author, String remark) {
        BlackList blackList = new BlackList();
        blackList.setAuthorId(authorid);
        blackList.setAuthor(author);
        blackList.setRemark(remark);
        blackList.setPost(BlackList.HIDE_POST);
        blackList.setForum(BlackList.HIDE_FORUM);
        blackList.setTimestamp(System.currentTimeMillis());
        saveBlackList(blackList);
    }

    public void delDefaultBlackList(int authorid, String author) {
        BlackList blackList = new BlackList();
        blackList.setAuthorId(authorid);
        blackList.setAuthor(author);
        delBlackList(blackList);
    }
}
