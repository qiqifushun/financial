package com.qiqi.financial.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.Menu;

import com.qiqi.financial.R;
import com.qiqi.financial.fragment.FragmentRecordList;

public class MainActivity extends BaseActivity implements OnPageChangeListener {

    private final int PAGES = 12;

    private long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        ListPageAdapter adapter = new ListPageAdapter(
                getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(this);

        viewPager.setCurrentItem(PAGES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == Activity.RESULT_OK) {
            ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
            ListPageAdapter adapter = (ListPageAdapter) viewPager.getAdapter();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_record_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    class ListPageAdapter extends FragmentStatePagerAdapter {

        public ListPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            FragmentRecordList fragmentRecordList = new FragmentRecordList();
            Bundle bundle = new Bundle();
            long time = getTime(position);
            bundle.putLong(FragmentRecordList.KEY_TIME, time);
            fragmentRecordList.setArguments(bundle);
            System.out.println("getItem:" + position);
            return fragmentRecordList;
        }

        @Override
        public int getCount() {
            return PAGES * 2;
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int position) {
        time = getTime(position);
        String title = new SimpleDateFormat("yyyy年MM月", Locale.CHINA)
                .format(new Date(time));
        setTitle(title);
        System.out.println("onPageSelected:" + position);
    }

    private long getTime(int position) {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        int rollmonth = position - PAGES;
        calendar.roll(Calendar.MONTH, rollmonth);

        int rolledmonth = month + rollmonth;
        int rollyear;
        if (rolledmonth < 0) {
            rollyear = rolledmonth / 12 - 1;
        } else {
            rollyear = rolledmonth / 12;
        }
        calendar.roll(Calendar.YEAR, rollyear);

        return calendar.getTimeInMillis();
    }

    public boolean sameMonth(long timeFragment) {
        String time1 = new SimpleDateFormat("yyyy-MM", Locale.CHINA)
                .format(new Date(timeFragment));
        String time2 = new SimpleDateFormat("yyyy-MM", Locale.CHINA)
                .format(new Date(time));
        return TextUtils.equals(time1, time2);
    }
}
