package com.planit.planit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

        // initialize user input and buttons
        email = (EditText)findViewById(R.id.email_input);
        password = (EditText) findViewById(R.id.password_input);
        loginButton = (AppCompatButton) findViewById(R.id.login_button);
        registerButton = (AppCompatButton) findViewById(R.id.register_button);

        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }

    public void signIn()
    {
        if(!validateForm())
        {
            return;
        }
    }

    public boolean validateForm()
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
            password.setError("Email cannot be empty.");
            return false;
        }

        return true;
    }

    public void register()
    {

    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.login_button:
                signIn();
                break;
            case R.id.register_button:
                register();
                break;
        }
    }
}
