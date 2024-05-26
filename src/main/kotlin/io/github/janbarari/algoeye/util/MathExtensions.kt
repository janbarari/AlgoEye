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

package io.github.janbarari.algoeye.util

import java.math.RoundingMode
import java.text.DecimalFormat

internal fun Long.byteToMegabyte(): Double = (this / 1048576.0).floorWithTwoDecimal()

internal fun Long.byteToKilobyte(): Double = (this / 1024.0).floorWithTwoDecimal()

internal fun Long.millisToSecond(): Long = this / 1000

internal fun Double.floorWithTwoDecimal(): Double {
    return try {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.FLOOR
        df.format(this).toDouble()
    } catch (e: NumberFormatException) {
        0.0
    }
}
