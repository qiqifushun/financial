package com.ldq.financial;

import com.ldq.financial.dao.Category;
import com.ldq.financial.dao.CategoryDao;
import com.ldq.financial.factory.DaoSessionFactory;

import android.app.Application;

public class FinancialApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initAppConfig();
        initDb();
    }

    private void initAppConfig() {
    }

    private void initDb() {
        Category category1 = new Category(1L, "日常开销", 0);
        Category category2 = new Category(2L, "工资", 0);
        CategoryDao categoryDao = DaoSessionFactory.getDaoSession(
                getApplicationContext()).getCategoryDao();
        categoryDao.insertOrReplace(category1);
        categoryDao.insertOrReplace(category2);
    }
}
