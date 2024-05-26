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

package io.github.janbarari.algoeye;

import static org.junit.jupiter.api.Assertions.*;
import io.github.janbarari.algoeye.assertion.AssertAlgoEye;
import io.github.janbarari.algoeye.assertion.AlgoEyeExceedExecutionException;
import io.github.janbarari.algoeye.io.IoFormatters;
import io.github.janbarari.algoeye.memory.MemoryFormatters;
import io.github.janbarari.algoeye.time.TimeFormatters;
import kotlin.Unit;
import org.junit.jupiter.api.Test;

public class JavaCompatibilityTest {

    public static void main(String[] args) throws AlgoEyeMultipleOperationException {
        ensureAlgoEyeCompatibility();
    }

    @Test
    void ensureAlgoEyeCompatibilityTest() throws AlgoEyeMultipleOperationException {
        ensureAlgoEyeCompatibility();
    }

    @Test
    void ensureAssertAlgoEyeWorksFine() {
        assertThrows(AlgoEyeExceedExecutionException.class ,() -> AssertAlgoEye.assertAlgoEye("Execution", AlgoEye.mbToByte(100), 100, eyeProgress -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return Unit.INSTANCE;
        }));
    }

    static void ensureAlgoEyeCompatibility() throws AlgoEyeMultipleOperationException {
        AlgoEye.algoEye("Java Compatibility", eyeProgress -> {
            while (eyeProgress.getProgress() < 100) {
                eyeProgress.incProgress();
                delay(100);
            }
            return Unit.INSTANCE;
        }).prettyPrint(
                MemoryFormatters.kb,
                TimeFormatters.millis,
                IoFormatters.kb
        );
    }

    public static void delay(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}

