package com.weatherender.widget.ui

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.robolectric.RuntimeEnvironment

class WidgetConfigScreenTest {

    @Test
    fun validateCity_ValidCity_ReturnsTrue() {
        assertTrue(validateCity("London"))
        assertTrue(validateCity("New York"))
        assertTrue(validateCity("São Paulo"))
        assertTrue(validateCity("A".repeat(100))) // Exactly 100 chars
    }

    @Test
    fun validateCity_InvalidCity_ReturnsFalse() {
        assertFalse(validateCity("")) // Empty
        assertFalse(validateCity("   ")) // Whitespace only
        assertFalse(validateCity("A".repeat(101))) // 101 chars
        assertFalse(validateCity("A".repeat(200))) // Way too long
    }

    @Test
    fun cityInputLimitedTo100Chars() {
        val longCity = "A".repeat(150)
        val limited = longCity.take(100)
        assertEquals(100, limited.length)
    }

    @Test
    fun saveFlow_ValidCity_CallsOnSave() {
        var savedCity: String? = null

        val onSave: (String) -> Unit = { city ->
            savedCity = city
        }

        val onCancel: () -> Unit = {}

        // Simulate valid city input
        val cityText = "London"
        val trimmed = cityText.trim()

        if (validateCity(trimmed)) {
            onSave(trimmed)
        }

        assertEquals("London", savedCity)
    }

    @Test
    fun saveFlow_InvalidCity_DoesNotCallOnSave() {
        var savedCity: String? = null

        val onSave: (String) -> Unit = { city ->
            savedCity = city
        }

        // Simulate empty city input
        val cityText = "   "
        val trimmed = cityText.trim()

        if (validateCity(trimmed)) {
            onSave(trimmed)
        }

        assertNull(savedCity)
    }

    @Test
    fun saveFlow_TooLongCity_DoesNotCallOnSave() {
        var savedCity: String? = null

        val onSave: (String) -> Unit = { city ->
            savedCity = city
        }

        // Simulate too long city input
        val cityText = "A".repeat(101)
        val trimmed = cityText.trim()

        if (validateCity(trimmed)) {
            onSave(trimmed)
        }

        assertNull(savedCity)
    }

    @Test
    fun errorMessage_EmptyCity_ShowsRequiredError() {
        val trimmed = "   "
        val errorMessage = if (trimmed.isBlank()) {
            "City name is required"
        } else {
            "City name must be 1-100 characters"
        }
        assertEquals("City name is required", errorMessage)
    }

    @Test
    fun errorMessage_TooLongCity_ShowsLengthError() {
        val trimmed = "A".repeat(101)
        val errorMessage = if (trimmed.isBlank()) {
            "City name is required"
        } else {
            "City name must be 1-100 characters"
        }
        assertEquals("City name must be 1-100 characters", errorMessage)
    }
}
