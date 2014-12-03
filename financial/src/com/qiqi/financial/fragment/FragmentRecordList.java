package com.qiqi.financial.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.qiqi.financial.R;
import com.qiqi.financial.activity.EditRecordActivity;
import com.qiqi.financial.activity.MainActivity;
import com.qiqi.financial.dao.Record;
import com.qiqi.financial.dao.RecordDao.Properties;
import com.qiqi.financial.factory.DaoSessionFactory;
import com.qiqi.financial.util.Util;

import de.greenrobot.dao.query.QueryBuilder;

public class FragmentRecordList extends BaseFragment {

    public static final String KEY_TIME = "time";

    private final int ID_ADD = 1;
    private final int ID_DELETE = 2;
    private final int ID_MODIFY = 3;

    private boolean displayIncome;

    private long time;

    private RecordAdapter mRecordAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            time = bundle.getLong(KEY_TIME);
        } else {
            time = System.currentTimeMillis();
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
        System.out.println("FragmentRecordList:onActivityCreated:"
                + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                        .format(new Date(time)) + ":" + mRecordAdapter);
    }

    private void loadData() {

        View view = getView();

        TextView textExpense = (TextView) view.findViewById(R.id.text_expense);
        LinearLayout layoutIncome = (LinearLayout) view
                .findViewById(R.id.layout_income);
        TextView textIncome = (TextView) view.findViewById(R.id.text_income);
        TextView textNetIncome = (TextView) view
                .findViewById(R.id.text_net_income);
        ListView listView = (ListView) view.findViewById(R.id.listview);
        TextView textViewEmpty = (TextView) view.findViewById(R.id.text_empty);

        long[] between = new long[2];
        Util.setMonthStartEnd(time, between);

        QueryBuilder<Record> queryBuilder = DaoSessionFactory
                .getDaoSession(getActivity())
                .getRecordDao()
                .queryBuilder()
                .where(Properties.Time.ge(between[0]),
                        Properties.Time.lt(between[1]));
        int padding = (int) getResources().getDimension(R.dimen.padding_10);
        if (!displayIncome) {
            queryBuilder.where(Properties.IsPayment.eq(true));
            layoutIncome.setVisibility(View.GONE);
            textExpense.setPadding(padding, padding, padding, padding);
        } else {
            layoutIncome.setVisibility(View.VISIBLE);
            textExpense.setPadding(padding, padding, padding, 0);
        }
        List<Record> list = queryBuilder.orderAsc(Properties.Time).list();

        float expense = 0;
        float income = 0;
        float netincome = 0;
        for (Record record : list) {
            float value = record.getValue();
            if (value > 0) {
                income = income + value;
            } else {
                expense = expense + value;
            }
        }
        netincome = income + expense;
        textExpense.setText("月支出：￥" + Util.getFormattedValue(expense));
        textIncome.setText("月收入：￥" + Util.getFormattedValue(income));
        textNetIncome.setText("净收入：￥" + Util.getFormattedValue(netincome));

        if (list.isEmpty()) {
            listView.setVisibility(View.GONE);
            textViewEmpty.setVisibility(View.VISIBLE);
            textViewEmpty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toEditActivity(0, time);
                }
            });
        } else {
            listView.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);
            mRecordAdapter = new RecordAdapter(list);
            listView.setAdapter(mRecordAdapter);
            registerForContextMenu(listView);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // inflater.inflate(R.menu.menu_record_list, menu);
        // MenuItem menuItem = menu.findItem(R.id.menu_display_income);
        // menuItem.setTitle(displayIncome ? R.string.hide_income
        // : R.string.display_income);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_add:
            Intent intent = new Intent(getActivity(), EditRecordActivity.class);
            intent.putExtra(FragmentRecordList.KEY_TIME, time);
            startActivityForResult(intent, 0);
            break;
        case R.id.menu_display_income:
            displayIncome = !displayIncome;
            loadData();
//            getActivity().invalidateOptionsMenu();
            break;

        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        int type = mRecordAdapter
                .getItemViewType(((AdapterContextMenuInfo) menuInfo).position);
        if (type == RecordAdapter.TYPE_RECORD) {
            menu.add(1, ID_ADD, 1, "添加");
            menu.add(1, ID_DELETE, 2, "删除");
            menu.add(1, ID_MODIFY, 3, "修改");
        }
        System.out.println("onCreateContextMenu:position:"
                + ((AdapterContextMenuInfo) menuInfo).position + ":"
                + mRecordAdapter);
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {

        boolean displayFragment = ((MainActivity) getActivity())
                .sameMonth(time);

        if (displayFragment) {

            AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
                    .getMenuInfo();
            String s = getFormattedDate(time);
            System.out.println("------onContextItemSelected:position:"
                    + menuInfo.position + ":" + mRecordAdapter + "@" + s);
            DisplayRecord displayRecord = (DisplayRecord) mRecordAdapter
                    .getItem(menuInfo.position);
            final Record record = displayRecord.record;

            switch (item.getItemId()) {

            case ID_ADD:
                toEditActivity(0, record.getTime());
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
                                        DaoSessionFactory.getDaoSession(
                                                getActivity()).delete(record);
                                        loadData();
                                    }
                                }).setNegativeButton("取消", null).create()
                        .show();
                break;

            case ID_MODIFY:
                toEditActivity(record.getId(), record.getTime());
                break;

            default:
                break;
            }
        }

        return super.onContextItemSelected(item);
    }

    private void toEditActivity(long recordId, long time) {
        Intent intent = new Intent(getActivity(), EditRecordActivity.class);
        if (recordId > 0) {
            intent.putExtra(EditRecordActivity.KEY_RECORD_ID, recordId);
        }
        if (time > 0) {
            intent.putExtra(KEY_TIME, time);
        }
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("FragmentRecordList:onActivityResult" + ":"
                + mRecordAdapter);
        if (resultCode == Activity.RESULT_OK) {
            loadData();
        }
    }

    class RecordAdapter extends BaseAdapter {

        private static final int TYPE_HEADER = 0;
        private static final int TYPE_RECORD = 1;

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
            } else {
                return TYPE_RECORD;
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
                holder.sum.setText("合计：￥"
                        + Util.getFormattedValue(displayRecord.sum));
            } else if (TYPE_RECORD == getItemViewType(position)) {
                HolderRecord holder = (HolderRecord) view.getTag();
                Record record = displayRecord.record;
                holder.name.setText(record.getRecordName());
                holder.value.setText("￥ "
                        + Util.getFormattedValue(record.getValue()));
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
