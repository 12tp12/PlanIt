package com.planit.planit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.planit.planit.utils.Utilities;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    // Firebase Authentication
    FirebaseAuth fAuth;
    FirebaseUser fUser;

    // User input and buttons
    EditText email;
    EditText password;
    AppCompatButton loginButton;
    AppCompatButton registerButton;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialize firebase auth
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();

        // initialize handles
        email = (EditText)findViewById(R.id.email_input);
        password = (EditText) findViewById(R.id.password_input);
        loginButton = (AppCompatButton) findViewById(R.id.login_button);
        registerButton = (AppCompatButton) findViewById(R.id.login_register_button);

        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }

    public void signIn()
    {
        if(!validate())
        {
            return;
        }

        fAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            fUser = fAuth.getCurrentUser();
                            final DatabaseReference fDatabase = FirebaseDatabase.getInstance().getReference();
                            fDatabase.child("emailsToPhones").child(Utilities.encodeKey(fAuth.getCurrentUser().getEmail()))
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(final DataSnapshot dataSnapshot) {
                                                    fDatabase.child("users").child(dataSnapshot.getValue(String.class))
                                                            .child("token").setValue(FirebaseInstanceId.getInstance().getToken());
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                            String welcomeText = "Welcome " + fUser.getDisplayName();
                            Toast.makeText(getApplicationContext(), welcomeText, Toast.LENGTH_SHORT).show();
                            Intent homeIntent = new Intent(getApplicationContext(), Home.class);
                            startActivity(homeIntent);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public boolean validate()
    {
        String emailText = email.getText().toString();
        String passText = password.getText().toString();

        if(emailText.isEmpty())
        {
            email.setError("Email cannot be empty.");
            return false;
        }
        if(passText.isEmpty())
        {
            password.setError("Password cannot be empty.");
            return false;
        }

        return true;
    }

    public void register()
    {

        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.login_button:
                signIn();
                break;
            case R.id.login_register_button:
                register();
                break;
        }
    }
}
