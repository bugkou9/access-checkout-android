package com.worldpay.access.checkout.validation.validators

import com.worldpay.access.checkout.api.configuration.CardConfiguration
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.validation.CardValidator
import com.worldpay.access.checkout.validation.ValidationResult
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class AccessCheckoutCardValidatorTest {

    private lateinit var panValidator: PANValidator
    private lateinit var cvvValidator: CVVValidator
    private lateinit var dateValidator: DateValidator
    private lateinit var cardConfiguration: CardConfiguration
    private lateinit var accessCheckoutCardValidator: CardValidator

    @Before
    fun setup() {
        panValidator = mock(PANValidator::class.java)
        cvvValidator = mock(CVVValidator::class.java)
        dateValidator = mock(DateValidator::class.java)
        cardConfiguration = mock(CardConfiguration::class.java)
        accessCheckoutCardValidator =
            AccessCheckoutCardValidator(
                cardConfiguration,
                panValidator,
                cvvValidator,
                dateValidator
            )
    }

    @Test
    fun `given a pan then should be delegated to PAN validator to validate`() {
        val pan = "0000111122223333"
        val result = ValidationResult(
            partial = true,
            complete = false
        )
        val validationResultPair = Pair(result, null)
        given(panValidator.validate(pan, cardConfiguration)).willReturn(validationResultPair)

        val validationResult = accessCheckoutCardValidator.validatePAN(pan)

        assertEquals(validationResultPair, validationResult)
    }

    @Test
    fun `given a pan and cvv then should be delegated to CVV validator to validate`() {
        val pan = "0000111122223333"
        val cvv = "1234"
        val result = ValidationResult(
            partial = true,
            complete = false
        )

        given(cvvValidator.validate(cvv, pan, cardConfiguration)).willReturn(Pair(result, VISA_BRAND))

        val validationResult = accessCheckoutCardValidator.validateCVV(cvv, pan)

        assertEquals(Pair(result, VISA_BRAND), validationResult)
    }

    @Test
    fun `given a month and a year field then should delegate to Date validator to validate`() {
        given(dateValidator.validate("01", "29", cardConfiguration)).willReturn(
            ValidationResult(
                partial = false,
                complete = true
            )
        )

        val validationResult = accessCheckoutCardValidator.validateDate("01", "29")

        assertEquals(
            ValidationResult(
                partial = false,
                complete = true
            ), validationResult)
    }

    @Test
    fun `given a month and a year field then should delegate to Date validator to validate length`() {
        given(dateValidator.canUpdate("01", "29", cardConfiguration)).willReturn(false)

        val canUpdate = accessCheckoutCardValidator.canUpdate("01", "29")

        assertFalse(canUpdate)
    }

    @Test
    fun `given a validator is constructed then should be able to fetch card configuration`() {
        assertEquals(cardConfiguration, accessCheckoutCardValidator.cardConfiguration)
    }

    @Test
    fun `given null card configuration will partially validate a luhn invalid card`() {

        val expected = Pair(
            ValidationResult(
                partial = true,
                complete = false
            ), null)

        accessCheckoutCardValidator =
            AccessCheckoutCardValidator(
                null
            )

        val validationResult = accessCheckoutCardValidator.validatePAN("4111111111111112")
        assertEquals(expected, validationResult)
    }

    @Test
    fun `given null card configuration will completely validate a luhn valid card`() {
        val expected = Pair(
            ValidationResult(
                partial = true,
                complete = true
            ), null)

        accessCheckoutCardValidator =
            AccessCheckoutCardValidator(
                null
            )

        val validationResult = accessCheckoutCardValidator.validatePAN("5555555555554444")
        assertEquals(expected, validationResult)
    }

}