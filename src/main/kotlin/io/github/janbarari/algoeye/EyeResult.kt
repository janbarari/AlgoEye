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

import io.github.janbarari.algoeye.io.IoFormatter
import io.github.janbarari.algoeye.io.IoFormatters
import io.github.janbarari.algoeye.memory.MemoryFormatter
import io.github.janbarari.algoeye.memory.MemoryFormatters
import io.github.janbarari.algoeye.time.TimeFormatter
import io.github.janbarari.algoeye.time.TimeFormatters
import io.github.janbarari.algoeye.util.ConsolePrinter
import io.github.janbarari.algoeye.util.floorWithTwoDecimal

data class EyeResult(
    val title: String,

    val averageCpuLoad: Double = 0.0,
    val maxCpuLoad: Double = 0.0,

    val memoryUsageInByte: Long = 0,
    val isMemoryPrecisionAccurate: Boolean = false,
    val gcTriggerCount: Long = 0,
    val isRanOutOfMemory: Boolean = false,
    val maxReachedHeapMemoryInByte: Long = 0,

    val ioReadSizeInByte: Long = 0,
    val ioWriteSizeInByte: Long = 0,
    val ioReadOps: Long = 0,
    val ioWriteOps: Long = 0,

    val executionDurationInMs: Long = 0,
) {
    @JvmOverloads
    fun prettyPrint(
        memoryFormatter: MemoryFormatter = MemoryFormatters.byte,
        timeFormatter: TimeFormatter = TimeFormatters.millis,
        ioFormatter: IoFormatter = IoFormatters.mb
    ) {
        val title = "%s Eye Result".format(title)

        if (isRanOutOfMemory) {
            val consoleLength = 8 + title.length + MemoryFormatters.mb.format(maxReachedHeapMemoryInByte).length
            ConsolePrinter(consoleLength).run {
                printFirstLine()
                printLine(title)
                printBreakLine()
                printLine("Ran out in", "~%s".format(MemoryFormatters.mb.format(maxReachedHeapMemoryInByte)))
                printLastLine()
            }
            return
        }

        val note = "Do not multi-task during execution. If GC triggers during the execution of the process, memory precision will drop."
        var consoleLength = 19 + title.length
        val memoryUsageTextLength = memoryFormatter.format(memoryUsageInByte).length
        val executionDurationTextLength = timeFormatter.format(executionDurationInMs).length
        consoleLength += if (memoryUsageTextLength > executionDurationTextLength) {
            memoryUsageTextLength
        } else {
            executionDurationTextLength
        }

        ConsolePrinter(consoleLength).run {
            printFirstLine()
            printLine(title)
            printBreakLine()

            printLine("CPU Avg Load", "${averageCpuLoad.floorWithTwoDecimal()}%")
            printLine("CPU Max Load", "${maxCpuLoad.floorWithTwoDecimal()}%")

            printBreakLine('-')

            printLine("Memory Usage", memoryFormatter.format(memoryUsageInByte))
            printLine("Memory Max Usage", memoryFormatter.format(maxReachedHeapMemoryInByte))
            printLine("Memory Precision", if (isMemoryPrecisionAccurate) "Accurate" else "Approximate")
            printLine("Memory GC Triggers", gcTriggerCount.toString())

            printBreakLine('-')

            printLine("IO Read/Write Ops", "%s/%s".format(ioReadOps, ioWriteOps))
            printLine("IO Read/Write Size", "%s/%s".format(ioFormatter.format(ioReadSizeInByte), ioFormatter.format(ioWriteSizeInByte)))

            printBreakLine('-')

            printLine("Execution Time", timeFormatter.format(executionDurationInMs))

            printBreakLine('-')

            splitText(note).forEach { note ->
                printLine(note)
            }
            printLastLine()
        }
    }
}

