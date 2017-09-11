package com.planit.planit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.planit.planit.utils.User;
import com.planit.planit.utils.Utilities;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "RegisterActivity";
    EditText firstName, lastName, email, password, repeatPassword, phoneNumber;

    FirebaseAuth fAuth;
    FirebaseUser fUser;
    DatabaseReference DBUsers, DBEmailToPhones;

    AppCompatButton registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fAuth = FirebaseAuth.getInstance();
        fAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null)
                {
                    fUser = fAuth.getCurrentUser();
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                            .setDisplayName(firstName.getText().toString() + lastName.getText().toString())
                            .build();
                    fUser.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Log.d(TAG, "User profile updated successfully");
                            }
                            else
                            {
                                Log.d(TAG, "User profile update failed");
                            }
                        }
                    });
                    Intent homeIntent = new Intent(RegisterActivity.this, Home.class);
                    startActivity(homeIntent);
                    finish();
                }
            }
        });

        fUser = fAuth.getCurrentUser();
        if (fUser != null)
        {
            // TODO: get to main activity, user is signed in
        }

        DBUsers = FirebaseDatabase.getInstance().getReference("users");
        DBEmailToPhones = FirebaseDatabase.getInstance().getReference("emailsToPhones");

        // initialize handles
        firstName = (EditText)findViewById(R.id.firstname_input);
        lastName = (EditText)findViewById(R.id.lastname_input);
        email = (EditText)findViewById(R.id.email_input);
        password = (EditText)findViewById(R.id.password_input);
        repeatPassword = (EditText)findViewById(R.id.re_password_input);
        phoneNumber = (EditText) findViewById(R.id.phone_input);

        registerButton = (AppCompatButton)findViewById(R.id.register_button);
        registerButton.setOnClickListener(this);
    }

    public void register()
    {
        if(!validate())
        {
            return;
        }
        fAuth.createUserWithEmailAndPassword(email.getText().toString(),
                                                password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                    "Created user successfuly", Toast.LENGTH_SHORT).show();
                            fUser = fAuth.getCurrentUser();
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(firstName.getText().toString() + " " + lastName.getText().toString())
                                    .build();
                            fUser.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                                            User user = new User(firstName.getText().toString(),
                                                                    lastName.getText().toString(),
                                                                    Utilities.encodeKey(email.getText().toString()),
                                                                    null,
                                                                    FirebaseInstanceId.getInstance().getToken());
                                                            DBUsers.child(phoneNumber.getText().toString()).
                                                                    setValue(user.toMapUser());
                                                            DBEmailToPhones.child(Utilities.encodeKey(email.getText().toString())).
                                                                    setValue(phoneNumber.getText().toString());
                                    } else {
                                        Log.d(TAG, "User profile update failed");
                                    }
                                }
                            });
                            Intent homeIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(homeIntent);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "User creation failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public boolean validate() {
        String firstNameStr = firstName.getText().toString();
        String lastNameStr = lastName.getText().toString();
        String emailStr = email.getText().toString();
        String phoneStr = phoneNumber.getText().toString();
        String passwordStr = password.getText().toString();
        String rePasswordStr = repeatPassword.getText().toString();

        if (firstNameStr.isEmpty())
        {
            firstName.setError("First name can't be empty.");
            return false;
        }
        if (lastNameStr.isEmpty())
        {
            lastName.setError("Last name can't be empty.");
            return false;
        }
        if (emailStr.isEmpty())
        {
            email.setError("Email can't be empty.");
            return false;
        }
        if (phoneStr.isEmpty())
        {
            phoneNumber.setError("Phone number can't be empty.");
            return false;
        }
        if (passwordStr.isEmpty())
        {
            password.setError("Password can't be empty.");
            return false;
        }
        if (rePasswordStr.isEmpty())
        {
            repeatPassword.setError("Please repeat password.");
            return false;
        }
        if (!rePasswordStr.equals(passwordStr))
        {
            repeatPassword.setError("Passwords do not match.");
            return false;
        }
        if (phoneStr.length() != 10)
        {
            phoneNumber.setError("Phone must have 10 digits");
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.register_button:
                register();
        }
    }
}
