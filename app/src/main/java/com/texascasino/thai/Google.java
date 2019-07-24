package com.texascasino.thai;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Google {
    static final String SKU_PREFIX = MainActivity.activity.getPackageName() + ".gempack";
    ;
    static final String SKU_PACK1 = SKU_PREFIX + "1";
    static final String SKU_PACK2 = SKU_PREFIX + "2";
    static final String SKU_PACK3 = SKU_PREFIX + "3";
    static final String SKU_PACK4 = SKU_PREFIX + "4";
    static final String SKU_PACK5 = SKU_PREFIX + "5";
    static final String SKU_PACK6 = SKU_PREFIX + "6";
    static final String SKU_PACK7 = SKU_PREFIX + "7";

    static String payOriginalJson;
    static String paygetSignature;

    static private String TAG = "Google";
    static List<SkuDetails> skuDetailList = new ArrayList<SkuDetails>();//商品信息

    static private BillingClient mBillingClient;
    static boolean PlayServiceState = false;

    //购买回调
    static PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
            Log.i(TAG, "onPurchasesUpdated--------" + billingResult.getResponseCode());
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null)//success
            {
                for (Purchase purchase : purchases) {
                    handlePurchase(purchase);
                }
                Toast.makeText(MainActivity.activity, "buy success", Toast.LENGTH_LONG).show();
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED)//user canceled
            {

            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED)//ITEM_ALREADY_OWNED
            {
                for (Purchase purchase : purchases) {
                    handlePurchase(purchase);
                }
            } else//fail
            {
                Toast.makeText(MainActivity.activity, "buy fail" + billingResult.getResponseCode(), Toast.LENGTH_LONG).show();
            }
        }
    };

    //消耗商品
    static void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            payOriginalJson = purchase.getOriginalJson();
            paygetSignature = purchase.getSignature();
            ConsumeParams consumeParams = ConsumeParams.newBuilder()
                    .setPurchaseToken(purchase.getPurchaseToken())
                    .setDeveloperPayload(purchase.getDeveloperPayload())
                    .build();

            mBillingClient.consumeAsync(consumeParams, consumeResponseListener);
        }
    }

    // 查询回调
    static SkuDetailsResponseListener skuDetailsResponseListener = new SkuDetailsResponseListener() {
        @Override
        public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
            Log.i(TAG, "onSkuDetailsResponse");
            for (SkuDetails skuDetails : skuDetailsList) {
                String sku = skuDetails.getSku();
                String price = skuDetails.getPrice();
                Log.i("Google sku", sku);
                Log.i("Google price", price);
                skuDetailList.add(skuDetails);
            }

        }
    };

    //消耗回调
    static ConsumeResponseListener consumeResponseListener = new ConsumeResponseListener() {

        @Override
        public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                Log.i(TAG, "onConsumeResponse: success--" + purchaseToken);
            } else {
                Log.i(TAG, "onConsumeResponse: error---" + billingResult.getResponseCode() + "  " + purchaseToken);
            }
        }
    };

    static JSONObject GetGoodInfo() {

        JSONObject jsonObject = new JSONObject();
        for (SkuDetails skuDetail : skuDetailList) {
            String sku = skuDetail.getSku();
            String price = skuDetail.getPrice();
            try {
                jsonObject.put(sku, price);
            } catch (JSONException e) {
                Log.i(TAG, "GetGoodInfo  error");
            }


        }
        return jsonObject;

    }

    //buy
    static void Pay(String productId) {

        SkuDetails skuDetails = null;
        for (SkuDetails skuDetail : skuDetailList) {
            String sku = skuDetail.getSku();
            if (productId.toString().equals(sku.toString())) {
                skuDetails = skuDetail;

            }
        }
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();

        BillingResult responseCode = mBillingClient.launchBillingFlow(MainActivity.activity, flowParams);

    }

    static void InitSDk(Activity activity) {
        //Create and initialize BillingManager which talks to BillingLibrary
        mBillingClient = BillingClient.newBuilder(activity).setListener(purchasesUpdatedListener).enablePendingPurchases().build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Log.i(TAG, "onBillingSetupFinished");
                    List<String> skuList = new ArrayList<>();
                    skuList.add(SKU_PACK1);
                    skuList.add(SKU_PACK2);
                    skuList.add(SKU_PACK3);
                    skuList.add(SKU_PACK4);
                    skuList.add(SKU_PACK5);
                    skuList.add(SKU_PACK6);
                    skuList.add(SKU_PACK7);
                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                    mBillingClient.querySkuDetailsAsync(params.build(), skuDetailsResponseListener);
                    PlayServiceState = true;
                }
                else
                {
                    Log.i(TAG, "onBillingSetupFinished error====" + billingResult.getResponseCode());
                }

            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.i(TAG, "onBillingServiceDisconnected");
                PlayServiceState = false;
            }
        });
    }


}
