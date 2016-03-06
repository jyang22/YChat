package com.example.yang1.ychat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YJ on 3/5/16.
 */
public class ConversationAdapter extends ArrayAdapter<Conversation> {

    private TextView chatText;
    private List<Conversation> conversationList = new ArrayList<Conversation>();
    private Context context;

    public ConversationAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    @Override
    public void add(Conversation object) {
        conversationList.add(object);
        super.add(object);
    }

    public int getCount() {
        return this.conversationList.size();
    }

    public Conversation getItem(int index) {
        return this.conversationList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Conversation conversationObj = getItem(position);
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (conversationObj.left) {
            row = inflater.inflate(R.layout.right, parent, false);
        }else{
            row = inflater.inflate(R.layout.left, parent, false);
        }
        chatText = (TextView) row.findViewById(R.id.msgr);
        chatText.setText(conversationObj.message);
        return row;
    }
}
