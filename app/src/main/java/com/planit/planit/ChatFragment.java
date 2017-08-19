package com.planit.planit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.planit.planit.utils.Event;
import com.planit.planit.utils.Message;
import com.planit.planit.utils.User;

import java.util.ArrayList;

/**
 * Created by HP on 17-Aug-17.
 */

public class ChatFragment extends Fragment implements View.OnClickListener{

    User currentUser;
    Event currentEvent;

    RecyclerView recyclerView;
    ChatAdapter cAdapter;

    EditText messageEditText;
    ImageButton sendButton;

    DatabaseReference mDatabase;
    ChildEventListener messagesListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle extras = getArguments();
        currentUser = new Gson().fromJson(extras.getString("user"), User.class);
        currentEvent = new Gson().fromJson(extras.getString("event"), Event.class);
        messagesListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message m = dataSnapshot.getValue(Message.class);
                cAdapter.addMessage(m);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("eventsChats/" + currentEvent.getKey()).orderByChild("currentTime");
        View view = inflater.inflate(R.layout.chat_fragment_layout, null);
        messageEditText = (EditText) view.findViewById(R.id.message_input);
        sendButton = (ImageButton) view.findViewById(R.id.message_send);
        sendButton.setOnClickListener(this);

        cAdapter = new ChatAdapter();

        recyclerView = (RecyclerView) view.findViewById(R.id.events_list_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(cAdapter);

        mDatabase.child("eventsChats/" + currentEvent.getKey()).addChildEventListener(messagesListener);

        return view;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        mDatabase.child("eventsChats/" + currentEvent.getKey()).removeEventListener(messagesListener);
        cAdapter.clear();

    }

    public void sendMessage()
    {
        if (!validateMessage())
        {
            return;
        }
        String messageContent = messageEditText.getText().toString();
        messageEditText.setText(null);
        Message message = new Message(currentUser.getPhoneNumber(), currentUser.getFullName(),
                messageContent);
        mDatabase.child("eventsChats/" + currentEvent.getKey()).push().setValue(message);
    }

    public boolean validateMessage()
    {
        String messageContent = messageEditText.getText().toString();
        if (messageContent.trim().isEmpty())
        {
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.message_send:
                sendMessage();
        }
    }

    public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {

        class ChatSentViewHolder extends RecyclerView.ViewHolder
        {
            CardView card;
            TextView mTime;
            TextView mContent;

            public ChatSentViewHolder(View view)
            {
                super(view);
                this.card = (CardView) view.findViewById(R.id.message_card);
                this.mTime = (TextView) view.findViewById(R.id.message_time);
                this.mContent = (TextView) view.findViewById(R.id.message_content);
            }
        }

        class ChatReceivedViewHolder extends RecyclerView.ViewHolder
        {
            CardView card;
            TextView mTime;
            TextView mContent;
            TextView mSender;

            public ChatReceivedViewHolder(View view)
            {
                super(view);
                this.card = (CardView) view.findViewById(R.id.message_card);
                this.mTime = (TextView) view.findViewById(R.id.message_time);
                this.mContent = (TextView) view.findViewById(R.id.message_content);
                this.mSender = (TextView) view.findViewById(R.id.message_sender);
            }
        }

        final int SENT_MESSAGE = 0;
        final int RECEIVED_MESSAGE = 1;

        ArrayList<Message> messages;

        public ChatAdapter()
        {
            this.messages = new ArrayList<>();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == SENT_MESSAGE)
            {
                view = LayoutInflater.from(getContext()).inflate(R.layout.chat_item_sent, parent, false);
                return new ChatSentViewHolder(view);
            }
            else
            {
                view = LayoutInflater.from(getContext()).inflate(R.layout.chat_item_received, parent, false);
                return new ChatReceivedViewHolder(view);
            }
        }

        public void addMessage(Message m)
        {
            this.messages.add(m);
            notifyItemInserted(getItemCount());
            recyclerView.smoothScrollToPosition(getItemCount());
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Message currentMessage = this.messages.get(position);

            int viewType = getItemViewType(position);

            if (viewType == SENT_MESSAGE)
            {
                ChatSentViewHolder sentHolder = (ChatSentViewHolder) holder;

                sentHolder.mContent.setText(currentMessage.getContent());
                sentHolder.mTime.setText(currentMessage.getHour());
            }
            else
            {
                ChatReceivedViewHolder receivedHolder = (ChatReceivedViewHolder) holder;
                receivedHolder.mSender.setText(currentMessage.getName());
                receivedHolder.mContent.setText(currentMessage.getContent());
                receivedHolder.mTime.setText(currentMessage.getHour());
            }
        }

        @Override
        public int getItemCount() {
            return this.messages.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (this.messages.get(position).getPhone().equals(currentUser.getPhoneNumber()))
            {
                return SENT_MESSAGE;
            }
            return RECEIVED_MESSAGE;
        }

        public void clear()
        {
            this.messages.clear();
        }
    }
}
