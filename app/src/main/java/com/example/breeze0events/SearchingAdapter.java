package com.example.breeze0events;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SearchingAdapter extends ArrayAdapter<Event> {
    public SearchingAdapter(Context context, List<Event> events) {
        super(context, 0, events);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 获取当前的事件对象
        Event event = getItem(position);

        // 如果没有可重用的视图，加载新视图
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.entrant_event_search_content, parent, false);
        }

        // 查找 TextView
        TextView idTextView = convertView.findViewById(R.id.Event_id);
        TextView nameTextView = convertView.findViewById(R.id.Event_title);

        // 设置 ID 和 Event name
        idTextView.setText(String.valueOf(event.getEventId()));
        nameTextView.setText(event.getName());

        return convertView;
    }
}


