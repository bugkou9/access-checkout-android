package com.worldpay.access.checkout.validation

import com.worldpay.access.checkout.api.configuration.CardValidationRule
import com.worldpay.access.checkout.validation.NewValidatorUtils.getValidationResultFor
import com.worldpay.access.checkout.validation.NewValidatorUtils.isNumeric
import com.worldpay.access.checkout.validation.NewValidatorUtils.regexMatches
import org.junit.Assert.*
import org.junit.Test

class NewValidatorUtilsTest {

    @Test
    fun `given single numeric char then isNumeric should return true`() {
        assertTrue(isNumeric("1"))
    }

    @Test
    fun `given all numeric chars then isNumeric should return true`() {
        assertTrue(isNumeric("1234567890"))
    }

    @Test
    fun `given all non-numeric chars then isNumeric should return false`() {
        assertFalse(isNumeric("ABCDEFGHI"))
    }

    @Test
    fun `given some non-numeric chars then isNumeric should return false`() {
        assertFalse(isNumeric("123ABC456"))
    }

    @Test
    fun `given empty then isNumeric should return false`() {
        assertFalse(isNumeric(""))
    }

    @Test
    fun `given special chars then isNumeric should return false`() {
        assertFalse(isNumeric("@£%&*!"))
    }

    @Test
    fun `given single numeric char with a leading space then isNumeric should return false`() {
        assertFalse(isNumeric(" 1"))
    }

    @Test
    fun `given all numeric chars with a leading space then isNumeric should return false`() {
        assertFalse(isNumeric(" 123"))
    }

    @Test
    fun `given single numeric char with a trailing space then isNumeric should return false`() {
        assertFalse(isNumeric("1 "))
    }

    @Test
    fun `given all numeric chars with a trailing space then isNumeric should return false`() {
        assertFalse(isNumeric("123 "))
    }

    @Test
    fun `given all numeric chars with a space in the middle then isNumeric should return false`() {
        assertFalse(isNumeric("1 4"))
    }

    // regexMatches
    @Test
    fun `given an empty regex then PAN should not match`() {
        assertFalse(regexMatches("", "1234"))
    }

    @Test
    fun `given an empty PAN then PAN should not match against regex`() {
        assertFalse(regexMatches("^1234$", ""))
    }

    @Test
    fun `given a PAN which matches a regex pattern then should return true`() {
        val pan = "11111111111111"
        val regex = "^1\\d{0,13}$"

        assertTrue(regexMatches(regex, pan))
    }

    @Test
    fun `given a PAN which doesn't match a regex length then should return false`() {
        val pan = "111111111111112"
        val regex = "^1\\d{0,13}$"

        assertFalse(regexMatches(regex, pan))
    }

    @Test
    fun `given a PAN which doesn't match a regex prefix then should return false`() {
        val pan = "21111111111111"
        val regex = "^1\\d{0,13}$"

        assertFalse(regexMatches(regex, pan))
    }

    @Test
    fun `given a PAN with invalid chars then return false`() {
        val pan = "1abcdefghijklm"
        val regex = "^1\\d{0,13}$"

        assertFalse(regexMatches(regex, pan))
    }

    @Test
    fun `given a PAN which matches in an OR pattern then should return true`() {
        val panPrefix1 = "413600"
        val panPrefix2 = "444509"
        val panPrefix3 = "444550"
        val panSuffix = "0000000"
        val regex = "^($panPrefix1|$panPrefix2|$panPrefix3)\\d{0,7}"

        assertTrue(regexMatches(regex, panPrefix1 + panSuffix))
        assertTrue(regexMatches(regex, panPrefix2 + panSuffix))
        assertTrue(regexMatches(regex, panPrefix3 + panSuffix))
    }

    @Test
    fun `given a PAN which does not matches in an OR pattern then should return false`() {
        val regex = "^(413600|444509|444550)\\d{0,7}"

        assertFalse(regexMatches(regex, "4136010000000"))
    }

    @Test
    fun `given a PAN which matches in a group pattern then should return true`() {
        val panPrefix1 = "34"
        val panPrefix2 = "37"
        val panSuffix = "0000000000000"
        val regex = "^3[47]\\d{0,13}\$"

        assertTrue(regexMatches(regex, panPrefix1 + panSuffix))
        assertTrue(regexMatches(regex, panPrefix2 + panSuffix))
    }

    @Test
    fun `given a PAN which does not match in a group pattern then should return false`() {
        val regex = "^3[47]\\d{0,13}\$"

        assertFalse(regexMatches(regex, "350000000"))
    }

    @Test
    fun `should return valid result when valid length is empty on getting validation result`() {
        val result = getValidationResultFor("", CardValidationRule("", emptyList()))
        assertEquals(true, result)
    }

}