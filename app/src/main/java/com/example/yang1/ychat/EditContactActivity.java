package com.example.yang1.ychat;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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

/**
 * Created by Yang1 on 2/25/16.
 */
public class EditContactActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    protected ParseRelation<ParseUser> contact;
    protected ParseUser curUser;
    protected List<ParseUser> users;
    protected ListView lv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_contact);
        // Show the Up button in the action bar.

        lv = (ListView) findViewById(android.R.id.list);
        lv.setOnItemClickListener(this);

        // buttons
        Button btn_chat = (Button) findViewById(R.id.btn_chat);
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditContactActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button btn_me = (Button) findViewById(R.id.btn_me);
        btn_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditContactActivity.this, PersonalInfoActivity.class);
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
                            EditContactActivity.this,
                            android.R.layout.simple_list_item_checked,
                            usernames);
                    lv.setAdapter(adapter);
                    lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    addFriendCheckmarks();

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditContactActivity.this);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!lv.isItemChecked(position)) {
            contact.add(users.get(position));
            Toast.makeText(EditContactActivity.this, "added", Toast.LENGTH_LONG).show();
            Log.i("contact","add");
        }
        else {
            // remove the friend
            contact.remove(users.get(position));
            Toast.makeText(EditContactActivity.this, "removed", Toast.LENGTH_LONG).show();
            Log.i("contact", "remove");

        }

        curUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("Error", e.getMessage());
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
                                lv.setItemChecked(i, true);
                            }
                        }
                    }
                }
                else {
                    Log.e("Error", e.getMessage());
                }
            }
        });
    }

}
