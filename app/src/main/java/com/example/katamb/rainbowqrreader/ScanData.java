package com.example.katamb.rainbowqrreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * Created by Amon on 9/23/2016.
 */
public class ScanData extends AppCompatActivity {
    private Button scan_btn, Display_btn;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scan_btn = (Button) findViewById(R.id.scan_btn);
    //final Activity activity = this;

    scan_btn.setOnClickListener(new View.OnClickListener() {

        @Override

        public void onClick(View view) {

            IntentIntegrator integrator = new IntentIntegrator(ScanData.this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            integrator.setPrompt("Scan");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(false);
            integrator.setBarcodeImageEnabled(false);
            integrator.initiateScan();
        }
    });


        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                   // email = user.getEmail();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                   // Toast.makeText(ScanData.this, " Logged in", Toast.LENGTH_SHORT).show();

                   // shiftPage(tokenx, email.toString());

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Toast.makeText(ScanData.this, " Login failure", Toast.LENGTH_SHORT).show();

                    shiftPage();
                }
                // ...
            }
        };

    }
    // });


    //  }

    @Override

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
            } else {


                //  Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();

                Intent intent = new Intent(ScanData.this,PayActivity.class);
                intent.putExtra("pay_card",result.getContents());
                startActivity(intent);
            }
        } else {

            super.onActivityResult(requestCode, resultCode, data);

        }
    }

    public void logout(View v) {
        FirebaseAuth.getInstance().signOut();
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


