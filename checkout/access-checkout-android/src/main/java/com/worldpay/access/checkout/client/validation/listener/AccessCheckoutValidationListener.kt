package com.worldpay.access.checkout.client.validation.listener

import com.worldpay.access.checkout.client.validation.model.CardBrand

interface AccessCheckoutValidationListener

interface AccessCheckoutValidationSuccessListener: AccessCheckoutValidationListener {
    fun onValidationSuccess()
}

interface AccessCheckoutCvvValidationListener: AccessCheckoutValidationSuccessListener {
    fun onCvvValidated(isValid: Boolean)
}

interface AccessCheckoutPanValidationListener: AccessCheckoutValidationSuccessListener {
    fun onPanValidated(isValid: Boolean)
}

interface AccessCheckoutBrandChangedListener {
    fun onBrandChange(cardBrand: CardBrand?)
}

interface AccessCheckoutExpiryDateValidationListener: AccessCheckoutValidationSuccessListener {
    fun onExpiryDateValidated(isValid: Boolean)
}

interface AccessCheckoutCardValidationListener:
    AccessCheckoutCvvValidationListener,
    AccessCheckoutPanValidationListener,
    AccessCheckoutExpiryDateValidationListener,
    AccessCheckoutBrandChangedListener
