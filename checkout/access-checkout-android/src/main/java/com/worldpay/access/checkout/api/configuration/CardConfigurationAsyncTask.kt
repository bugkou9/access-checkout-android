package com.worldpay.access.checkout.api.configuration

import android.os.AsyncTask
import com.worldpay.access.checkout.api.*
import com.worldpay.access.checkout.api.AccessCheckoutException.AccessCheckoutConfigurationException
import com.worldpay.access.checkout.api.AsyncTaskResult
import com.worldpay.access.checkout.api.AsyncTaskUtils.callbackOnTaskResult
import com.worldpay.access.checkout.api.HttpClient
import com.worldpay.access.checkout.api.URLFactory
import com.worldpay.access.checkout.config.CardConfigurationParser
import com.worldpay.access.checkout.logging.LoggingUtils.Companion.debugLog
import com.worldpay.access.checkout.model.CardConfiguration
import java.net.URL

internal class CardConfigurationAsyncTask(private val callback: Callback<CardConfiguration>,
                                          private val urlFactory: URLFactory = URLFactoryImpl(),
                                          private val httpClient: HttpClient = HttpClient(),
                                          private val cardConfigurationParser: CardConfigurationParser = CardConfigurationParser()
) :
    AsyncTask<String, Void, AsyncTaskResult<CardConfiguration>>() {

    companion object {
        private const val TAG = "CardConfigurationAsyncTask"
        private const val CARD_CONFIGURATION_RESOURCE = "access-checkout/cardConfiguration.json"
    }

    override fun doInBackground(vararg params: String?): AsyncTaskResult<CardConfiguration> {
        return try {
            val baseURL = validateAndGetURL(params)
            val url = urlFactory.getURL("$baseURL/$CARD_CONFIGURATION_RESOURCE")
            val cardConfiguration = httpClient.doGet(url, cardConfigurationParser)
            debugLog(TAG, "Received card configuration: $cardConfiguration")
            AsyncTaskResult(cardConfiguration)
        } catch (ex: AccessCheckoutConfigurationException) {
            debugLog(TAG, "AccessCheckoutException thrown when fetching card configuration: $ex")
            AsyncTaskResult(ex)
        } catch (ex: Exception) {
            val message = "There was an error when trying to fetch the card configuration"
            debugLog(TAG, "$message: $ex")
            val accessCheckoutConfigurationException = AccessCheckoutConfigurationException(message, ex)
            AsyncTaskResult(accessCheckoutConfigurationException)
        }
    }

    override fun onPostExecute(result: AsyncTaskResult<CardConfiguration>) {
        callbackOnTaskResult(callback, result)
    }

    private fun validateAndGetURL(params: Array<out String?>): String {
        if (params.isEmpty()) throw AccessCheckoutConfigurationException("Null URL specified")
        return params[0]?.let {
            if (it.isBlank()) {
                throw AccessCheckoutConfigurationException("Blank URL specified")
            }
            parseURL(it)
            it
        } ?: throw AccessCheckoutConfigurationException("Null URL specified")
    }

    private fun parseURL(url: String) {
        try {
            URL(url)
        } catch (ex: Exception) {
            throw AccessCheckoutConfigurationException("Invalid URL specified", ex)
        }
    }
}
