package com.thaigame.poker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.billingclient.api.ProductDetails;
//import com.game.utils.DownLoadUtils;
//import com.game.utils.IDownloadlister;
import com.config.Config;
import com.superz.moga.BuildConfig;
import com.superz.moga.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
        //全屏显示
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        getWindow().setAttributes(lp);
        adapterNotchScreen();
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
                Log.e(TAG, "onQuerySkuDetailsDone--1-"+s);
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
                    Google.Pay((String) ProductList.get(0));
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
                    Google.Pay((String) ProductList.get(1));
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
                    Google.Pay((String) ProductList.get(2));
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
                    Google.Pay((String) ProductList.get(3));
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
                Log.i(TAG, "getInfo " + s);
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

        //
        TextView tx = findViewById(R.id.textView2);
        if(tx!=null)
        {
            String name = BuildConfig.CHANNEL;//Config.chanelName;
            tx.setText(name);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    public static float[] getSafeArea() {

        if (android.os.Build.VERSION.SDK_INT >= 28) {
            do {
                Object windowInsectObj = activity.getWindow().getDecorView().getRootWindowInsets();

                if(windowInsectObj == null) break;

                Class<?> windowInsets = WindowInsets.class;
                try {
                    Method wiGetDisplayCutout = windowInsets.getMethod("getDisplayCutout");
                    Object cutout = wiGetDisplayCutout.invoke(windowInsectObj);

                    if(cutout == null) break;

                    Class<?> displayCutout = cutout.getClass();
                    Method dcGetLeft = displayCutout.getMethod("getSafeInsetLeft");
                    Method dcGetRight = displayCutout.getMethod("getSafeInsetRight");
                    Method dcGetBottom = displayCutout.getMethod("getSafeInsetBottom");
                    Method dcGetTop = displayCutout.getMethod("getSafeInsetTop");

                    if (dcGetLeft != null && dcGetRight != null && dcGetBottom != null && dcGetTop != null) {
                        int left = (Integer) dcGetLeft.invoke(cutout);
                        int right = (Integer) dcGetRight.invoke(cutout);
                        int top = (Integer) dcGetTop.invoke(cutout);
                        int bottom = (Integer) dcGetBottom.invoke(cutout);
                        return new float[]{top, left, bottom, right};
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }while(false);
        }
        return new float[]{0,0,0,0};
    }

    private void adapterNotchScreen() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            final View decorView = getWindow().getDecorView();
            decorView.post(new Runnable() {
                @Override
                public void run() {
                    WindowInsets rootWindowInsets = decorView.getRootWindowInsets();
                    if (rootWindowInsets != null) {
                        DisplayCutout displayCutout = rootWindowInsets.getDisplayCutout();
                        // 当全屏顶部显示黑边时，getDisplayCutout()返回为null
                        /**
                         * 在全屏时可以设置 attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                         * 让屏幕延伸到刘海屏区域
                         */
                        if (displayCutout == null)
                            return;
                        Log.e("TAG", "安全区域距离屏幕左边的距离 SafeInsetLeft:" + displayCutout.getSafeInsetLeft());
                        Log.e("TAG", "安全区域距离屏幕右部的距离 SafeInsetRight:" + displayCutout.getSafeInsetRight());
                        Log.e("TAG", "安全区域距离屏幕顶部的距离 SafeInsetTop:" + displayCutout.getSafeInsetTop());
                        Log.e("TAG", "安全区域距离屏幕底部的距离 SafeInsetBottom:" + displayCutout.getSafeInsetBottom());
                        // 获得刘海区域
                        List<Rect> rects = displayCutout.getBoundingRects();
                        if (rects == null || rects.size() == 0) {
                            Log.e("TAG", "不是刘海屏");
                        } else {
                            Log.e("TAG", "刘海屏数量:" + rects.size());
                            for (Rect rect : rects) {
                                Log.e("TAG", "刘海屏区域：" + rect.toString());
                            }
                        }
//                        tv_timer.setTranslationY(displayCutout.getSafeInsetTop());
                    }
                }
            });
        }
    }
}
