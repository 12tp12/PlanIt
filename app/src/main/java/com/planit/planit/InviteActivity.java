package com.planit.planit;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.planit.planit.utils.User;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

/**
 * Created by HP on 01-Aug-17.
 */

public class InviteActivity extends AppCompatActivity implements View.OnClickListener {

    DatabaseReference mDatabase;
    final int REQUEST_READ_CONTACTS = 1;
    RecyclerView recyclerView;
    Adapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_invite);

        recyclerView = (RecyclerView) findViewById(R.id.contacts_recycler_view);
        if(recyclerView == null)
        {
            Toast.makeText(getApplicationContext(), "null rv", Toast.LENGTH_SHORT);
            Log.d("rv", "null");
            finish();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "not null rv", Toast.LENGTH_SHORT);
        }
        if(ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
        {
            // permission granted
            //showContacts();
        }
        else
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    showContacts();

                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT);
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void showContacts()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        ContentResolver cr = getContentResolver();
        // get all contacts
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        final ArrayList<User> usersList = new ArrayList<>();

        // has contancts
        if (cur.getCount() > 0) {
            // iterating contacts
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = validatePhone(pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER)));
                        Log.d("PHONE IS", phoneNo);
                        DatabaseReference user = mDatabase.child("users").child(phoneNo);
                        user.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User u = dataSnapshot.getValue(User.class);
                                usersList.add(u);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    pCur.close();
                }
            }
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new Adapter(usersList);
        recyclerView.setAdapter(adapter);
        if (adapter.getItemCount() == 0)
        {
            Toast.makeText(this, "No Contacts", Toast.LENGTH_SHORT);
        }
    }

    public String validatePhone(String phone)
    {
        phone = phone.replace("+972", "0").replaceAll("-", "").replaceAll(" ", "");
        return phone;
    }

    public void onClick(View view)
    {

    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>
    {
        protected class ViewHolder extends RecyclerView.ViewHolder
        {
            public TextView name;
            public TextView phone;

            protected ViewHolder(CardView view)
            {
                super(view);
            }
        }


        private ArrayList<User> usersList;

        public Adapter()
        {
            this.usersList = new ArrayList<>();
        }

        public Adapter(ArrayList<User> list)
        {
            this.usersList = new ArrayList<>();
            this.usersList.addAll(list);
        }

        public void addUser(final User user)
        {
            this.usersList.add(user);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount()
        {
            return usersList.size();
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position)
        {
            final User currentUser = this.usersList.get(position);
            holder.name.setText(currentUser.getFullName());
            holder.phone.setText(currentUser.getPhoneNumber());
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CardView cView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item,
                    parent, false);
            ViewHolder hView = new ViewHolder(cView);
            return hView;
        }
    }
}
