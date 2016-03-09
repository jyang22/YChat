package com.example.yang1.ychat;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class ContactActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView listView;
    ParseUser curUser;
    ParseRelation<ParseUser> contact;
    List<ParseUser> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        listView = (ListView)findViewById(R.id.listview_contact);
        listView.setOnItemClickListener(this);

        // buttons
        Button btn_chat = (Button) findViewById(R.id.btn_chat);
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button btn_me = (Button) findViewById(R.id.btn_me);
        btn_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactActivity.this, PersonalInfoActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        curUser = ParseUser.getCurrentUser();
        contact = curUser.getRelation("contact");

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending("username");
        query.setLimit(20);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> usersList, ParseException e) {

                if (e == null) {
                    // Success
                    users = usersList;
                    String[] usernames = new String[users.size()];
                    int i = 0;
                    for (ParseUser user : users) {
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            ContactActivity.this,
                            android.R.layout.simple_list_item_multiple_choice,
                            usernames);
                    listView.setAdapter(adapter);
                    listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    listView.setItemsCanFocus(false);
                    addFriendCheckmarks();

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ContactActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle("Error")
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        if (listView.isItemChecked(position)) {
            // add relation
            contact.add(users.get(position));
            Toast.makeText(ContactActivity.this, "added", Toast.LENGTH_LONG).show();
        } else {
            // remove the friend
            contact.remove(users.get(position));
            Toast.makeText(ContactActivity.this, "removed", Toast.LENGTH_LONG).show();
        }
        curUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(ContactActivity.this, "saved", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void addFriendCheckmarks() {
        contact.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if (e == null) {
                    // list returned - look for a match
                    for (int i = 0; i < users.size(); i++) {
                        ParseUser user = users.get(i);

                        for (ParseUser friend : friends) {
                            if (friend.getObjectId().equals(user.getObjectId())) {
                                listView.setItemChecked(i, true);
                            }
                        }
                    }
                } else {
                    Log.e("Error", e.getMessage());
                }
            }
        });
    }

}
