package com.example.gpstracker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity2 extends AppCompatActivity {
    private Button regButton;

    private EditText user, pass, name;
    private FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;

    // creating a variable for our Database
    // Reference for Firebase.
    DatabaseReference databaseReference;
    String UID;
    UserData userData;
    String email1;
    String name1;
    String pass1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        user = findViewById(R.id.usernameReg);
        pass = findViewById(R.id.passwordReg);
        name = findViewById(R.id.name);
        regButton = findViewById(R.id.Reg);

        name1 = name.getText().toString();
        email1 = user.getText().toString();
        pass1 = pass.getText().toString();

        userData= new UserData();

//        userInfo = new
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        // below line is used to get reference for our database.
//

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registerNewUser();
            }
        });

    }

    private void addDatatoFirebase(String name, String user, String pass) {
        // below 3 lines of code is used to set
        // data in our object class.
        userData.setUserName(name);
        userData.setEmail(user);
        userData.setPass(pass);

        // we are use add value event listener method
        // which is called with database reference.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // inside the method of on Data change we are setting
                // our object class to our database reference.
                // data base reference will sends data to firebase.
                databaseReference.setValue(userData);

                // after adding this data we are showing toast message.
                Toast.makeText(MainActivity2.this, "data added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // if the data is not added or it is cancelled then
                // we are displaying a failure toast message.
                Toast.makeText(MainActivity2.this, "Fail to add data " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerNewUser() {

        // show the visibility of progress bar to show loading
//        progressbar.setVisibility(View.VISIBLE);

        // Take the value of two edit texts in Strings
        String email, password, name1;
        email = user.getText().toString();
        password = pass.getText().toString();
        name1 = name.getText().toString();


        // Validations for input email and password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter email!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter password!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }


        if (TextUtils.isEmpty(name1)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter full name!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // create new user or register new user
        mAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                            "Registration successful!",
                                            Toast.LENGTH_LONG)
                                    .show();

                            // hide the progress bar
//                            progressBar.setVisibility(View.GONE);

                            // if the user created intent to login activity
                            UID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                            databaseReference = firebaseDatabase.getReference(UID);
                            addDatatoFirebase(name1, email, password);
                            Toast.makeText(getApplicationContext(),
                                            UID,
                                            Toast.LENGTH_LONG)
                                    .show();
                            Intent intent
                                    = new Intent(MainActivity2.this,
                                    MainActivity.class);
                            startActivity(intent);
                        } else {

                            // Registration failed
                            Toast.makeText(
                                            getApplicationContext(),
                                            "Registration failed!!"
                                                    + " Please try again later",
                                            Toast.LENGTH_LONG)
                                    .show();

                            // hide the progress bar
//                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}