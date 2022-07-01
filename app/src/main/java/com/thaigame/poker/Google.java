package com.thaigame.poker;

import android.annotation.SuppressLint;
import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.thaigame.utils.Security;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class Google {

    public interface GoogleState {
        void onBillingServiceDisconnected();

        void onBillingSetupFinished();

        void onBillingSetupError(int code);

        void onQuerySkuDetailsDone();


    }

    static GoogleState f_call;
    static String payOriginalJson;
    static String paygetSignature;
    static String base64PublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAowzbGJf2Y9Q32fO/TgNvbC9SRmfYMunEirkUGpifg5NpfxkCURYS9emYaDlvcb22JiRZZPs+8so1apnE4f3kDGNNts9szJGA7amatvD+gW7/MSaTsieK9eBV53Xn32sSdG+fxgZKJhTuSf8GqWi06IMQurmO24RKkBROZwJfrMarZ8vZCyKX7LJJR7nhyDMvvQsFWszTn+g5FTLxXHu0eNw8OWABRaHZy+ICdDxu74MxA1jrGisqLAeAP2iKD0jsQUdATBlE9O/yCyHvAx/mvSe5VbTqDZ0z9GpFDejUYM2w3tbZy6Pkq+ocAoky842q4OORGTL9+hw6zSIRRlXdzQIDAQAB";

    static private String TAG = "Google";
    static List<SkuDetails> skuDetailList = new ArrayList<SkuDetails>();//商品信息

    static private BillingClient mBillingClient;
    static boolean PlayServiceState = false;

    static void setGoogleState(GoogleState call) {
        f_call = call;
    }

    //购买回调
    static PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {

            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null)//success
            {

                for (Purchase purchase : purchases) {
                    Log.e(TAG, "支付成功=" + billingResult.getResponseCode() + "---" + purchase.getSku());
                    handlePurchase(purchase);

                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED)//user canceled
            {
                Log.e(TAG, "玩家取消=" + billingResult.getResponseCode());
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED)//ITEM_ALREADY_OWNED
            {

                for (Purchase purchase : purchases) {
                    Log.e(TAG, "已经拥有了这个商品=" + billingResult.getResponseCode() + "---" + purchase.getSku());
                    handlePurchase(purchase);
                }
            } else//fail
            {
                Log.e(TAG, "购买失败" + billingResult.getResponseCode());
                Toast.makeText(MainActivity.activity, "购买失败=" + billingResult.getResponseCode(), Toast.LENGTH_LONG).show();
            }
        }
    };

    //客户端验证是否是ok的 一般都是发给服务器验证
    static boolean verifyPurchase(String signedData, String signature) {
        Boolean b = Security.verifyPurchase(base64PublicKey, signedData, signature);
        Log.e("verifyPurchase", String.valueOf(b));
        return b;
    }

    //获取上次支付的json数据
    static String GetLastOriginalJson() {
        return payOriginalJson;
    }

    //获取上次支付的签名数据
    static String GetLastSignature() {
        return paygetSignature;
    }

    //查询的时候发现有未消耗的商品
    static void handlePurchase(PurchaseHistoryRecord purchase) {
        Log.i(TAG, "消耗=" + purchase.toString());
        handlePurchase(purchase.getPurchaseToken());
    }

    //消耗商品
    static void handlePurchase(Purchase purchase) {
        Log.i(TAG, "消耗=" + purchase.toString());
        payOriginalJson = purchase.getOriginalJson();
        paygetSignature = purchase.getSignature();
        handlePurchase(purchase.getPurchaseToken());

    }


    static void handlePurchase(String purchaseToken) {
        ConsumeParams consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchaseToken)
//                    .setDeveloperPayload(purchase.getDeveloperPayload())//弃用
                .build();

        mBillingClient.consumeAsync(consumeParams, consumeResponseListener);
    }

    // 查询回调
    static SkuDetailsResponseListener skuDetailsResponseListener = new SkuDetailsResponseListener() {
        @SuppressLint("LongLogTag")
        @Override
        public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
            Log.e(TAG, "onSkuDetailsResponse");
            for (SkuDetails skuDetails : skuDetailsList) {
                String sku = skuDetails.getSku();
                String price = skuDetails.getPrice();
                String price_currency_code = skuDetails.getPriceCurrencyCode();
                Log.e("Google sku", sku);
                Log.e("Google price", price);
                Log.e("Google price_currency_code", price_currency_code);
                skuDetailList.add(skuDetails);
            }
            f_call.onQuerySkuDetailsDone();
        }
    };

    //消耗回调
    static ConsumeResponseListener consumeResponseListener = new ConsumeResponseListener() {

        @Override
        public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                Log.e(TAG, "消耗成功" + purchaseToken);
            } else {
                Log.e(TAG, "消耗失败" + billingResult.getResponseCode() + "  " + purchaseToken);
            }
        }
    };

    //获取商品信息
    static List<SkuDetails> GetGoodInfo() {
        return skuDetailList;
    }

    //buy
    static void Pay(String productId) {
        if (PlayServiceState == false) {//断开了连接
            Log.e(TAG, "onBillingServiceDisconnected  can not Pay==" + productId);
            return;
        }
        SkuDetails skuDetails = null;
        for (SkuDetails skuDetail : skuDetailList) {
            String sku = skuDetail.getSku();
            if (productId.toString().equals(sku.toString())) {
                skuDetails = skuDetail;

            }
        }
        if (skuDetails == null) {
            Log.e(TAG, "can not find sku in skuDetails " + productId);
            return;
        }
        Log.e(TAG, "Pay===" + productId);
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();
        BillingResult responseCode = mBillingClient.launchBillingFlow(MainActivity.activity, flowParams);

    }

    static void InitSDk(Activity activity, final String goods) {
        //Create and initialize BillingManager which talks to BillingLibrary
        mBillingClient = BillingClient.newBuilder(activity).setListener(purchasesUpdatedListener).enablePendingPurchases().build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Log.e(TAG, "onBillingSetupFinished  OK");
                    checkNoConsumed();//检测未消耗的商品

                    List<String> skuList = new ArrayList<>();
                    try {
                        JSONArray contentArray = new JSONArray(goods);
                        for (int i = 0; i < contentArray.length(); i++) {
                            Log.e(TAG, "contentArray1---" + contentArray.get(i));
                            skuList.add((String) contentArray.get(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                    mBillingClient.querySkuDetailsAsync(params.build(), skuDetailsResponseListener);
                    PlayServiceState = true;
                    f_call.onBillingSetupFinished();
                } else {
                    Log.e(TAG, "onBillingSetupFinished error====" + billingResult.getResponseCode());
                    f_call.onBillingSetupError(billingResult.getResponseCode());
                }

            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
//                Log.e(TAG, "onBillingServiceDisconnected");
                PlayServiceState = false;
//                InitSDk(MainActivity.activity);//断开连接在连一次
                f_call.onBillingServiceDisconnected();
            }
        });
    }

    //检查未消耗的商品
    static void checkNoConsumed() {

        Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
        if (purchasesResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            List<Purchase> purchaseList = purchasesResult.getPurchasesList();
            for (Purchase purchase : purchaseList) {
                handlePurchase(purchase);
            }
        }
    }

    //获取历史订单
    static void getHistoreyPurchase() {
        mBillingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP, new PurchaseHistoryResponseListener() {
            @Override
            public void onPurchaseHistoryResponse(@NonNull BillingResult billingResult, @Nullable List<PurchaseHistoryRecord> list) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    for (PurchaseHistoryRecord record : list) {
                        handlePurchase(record);
                    }
                }
            }
        });
    }

}
