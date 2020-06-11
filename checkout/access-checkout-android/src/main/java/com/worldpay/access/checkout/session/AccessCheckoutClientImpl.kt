package com.worldpay.access.checkout.session

import android.content.Context
import android.content.Intent
import com.worldpay.access.checkout.client.session.AccessCheckoutClient
import com.worldpay.access.checkout.client.session.listener.SessionResponseListener
import com.worldpay.access.checkout.client.session.model.CardDetails
import com.worldpay.access.checkout.client.session.model.SessionType
import com.worldpay.access.checkout.session.broadcast.LocalBroadcastManagerFactory
import com.worldpay.access.checkout.session.broadcast.receivers.NUM_OF_SESSION_TYPES_REQUESTED
import com.worldpay.access.checkout.session.broadcast.receivers.SessionBroadcastReceiver
import com.worldpay.access.checkout.session.broadcast.receivers.SessionBroadcastReceiver.Companion.NUMBER_OF_SESSION_TYPE_KEY
import com.worldpay.access.checkout.session.handlers.SessionRequestHandlerFactory

internal class AccessCheckoutClientImpl(
    private val sessionHandlerFactory: SessionRequestHandlerFactory,
    activityLifecycleObserverInitialiser: ActivityLifecycleObserverInitialiser,
    private val localBroadcastManagerFactory: LocalBroadcastManagerFactory,
    private val context: Context
) : AccessCheckoutClient {

    init {
        activityLifecycleObserverInitialiser.initialise()
    }

    /**
     * Method which triggers a generate session state request to the Access Worldpay sessions API. The response will come back through the
     * [SessionResponseListener]
     *
     * @param cardDetails the cardDetails to submit - see [CardDetailsBuilder]
     * @param sessionTypes the list of tokens that is being requested
     */
    override fun generateSession(cardDetails: CardDetails, sessionTypes: List<SessionType>) {
        broadcastSessionTypeInfo(sessionTypes)

        val handlers = sessionHandlerFactory.getTokenHandlers()
        for (handler in handlers) {
            if (handler.canHandle(sessionTypes)) {
                handler.handle(cardDetails)
            }
        }
    }

    private fun broadcastSessionTypeInfo(sessionTypes: List<SessionType>) {
        val broadcastIntent = Intent(context, SessionBroadcastReceiver::class.java)
        broadcastIntent.putExtra(NUMBER_OF_SESSION_TYPE_KEY, sessionTypes.size)
        broadcastIntent.action = NUM_OF_SESSION_TYPES_REQUESTED
        localBroadcastManagerFactory.createInstance().sendBroadcast(broadcastIntent)
    }

}

