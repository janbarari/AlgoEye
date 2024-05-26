/**
 * MIT License
 * Copyright (c) 2022 Mehdi Janbarari (@janbarari)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.janbarari.algoeye

import io.github.janbarari.algoeye.assertion.AlgoEyeExceedExecutionException
import io.github.janbarari.algoeye.assertion.AlgoEyeExceedMemoryException
import io.github.janbarari.algoeye.assertion.AlgoEyeRanOutOfMemoryException
import io.github.janbarari.algoeye.assertion.assertAlgoEye
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AlgoEyeKtTest {

    @Test
    fun `check algoEye function works correctly`() {
        algoEye("Algorithm #0629") {

        }.prettyPrint()
    }

    @Test
    fun `check assertAlgoEye passes when execution doesn't exceed the thresholds`() {
        assertThrows<AlgoEyeExceedMemoryException> {
            assertAlgoEye("Test Scenario", 1000, 10000) {
                (0..100000).toList().reversed()
            }
        }
    }

    @Test
    fun `check assertAlgoEye throws exception when execution does exceed the thresholds`() {
        assertThrows<AlgoEyeExceedExecutionException> {
            assertAlgoEye("check assertAlgoEye throws exception when execution does exceed the thresholds", 10.mbToByte(), 5) {
                (0..100000).toList().reversed()
            }
        }
    }

    @Test
    fun `check assertAlgoEye throws exception when process ran out of memory`() {
        assertThrows<AlgoEyeRanOutOfMemoryException> {
            assertAlgoEye("check assertAlgoEye throws exception when process ran out of memory", 10.mbToByte(), 10.secondToMillis()) {
                (0..100000000000).toList().reversed()
            }
        }
    }

}
