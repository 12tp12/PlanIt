package com.planit.planit.FireBaseService.MyFirebaseInstanceIDService;

/**
 * Created by תומר on 7/26/2017.
 */
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.planit.planit.Home;
import com.planit.planit.LoginActivity;
import com.planit.planit.R;
import com.google.firebase.database.DatabaseReference;
import com.planit.planit.utils.User;
import com.planit.planit.utils.Utilities;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIIDService";
    String refreshedToken;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(final String token) {

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        if(fAuth.getCurrentUser() != null)
        {
            final DatabaseReference fDatabase = FirebaseDatabase.getInstance().getReference();
            fDatabase.child("emailsToPhones").child(Utilities.encodeKey(fAuth.getCurrentUser().getEmail()))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            fDatabase.child("users").child(dataSnapshot.getValue(String.class))
                                    .child("token").setValue(token);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    

	/*
	The token may be rotated whenever:
	1. The app deletes Instance ID
	2. The app is restored on a new device
	3. The user uninstalls/reinstall the app
	4. The user clears app data.
	*/
}