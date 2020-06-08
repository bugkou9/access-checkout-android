package com.worldpay.access.checkout.validation.result

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.worldpay.access.checkout.client.validation.AccessCheckoutCvvValidationListener
import com.worldpay.access.checkout.validation.ValidationResult
import org.junit.Before
import org.junit.Test
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CvvValidationResultHandlerTest {

    private val validationListener = mock<AccessCheckoutCvvValidationListener>()
    private val validationStateManager = ValidationStateManager()

    private lateinit var validationResultHandler: CvvValidationResultHandler

    @Before
    fun setup() {
        validationResultHandler = CvvValidationResultHandler(validationListener, validationStateManager)
    }

    @Test
    fun `should call listener when cvv is valid`() {
        val validationResult = ValidationResult(partial = true, complete = true)

        validationResultHandler.handleResult(validationResult)

        verify(validationListener).onCvvValidated(true)
        verifyNoMoreInteractions(validationListener)

        assertTrue(validationStateManager.cvvValidated.get())
    }

    @Test
    fun `should call listener when cvv is invalid`() {
        val validationResult = ValidationResult(partial = true, complete = false)

        validationResultHandler.handleResult(validationResult)

        verify(validationListener).onCvvValidated(false)
        verifyNoMoreInteractions(validationListener)

        assertFalse(validationStateManager.cvvValidated.get())
    }

    @Test
    fun `should call onValidationSuccess when all fields are valid`() {
        val validationResult = ValidationResult(partial = true, complete = true)

        val validationStateManager = mock<ValidationStateManager>()
        given(validationStateManager.isAllValid()).willReturn(true)
        given(validationStateManager.cvvValidated).willReturn(AtomicBoolean(false))

        val validationResultHandler = CvvValidationResultHandler(validationListener, validationStateManager)
        validationResultHandler.handleResult(validationResult)

        verify(validationListener).onCvvValidated(true)
        verify(validationListener).onValidationSuccess()
        verifyNoMoreInteractions(validationListener)
    }

}