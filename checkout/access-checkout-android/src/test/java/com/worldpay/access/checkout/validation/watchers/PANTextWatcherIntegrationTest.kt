package com.worldpay.access.checkout.validation.watchers

import android.widget.EditText
import com.nhaarman.mockitokotlin2.*
import com.worldpay.access.checkout.api.configuration.DefaultCardRules.CVV_DEFAULTS
import com.worldpay.access.checkout.testutils.CardConfigurationUtil
import com.worldpay.access.checkout.testutils.CardConfigurationUtil.Brands.VISA_BRAND
import com.worldpay.access.checkout.testutils.CardNumberUtil.PARTIAL_VISA
import com.worldpay.access.checkout.testutils.CardNumberUtil.VISA_PAN
import com.worldpay.access.checkout.validation.result.PanValidationResultHandler
import com.worldpay.access.checkout.validation.validators.CVCValidationRuleManager
import com.worldpay.access.checkout.validation.validators.CVCValidator
import com.worldpay.access.checkout.validation.validators.NewPANValidator
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowInstrumentation
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class PANTextWatcherIntegrationTest {

    private val context = ShadowInstrumentation.getInstrumentation().context

    private val cvv = EditText(context)
    private val pan = EditText(context)
    private val cvcValidationRuleManager = CVCValidationRuleManager()

    private val cvcValidator = mock<CVCValidator>()
    private val panValidationResultHandler = mock<PanValidationResultHandler>()

    @Before
    fun setup() {
        val cvvTextWatcher = CVVTextWatcher(
            cvcValidator = cvcValidator
        )

        cvv.addTextChangedListener(cvvTextWatcher)

        val panTextWatcher = PANTextWatcher(
            cardConfiguration = CardConfigurationUtil.Configurations.CARD_CONFIG_BASIC,
            panValidator = NewPANValidator(),
            cvcValidator = cvcValidator,
            cvvEditText = cvv,
            panValidationResultHandler = panValidationResultHandler,
            cvcValidationRuleManager = cvcValidationRuleManager
        )

        pan.addTextChangedListener(panTextWatcher)
    }


    @Test
    fun `should validate pan as false when partial unknown pan is entered`() {
        pan.setText("00000")

        verify(panValidationResultHandler).handleResult(false, null)
    }

    @Test
    fun `should validate pan as false when partial visa pan is entered`() {
        pan.setText(PARTIAL_VISA)

        verify(panValidationResultHandler).handleResult(false, VISA_BRAND)
    }

    @Test
    fun `should validate pan as true when full visa pan is entered`() {
        pan.setText(VISA_PAN)

        verify(panValidationResultHandler).handleResult(true, VISA_BRAND)
    }

    @Test
    fun `should validate pan as true when 19 character unknown valid luhn is entered`() {
        pan.setText("0999008073997244886")

        verify(panValidationResultHandler).handleResult(true, null)
    }

    @Test
    fun `should validate pan as true when 19 character unknown invalid luhn is entered`() {
        pan.setText("0999008073997244887")

        verify(panValidationResultHandler).handleResult(false, null)
    }

    @Test
    fun `should validate cvv when pan brand is recognised and cvv is not empty`() {
        assertEquals(CVV_DEFAULTS, cvcValidationRuleManager.getRule())

        cvv.setText("123")
        reset(cvcValidator)

        pan.setText(VISA_PAN)

        assertEquals(VISA_BRAND.cvv, cvcValidationRuleManager.getRule())

        verify(cvcValidator).validate("123")
        verify(panValidationResultHandler).handleResult(true, VISA_BRAND)
    }

    @Test
    fun `should not validate cvv when pan brand is recognised and cvv is empty`() {
        assertEquals(CVV_DEFAULTS, cvcValidationRuleManager.getRule())

        pan.setText(VISA_PAN)

        assertEquals(VISA_BRAND.cvv, cvcValidationRuleManager.getRule())

        verify(cvcValidator, never()).validate(any())
        verify(panValidationResultHandler).handleResult(true, VISA_BRAND)
    }

}
