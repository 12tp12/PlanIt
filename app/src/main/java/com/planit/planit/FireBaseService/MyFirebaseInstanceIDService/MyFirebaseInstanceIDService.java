package com.planit.planit.FireBaseService.MyFirebaseInstanceIDService;

/**
 * Created by תומר on 7/26/2017.
 */
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.planit.planit.Home;
import com.planit.planit.R;
import com.google.firebase.database.DatabaseReference;

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

    private void sendRegistrationToServer(String token) {
//        // Add custom implementation, as needed.
//
//        SharedPreferenceUtils.getInstance(this).setValue(getString(R.string.firebase_cloud_messaging_token), token);
//
//        // To implement: Only if user is registered, i.e. UserId is available in preference, update token on server.
//        int userId = SharedPreferenceUtils.getInstance(this).getIntValue(getString(R.string.user_id), 0);
//        if(userId != 0){
//            // Implement code to update registration token to server
//        }
    }

	/*
	The token may be rotated whenever:
	1. The app deletes Instance ID
	2. The app is restored on a new device
	3. The user uninstalls/reinstall the app
	4. The user clears app data.
	*/
}