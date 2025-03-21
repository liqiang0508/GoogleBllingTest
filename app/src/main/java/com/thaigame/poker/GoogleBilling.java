package com.thaigame.poker;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Google支付
 */
public class GoogleBilling {

    protected  String TAG = "GoogleBilling";
    //查询的商品详情
    private Map<String,ProductDetails>productDetailMap = new HashMap<>();
    //已经购买的商品
    protected Map<String,Purchase>purchaseMap = new HashMap<>();
    //BillingClient
    private BillingClient billingClient = null;


    public GoogleBilling(Context context, PurchasesUpdatedListener listener)
    {
        if(billingClient==null)
        {
            billingClient = BillingClient.newBuilder(context)
                    .setListener(listener)
                    .build();
        }
    }

    /**
     * 开始连接
     * @param listener
     */
    public void startConnection(BillingClientStateListener listener)
    {
        if(billingClient!=null)
        {
            billingClient.startConnection(listener);
        }

    }


    /**
     * 查询商品
     * @param productIds
     * @param listener
     */
    public void queryProductDetail(String[] productIds,ProductDetailsResponseListener listener)
    {
        if(!isReady())
        {
            Log.e(TAG, "billingClient is not Ready.");
            return;
        }

        if(productIds.length == 0)
        {
            Log.e(TAG, "Product id must be provided.");
            return;
        }
        List<QueryProductDetailsParams.Product> productList = new ArrayList<QueryProductDetailsParams.Product>();
        for (String productId : productIds) {
            QueryProductDetailsParams.Product product = QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build();
        }
        QueryProductDetailsParams queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(productList)
                        .build();

        billingClient.queryProductDetailsAsync(queryProductDetailsParams, new ProductDetailsResponseListener() {
            @Override
            public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull List<ProductDetails> list) {
                if(billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK)
                {
                    for (ProductDetails product:list)
                    {
                        productDetailMap.put(product.getProductId(),product);
                    }
                }
                listener.onProductDetailsResponse(billingResult,list);
            }
        });
    }

    /**
     * billingClient 是否已经连接
     * @return
     */
    public boolean isReady()
    {
        return billingClient.isReady();
    }

    /**
     * 开始购买
     * @param activity
     * @return {BillingResult}
     */
    public BillingResult launchBillingFlow(String[] productIds,Activity activity)
    {
        List<BillingFlowParams.ProductDetailsParams> productDetailsParamsList = new ArrayList<BillingFlowParams.ProductDetailsParams>();
        for(String product:productIds)
        {
            ProductDetails skuDetails =  productDetailMap.get(product);
            if(skuDetails!=null)
            {
                BillingFlowParams.ProductDetailsParams _productDetails = BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(skuDetails)
                        .setOfferToken(String.valueOf(skuDetails.getSubscriptionOfferDetails()))
                        .build();

                productDetailsParamsList.add(_productDetails);
            }

        }

        if(!productDetailsParamsList.isEmpty())
        {
            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build();
            return billingClient.launchBillingFlow(activity, billingFlowParams);
        }

        return BillingResult.newBuilder().setResponseCode(BillingClient.BillingResponseCode.ERROR).build();
    }

    /**
     * 消耗商品
     * @param purchase
     * @param listener
     */
    public void consumeProduct(Purchase purchase, ConsumeResponseListener listener)
    {

        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

        billingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String purchaseToken) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.e(TAG, "consumeProduct success:" + purchaseToken);
                    //移除
                    List<String> products = purchase.getProducts();
                    for(String product:products)
                    {
                        purchaseMap.remove(product);
                    }

                } else {
                    Log.e(TAG, "consumeProduct failed:" + billingResult.getResponseCode() + "  " + purchaseToken);
                }
                listener.onConsumeResponse(billingResult,purchaseToken);
            }
        });
    }

}
