package com.example.yang1.ychat;

/**
 * Created by Yang1 on 2/26/16.
 */

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;


public class MessageAdapter extends ArrayAdapter<ParseObject> {

    protected Context mContext;
    protected final List<ParseObject> mUsers;
    String[] mMessages;

    public MessageAdapter(Context context,int resource, List<ParseObject> users) {
        super(context, resource, users);
        this.mContext = context;
        this.mUsers = users;
    }

    private static class ScrapeViewHolder {
        ImageView iconImageView;
        TextView nameLabel;
        TextView firstMessage;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ScrapeViewHolder holder;

        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.message_item, null);
            holder = new ScrapeViewHolder();
            holder.iconImageView = (ImageView)row.findViewById(R.id.messageIcon);
            holder.iconImageView.setScaleType(ImageView.ScaleType.CENTER);
            holder.nameLabel = (TextView)row.findViewById(R.id.senderLabel);
            holder.firstMessage = (TextView)row.findViewById(R.id.textView_first_message);

            row.setTag(holder);
        }
        else {
            holder = (ScrapeViewHolder)row.getTag();
        }

        ParseObject user = mUsers.get(position);
        ParseQuery<ParseObject> query = new ParseQuery("message");
        query.whereEqualTo("senderName", ParseUser.getCurrentUser().getUsername());
        query.whereEqualTo("receiverName", user.getString("username"));
        try {
            List<ParseObject> messages = query.find();
            if (messages.size() > 0) {
                mMessages = new String[messages.size()];
                int i = 0;
                for (ParseObject message : messages) {
                    mMessages[i] = message.getString("text");
                    i++;
                }
            } else {
                mMessages = new String[1];
                mMessages[0] = "...";
            }
        } catch (Exception e) {
        }

        // set the sender name
        holder.nameLabel.setText(user.getString("username"));
        holder.firstMessage.setText(mMessages[mMessages.length - 1]);

        // set the image thumbnail
        ParseFile file = user.getParseFile("avatar");
        Uri fileUri = Uri.parse(file.getUrl());
        Picasso.with(mContext).load(fileUri.toString()).into(holder.iconImageView);

        return row;
    }
}

