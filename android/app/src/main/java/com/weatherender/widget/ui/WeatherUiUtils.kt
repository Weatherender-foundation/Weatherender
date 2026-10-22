package com.weatherender.widget.ui

import kotlin.math.roundToInt

fun safeRoundToInt(value: Double): Int =
    if (value.isNaN() || value.isInfinite()) 0 else value.roundToInt()

fun safeDoubleString(value: Double?): String =
    value?.takeUnless { it.isNaN() || it.isInfinite() }?.toString() ?: "0.0"
