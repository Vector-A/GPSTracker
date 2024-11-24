package com.example.gpstracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ImageView logoImage;
    private CardView loginCard;
    private Button loginButton;

    private EditText user, pass;
    private FirebaseAuth mAuth;

    DatabaseReference ref;
    String value;
    String user1;
    String pass1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        final TextView Reg = findViewById(R.id.textView2);
        user = findViewById(R.id.usernameInput);
        pass = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
//        // Initialize views
//        initViews();
//
//        // Apply responsive adjustments based on screen size
//        adjustLayoutForScreenSize();
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        ref = database.getReference("Data");
        mAuth = FirebaseAuth.getInstance();
//
////        ref.setValue("this1/n");
//
////        getData();
//
//
        Reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(i);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(MainActivity.this, MainActivity3.class);
//                i.putExtra("UID", currentUser);
//                startActivity(i);
                loginUserAccount();
            }
        });
    }

    private void loginUserAccount() {

        // show the visibility of progress bar to show loading
//        progressbar.setVisibility(View.VISIBLE);

        // Take the value of two edit texts in Strings
        String email, password;
        email = user.getText().toString();
        password = pass.getText().toString();

        // validations for input email and password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email!!", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!!", Toast.LENGTH_LONG).show();
            return;
        }

        // signin existing user
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Login successful!!", Toast.LENGTH_LONG).show();


                    Intent i = new Intent(MainActivity.this, MainActivity3.class);
                    i.putExtra("UID", currentUser);
                    startActivity(i);
                } else {

                    // sign-in failed
                    Toast.makeText(getApplicationContext(), "Login failed!!", Toast.LENGTH_LONG).show();

                    // hide the progress bar
//                                    progressbar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getData() {

        // calling add value event listener method
        // for getting the values from database.
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // this method is call to get the realtime
                // updates in the data.
                // this method is called when the data is
                // changed in our Firebase console.
                // below line is for getting the data from
                // snapshot of our database.
                value = snapshot.getValue(String.class);
                Toast.makeText(MainActivity.this, value, Toast.LENGTH_SHORT).show();
                // after getting the value we are setting
                // our value to our text view in below line.

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // calling on cancelled method when we receive
                // any error or we are not able to get the data.
                Toast.makeText(MainActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initViews() {
        logoImage = findViewById(R.id.logoImage);
        loginCard = findViewById(R.id.loginCard);
        loginButton = findViewById(R.id.loginButton);
        user = findViewById(R.id.usernameInput);
        pass = findViewById(R.id.passwordInput);
    }

    private void adjustLayoutForScreenSize() {
        // Get screen width in dp
        float screenWidthDp = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().density;

        // Get screen height in dp
        float screenHeightDp = getResources().getDisplayMetrics().heightPixels / getResources().getDisplayMetrics().density;

        // For 5" screens (typically around 360dp width)
        if (screenWidthDp <= 360) {
            // Adjust logo size
            ConstraintLayout.LayoutParams logoParams = (ConstraintLayout.LayoutParams) logoImage.getLayoutParams();
            logoParams.matchConstraintPercentWidth = 0.4f; // 40% of screen width
            logoImage.setLayoutParams(logoParams);

            // Adjust card margins
            ConstraintLayout.LayoutParams cardParams = (ConstraintLayout.LayoutParams) loginCard.getLayoutParams();
            cardParams.topMargin = dpToPx(16); // Smaller top margin
            loginCard.setLayoutParams(cardParams);

            // Adjust padding
            loginCard.setContentPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));

            // Adjust text sizes
            user.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            pass.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            loginButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }
        // For 6" screens (typically around 400dp width)
        else {
            // Adjust logo size
            ConstraintLayout.LayoutParams logoParams = (ConstraintLayout.LayoutParams) logoImage.getLayoutParams();
            logoParams.matchConstraintPercentWidth = 0.5f; // 50% of screen width
            logoImage.setLayoutParams(logoParams);

            // Adjust card margins
            ConstraintLayout.LayoutParams cardParams = (ConstraintLayout.LayoutParams) loginCard.getLayoutParams();
            cardParams.topMargin = dpToPx(32); // Larger top margin
            loginCard.setLayoutParams(cardParams);

            // Adjust padding
            loginCard.setContentPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

            // Adjust text sizes
            user.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            pass.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            loginButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}