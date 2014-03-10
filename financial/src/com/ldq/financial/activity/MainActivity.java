package com.ldq.financial.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.ldq.financial.R;
import com.ldq.financial.fragment.FragmentRecordList;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FragmentRecordList fragmentRecordList = new FragmentRecordList();
        Bundle bundle = new Bundle();
        bundle.putLong(FragmentRecordList.KEY_TIME, System.currentTimeMillis());
        fragmentRecordList.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.framelayout, fragmentRecordList).commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_add:
            Intent intent = new Intent(MainActivity.this,
                    EditRecordActivity.class);
            startActivity(intent);
            break;

        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }
}
