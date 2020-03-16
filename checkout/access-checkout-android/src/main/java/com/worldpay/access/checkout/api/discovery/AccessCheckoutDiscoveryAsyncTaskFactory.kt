package com.worldpay.access.checkout.api.discovery

import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.HttpClient
import com.worldpay.access.checkout.api.serialization.LinkDiscoveryDeserializer

internal class AccessCheckoutDiscoveryAsyncTaskFactory {

    fun getAsyncTask(callback: Callback<String>): AccessCheckoutDiscoveryAsyncTask {
        return AccessCheckoutDiscoveryAsyncTask(
            callback,
            LinkDiscoveryDeserializer(DiscoverLinks.verifiedTokens.service),
            LinkDiscoveryDeserializer(DiscoverLinks.verifiedTokens.endpoint),
            HttpClient())
    }

    fun getAsyncCVVTask(callback: Callback<String>) : AccessCheckoutDiscoveryAsyncTask {
        return AccessCheckoutDiscoveryAsyncTask(
            callback,
            LinkDiscoveryDeserializer(DiscoverLinks.sessions.service),
            LinkDiscoveryDeserializer(DiscoverLinks.sessions.endpoint),
            HttpClient())
    }

}