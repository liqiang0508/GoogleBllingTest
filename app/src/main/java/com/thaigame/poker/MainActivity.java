package com.thaigame.poker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.billingclient.api.ProductDetails;
import com.game.utils.DownLoadUtils;
import com.game.utils.IDownloadlister;
import com.superz.moga.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class MainActivity extends Activity {

    public String TAG = "MainActivity";
    public static MainActivity activity;
    private JSONArray ProductList = new JSONArray();
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
                Log.e(TAG, "onBillingSetupError---" + code);
            }

            @Override
            public void onQuerySkuDetailsDone() {
                Log.e(TAG, "onQuerySkuDetailsDone---");
                TextView text = (TextView) findViewById(R.id.GoodsInfos);
                String s = "";
                List<ProductDetails> objs = Google.GetGoodInfo();
                for (ProductDetails skuDetail : objs) {
                    String sku = skuDetail.getProductId();
                    String price = skuDetail.getOneTimePurchaseOfferDetails().getFormattedPrice();
                    s = s + sku + ":" + price + "\n";
                }
                text.setText(s);
            }
        });
        for (int i = 1; i <=4; i++) {
            String SKU_PREFIX = "moga_test_pay_00" + i;
            ProductList.put(SKU_PREFIX);
        }
        Log.i(TAG, ProductList.toString());
        Google.InitSDk( ProductList.toString());
        Button bt = (Button) findViewById(R.id.buy1);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Google.Pay((String) ProductList.get(1));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Button bt2 = (Button) findViewById(R.id.buy2);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Google.Pay((String) ProductList.get(2));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Button bt3 = (Button) findViewById(R.id.buy3);
        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Google.Pay((String) ProductList.get(3));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Button bt4 = (Button) findViewById(R.id.buy4);
        bt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Google.Pay((String) ProductList.get(4));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Button bt5 = (Button) findViewById(R.id.buy5);
        bt5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.i(TAG, "onClick: 11");
                try {
                    Google.Pay((String) ProductList.get(1));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Button getInfo = (Button) findViewById(R.id.getInfo);
//        getInfo.setVisibility(View.INVISIBLE);
        getInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView text = (TextView) findViewById(R.id.GoodsInfos);
                String s = "";
                List<ProductDetails> objs = Google.GetGoodInfo();
                for (ProductDetails skuDetail : objs) {
                    String sku = skuDetail.getProductId();
                    String price = skuDetail.getOneTimePurchaseOfferDetails().getFormattedPrice();
                    s = s + sku + ":" + price + "\n";
                }
                text.setText(s);
            }
        });

        Button tosecond = (Button) findViewById(R.id.tosecond);
        tosecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.i(TAG, "onClick:tosecond ");
                Intent intent = new Intent(MainActivity.this,SecondActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
//                DownLoadUtils.builder()
//                        .setContext(activity)
//                        .setFileName("666.apk")
//                        .setLister(new IDownloadlister() {
//                            @Override
//                            public void onDownloadStart() {
//                                Log.i(TAG, "onDownloadStart");
//                            }
//
//                            @Override
//                            public void onDownloadPause() {
//                                Log.i(TAG, "onDownloadPause");
//                            }
//
//                            @Override
//                            public void onDownloadRunning(int current) {
//                                Log.i(TAG, "onDownloadRunning===" + current);
//                            }
//
//                            @Override
//                            public void onSuccess(String filePath) {
//                                DownLoadUtils.builder().installAPK(filePath);
//                            }
//
//                            @Override
//                            public void onFailed() {
//                                Log.i(TAG, "onFailed");
//                            }
//
//
//                        })
//                        .download();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();


    }
}
