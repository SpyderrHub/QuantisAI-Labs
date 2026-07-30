import com.android.billingclient.api.*
fun test(billingClient: BillingClient, params: QueryProductDetailsParams) {
    billingClient.queryProductDetailsAsync(params) { billingResult, list ->
        println(list)
    }
}
