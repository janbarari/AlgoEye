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

@file:JvmName("AlgoEye")

package io.github.janbarari.algoeye

import io.github.janbarari.algoeye.memory.MemoryUsage
import io.github.janbarari.algoeye.memory.getGcCount
import io.github.janbarari.algoeye.memory.getSafeMemoryUsage
import io.github.janbarari.algoeye.util.lock
import oshi.SystemInfo
import oshi.hardware.CentralProcessor
import java.lang.management.ManagementFactory
import kotlin.jvm.Throws

var gcTriggerCount: Long = 0L
    set(value) {
        field = if (value >= 0) value else 0
    }

var memoryUsage: Long = 0L
    set(value) {
        field = if (value >= 0) value else 0
    }

var beforeIOReadSizeInByte: Long = 0L
    set(value) {
        field = if (value >= 0) value else 0
    }

var beforeIOWriteSizeInByte: Long = 0L
    set(value) {
        field = if (value >= 0) value else 0
    }

var beforeIOReadOps: Long = 0L
    set(value) {
        field = if (value >= 0) value else 0
    }

var beforeIOWriteOps: Long = 0L
    set(value) {
        field = if (value >= 0) value else 0
    }

var afterIOReadSizeInByte = 0L
    set(value) {
        field = if (value >= 0) value else 0
    }

var afterIOWriteSizeInByte = 0L
    set(value) {
        field = if (value >= 0) value else 0
    }

var afterIOReadOps = 0L
    set(value) {
        field = if (value >= 0) value else 0
    }

var afterIOWriteOps = 0L
    set(value) {
        field = if (value >= 0) value else 0
    }

var maximumReachedHeapMemoryInByte = 0L
    set(value) {
        field = if (value >= 0) value else 0
    }

var averageCpuLoad = 0.0
    set(value) {
        field = if (value >= 0.0) value else 0.0
    }

var maxCpuLoad = 0.0
    set(value) {
        field = if (value >= 0.0) value else 0.0
    }

@Throws(AlgoEyeMultipleOperationException::class)
inline fun algoEye(title: String, crossinline block: EyeProgress.() -> Unit): EyeResult {
    if (lock.isLocked) {
        throw AlgoEyeMultipleOperationException()
    }
    lock.lock()

    gcTriggerCount = 0
    memoryUsage = 0
    beforeIOReadSizeInByte = 0
    beforeIOWriteSizeInByte = 0
    beforeIOReadOps = 0
    beforeIOWriteOps = 0
    afterIOReadSizeInByte = 0
    afterIOWriteSizeInByte = 0
    afterIOReadOps = 0
    afterIOWriteOps = 0
    maximumReachedHeapMemoryInByte = 0
    averageCpuLoad = 0.0
    maxCpuLoad = 0.0

    val eyeProgress = EyeProgress(title)
    val hardware = SystemInfo().hardware
    val cpu = hardware.processor
    var prevTicks = LongArray(CentralProcessor.TickType.entries.size)
    var isTrackerActive = true

    hardware.diskStores.run {
        beforeIOReadSizeInByte = sumOf { it.readBytes }
        beforeIOWriteSizeInByte = sumOf { it.writeBytes }
        beforeIOReadOps = sumOf { it.reads }
        beforeIOWriteOps = sumOf { it.writes }
    }

    val trackerThread = Thread {
        try {
            while (isTrackerActive) {
                val usedHeap = ManagementFactory.getMemoryMXBean().heapMemoryUsage.used
                if (usedHeap > maximumReachedHeapMemoryInByte) {
                    maximumReachedHeapMemoryInByte = usedHeap
                }

                hardware.diskStores.run {
                    afterIOReadSizeInByte = sumOf { it.readBytes }
                    afterIOWriteSizeInByte = sumOf { it.writeBytes }
                    afterIOReadOps = sumOf { it.reads }
                    afterIOWriteOps = sumOf { it.writes }
                }

                val cpuLoad = cpu.getSystemCpuLoadBetweenTicks(prevTicks) * 100
                prevTicks = cpu.systemCpuLoadTicks
                if (cpuLoad > 0.0) {
                    averageCpuLoad = (averageCpuLoad + cpuLoad) / 2.0
                    if (maxCpuLoad == 0.0 || maxCpuLoad < averageCpuLoad) {
                        maxCpuLoad = averageCpuLoad
                    }
                    if (cpuLoad > (cpuLoad * 15 / 100) + averageCpuLoad) {
                        maxCpuLoad = (maxCpuLoad + cpuLoad) / 2
                    }
                }

                Thread.sleep(50)
            }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        } catch (e: OutOfMemoryError) {
            Thread.currentThread().interrupt()
        }
    }.apply {
        start()
    }

    val beforeExecutionTimestamp = System.currentTimeMillis()
    val memoryUsageBeforeExecution = getSafeMemoryUsage()
    var afterUnsafeMemoryUsage: Long
    var afterExecutionTimestamp: Long
    try {
        block(eyeProgress).apply {
            isTrackerActive = false
            afterUnsafeMemoryUsage = ManagementFactory.getMemoryMXBean().heapMemoryUsage.used
            afterExecutionTimestamp = System.currentTimeMillis()
        }
    } catch (exception: OutOfMemoryError) {
        lock.unlock()
        trackerThread.interrupt()
        if (eyeProgress.isEyeProgressInitiated) {
            eyeProgress.setProgress(100)
        }
        return EyeResult(
            title = title,
            isRanOutOfMemory = true,
            maxReachedHeapMemoryInByte = maximumReachedHeapMemoryInByte
        )
    }
    trackerThread.interrupt()

    if (eyeProgress.isEyeProgressInitiated) {
        eyeProgress.setProgress(100)
    }

    var isMemoryPrecisionAccurate = true
    gcTriggerCount = 0L
    val memoryUsageAfterExecution = MemoryUsage(
        usedMemoryInBytes = afterUnsafeMemoryUsage,
        gcCount = getGcCount()
    )
    if (memoryUsageAfterExecution.gcCount != memoryUsageBeforeExecution.gcCount) {
        isMemoryPrecisionAccurate = false
        gcTriggerCount = memoryUsageAfterExecution.gcCount - memoryUsageBeforeExecution.gcCount
    }
    memoryUsage = memoryUsageAfterExecution.usedMemoryInBytes - memoryUsageBeforeExecution.usedMemoryInBytes

    lock.unlock()

    return EyeResult(
        title = title,

        averageCpuLoad = averageCpuLoad,
        maxCpuLoad = maxCpuLoad,

        memoryUsageInByte = memoryUsage,
        maxReachedHeapMemoryInByte = maximumReachedHeapMemoryInByte,
        isMemoryPrecisionAccurate = isMemoryPrecisionAccurate,
        gcTriggerCount = gcTriggerCount,

        ioReadSizeInByte = (afterIOReadSizeInByte - beforeIOReadSizeInByte).coerceAtLeast(0L),
        ioWriteSizeInByte = (afterIOWriteSizeInByte - beforeIOWriteSizeInByte).coerceAtLeast(0L),
        ioReadOps = (afterIOReadOps - beforeIOReadOps).coerceAtLeast(0L),
        ioWriteOps = (afterIOWriteOps - beforeIOWriteOps).coerceAtLeast(0L),

        executionDurationInMs = (afterExecutionTimestamp - beforeExecutionTimestamp).coerceAtLeast(0L),
    )
}

fun Int.kbToByte(): Long {
    return (this * 1024.0).toLong()
}

fun Int.mbToByte(): Long {
    return (this * 1048576.0).toLong()
}

fun Int.secondToMillis(): Long {
    return (this * 1000.0).toLong()
}
