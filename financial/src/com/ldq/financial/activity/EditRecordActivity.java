package com.ldq.financial.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

import com.ldq.financial.R;
import com.ldq.financial.fragment.FragmentEditRecord;

public class EditRecordActivity extends FragmentActivity {

    public static final String KEY_RECORD_ID = "record_id";

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_edit_record);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        long recordId = getIntent().getLongExtra(KEY_RECORD_ID, -1L);
        FragmentEditRecord fragmentEditRecord = new FragmentEditRecord();
        if (recordId != -1) {
            Bundle bundle = new Bundle();
            bundle.putLong(KEY_RECORD_ID, recordId);
            fragmentEditRecord.setArguments(bundle);
        }
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
