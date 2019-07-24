package com.texascasino.thai;

import android.app.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.BillingResponseCode;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;

//import static com.android.billingclient.api.BillingClient.BillingResponse;

import java.util.List;

//import com.android.billingclient.api.BillingClient.BillingResponse;
public class MainActivity extends Activity  {

    public String TAG = "MainActivity";
    public static MainActivity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;


        Google.InitSDk(activity);

        Button bt = (Button) findViewById(R.id.buy1);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: 11");
                Google.Pay("com.texascasino.thai.gempack1");
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();


    }
}
