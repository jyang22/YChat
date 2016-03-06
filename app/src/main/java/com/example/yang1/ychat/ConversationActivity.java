package com.example.yang1.ychat;

import android.content.Intent;
import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ConversationActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private ConversationAdapter conversationAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private boolean side = false;
    private String receiverName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        // get receiver name
        Intent intent = getIntent();
        receiverName = intent.getStringExtra("receiverName");

        buttonSend = (Button) findViewById(R.id.send_btn);
        listView = (ListView) findViewById(R.id.message_listview);
        conversationAdapter = new ConversationAdapter(getApplicationContext(), R.layout.right);
        listView.setAdapter(conversationAdapter);

        chatText = (EditText) findViewById(R.id.message_edittext);
        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage();
                }
                return false;
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessage();
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(conversationAdapter);

        //to scroll the list view to bottom on data change
        conversationAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(conversationAdapter.getCount() - 1);
            }
        });
    }

    private boolean sendChatMessage() {
//        conversationAdapter.add(new Conversation(side, chatText.getText().toString()));

        ParseObject message = new ParseObject("message");
        message.put("senderId", ParseUser.getCurrentUser().getObjectId());
        message.put("senderName", ParseUser.getCurrentUser().getUsername());
        message.put("fileType", "text");
        message.put("receiverName", receiverName);


//        byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);
//        fileBytes = FileHelper.reduceImageForUpload(fileBytes);
//        String fileName = FileHelper.getFileName(this, mMediaUri, "image");
//        ParseFile file = new ParseFile(fileName, fileBytes);
//        message.put("file", file);

        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(ConversationActivity.this, "Message sent", Toast.LENGTH_LONG).show();
                }
            }
        });

        chatText.setText("");
        side = !side;
        return true;
    }
}
