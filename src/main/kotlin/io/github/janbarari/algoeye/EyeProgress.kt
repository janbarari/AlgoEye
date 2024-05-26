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

class EyeProgress(private val title: String) {
    var isEyeProgressInitiated = false
    private var progress: Int = 0

    private fun ensureInitiated() {
        if (!isEyeProgressInitiated) {
            isEyeProgressInitiated = true
            println()
        }
    }
    fun setProgress(value: Int) {
        ensureInitiated()
        if (value == progress) return
        if (value > 100) {
            progress = 100
            printProgress()
            return
        }
        progress = value
        printProgress()
    }

    fun getProgress(): Int = progress

    @JvmOverloads
    fun incProgress(value: Int = 1) {
        ensureInitiated()
        progress += value
        if (progress > 100) progress = 100
        printProgress()
    }

    private fun printProgress(progressBarLength: Int = 24) {
        print("\r  AlgoEye(${title}) [")
        val progressChars = (progress.toDouble() / 100 * progressBarLength).toInt()
        repeat(progressChars) { print("◼") }
        repeat(progressBarLength - progressChars) { print(" ") }
        print("] $progress%")
    }
}
