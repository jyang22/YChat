package com.example.yang1.ychat;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.Parse;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    File imageFile;
    String albumName = "Project3";

    // get the user and contact
    ParseUser currentUser;
    ParseRelation<ParseObject> contactRelation;
    List<ParseObject> mUsers = new ArrayList<>();
    protected ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.main_listView);
        listView.setOnItemClickListener(this);

        currentUser = ParseUser.getCurrentUser();
        if (currentUser == null){
            navigateToLogin();
        }

        // buttons
        Button btn_contact = (Button) findViewById(R.id.btn_contact);
        btn_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ContactActivity.class);
                startActivity(intent);
            }
        });

        Button btn_me = (Button) findViewById(R.id.btn_me);
        btn_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PersonalInfoActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        contactRelation = currentUser.getRelation("contact");
        ParseQuery contactQuery = contactRelation.getQuery();
        try {
            List<ParseObject> contacts = contactQuery.find();
            mUsers = contacts;
            listView.setAdapter(new MessageAdapter(this, R.layout.message_item, mUsers));
        } catch (Exception e) {

        }
//        contactQuery.findInBackground(new FindCallback<ParseObject>() {
//            public void done(List<ParseObject> contacts, ParseException e) {
//                if (e == null) {
//                    mUsers = contacts;
//                    listView.setAdapter(new MessageAdapter(MainActivity.this, R.layout.message_item, mUsers));
//                } else {
//                    // Something went wrong.
//                }
//            }
//        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ParseObject user = mUsers.get(position);
        String receiverName = user.getString("username");

        Intent intent = new Intent(this, ConversationActivity.class);
        intent.putExtra("receiverName", receiverName);
        startActivity(intent);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_logout:
                ParseUser.logOut();
                navigateToLogin();
                break;

//            case R.id.menu_edit_contact:
//                Intent intent = new Intent(MainActivity.this,EditContactActivity.class);
//                startActivity(intent);
//                break;

            case R.id.action_uninstall:
                // uninstall
                Uri packageURI = Uri.parse("package:com.example.yang1.ychat");
                Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageURI);
                startActivity(uninstallIntent);
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
            Log.d("y", "Album directory exists");
        } else if (file.mkdirs()) {
            Log.i("y", "Album directory is created");
        } else {
            Log.e("y", "Failed to create album directory.  Check permissions and storage.");
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

            ParseObject message = new ParseObject("message");
            message.put("senderId", ParseUser.getCurrentUser().getObjectId());
            message.put("senderName", ParseUser.getCurrentUser().getUsername());
            message.put("fileType", "image");

            byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);
            fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            String fileName = FileHelper.getFileName(this, mMediaUri, "image");
            ParseFile file = new ParseFile(fileName, fileBytes);
            message.put("file", file);

            message.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(MainActivity.this, "Message sent", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
