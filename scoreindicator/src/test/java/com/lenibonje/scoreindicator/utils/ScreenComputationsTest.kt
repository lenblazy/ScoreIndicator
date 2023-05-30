package com.lenibonje.scoreindicator.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class ScreenComputationsTest {
    private lateinit var SUT: ScreenComputations

    @Before
    fun setUp() {
        SUT = ScreenComputations(2.5f)
    }

    @Test
    fun `check for computer ability to multiply`() {
        assertThat(SUT.dpToPx(1)).isEqualTo(2.5f)
    }

    @Test
    fun `check different screen density`() {
        SUT = ScreenComputations(3f)
        assertThat(SUT.dpToPx(3)).isEqualTo(9f)
    }

}