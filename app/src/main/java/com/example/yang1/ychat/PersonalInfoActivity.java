package com.example.yang1.ychat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PersonalInfoActivity extends AppCompatActivity {

    ParseUser currentUser;
    File imageFile;
    String albumName = "Project3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        currentUser = ParseUser.getCurrentUser();
        if (currentUser == null){
            navigateToLogin();
        }

        // set username
        TextView textView_username = (TextView)findViewById(R.id.textView_username);
        String username = currentUser.getUsername();
        textView_username.setText(username);

        // set email
        TextView textView_email = (TextView)findViewById(R.id.textView_email);
        String email = currentUser.getEmail();
        textView_email.setText(email);

        // set avatar
        ImageView imageView_avatar = (ImageView)findViewById(R.id.avatar);
        ParseFile file = currentUser.getParseFile("avatar");
        String imageUrl = file.getUrl();
        Uri imageUri = Uri.parse(imageUrl);
        Picasso.with(PersonalInfoActivity.this).load(imageUri.toString()).into(imageView_avatar);

        // change avatar
        imageView_avatar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageFile = createImageFile();
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                startActivityForResult(takePhotoIntent, 1234);
                return true;
            }
        });

        // buttons
        Button btn_chat = (Button) findViewById(R.id.btn_chat);
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonalInfoActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button btn_contact = (Button) findViewById(R.id.btn_contact);
        btn_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonalInfoActivity.this, ContactActivity.class);
                startActivity(intent);
            }
        });

    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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
            Uri mMediaUri = Uri.fromFile(imageFile);

            byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);
            fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            String fileName = FileHelper.getFileName(this, mMediaUri, "image");
            ParseFile file = new ParseFile(fileName, fileBytes);
            currentUser.put("avatar", file);

            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
