package com.worldpay.access.checkout.api.ssl.server

import javax.net.ssl.SSLEngine
import org.eclipse.jetty.util.ssl.SslContextFactory

class NoSslFactory : SslContextFactory() {

    override fun customize(sslEngine: SSLEngine) {}
}
