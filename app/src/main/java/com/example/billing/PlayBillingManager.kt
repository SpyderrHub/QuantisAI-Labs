package com.example.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.*
import com.example.data.FirestoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayBillingManager(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
    private val firestoreRepository: FirestoreRepository,
    private val userId: String?,
    private val onPurchaseSuccess: (String) -> Unit = {}
) {
    private val _productDetailsList = MutableStateFlow<List<ProductDetails>>(emptyList())
    val productDetailsList: StateFlow<List<ProductDetails>> = _productDetailsList
    
    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(context, "Purchase canceled", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Purchase error: ${billingResult.debugMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .build()

    init {
        startConnection()
    }

    private fun startConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryProducts()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    private fun queryProducts() {
        val productIds = listOf(
            "starter_monthly",
            "starter-yearly",
            "creator_monthly",
            "creator_yearly",
            "pro_monthly",
            "pro_yearly"
        )

        val productList = productIds.map {
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(it)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        }

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        coroutineScope.launch {
            val result = billingClient.queryProductDetails(params)
            if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val detailsList = result.productDetailsList ?: emptyList()
                _productDetailsList.value = detailsList
            }
        }
    }

    fun launchBillingFlow(activity: Activity, productDetails: ProductDetails) {
        val offerDetails = productDetails.subscriptionOfferDetails?.firstOrNull { it.offerId == null } ?: productDetails.subscriptionOfferDetails?.firstOrNull()
        val offerToken = offerDetails?.offerToken ?: return
        
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        grantEntitlement(purchase)
                    }
                }
            } else {
                grantEntitlement(purchase)
            }
        }
    }

    private fun grantEntitlement(purchase: Purchase) {
        if (userId.isNullOrEmpty()) return
        val product = purchase.products.firstOrNull() ?: return
        
        val plan = when (product) {
            "starter_monthly", "starter-yearly" -> "starter"
            "creator_monthly", "creator_yearly" -> "creator"
            "pro_monthly", "pro_yearly" -> "pro"
            else -> "free"
        }
        
        val type = if (product.contains("monthly")) "monthly" else "yearly"
        
        val creditsToAdd = when (plan) {
            "free" -> 3000
            "starter" -> 50000
            "creator" -> 300000
            "pro" -> 1000000
            else -> 0
        }

        coroutineScope.launch {
            val success = firestoreRepository.updateSubscription(userId, plan, type, creditsToAdd)
            withContext(Dispatchers.Main) {
                if (success) {
                    Toast.makeText(context, "Subscription successful! Features unlocked.", Toast.LENGTH_LONG).show()
                    onPurchaseSuccess(plan)
                } else {
                    Toast.makeText(context, "Subscription recorded but failed to update profile.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
