package com.example.yang1.ychat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ParseException;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YJ on 3/5/16.
 */
public class ConversationAdapter extends ArrayAdapter<ParseObject> {

    private Context context;
    private List<ParseObject> messages;

    public ConversationAdapter(Context context, int textViewResourceId, List<ParseObject> messages) {
        super(context, textViewResourceId, messages);
        this.context = context;
        this.messages = messages;
    }

//    @Override
//    public void add(Conversation object) {
//        conversationList.add(object);
//        super.add(object);
//    }

    public int getCount() {
        return this.messages.size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ParseObject messageObj = getItem(position);
        View row;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View row = inflater.inflate(R.layout.left, null);
        if (messageObj.getBoolean("sendByUser")) {
            row = inflater.inflate(R.layout.right, parent, false);
        }else{
            row = inflater.inflate(R.layout.left, parent, false);
        }

        TextView chatText = (TextView) row.findViewById(R.id.textView_msgr);
        ImageView chatImage = (ImageView) row.findViewById(R.id.imageView_msgr);
        if (messageObj.getString("fileType").equals("text")) {
            chatImage.setVisibility(View.GONE);
            chatText.setText(messageObj.getString("text"));
//            chatText.setBackgroundColor(0xFFFFFFFF);
        } else {
            chatText.setVisibility(View.GONE);
            // get file
            ParseFile file = messageObj.getParseFile("file");
            String imageUrl = file.getUrl();
            Uri imageUri = Uri.parse(imageUrl);
            Picasso.with(context).load(imageUri.toString()).into(chatImage);
//            chatImage.setBackgroundColor(0xFFFFFFFF);
        }
        return row;
    }
}
