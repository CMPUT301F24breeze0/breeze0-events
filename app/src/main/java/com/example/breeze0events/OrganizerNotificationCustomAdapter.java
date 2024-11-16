package com.example.breeze0events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.util.Pair;
import java.util.ArrayList;
import java.util.HashSet;

public class OrganizerNotificationCustomAdapter extends ArrayAdapter<Pair<String, String>> {
    private Context context;
    private ArrayList<Pair<String, String>> data;
    private HashSet<Integer> selectedPositions;

    public OrganizerNotificationCustomAdapter(Context context, ArrayList<Pair<String, String>> data, HashSet<Integer> selectedPositions) {
        super(context, 0, data);
        this.context = context;
        this.data = data;
        this.selectedPositions = selectedPositions;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Pair<String, String> getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.organizer_contact_layout, parent, false);
        }

        // 获取列表项中的 TextView
        TextView nameTextView = convertView.findViewById(R.id.entrant_name);
        TextView idTextView = convertView.findViewById(R.id.entrant_id);

        // 获取当前的 Pair 对象（entrantId 和 entrantName）
        Pair<String, String> currentEntrant = getItem(position);

        // 检查并设置 TextView 的内容
        if (currentEntrant != null) {
            nameTextView.setText(currentEntrant.second); // 设置名字
            idTextView.setText(currentEntrant.first);    // 设置 ID
        }

        nameTextView.setTextColor(context.getResources().getColor(R.color.text_color));
        idTextView.setTextColor(context.getResources().getColor(R.color.text_color));

        // 设置选中状态的背景颜色
        if (selectedPositions.contains(position)) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.listview_divider_color)); // 选中的颜色
        } else {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.button_color)); // 未选中的颜色
        }

        return convertView;
    }
}