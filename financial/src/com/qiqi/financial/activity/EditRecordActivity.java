package com.qiqi.financial.activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.qiqi.financial.R;
import com.qiqi.financial.fragment.FragmentEditRecord;
import com.qiqi.financial.fragment.FragmentRecordList;

public class EditRecordActivity extends BaseActivity {

    public static final String KEY_RECORD_ID = "record_id";

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_edit_record);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentEditRecord fragmentEditRecord = new FragmentEditRecord();
        Bundle bundle = new Bundle();
        long recordId = getIntent().getLongExtra(KEY_RECORD_ID, -1L);
        if (recordId != -1) {
            bundle.putLong(KEY_RECORD_ID, recordId);
        }
        long time = getIntent().getLongExtra(FragmentRecordList.KEY_TIME, -1L);
        if (time != -1) {
            bundle.putLong(FragmentRecordList.KEY_TIME, time);
        }
        fragmentEditRecord.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.framelayout, fragmentEditRecord).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            break;

        default:
            break;
        }
        return false;
    }
}
