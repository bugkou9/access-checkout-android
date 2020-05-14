package com.worldpay.access.checkout.session.api.request

import com.worldpay.access.checkout.api.discovery.DiscoverLinks
import com.worldpay.access.checkout.client.SessionType
import java.io.Serializable

/**
 * [SessionRequestInfo] contains all the necessary information to request a session
 *
 * @param baseUrl - the base API URL
 * @param requestBody - the [SessionRequest] with the card and merchant information
 * @param sessionType - the [SessionType] that is being requested
 * @param discoverLinks - [DiscoverLinks] containing the endpoints to be discovered in the API
 */
internal class SessionRequestInfo private constructor(
    val baseUrl: String,
    val requestBody: SessionRequest,
    val sessionType: SessionType,
    val discoverLinks: DiscoverLinks
): Serializable {

    /**
     * A builder for constructing a [SessionRequestInfo]
     */
    internal data class Builder(
        private var baseUrl: String? = null,
        private var requestBody: SessionRequest? = null,
        private var sessionType: SessionType? = null,
        private var discoverLinks: DiscoverLinks? = null
    ) {

        fun baseUrl(baseUrl: String) = apply { this.baseUrl = baseUrl }

        fun requestBody(requestBody: SessionRequest) = apply { this.requestBody = requestBody }

        fun sessionType(sessionType: SessionType) = apply { this.sessionType = sessionType }

        fun discoverLinks(discoverLinks: DiscoverLinks) = apply { this.discoverLinks = discoverLinks }

        fun build() =
            SessionRequestInfo(
                baseUrl as String,
                requestBody as SessionRequest,
                sessionType as SessionType,
                discoverLinks as DiscoverLinks
            )
    }

}