package com.example.yang1.ychat;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConversationActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private ConversationAdapter conversationAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private boolean side = false;
    private String receiverName;
    private String senderName;
    private String text;
    ParseUser currentUser;
    List<ParseObject> messages;
    File imageFile;
    String albumName = "Project3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        currentUser = ParseUser.getCurrentUser();
        if (currentUser == null){
            navigateToLogin();
        }
        senderName = currentUser.getUsername();

        // get receiver name
        Intent intent = getIntent();
        receiverName = intent.getStringExtra("receiverName");
        listView = (ListView) findViewById(R.id.message_listview);

        // set the title
        getSupportActionBar().setTitle(receiverName);

        buttonSend = (Button) findViewById(R.id.send_btn);

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
    }

    @Override
    public void onResume() {
        super.onResume();

        // get data from parse server
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("message");
        query.whereEqualTo("senderName", senderName);
        query.whereEqualTo("receiverName", receiverName);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
                if (e == null) {

                    listView.setAdapter(new ConversationAdapter(ConversationActivity.this, R.layout.right, messages));
                }
            }
        });
    }

    private boolean sendChatMessage() {
        text = chatText.getText().toString();

        if (text.length() != 0) {
            ParseObject message1 = new ParseObject("message");
            message1.put("senderId", ParseUser.getCurrentUser().getObjectId());
            message1.put("senderName", ParseUser.getCurrentUser().getUsername());
            message1.put("fileType", "text");
            message1.put("receiverName", receiverName);
            message1.put("text", text);
            message1.put("sendByUser", true);

            message1.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(ConversationActivity.this, "Message sent", Toast.LENGTH_LONG).show();
                    }
                }
            });

            ParseObject message2 = new ParseObject("message");
            // should be receiver id
            message2.put("senderId", ParseUser.getCurrentUser().getObjectId());
            message2.put("senderName", receiverName);
            message2.put("fileType", "text");
            message2.put("receiverName", ParseUser.getCurrentUser().getUsername());
            message2.put("text", text);
            message2.put("sendByUser", false);

            message2.saveInBackground(new SaveCallback() {
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
        return false;
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.conversation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_camera:
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageFile = createImageFile();
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                startActivityForResult(takePhotoIntent, 1234);
                break;
        }
        return true;
    }

    private File createImageFile() {
        File image = null;
        try {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getAlbumStorageDir();
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (Exception e) {
            // we should do some meaningful error handling here !!!
        }
        return image;
    }

    public File getAlbumStorageDir() {
        // Same as Environment.getExternalStorageDirectory() + "/Pictures/" + albumName
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);
        if (file.exists()) {
            Log.d("jsun", "Album directory exists");
        } else if (file.mkdirs()) {
            Log.i("jsun", "Album directory is created");
        } else {
            Log.e("jsun", "Failed to create album directory.  Check permissions and storage.");
        }
        return file;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode != 1234) return;

        if (resultCode != Activity.RESULT_OK) {
            imageFile.delete();
            return;
        }
        try {
            Log.i("onActivityResult","Here");

            //Uri mMediaUri = data.getData();
            Uri mMediaUri = Uri.fromFile(imageFile);
            Log.i(TAG, "Media URI: " + mMediaUri);

            ParseObject message1 = new ParseObject("message");
            message1.put("senderId", ParseUser.getCurrentUser().getObjectId());
            message1.put("senderName", ParseUser.getCurrentUser().getUsername());
            message1.put("fileType", "image");
            message1.put("receiverName", receiverName);
            message1.put("sendByUser", true);
            message1.put("text", "image");

            byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);
            fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            String fileName = FileHelper.getFileName(this, mMediaUri, "image");
            ParseFile file = new ParseFile(fileName, fileBytes);
            message1.put("file", file);

            message1.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(ConversationActivity.this, "Message sent", Toast.LENGTH_LONG).show();
                    }
                }
            });

            ParseObject message2 = new ParseObject("message");
            // should be receiver id
            message2.put("senderId", ParseUser.getCurrentUser().getObjectId());
            message2.put("senderName", receiverName);
            message2.put("fileType", "text");
            message2.put("receiverName", ParseUser.getCurrentUser().getUsername());
            message2.put("text", text);
            message2.put("sendByUser", false);
            message2.put("file", file);
            message2.put("text", "image");
            message2.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(ConversationActivity.this, "Message sent", Toast.LENGTH_LONG).show();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
