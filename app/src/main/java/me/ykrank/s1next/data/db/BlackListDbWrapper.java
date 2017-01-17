package me.ykrank.s1next.data.db;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.List;

import javax.inject.Inject;

import me.ykrank.s1next.App;
import me.ykrank.s1next.data.db.dbmodel.BlackList;
import me.ykrank.s1next.data.db.dbmodel.BlackListDao;
import me.ykrank.s1next.util.LooperUtil;
import me.ykrank.s1next.util.RxJavaUtil;
import rx.Observable;

import static me.ykrank.s1next.data.db.dbmodel.BlackListDao.Properties;

/**
 * 对黑名单数据库的操作包装
 * Created by AdminYkrank on 2016/2/23.
 */
public class BlackListDbWrapper {
    private static BlackListDbWrapper blackListWrapper = new BlackListDbWrapper();

    @Inject
    AppDaoSessionManager appDaoSessionManager;

    private BlackListDbWrapper() {
        App.getAppComponent().inject(this);
    }

    public static BlackListDbWrapper getInstance() {
        return blackListWrapper;
    }

    private BlackListDao getBlackListDao() {
        return appDaoSessionManager.getDaoSession().getBlackListDao();
    }

    public List<BlackList> getAllBlackList(int limit, int offset) {
        return getBlackListDao().queryBuilder()
                .limit(limit)
                .offset(offset)
                .list();
    }

    public Observable<Cursor> getBlackListCursor() {
        return Observable.just(getBlackListDao().queryBuilder())
                .compose(RxJavaUtil.iOTransformer())
                .map(builder -> {
                    // TODO: 2017/1/17  remove this
                    LooperUtil.enforceOnMainThread();
                    return builder.buildCursor().query();
                });
    }

    public BlackList fromCursor(@NonNull Cursor cursor) {
        return getBlackListDao().readEntity(cursor, 0);
    }

    /**
     * 默认情况下的黑名单查找。如果用户id合法则优先id，否则查找用户name
     *
     * @param id
     * @param name
     * @return
     */
    public BlackList getBlackListDefault(int id, String name) {
        BlackList oBlackList = null;
        if (id > 0) {
            oBlackList = getBlackListWithAuthorId(id);
        } else if (name != null && !TextUtils.isEmpty(name)) {
            oBlackList = getBlackListWithAuthorName(name);
        }
        return oBlackList;
    }

    /**
     * 根据用户id查找记录
     *
     * @param id
     * @return
     */
    public BlackList getBlackListWithAuthorId(int id) {
        return getBlackListDao().queryBuilder()
                .where(Properties.AuthorId.eq(id))
                .unique();
    }

    /**
     * 根据用户名查找记录
     *
     * @param name
     * @return
     */
    public BlackList getBlackListWithAuthorName(String name) {
        return getBlackListDao().queryBuilder()
                .where(Properties.Author.eq(name))
                .unique();
    }

    @BlackList.ForumFLag
    public int getForumFlag(int id, String name) {
        BlackList oBlackList = getBlackListDefault(id, name);
        if (oBlackList != null) return oBlackList.getForum();
        return BlackList.NORMAL;
    }

    @BlackList.PostFLag
    public int getPostFlag(int id, String name) {
        BlackList oBlackList = getBlackListDefault(id, name);
        if (oBlackList != null) return oBlackList.getPost();
        return BlackList.NORMAL;
    }

    public void saveBlackList(@NonNull BlackList blackList) {
        BlackList oBlackList = getBlackListDefault(blackList.getAuthorId(), blackList.getAuthor());
        if (oBlackList == null) {
            getBlackListDao().save(blackList);
        } else {
            oBlackList.copyFrom(blackList);
            getBlackListDao().save(oBlackList);
        }
    }

    public void delBlackList(@NonNull BlackList blackList) {
        BlackList oBlackList = getBlackListDefault(blackList.getAuthorId(), blackList.getAuthor());
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
