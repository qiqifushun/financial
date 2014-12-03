package com.qiqi.financial.factory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.qiqi.financial.dao.DaoMaster;
import com.qiqi.financial.dao.DaoSession;

public class DaoSessionFactory {

    private static DaoSession daoSession;

    public static synchronized DaoSession getDaoSession(Context context) {
        if (daoSession == null) {
            SQLiteDatabase db = new DaoMaster.DevOpenHelper(context,
                    "financial.db", null).getReadableDatabase();
            daoSession = new DaoMaster(db).newSession();
        }
        return daoSession;
    }
}
