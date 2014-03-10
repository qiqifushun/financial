package com.ldq.financial.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ldq.financial.R;
import com.ldq.financial.activity.EditRecordActivity;
import com.ldq.financial.dao.Record;
import com.ldq.financial.dao.RecordDao.Properties;
import com.ldq.financial.factory.DaoSessionFactory;
import com.ldq.financial.util.Util;

public class FragmentRecordList extends Fragment {

    public static final String KEY_TIME = "time";

    private final int ID_ADD = 1;
    private final int ID_DELETE = 2;
    private final int ID_MODIFY = 3;

    private long time;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            time = bundle.getLong(KEY_TIME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData();
    }

    private void loadData() {

        View view = getView();

        ListView listView = (ListView) view.findViewById(R.id.listview);
        TextView textViewEmpty = (TextView) view.findViewById(R.id.text_empty);

        long[] between = new long[2];
        Util.setMonthStartEnd(time, between);
        List<Record> list = DaoSessionFactory
                .getDaoSession(getActivity())
                .getRecordDao()
                .queryBuilder()
                .where(Properties.Time.ge(between[0]),
                        Properties.Time.lt(between[1]))
                .orderAsc(Properties.Time).list();

        if (list.isEmpty()) {
            listView.setVisibility(View.GONE);
            textViewEmpty.setVisibility(View.VISIBLE);
            textViewEmpty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),
                            EditRecordActivity.class);
                    startActivityForResult(intent, 0);
                }
            });
        } else {
            listView.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);
            RecordAdapter recordAdapter = new RecordAdapter(list);
            listView.setAdapter(recordAdapter);
            registerForContextMenu(listView);
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
            ContextMenuInfo menuInfo) {
        ListAdapter adapter = ((ListView) view).getAdapter();
        int type = adapter
                .getItemViewType(((AdapterContextMenuInfo) menuInfo).position);
        if (type == RecordAdapter.TYPE_RECORD) {
            menu.add(1, ID_ADD, 1, "添加");
            menu.add(1, ID_DELETE, 2, "删除");
            menu.add(1, ID_MODIFY, 3, "修改");
        }
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
        case ID_ADD:
            Intent add = new Intent(getActivity(), EditRecordActivity.class);
            startActivity(add);
            break;
        case ID_DELETE:
            new AlertDialog.Builder(getActivity())
                    .setTitle("警告")
                    .setMessage("确定要删除此条记录吗？")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
                                            .getMenuInfo();
                                    ListView listView = (ListView) getView()
                                            .findViewById(R.id.listview);
                                    RecordAdapter adapter = (RecordAdapter) listView
                                            .getAdapter();
                                    DisplayRecord displayRecord = (DisplayRecord) adapter
                                            .getItem(menuInfo.position);
                                    DaoSessionFactory.getDaoSession(
                                            getActivity()).delete(
                                            displayRecord.record);
                                    loadData();
                                }
                            }).setNegativeButton("取消", null).create().show();
            break;
        case ID_MODIFY:
            Intent modify = new Intent(getActivity(), EditRecordActivity.class);
            AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
                    .getMenuInfo();
            ListView listView = (ListView) getView()
                    .findViewById(R.id.listview);
            RecordAdapter adapter = (RecordAdapter) listView.getAdapter();
            DisplayRecord displayRecord = (DisplayRecord) adapter
                    .getItem(menuInfo.position);
            modify.putExtra(EditRecordActivity.KEY_RECORD_ID,
                    displayRecord.record.getId());
            startActivity(modify);
            break;

        default:
            break;
        }
        return true;
    }

    class RecordAdapter extends BaseAdapter {

        private static final int TYPE_HEADER = 1;
        private static final int TYPE_RECORD = 2;

        private List<DisplayRecord> displayRecords;

        public RecordAdapter(List<Record> list) {
            this.displayRecords = genDisplayRecordList(list);
        }

        @Override
        public int getCount() {
            return displayRecords.size();
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            DisplayRecord displayRecord = (DisplayRecord) getItem(position);
            if (displayRecord.date != null) {
                return TYPE_HEADER;
            } else if (displayRecord.record != null) {
                return TYPE_RECORD;
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            return displayRecords.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                if (TYPE_HEADER == getItemViewType(position)) {
                    convertView = LayoutInflater.from(getActivity()).inflate(
                            R.layout.list_item_record_header, parent, false);
                    HolderHeader holder = new HolderHeader();
                    holder.date = (TextView) convertView
                            .findViewById(R.id.text_date);
                    holder.sum = (TextView) convertView
                            .findViewById(R.id.text_sum);
                    convertView.setTag(holder);
                } else if (TYPE_RECORD == getItemViewType(position)) {
                    convertView = LayoutInflater.from(getActivity()).inflate(
                            R.layout.list_item_record_detail, parent, false);
                    HolderRecord holder = new HolderRecord();
                    holder.name = (TextView) convertView
                            .findViewById(R.id.text_name);
                    holder.value = (TextView) convertView
                            .findViewById(R.id.text_value);
                    holder.category = (TextView) convertView
                            .findViewById(R.id.text_category);
                    holder.remark = (TextView) convertView
                            .findViewById(R.id.text_remark);
                    convertView.setTag(holder);
                }
            }

            bindView(convertView, position);

            return convertView;
        }

        private void bindView(View view, int position) {
            DisplayRecord displayRecord = (DisplayRecord) getItem(position);
            if (TYPE_HEADER == getItemViewType(position)) {
                HolderHeader holder = (HolderHeader) view.getTag();
                holder.date.setText(displayRecord.date);
                holder.sum.setText("合计：￥" + displayRecord.sum);
            } else if (TYPE_RECORD == getItemViewType(position)) {
                HolderRecord holder = (HolderRecord) view.getTag();
                Record record = displayRecord.record;
                holder.name.setText(record.getRecordName());
                holder.value.setText("￥ " + record.getValue());
                holder.category.setText("类别："
                        + record.getCategory().getCategoryName());
                holder.remark.setText("备注：" + record.getRemarks());
            }
        }

        private class HolderHeader {
            TextView date;
            TextView sum;
        }

        private class HolderRecord {
            TextView name;
            TextView value;
            TextView category;
            TextView remark;
        }

    }

    // 用于显示的记录信息
    private class DisplayRecord {
        String date;
        float sum;
        Record record;

        public DisplayRecord(String date, float sum, Record record) {
            this.date = date;
            this.sum = sum;
            this.record = record;
        }
    }

    private List<DisplayRecord> genDisplayRecordList(List<Record> list) {
        ArrayList<DisplayRecord> displayRecords = new ArrayList<DisplayRecord>();
        DisplayRecord sumRecord = null;
        float sum = 0;
        for (int i = 0, size = list.size(); i < size; i++) {
            Record record = list.get(i);
            if (i == 0) {
                long time1 = record.getTime();
                String date = getFormattedDate(time1);
                DisplayRecord displayRecord = new DisplayRecord(date, 0, null);
                displayRecords.add(displayRecord);

                sumRecord = displayRecord;

                displayRecords.add(new DisplayRecord(null, 0, record));
                sum += record.getValue();

            } else {
                long time1 = record.getTime();
                Record lastRecord = list.get(i - 1);
                long time2 = lastRecord.getTime();
                if (isSameDay(time1, time2)) {
                    DisplayRecord displayRecord = new DisplayRecord(null, 0,
                            record);
                    displayRecords.add(displayRecord);
                    sum += record.getValue();
                } else {

                    sumRecord.sum = sum;
                    sum = 0;

                    String date = getFormattedDate(time1);
                    DisplayRecord displayRecord = new DisplayRecord(date, 0,
                            null);
                    displayRecords.add(displayRecord);
                    sumRecord = displayRecord;

                    displayRecords.add(new DisplayRecord(null, 0, record));
                    sum += record.getValue();

                }
            }
        }

        if (sumRecord != null) {
            sumRecord.sum = sum;
            sum = 0;
        }

        return displayRecords;
    }

    private boolean isSameDay(long time1, long time2) {
        return TextUtils.equals(getFormattedDate(time1),
                getFormattedDate(time2));
    }

    private String getFormattedDate(long time) {
        return new SimpleDateFormat("yyyy-MM-dd E", Locale.CHINA)
                .format(new Date(time));
    }
}
