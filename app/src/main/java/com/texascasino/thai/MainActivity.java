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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

//import com.android.billingclient.api.BillingClient.BillingResponse;
public class MainActivity extends Activity {

    public String TAG = "MainActivity";
    public static MainActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;


        Google.InitSDk(activity);
        Google.setGoogleState(new Google.GoogleState() {

            @Override
            public void onBillingServiceDisconnected() {
                Log.e(TAG, "onBillingServiceDisconnected---");
            }

            @Override
            public void onBillingServiceConnected() {
                Log.e(TAG, "onBillingServiceConnected---");

            }

            @Override
            public void onQuerySkuDetailsDone() {
                Log.e(TAG, "onQuerySkuDetailsDone---");
                TextView text = (TextView) findViewById(R.id.GoodsInfos);
                JSONObject obj = Google.GetGoodInfo();
                Iterator<String> it = obj.keys();
                String s = "";
                try {
                    while (it.hasNext()) {
// 获得key
                        String key = it.next();
                        String value = obj.getString(key);
                        s = s + key + ":" + value + "\n";
                    }
                } catch (JSONException e) {
                    Log.i(TAG, "error---getString");
                }

                Log.i(TAG, "setText--------" + s);
                text.setText(s);
            }
        });

        Button bt = (Button) findViewById(R.id.buy1);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.i(TAG, "onClick: 11");
                Google.Pay("com.texascasino.thai.gempack1");
            }
        });

        Button bt2 = (Button) findViewById(R.id.buy2);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.i(TAG, "onClick: 11");
                Google.Pay("com.texascasino.thai.gempack2");
            }
        });

        Button bt3 = (Button) findViewById(R.id.buy3);
        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.i(TAG, "onClick: 11");
                Google.Pay("com.texascasino.thai.gempack3");
            }
        });

        Button bt4 = (Button) findViewById(R.id.buy4);
        bt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.i(TAG, "onClick: 11");
                Google.Pay("com.texascasino.thai.gempack4");
            }
        });

        Button bt5 = (Button) findViewById(R.id.buy5);
        bt5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.i(TAG, "onClick: 11");
                Google.Pay("com.texascasino.thai.gempack5");
            }
        });

        Button getInfo = (Button) findViewById(R.id.getInfo);
        getInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView text = (TextView) findViewById(R.id.GoodsInfos);
                JSONObject obj = Google.GetGoodInfo();
                Iterator<String> it = obj.keys();
                String s = "";
                try {
                    while (it.hasNext()) {
// 获得key
                        String key = it.next();
                        String value = obj.getString(key);
                        s = s + key + ":" + value + "\n";
                    }
                } catch (JSONException e) {
                    Log.i(TAG, "error---getString");
                }

                Log.i(TAG, "setText--------" + s);
                text.setText(s);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();


    }
}
