package com.worldpay.access.checkout

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.worldpay.access.checkout.MockServer.defaultStubMappings
import com.worldpay.access.checkout.MockServer.simulateErrorResponse
import com.worldpay.access.checkout.MockServer.simulateHttpRedirect
import com.worldpay.access.checkout.MockServer.simulateRootResourceTemporaryServerError
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MockServerTest {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    private val port = 8080

    @Before
    fun setup() {
        defaultStubMappings(activityRule.activity)
    }

    @Test
    fun givenRootResourceRequested_ThenRootResourceWithVTSLinkReturned() {
        val client = OkHttpClient()

        val baseURL = "http://localhost:$port"
        val request = Request.Builder()
            .url("$baseURL/")
            .build()

        client.newCall(request).execute().use { response ->
            assertThat(response.body()?.string(), CoreMatchers.containsString("service:verifiedTokens"))
        }
    }

    @Test
    fun givenServiceRootResourceRequested_ThenServiceRootResourceWithSessionLinkReturned() {
        val client = OkHttpClient()

        val baseURL = "http://localhost:$port"
        val request = Request.Builder()
            .url("$baseURL/verifiedTokens")
            .build()

        client.newCall(request).execute().use { response ->
            assertThat(response.body()?.string(), CoreMatchers.containsString("verifiedTokens:sessions"))
        }
    }

    @Test
    fun givenSessionReferenceRequestedWithValidCustomSDKHeaderForReleaseVersion_ThenLinkToSessionReferenceReturned() {
        val client = OkHttpClient()

        val baseURL = "http://localhost:$port"
        val request = Request.Builder()
            .method("POST", RequestBody.create(MediaType.parse("application/vnd.worldpay.verified-tokens-v1.hal+json"), "{}"))
            .header("Accept", "application/vnd.worldpay.verified-tokens-v1.hal+json")
            .header("X-WP-SDK", "access-checkout-android/${BuildConfig.VERSION_CODE}.0.0")
            .url("$baseURL/verifiedTokens/sessions")
            .build()

        client.newCall(request).execute().use { response ->
            assertThat(response.body()?.string(), CoreMatchers.containsString(activityRule.activity.resources.getString(
                R.string.session_reference
            )))
        }
    }

    @Test
    fun givenSessionReferenceRequestedWithValidCustomSDKHeaderForSnapshotVersion_ThenLinkToSessionReferenceReturned() {
        val client = OkHttpClient()

        val baseURL = "http://localhost:$port"
        val request = Request.Builder()
            .method("POST", RequestBody.create(MediaType.parse("application/vnd.worldpay.verified-tokens-v1.hal+json"), "{}"))
            .header("Accept", "application/vnd.worldpay.verified-tokens-v1.hal+json")
            .header("X-WP-SDK", "access-checkout-android/${BuildConfig.VERSION_CODE}.0.0-SNAPSHOT")
            .url("$baseURL/verifiedTokens/sessions")
            .build()

        client.newCall(request).execute().use { response ->
            assertThat(response.body()?.string(), CoreMatchers.containsString(activityRule.activity.resources.getString(
                R.string.session_reference
            )))
        }
    }

    @Test
    fun givenSessionReferenceRequestedWithInvalidCustomSDKHeader_Then404ReturnedByMockServer() {
        val client = OkHttpClient()

        val baseURL = "http://localhost:$port"
        val request = Request.Builder()
            .method("POST", RequestBody.create(MediaType.parse("application/vnd.worldpay.verified-tokens-v1.hal+json"), "{}"))
            .header("Accept", "application/vnd.worldpay.verified-tokens-v1.hal+json")
            .header("X-WP-SDK", "access-checkout-android/bad_version")
            .url("$baseURL/verifiedTokens/sessions")
            .build()

        client.newCall(request).execute().use { response ->
            assertThat(response.code(), equalTo(404))
        }
    }

    @Test
    fun givenSessionReferenceRequestedWithParticularCardNumber_Then400Returned() {
        simulateErrorResponse(activityRule.activity)

        val client = OkHttpClient()

        val expectedResponse = """{
                                "errorName": "bodyDoesNotMatchSchema",
                                "message": "The json body provided does not match the expected schema",
                                "validationErrors": [
                                    {
                                        "errorName": "panFailedLuhnCheck",
                                        "message": "The identified field contains a PAN that has failed the Luhn check.",
                                        "jsonPath": "$.cardNumber"
                                    }
                                ]
                            }""".trimIndent()
        val baseURL = "http://localhost:$port"
        val request = Request.Builder()
            .method("POST", RequestBody.create(MediaType.parse("application/vnd.worldpay.verified-tokens-v1.hal+json"), "{ \"cardNumber\":\"7687655651111111113\" }"))
            .header("Accept", "application/vnd.worldpay.verified-tokens-v1.hal+json")
            .url("$baseURL/verifiedTokens/sessions")
            .build()

        client.newCall(request).execute().use { response ->
            assertThat(response.code(), equalTo(400))
            assertThat(response.body()?.string(), equalTo(expectedResponse))
        }
    }

    @Test
    fun givenTemporaryServerErrorHittingRootResource_ThenShouldBeSuccessAfterRetry() {
        simulateRootResourceTemporaryServerError()

        val client = OkHttpClient()

        val baseURL = "http://localhost:$port"
        val request = Request.Builder()
            .url("$baseURL/")
            .build()

        client.newCall(request).execute().use { response ->
            assertThat(response.code(), CoreMatchers.equalTo(500))
        }

        client.newCall(request).execute().use { response ->
            assertThat(response.body()?.string(), CoreMatchers.containsString("service:verifiedTokens"))
        }
    }

    @Test
    fun givenHttpRedirectResponse_ThenShouldFollowNewUrl() {
        simulateHttpRedirect(activityRule.activity)

        val client = OkHttpClient()

        val baseURL = "http://localhost:$port"
        val request = Request.Builder()
            .method("POST", RequestBody.create(MediaType.parse("application/vnd.worldpay.verified-tokens-v1.hal+json"), "{}"))
            .header("Accept", "application/vnd.worldpay.verified-tokens-v1.hal+json")
            .header("X-WP-SDK", "access-checkout-android/${BuildConfig.VERSION_CODE}.0.0")
            .url("$baseURL/verifiedTokens/sessions")
            .build()

        val relocatedRequest = Request.Builder()
            .method("POST", RequestBody.create(MediaType.parse("application/vnd.worldpay.verified-tokens-v1.hal+json"), "{}"))
            .header("Accept", "application/vnd.worldpay.verified-tokens-v1.hal+json")
            .header("X-WP-SDK", "access-checkout-android/${BuildConfig.VERSION_CODE}.0.0")
            .url("$baseURL/newVerifiedTokensLocation/sessions")
            .build()

        client.newCall(request).execute().use { response ->
            assertThat(response.header("Location"), equalTo("$baseURL/newVerifiedTokensLocation/sessions"))
            assertThat(response.code(), equalTo(308))
        }

        client.newCall(relocatedRequest).execute().use { response ->
            assertThat(response.body()?.string(), CoreMatchers.containsString(activityRule.activity.resources.getString(
                R.string.session_reference
            )))
        }

    }
}