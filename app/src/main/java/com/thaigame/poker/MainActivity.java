package com.thaigame.poker;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


//import static com.android.billingclient.api.BillingClient.BillingResponse;

import com.thaigame.poker.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

//import com.android.billingclient.api.BillingClient.BillingResponse;
public class MainActivity extends Activity {

    public String TAG = "MainActivity";
    public static MainActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        activity = this;



        Google.setGoogleState(new Google.GoogleState() {

            @Override
            public void onBillingServiceDisconnected() {
                Log.e(TAG, "onBillingServiceDisconnected---");
            }

            @Override
            public void onBillingSetupFinished() {
                Log.e(TAG, "onBillingSetupFinished---");
            }

            @Override
            public void onBillingSetupError(int code) {
                Log.e(TAG, "onBillingSetupError---"+code);
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
        Google.InitSDk(activity);

        Button bt = (Button) findViewById(R.id.buy1);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.i(TAG, "onClick: 11");
                Google.Pay("com.thaigame.poker.gempack1");
            }
        });

        Button bt2 = (Button) findViewById(R.id.buy2);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.i(TAG, "onClick: 11");
                Google.Pay("com.thaigame.poker.gempack2");
            }
        });

        Button bt3 = (Button) findViewById(R.id.buy3);
        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.i(TAG, "onClick: 11");
                Google.Pay("com.thaigame.poker.gempack3");
            }
        });

        Button bt4 = (Button) findViewById(R.id.buy4);
        bt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.i(TAG, "onClick: 11");
                Google.Pay("com.thaigame.poker.gempack4");
            }
        });

        Button bt5 = (Button) findViewById(R.id.buy5);
        bt5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.i(TAG, "onClick: 11");
                Google.Pay("com.thaigame.poker.gempack5");
            }
        });

        Button getInfo = (Button) findViewById(R.id.getInfo);
        getInfo.setVisibility(View.INVISIBLE);
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

        Button tosecond = (Button) findViewById(R.id.tosecond);
        tosecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick:tosecond ");
                Intent intent = new Intent(MainActivity.this,SecondActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();


    }
}
