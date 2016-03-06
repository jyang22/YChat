package com.example.yang1.ychat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseUser;

public class PersonalInfoActivity extends AppCompatActivity {

    ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        currentUser = ParseUser.getCurrentUser();
        if (currentUser == null){
            navigateToLogin();
        }

        TextView textView_username = (TextView)findViewById(R.id.textView_username);
        String username = currentUser.getUsername();
        textView_username.setText(username);

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
                Intent intent = new Intent(PersonalInfoActivity.this, EditContactActivity.class);
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
}
