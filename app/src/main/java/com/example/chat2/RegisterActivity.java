package com.example.chat2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPass;
    private EditText editTextRepeatPass;
    private CheckBox checkBoxAccept;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        editTextUsername = findViewById(R.id.ET_reg_username);
        editTextEmail = findViewById(R.id.ET_reg_email);
        editTextPass = findViewById(R.id.ET_reg_password);
        editTextRepeatPass = findViewById(R.id.ET_reg_passwordRepeat);
        checkBoxAccept = findViewById(R.id.checkbox_reg_accept);
        progressDialog = new ProgressDialog(this);

    }

    private boolean regexValidation (String regexPattern, String textToCheck){
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(textToCheck);

        return matcher.matches();
    }

    //this function returns:
    //1 - valid username
    //2 - this username contains invalid characters (valid characters: letters, numbers and _)
    //3 - this username is too short (less than 4 characters)
    //4 - this username is too long  (more than 20 characters)
    //5 - this username is taken
    private int usernameValidation(String username){
        //verify if the username is already taken
        if(!regexValidation("\\w+", username))//username contains non-word characters
            return 2;
        else if(username.length() < 4)
                 return  3;
            else if(username.length() > 20)
                     return 4;
                 else return 1;

    }

    //this function returns:
    //1 - valid password
    //2 - this password doesn't have a valid format (lowercase and uppercase letters, numbers and at least one special character)
    //3 - this password is too short (less than 6 characters)
    //4 - this password is too long (more than 30 characters)
    private  int passwordValidation(String password){

        Pattern letters = Pattern.compile("[a-zA-z]");
        Pattern digits = Pattern.compile("[0-9]");
        Pattern specials = Pattern.compile ("[!@#$%&*()_+=|<>?{}\\[\\]~-]");

        Matcher hasLetter = letters.matcher(password);
        Matcher hasDigit = digits.matcher(password);
        Matcher hasSpecial = specials.matcher(password);

        if(!(hasLetter.find() && hasDigit.find() && hasSpecial.find()))
            return 2;
        else
            if(password.length()<6)
                return 3;
            else if(password.length()>30)
                return 4;
            return 1;
    }

    public void register(View view){
        final String username = editTextUsername.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String pass = editTextPass.getText().toString().trim();
        String repeatPass = editTextRepeatPass.getText().toString().trim();

        if(TextUtils.isEmpty(username)){
            //username is empty
            Toast.makeText(this, "Username field can not be empty.", Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(email)){
            //email is empty
            Toast.makeText(this, "Email field can not be empty", Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(pass)){
            //password is empty
            Toast.makeText(this, "Password field can not be empty", Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(repeatPass)){
            //repeat password is empty
            Toast.makeText(this, "Repeat password field can not be empty", Toast.LENGTH_LONG).show();
            return;
        }

        switch (usernameValidation(username)){
            case 1: break;
            case 2:
                Toast.makeText(this, "Username can contain only: upper and lower case letters, numbers and _ ", Toast.LENGTH_LONG).show();
                return;
            case 3:
                Toast.makeText(this, "Username too short. Min length: 4", Toast.LENGTH_LONG).show();
                return;
            case 4:
                Toast.makeText(this, "Username too long. Max length: 20", Toast.LENGTH_LONG).show();
                return;
            case 5:
                Toast.makeText(this, "Username already exists", Toast.LENGTH_LONG).show();
                return;
        }

        if(!regexValidation("[\\w+\\.]+@[a-zA-Z0-9-]+\\.[a-zA-Z]+", email)){
            //email doesn't have an email format
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_LONG).show();
            return;
        }

        switch (passwordValidation(pass)){
            case 1: break;
            case 2:
                Toast.makeText(this, "Password should contain: lower & upper case letters, digits and at least one special character", Toast.LENGTH_LONG)
                        .show();
                return;
            case 3:
                Toast.makeText(this, "Password too short. Min length: 6", Toast.LENGTH_LONG).show();
                return;
            case 4:
                Toast.makeText(this, "Password too long. Max length: 30", Toast.LENGTH_LONG).show();
                return;
        }

        if(!repeatPass.equals(pass))
            return;

        progressDialog.setMessage("Creating account...");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //store the additional fields in firebase database
                            final ChatUser user = new ChatUser(username);
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(uid)
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        finish();
                                        Toast.makeText(RegisterActivity.this, R.string.registration_success, Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getApplicationContext(), MainChatActivity.class);
                                        intent.putExtra("user", user);
                                        intent.putExtra("firebaseUser", mAuth.getCurrentUser());
                                        startActivity(intent);
                                    }
                                    else {
                                        Toast.makeText(RegisterActivity.this, R.string.registration_failure, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                            //create a message table for all messages this user will send
                            //this table will contain as children all users' UIDs the current user sent messages to
                            //it won't contain the messages sent to the current user
                            FirebaseDatabase.getInstance().getReference("Messages").child(uid);
                        }else
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                    }
                });

    }
}
