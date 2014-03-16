package com.ldq.financial.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ldq.financial.R;
import com.ldq.financial.activity.EditRecordActivity;
import com.ldq.financial.dao.Category;
import com.ldq.financial.dao.Record;
import com.ldq.financial.factory.DaoSessionFactory;

public class FragmentEditRecord extends Fragment implements OnClickListener {

    private Record record;

    private long time;
    private long categoryId;

    private EditText editTextName;
    private EditText editTextValue;
    private TextView textMinus;
    private CheckBox checkBox;
    private Button buttonTime;
    private Spinner spinner;
    private EditText editTextRemark;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Bundle bundle = getArguments();
        if (bundle != null) {
            long recordId = bundle.getLong(EditRecordActivity.KEY_RECORD_ID);
            time = bundle.getLong(FragmentRecordList.KEY_TIME);
            record = DaoSessionFactory.getDaoSession(getActivity())
                    .getRecordDao().load(recordId);
        } else {
            time = System.currentTimeMillis();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_record, container,
                false);
        editTextName = (EditText) view.findViewById(R.id.edit_name);
        editTextValue = (EditText) view.findViewById(R.id.edit_value);
        textMinus = (TextView) view.findViewById(R.id.text_minus);
        checkBox = (CheckBox) view.findViewById(R.id.check_dispense);
        buttonTime = (Button) view.findViewById(R.id.btn_time);
        spinner = (Spinner) view.findViewById(R.id.spinner1);
        editTextRemark = (EditText) view.findViewById(R.id.edit_remark);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final List<Category> list = DaoSessionFactory
                .getDaoSession(getActivity()).getCategoryDao().loadAll();
        ArrayList<String> categories = new ArrayList<String>();
        for (Category category : list) {
            categories.add(category.getCategoryName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                categoryId = list.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        int selected = 0;

        if (record != null) {
            editTextName.setText(record.getRecordName());
            editTextValue.setText("" + Math.abs(record.getValue()));
            textMinus.setVisibility(record.getIsPayment() ? View.VISIBLE
                    : View.INVISIBLE);
            checkBox.setChecked(!record.getIsPayment());

            for (int i = 0, size = list.size(); i < size; i++) {
                Category category = list.get(i);
                if (category.getId() == record.getCategoryId()) {
                    selected = i;
                    break;
                }
            }

            editTextRemark.setText(record.getRemarks());
        } else {
            selected = 0;
        }

        spinner.setSelection(selected);

        buttonTime.setText(new SimpleDateFormat("yyyy-MM-dd E", Locale.CHINA)
                .format(new Date(time)));
        buttonTime.setOnClickListener(this);
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                textMinus.setVisibility(isChecked ? View.INVISIBLE
                        : View.VISIBLE);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit_record, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        case R.id.menu_ok:
            if (record == null) {
                record = new Record();
            }
            record.setRecordName(editTextName.getText().toString().trim());
            record.setIsPayment(!checkBox.isChecked());

            float value = Float.valueOf(editTextValue.getText().toString()
                    .trim());
            if (record.getIsPayment()) {
                record.setValue(-value);
            } else {
                record.setValue(value);
            }

            record.setTime(time);
            record.setCategoryId(categoryId);
            record.setRemarks(editTextRemark.getText().toString().trim());
            DaoSessionFactory.getDaoSession(getActivity()).getRecordDao()
                    .insertOrReplace(record);

            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
            break;

        default:
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_time:
            final Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            DatePicker datePicker = new DatePicker(getActivity());
            datePicker.init(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    new DatePicker.OnDateChangedListener() {
                        @Override
                        public void onDateChanged(DatePicker view, int year,
                                int monthOfYear, int dayOfMonth) {
                            calendar.set(year, monthOfYear, dayOfMonth);
                        }
                    });
            new AlertDialog.Builder(getActivity())
                    .setTitle("日期")
                    .setView(datePicker)
                    .setPositiveButton("確定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    time = calendar.getTimeInMillis();
                                    buttonTime.setText(new SimpleDateFormat(
                                            "yyyy-MM-dd E", Locale.CHINA)
                                            .format(new Date(time)));
                                }
                            }).setNegativeButton("取消", null).create().show();
            break;

        default:
            break;
        }
    }
}
