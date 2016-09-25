package com.example.katamb.rainbowqrreader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Amon on 9/11/2016.
 */
public class PayActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "PayActivity";
    public Button charge;
    public  EditText amount;

    public InputStream is;
    public TextView balance;
    public String amountx,cardx,emailforbalance,token,tokenx,card;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main_menu);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
      //  setSupportActionBar(toolbar);

       // amount = (EditText)findViewById(R.id.amount);

        Intent intent = getIntent();

        card = intent.getStringExtra("pay_card");

       // Toast.makeText(PayActivity.this, card, Toast.LENGTH_SHORT).show();


         SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
         tokenx = settings.getString("Token","");

       //
/*
        Uri data = getIntent().getData();
        if (data==null) { } else {
            // //String datos=data.toString();
            Toast.makeText(PayActivity.this, data.toString(), Toast.LENGTH_SHORT).show();
        }
*/
       // LocalBroadcastManager.getInstance(this).registerReceiver(tokenReceiver, new IntentFilter("tokenReceiver"));

       charge = (Button)findViewById(R.id.charge);
        balance = (TextView) findViewById(R.id.balance);
/*
        balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(PayActivity.this, tokenx, Toast.LENGTH_SHORT).show();
               // Log.d("Token",tokenx);
                new ResponseBalanceData().execute(emailforbalance);
            }
        });
*/
        mAuth = FirebaseAuth.getInstance();




        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                  // Toast.makeText(PayActivity.this, " Logged in", Toast.LENGTH_LONG).show();
                    emailforbalance = user.getEmail();
                   // new ResponseBalanceData().execute(emailforbalance);

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Toast.makeText(PayActivity.this, " Login failure", Toast.LENGTH_SHORT).show();

                    shiftPage();
                }
                // ...
            }
        };

       // new ResponseBalanceData().execute(emailforbalance);

charge.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
       // dataprocess();
        charge = (Button)findViewById(R.id.charge);
        amount = (EditText)findViewById(R.id.amount);


        //  message = (Button)findViewById(R.id.fab) ;
        // about = (Button)findViewById(R.id.fab2);
        if (amount != null) {
            amountx = amount.getText().toString();
        }
        // cardx = card.toString();

        new ResponsePaymentData().execute(amountx,card);
       new ResponseBalanceData().execute(emailforbalance);

    }
});



    }



    public class ResponsePaymentData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            // These two need to be declared outside the try/catch

            String response = null;
            Log.d(TAG, params[0]);
            Log.d(TAG, params[1]);

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                URL url = new URL("http://ec2-54-191-230-33.us-west-2.compute.amazonaws.com/rainbow/view/codecharge.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

              //  Intent intent = getIntent();

              //  String email = intent.getStringExtra("email");

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("amount",params[0])
                        .appendQueryParameter("card",params[1])
                        .appendQueryParameter("token",tokenx)
                        .appendQueryParameter("seller_email", emailforbalance);
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
                BufferedReader  reader = new BufferedReader(new InputStreamReader(inputStream));

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
               // Toast.makeText(PayActivity.this,"Failed Payment", Toast.LENGTH_SHORT).show();
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Toast.makeText(PayActivity.this,s, Toast.LENGTH_SHORT).show();

        }
    }



    public class ResponseBalanceData extends AsyncTask<String, String, String> {



        @Override
        protected String doInBackground(String... params) {

            // These two need to be declared outside the try/catch

            String response = null;
            //Log.d(TAG, params[0]);
            //Log.d(TAG, params[1]);

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                URL url = new URL("http://ec2-54-191-230-33.us-west-2.compute.amazonaws.com/rainbow/view/sellerbalance.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

             //   Intent intent = getIntent();

               // String email = intent.getStringExtra("email");

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("email",params[0]);
                //.appendQueryParameter("card_buyer", "2016");
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
                BufferedReader  reader = new BufferedReader(new InputStreamReader(inputStream));

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

                 //return response;

                // Toast.makeText(PayActivity.this,response, Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                //  Log.e( e.toString());
                Toast.makeText(PayActivity.this,"No data Currently", Toast.LENGTH_SHORT).show();
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return "0";
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

          balance.setText(s);
            //Toast.makeText(PayActivity.this,s, Toast.LENGTH_SHORT).show();

        }
    }

    public void logout(View v) {

        FirebaseAuth.getInstance().signOut();
       // Toast.makeText(PayActivity.this, " Logout", Toast.LENGTH_SHORT).show();

    }

    public void RefreshData(View v) {

       // FirebaseAuth.getInstance().signOut();
       // Toast.makeText(PayActivity.this, " Logout", Toast.LENGTH_SHORT).show();
        new ResponseBalanceData().execute(emailforbalance);

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

    public void shiftPage(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }





 }
