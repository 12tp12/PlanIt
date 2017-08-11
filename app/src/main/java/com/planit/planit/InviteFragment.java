package com.planit.planit;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v4.app.ListFragment;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


/**
 * Created by HP on 03-Aug-17.
 */

public class InviteFragment extends ListFragment implements
        AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>
{
    private ContactsAdapter mAdapter; // The main query adapter
    private DatabaseReference mDatabase;

    private String searchTerm;

    public InviteFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new ContactsAdapter(getActivity());
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.invite_list_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setListAdapter(mAdapter);
        getListView().setOnItemClickListener(this);
        getLoaderManager().initLoader(ContactsQuery.QUERY_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri contentUri = ContactsQuery.CONTENT_URI;

        if (searchTerm != null) {
            contentUri = Uri.withAppendedPath(ContactsQuery.CONTENT_URI_FILTER, Uri.encode(searchTerm));
        }
        return new CursorLoader(getActivity(),
                contentUri,
                ContactsQuery.PROJECTION,
                ContactsQuery.SELECTION,
                ContactsQuery.SELECTION_ARGS,
                ContactsQuery.SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final ContactsAdapter adapter = (ContactsAdapter)parent.getAdapter();
        ContactsAdapter.ViewHolder mView = (ContactsAdapter.ViewHolder) view.getTag();
        if (!mView.name.isEnabled())
        {
            Toast.makeText(getContext(), "Oops! Seems like " + mView.name.getText().toString()
            + " doesn't have PlanIt installed.", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("CLICK DEBUG", "in onItemClick, phone is " + mView.phone.getText().toString());
        final String phoneNumber = mView.phone.getText().toString();

        adapter.setSelected(phoneNumber);
        adapter.notifyDataSetChanged();
    }

    public void onQueryNotify(String newText)
    {
        if (newText == null || newText.isEmpty())
        {
            searchTerm = null;
            getLoaderManager().restartLoader(
                    ContactsQuery.QUERY_ID, null, InviteFragment.this);
            return;
        }
        searchTerm = newText;
        // Restarts the loader. This triggers onCreateLoader(), which builds the
        // necessary content Uri from mSearchTerm.
        getLoaderManager().restartLoader(
                ContactsQuery.QUERY_ID, null, InviteFragment.this);
    }

    public HashMap<String, Boolean> getSelectedContacts()
    {
        return this.mAdapter.mSelected;
    }

    private class ContactsAdapter extends CursorAdapter
    {
        private LayoutInflater mInflater;
        private HashMap<String, Boolean> mSelected;

        public ContactsAdapter(Context context)
        {
            super(context, null, 0);

            mInflater = LayoutInflater.from(context);
            mSelected = new HashMap<>();
        }

        public void setSelected(String number)
        {
            if (mSelected.containsKey(number))
            {
                mSelected.remove(number);
                return;
            }
            mSelected.put(number, true);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            // Inflates the list item layout.
            final CardView itemLayout =
                    (CardView)mInflater.inflate(R.layout.contact_item, parent, false);

            // Creates a new ViewHolder in which to store handles to each view resource. This
            // allows bindView() to retrieve stored references instead of calling findViewById for
            // each instance of the layout.
            final ViewHolder holder = new ViewHolder();
            holder.card = itemLayout;
            holder.name = (TextView) itemLayout.findViewById(R.id.invite_name_text);
            holder.phone = (TextView) itemLayout.findViewById(R.id.invite_phone_number);

            // Stores the resourceHolder instance in itemLayout. This makes resourceHolder
            // available to bindView and other methods that receive a handle to the item view.
            itemLayout.setTag(holder);

            // Returns the item layout view
            return itemLayout;
        }

        @Override
        public void bindView(View view, Context context, final Cursor cursor) {
            final ViewHolder mView = (ViewHolder) view.getTag();

            mView.name.setText(cursor.getString(ContactsQuery.DISPLAY_NAME));
            String phone = validatePhone(cursor.getString(ContactsQuery.NUMBER));
            if (phone.equals(""))
            {
                view.setVisibility(View.INVISIBLE);
            }
            else
            {
                mView.phone.setText(phone);
            }
            mDatabase.child("users").child(phone).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists())
                            {
                                Log.d("CLICK DEBUG", "phone " + dataSnapshot.getKey() +
                                        " doesnt exist");
                                // make text grayed out so user will know this contact
                                // is unavailable
                                mView.name.setEnabled(false);
                                mView.phone.setEnabled(false);
                                return;
                            }
                            mView.name.setEnabled(true);
                            mView.phone.setEnabled(true);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    }
            );
            if(mSelected.containsKey(phone) && mSelected.get(phone))
            {
                Log.d("CLICK DEBUG", "item is selected");
                mView.card.setCardBackgroundColor(Color.GRAY);
            }
            else
            {
                mView.card.setCardBackgroundColor(Color.parseColor("#FFFFFFFF"));
            }
        }

        public String validatePhone(String phone)
        {
            phone = phone.trim().replace("+972", "0").replace(" ","").replace("-","");
            if(phone.length() != 10)
            {
                return "";
            }
            return phone;
        }

        public class ViewHolder
        {
            CardView card;
            TextView name;
            TextView phone;
        }
    }
    /**
     * This interface defines constants for the Cursor and CursorLoader, based on constants defined
     * in the {@link android.provider.ContactsContract.CommonDataKinds.Phone} class.
     */
    public interface ContactsQuery {

        // An identifier for the loader
        final static int QUERY_ID = 1;

        // A content URI for the Contacts table
        final static Uri CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        final static Uri CONTENT_URI_FILTER = ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI;

        // The selection clause for the CursorLoader query. The search criteria defined here
        // restrict results to contacts that have a display name and are linked to visible groups.
        // Notice that the search on the string provided by the user is implemented by appending
        // the search string to CONTENT_FILTER_URI.
        @SuppressLint("InlinedApi")
        final static String SELECTION =
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + "<>''"
                        + " AND " + ContactsContract.CommonDataKinds.Phone.IN_VISIBLE_GROUP + "=1"
                        + " AND " + ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + " > 0"
                        + " AND " + ContactsContract.CommonDataKinds.Phone.TYPE + "=" +
                                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                        + " AND (" + ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE ? OR "
                        + ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE ? ) AND " +
                        ContactsContract.CommonDataKinds.Phone.ACCOUNT_TYPE_AND_DATA_SET + "= ?";

        final static String[] SELECTION_ARGS = {"+972_________", "05________", "com.whatsapp"};

        // The desired sort order for the returned Cursor. In Android 3.0 and later, the primary
        // sort key allows for localization. In earlier versions. use the display name as the sort
        // key.
        @SuppressLint("InlinedApi")
        final static String SORT_ORDER =
                ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY;

        // The projection for the CursorLoader query. This is a list of columns that the Contacts
        // Provider should return in the Cursor.
        @SuppressLint("InlinedApi")
        final static String[] PROJECTION = {

                // The contact's row id
                ContactsContract.CommonDataKinds.Phone._ID,

                // A pointer to the contact that is guaranteed to be more permanent than _ID. Given
                // a contact's current _ID value and LOOKUP_KEY, the Contacts Provider can generate
                // a "permanent" contact URI.
                ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,

                // In platform version 3.0 and later, the Contacts table contains
                // DISPLAY_NAME_PRIMARY, which either contains the contact's displayable name or
                // some other useful identifier such as an email address. This column isn't
                // available in earlier versions of Android, so you must use Contacts.DISPLAY_NAME
                // instead.
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,

                ContactsContract.CommonDataKinds.Phone.NUMBER,

                // The sort order column for the returned Cursor, used by the AlphabetIndexer
                SORT_ORDER,
        };

        // The query column numbers which map to each value in the projection
        final static int ID = 0;
        final static int LOOKUP_KEY = 1;
        final static int DISPLAY_NAME = 2;
        final static int NUMBER = 3;
        final static int SORT_KEY = 4;
    }
}
