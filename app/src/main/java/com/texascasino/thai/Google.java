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
import com.texascasino.utils.Security;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Google {

    public  interface GoogleState{
          void onBillingServiceDisconnected();
          void onBillingSetupFinished();
          void onBillingSetupError(int code);
          void onQuerySkuDetailsDone();

    }
    static final String SKU_PREFIX = MainActivity.activity.getPackageName() + ".gempack";
    static GoogleState f_call;

    static int IapCount = 7;
    static String payOriginalJson;
    static String paygetSignature;
    static String base64PublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgSSLc483z2WOBlsTQADvgeEAQQXRf0kvPXgtdAlIj0bEVc57obaReqg6NLhIRg5YDwB70PGSCnYq/XTHsywr22+qC+QgKZ7G8M0yngNBPNthAbQTPaEsn8tBj32n/bjuVSz6CbLJLdP5Gcy/UZeyF7oITiUTzMBMwAKXvdMaArVcWARi74vNAiOPp0AdweBZP1yqFjDbk0HTOX4iDSyX6CSvGWcwf2fcvwcBm0OAPYgEEUuBtgx+wq/Hqy1okHjQJHwok1RxqaE87m76hAP0EvmeCYDb7Lu4agisVqvQ084fG6HNOUybTJh2eNs04moBR1snbWXpLrYUlTmy6xtjvwIDAQAB";

    static private String TAG = "Google";
    static List<SkuDetails> skuDetailList = new ArrayList<SkuDetails>();//商品信息

    static private BillingClient mBillingClient;
    static boolean PlayServiceState = false;

    static void setGoogleState(GoogleState call)
    {
        f_call = call;
    }
    //购买回调
    static PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {

            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null)//success
            {

                for (Purchase purchase : purchases) {
                    Log.e(TAG, "onPurchasesUpdated--------OK----" + billingResult.getResponseCode() + "---" + purchase.getSku());
                    handlePurchase(purchase);

                }
                //Toast.makeText(MainActivity.activity, "buy success", Toast.LENGTH_LONG).show();
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED)//user canceled
            {
                Log.e(TAG, "onPurchasesUpdated--------USER_CANCELED---" + billingResult.getResponseCode());
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED)//ITEM_ALREADY_OWNED
            {

                for (Purchase purchase : purchases) {
                    Log.e(TAG, "onPurchasesUpdated--------ITEM_ALREADY_OWNED----" + billingResult.getResponseCode()+"---"+purchase.getSku());
                    handlePurchase(purchase);
                }
            } else//fail
            {
                Log.e(TAG, "onPurchasesUpdated error-----" + billingResult.getResponseCode());
//                Toast.makeText(MainActivity.activity, "onPurchasesUpdated fail-" + billingResult.getResponseCode(), Toast.LENGTH_LONG).show();
            }
        }
    };

    //客户端验证是否是ok的 一般都是发给服务器验证
    static boolean verifyPurchase(String signedData, String signature) {
        Boolean b = Security.verifyPurchase(base64PublicKey, payOriginalJson.toString(), paygetSignature);
        Log.e("b==", b + "");
        return b;
    }

    //获取上次支付的json 数据
    static String GetLastOriginalJson() {
        return payOriginalJson;
    }

    // 签名数据
    static String GetLastSignature() {
        return paygetSignature;
    }

    //消耗商品
    static void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            payOriginalJson = purchase.getOriginalJson();
            paygetSignature = purchase.getSignature();
            Log.e(TAG, "handlePurchase:-- " + payOriginalJson + "\n" + paygetSignature + "\n");
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
            Log.e(TAG, "onSkuDetailsResponse");
            for (SkuDetails skuDetails : skuDetailsList) {
                String sku = skuDetails.getSku();
                String price = skuDetails.getPrice();
                Log.e("Google sku", sku);
                Log.e("Google price", price);
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
                Log.e(TAG, "onConsumeResponse: success--" + purchaseToken);
            } else {
                Log.e(TAG, "onConsumeResponse: error---" + billingResult.getResponseCode() + "  " + purchaseToken);
            }
        }
    };

    //获取商品信息
    static JSONObject GetGoodInfo() {
        JSONObject jsonObject = new JSONObject();
        for (SkuDetails skuDetail : skuDetailList) {
            String sku = skuDetail.getSku();
            String price = skuDetail.getPrice();
            try {
                jsonObject.put(sku, price);
            } catch (JSONException e) {
                Log.e(TAG, "GetGoodInfo  error");
            }


        }
        return jsonObject;

    }

    //buy
    static void Pay(String productId) {
        if (PlayServiceState == false) {//断开了连接
            Log.e(TAG, "onBillingServiceDisconnected  can not Pay" + productId);
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

    static void InitSDk(Activity activity) {
        //Create and initialize BillingManager which talks to BillingLibrary
        mBillingClient = BillingClient.newBuilder(activity).setListener(purchasesUpdatedListener).enablePendingPurchases().build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Log.e(TAG, "onBillingSetupFinished  OK");
                    List<String> skuList = new ArrayList<>();
                    for (int i = 1;i<=IapCount;i++)
                    {
                        skuList.add(SKU_PREFIX+i);
                    }

                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                    mBillingClient.querySkuDetailsAsync(params.build(), skuDetailsResponseListener);
                    PlayServiceState = true;
                    f_call.onBillingSetupFinished();
                } else {
//                    Log.e(TAG, "onBillingSetupFinished error====" + billingResult.getResponseCode());
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

}
