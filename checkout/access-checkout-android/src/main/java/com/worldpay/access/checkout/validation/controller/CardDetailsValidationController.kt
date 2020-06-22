package com.worldpay.access.checkout.validation.controller

import android.widget.EditText
import com.worldpay.access.checkout.api.Callback
import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.api.configuration.CardConfigurationClient
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CARD_DEFAULTS
import com.worldpay.access.checkout.util.logging.LoggingUtils.debugLog

internal class CardDetailsValidationController(
    private val panEditText: EditText,
    private val expiryDateEditText: EditText,
    private val cvcEditText: EditText,
    baseUrl: String,
    cardConfigurationClient: CardConfigurationClient,
    private val fieldDecoratorFactory: FieldDecoratorFactory
) {

    init {
        decorateFields(CardConfiguration(emptyList(), CARD_DEFAULTS))

        // fetch remote cardConfiguration - resets the cardConfiguration field if a remote one is found
        cardConfigurationClient.getCardConfiguration(baseUrl, getCardConfigurationCallback())
    }

    private fun decorateFields(cardConfiguration: CardConfiguration) {
        fieldDecoratorFactory.decorateCvcField(cvcEditText, panEditText, cardConfiguration)
        fieldDecoratorFactory.decoratePanField(panEditText, cvcEditText, cardConfiguration)
        fieldDecoratorFactory.decorateExpiryDateFields(expiryDateEditText, cardConfiguration)
    }

    private fun getCardConfigurationCallback(): Callback<CardConfiguration> {
        return object : Callback<CardConfiguration> {
            override fun onResponse(error: Exception?, response: CardConfiguration?) {
                response?.let {cardConfig ->
                    debugLog(javaClass.simpleName, "Retrieved remote card configuration")
                    decorateFields(cardConfig)
                }
                error?.let {
                    debugLog(javaClass.simpleName,"Error while fetching card configuration: $it")
                }
            }
        }
    }

}
