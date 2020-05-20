package com.worldpay.access.checkout.api.discovery

import android.os.AsyncTask
import com.worldpay.access.checkout.api.AccessCheckoutException.*
import com.worldpay.access.checkout.api.AsyncTaskResult
import com.worldpay.access.checkout.api.AsyncTaskUtils.callbackOnTaskResult
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.HttpClient
import com.worldpay.access.checkout.api.serialization.Deserializer
import com.worldpay.access.checkout.util.logging.LoggingUtils.debugLog
import java.net.MalformedURLException
import java.net.URL

/**
 * This class is responsible for discovering an API given the list of endpoints. On completion,
 * the given callback will be called with the corresponding error or success response from the
 * Discovery API.
 *
 * @property [callback] The callback via which the result is returned
 * @property [endpoints] A [List] of [Endpoint] that is iterated over to navigate through the discovery tree
 * @property [httpClient] An [HttpClient] that is responsible for sending HTTP requests to the Access Worldpay services
 */
internal class ApiDiscoveryAsyncTask(
    private val callback: Callback<String>,
    private val endpoints: List<Endpoint>,
    private val httpClient: HttpClient
) : AsyncTask<String, Any, AsyncTaskResult<String>>() {

    override fun doInBackground(vararg params: String?): AsyncTaskResult<String> {
        return try {
            debugLog(javaClass.simpleName, "Sending request to service discovery endpoint")

            var resourceUrl = params[0]

            for (e in endpoints) {
                resourceUrl = fetchLinkFromUrl(resourceUrl, e.getDeserializer())
            }

            debugLog(javaClass.simpleName, "Received response from service discovery endpoint")
            AsyncTaskResult(resourceUrl as String)
        } catch (ex: Exception) {
            val errorMessage = "An error was thrown when trying to make a connection to the service"
            when (ex) {
                is AccessCheckoutHttpException, is AccessCheckoutError -> {
                    debugLog(javaClass.simpleName, errorMessage)
                    AsyncTaskResult(AccessCheckoutDiscoveryException(errorMessage, ex))
                }
                else -> {
                    debugLog(javaClass.simpleName, ex.message ?: errorMessage)
                    AsyncTaskResult(ex)
                }
            }
        }
    }

    override fun onPostExecute(result: AsyncTaskResult<String>) {
        callbackOnTaskResult(callback, result)
    }

    private fun fetchLinkFromUrl(url: String?, deserializer: Deserializer<String>): String {
        val httpUrl = try {
            URL(url)
        } catch (e: MalformedURLException) {
            debugLog(javaClass.simpleName, "Invalid URL supplied: $url")
            throw AccessCheckoutDiscoveryException("Invalid URL supplied: $url", e)
        }
        return httpClient.doGet(httpUrl, deserializer)
    }

}
