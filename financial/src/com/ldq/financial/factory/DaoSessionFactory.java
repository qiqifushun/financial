package com.ldq.financial.factory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.ldq.financial.dao.DaoMaster;
import com.ldq.financial.dao.DaoSession;

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
