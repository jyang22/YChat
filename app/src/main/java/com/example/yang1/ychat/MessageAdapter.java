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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;


public class MessageAdapter extends ArrayAdapter<ParseObject> {
    protected Context mContext;
    protected final List<ParseObject> mUsers;

    //    public MessageAdapter(Context context,int resource, List<ParseObject> contacts) {
    public MessageAdapter(Context context,int resource, List<ParseObject> users) {
        super(context, resource, users);
        this.mContext = context;
        this.mUsers = users;
//        mContacts = contacts;
//        mMessages = messages;
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

            row.setTag(holder);
        }
        else {
            holder = (ScrapeViewHolder)row.getTag();
        }

//        ParseObject message = mContacts.get(position);
        ParseObject user = mUsers.get(position);


//        if (message.getString("fileType").equals("image")) {
//            holder.iconImageView.setImageResource(R.drawable.ic_action_picture);
//        }
//        else {
//            holder.iconImageView.setImageResource(R.drawable.ic_action_play_over_video);
//        }

        // set the sender name
//        holder.nameLabel.setText(message.getString("senderName"));
        holder.nameLabel.setText(user.getString("username"));

        // set the image thumbnail
//        ParseFile file = message.getParseFile("file");
//        Uri fileUri = Uri.parse(file.getUrl());
//        Picasso.with(mContext).load(fileUri.toString()).into(holder.iconImageView);

        return row;
    }

    private static class ScrapeViewHolder {
        ImageView iconImageView;
        TextView nameLabel;
    }
}



//public class MessageAdapter extends ArrayAdapter<ParseObject> {
//    protected Context mContext;
//    protected List<ParseUser> mUsers;
////    protected List<ParseObject> mContacts;
////    protected List<ParseObject> mMessages;
//
////    public MessageAdapter(Context context,int resource, List<ParseObject> contacts) {
//    public MessageAdapter(Context context,int resource, List<ParseUser> users) {
//        super(context, resource, users);
//        mContext = context;
//        mUsers = users;
////        mContacts = contacts;
////        mMessages = messages;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ScrapeViewHolder holder;
//
//        View row = convertView;
//        if (row == null) {
//            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService
//                    (Context.LAYOUT_INFLATER_SERVICE);
//            row = inflater.inflate(R.layout.message_item, null);
//            holder = new ScrapeViewHolder();
//            holder.iconImageView = (ImageView)row.findViewById(R.id.messageIcon);
//            holder.iconImageView.setScaleType(ImageView.ScaleType.CENTER);
//            holder.nameLabel = (TextView)row.findViewById(R.id.senderLabel);
//
//            row.setTag(holder);
//        }
//        else {
//            holder = (ScrapeViewHolder)row.getTag();
//        }
//
////        ParseObject message = mContacts.get(position);
//        ParseUser user = mUsers.get(position);
//
//
////        if (message.getString("fileType").equals("image")) {
////            holder.iconImageView.setImageResource(R.drawable.ic_action_picture);
////        }
////        else {
////            holder.iconImageView.setImageResource(R.drawable.ic_action_play_over_video);
////        }
//
//        // set the sender name
////        holder.nameLabel.setText(message.getString("senderName"));
//        holder.nameLabel.setText(user.getUsername());
//
//        // set the image thumbnail
////        ParseFile file = message.getParseFile("file");
////        Uri fileUri = Uri.parse(file.getUrl());
////        Picasso.with(mContext).load(fileUri.toString()).into(holder.iconImageView);
//
//        return row;
//    }
//
//    private static class ScrapeViewHolder {
//        ImageView iconImageView;
//        TextView nameLabel;
//    }
//}
