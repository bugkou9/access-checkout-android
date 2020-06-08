package com.worldpay.access.checkout.client.validation

import android.widget.EditText
import com.worldpay.access.checkout.util.ValidationUtil.validateNotNull

class CvvValidationConfig private constructor(
    val cvv: EditText,
    val validationListener: AccessCheckoutCvvValidationListener
): ValidationConfig {

    class Builder {

        private var cvv: EditText? = null
        private var validationListener: AccessCheckoutCvvValidationListener? = null

        fun cvv(cvv: EditText): Builder {
            this.cvv = cvv
            return this
        }

        fun validationListener(validationListener: AccessCheckoutCvvValidationListener): Builder {
            this.validationListener = validationListener
            return this
        }

        fun build(): CvvValidationConfig {
            validateNotNull(cvv, "cvv component")
            validateNotNull(validationListener, "validation listener")

            return CvvValidationConfig(
                cvv = cvv as EditText,
                validationListener = validationListener as AccessCheckoutCvvValidationListener
            )
        }

    }
}