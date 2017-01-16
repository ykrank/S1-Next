package me.ykrank.s1next.widget;

import android.content.Context;

import me.ykrank.s1next.data.db.dbmodel.DaoMaster;

/**
 * Created by ykrank on 2017/1/16.
 */

public class AppDaoOpenHelper extends DaoMaster.OpenHelper {
    public AppDaoOpenHelper(Context context, String name) {
        super(context, name);
    }
}
