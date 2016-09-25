package com.example.katamb.rainbowqrreader;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private Button scan_btn, Display_btn;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "MainActivity";

    EditText emailx, passwordx;
    public String emailz, passwordz, email, tokenx;
    Button signin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        signin = (Button) findViewById(R.id.button);
        emailx = (EditText) findViewById(R.id.email);
        passwordx = (EditText) findViewById(R.id.password);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        tokenx = settings.getString("Token", "");

        emailz = emailx.getText().toString();
        passwordz = passwordx.getText().toString();
        //firbase authentication
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    email = user.getEmail();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                     Toast.makeText(MainActivity.this, " Logged in", Toast.LENGTH_SHORT).show();

                    shiftPage(tokenx, email.toString());

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Toast.makeText(MainActivity.this, " Login failure", Toast.LENGTH_SHORT).show();
                    //signInRegisteredUser("amonxnyee@gmail.com", "anitadavid");
                }
                // ...
            }
        };

        // ...

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Snackbar.make(v, emailx.getText().toString(), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();*/
                try {
                   // Snackbar.make(v, "Trying ...", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    Toast.makeText(MainActivity.this, "Trying ...", Toast.LENGTH_SHORT).show();
                    mAuth.signInWithEmailAndPassword(emailx.getText().toString(), passwordx.getText().toString())
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        Log.w(TAG, "signInWithEmail:failed", task.getException());
                                        Toast.makeText(MainActivity.this, "Failed Login Attempt", Toast.LENGTH_SHORT).show();
                                       /* Intent intent = new Intent(MainActivity.this,PayActivity.class);
                                        startActivity(intent);*/

                                    }

                                    // ...
                                }
                            });
                } catch (Exception e) {

                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();

                }
            }

        });




    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    public void shiftPage(String tokenz, String emaily) {

        new SendDeviceDetails().execute(tokenz, emaily);
        Intent intent = new Intent(MainActivity.this, ScanData.class);
        intent.putExtra("email", email);
        // intent.putExtra("lastName", "Your Last Name Here");
        startActivity(intent);
    }




    public class SendDeviceDetails extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {

            // These two need to be declared outside the try/catch

            String response = null;
            //Log.d(TAG, params[0]);
            //Log.d(TAG, params[1]);

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                URL url = new URL("http://ec2-54-191-230-33.us-west-2.compute.amazonaws.com/rainbow/view/devicetokenseller.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                //   Intent intent = getIntent();

                // String email = intent.getStringExtra("email");

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("token", params[0])
                        .appendQueryParameter("email", params[1]);
                String query = builder.build().getEncodedQuery();

                OutputStream os = conn.getOutputStream();
                // InputStream is = conn.getInputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                conn.connect();

                // Toast.makeText(PayActivity.this,"Payment Done...", Toast.LENGTH_SHORT).show();


                // Read the input stream into a String
                InputStream inputStream = conn.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                response = buffer.toString();
                Log.d(TAG, response);
                // return response;

                // Toast.makeText(PayActivity.this,response, Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                //  Log.e( e.toString());
                // Toast.makeText(MainActivity.this, "No data Currently", Toast.LENGTH_SHORT).show();
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return "0";
            }
            return response;
        }

    }
}






