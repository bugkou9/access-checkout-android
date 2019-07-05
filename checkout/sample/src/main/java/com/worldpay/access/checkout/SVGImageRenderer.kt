package com.worldpay.access.checkout

import android.graphics.drawable.PictureDrawable
import android.view.View
import android.widget.ImageView
import com.worldpay.access.checkout.logging.AccessCheckoutLogger
import com.worldpay.access.checkout.logging.Logger
import java.io.InputStream
import java.lang.Exception

interface SVGImageRenderer {
    fun renderImage(inputStream: InputStream, targetView: ImageView)
}

class SVGImageRendererImpl(private val runOnUiThreadFunc: (Runnable) -> Unit,
                           private val logger: Logger = AccessCheckoutLogger(),
                           private val svgWrapper: SVGWrapper = SVGWrapper.svgWrapper): SVGImageRenderer {

    override fun renderImage(inputStream: InputStream, targetView: ImageView) {
        try {
            val svg = svgWrapper.getSVGFromInputStream(inputStream)
            val drawable = PictureDrawable(svg.renderToPicture(targetView.measuredWidth, targetView.measuredHeight))
            runOnUiThreadFunc(Runnable {
                targetView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                targetView.setImageDrawable(drawable)
            })
        } catch (e: Exception) {
            logger.errorLog("SVGImageRendererImpl", "Failed to parse SVG image: ${e.message}")
        }
    }
}