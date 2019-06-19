package com.worldpay.access.checkout.matchers

import android.support.test.espresso.matcher.BoundedMatcher
import android.view.View
import android.widget.EditText
import org.hamcrest.Description

class EditTextColorMatcher private constructor(private val color: Int) : BoundedMatcher<View, EditText>(
    EditText::class.java) {
    override fun matchesSafely(item: EditText) = item.currentTextColor == color

    override fun describeTo(description: Description) {
        description.appendText("with edit text color:")
            .appendValue(color)
    }

    companion object {
        @JvmStatic
        fun withEditTextColor(color: Int) = EditTextColorMatcher(color)
    }
}
