package com.worldpay.access.checkout.session.request

import com.worldpay.access.checkout.client.CardDetails
import com.worldpay.access.checkout.client.SessionType

interface SessionRequestHandler {

    fun canHandle(sessionTypes: List<SessionType>): Boolean

    fun handle(cardDetails: CardDetails)

}
