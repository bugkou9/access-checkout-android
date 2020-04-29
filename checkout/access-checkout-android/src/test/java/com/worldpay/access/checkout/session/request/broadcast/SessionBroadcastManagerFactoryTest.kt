package com.worldpay.access.checkout.session.request.broadcast

import com.worldpay.access.checkout.views.SessionResponseListener
import org.junit.Test
import org.mockito.Mockito.mock
import kotlin.test.assertNotNull

class SessionBroadcastManagerFactoryTest {

    @Test
    fun `should instantiate a session broadcast manager`() {
        val localBroadcastManagerFactoryMock = mock(LocalBroadcastManagerFactory::class.java)
        val externalSessionResponseListenerMock = mock(SessionResponseListener::class.java)

        val sessionBroadcastManagerFactory = SessionBroadcastManagerFactory(
            localBroadcastManagerFactoryMock,
            externalSessionResponseListenerMock
        )

        assertNotNull(sessionBroadcastManagerFactory.createInstance())
    }

}