/*
 * Copyright 2006 Antonio S. R. Gomes
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package net.sf.profiler4j.test;

import java.io.IOException;
import java.util.Locale;

import net.sf.profiler4j.agent.Agent;
import net.sf.profiler4j.agent.Transformer;

/**
 * Simple test class used for experimenting purposes only. Ignore it.
 * 
 * @author Antonio S. R. Gomes
 */
public class Test1 {

    public static void main(String[] args) throws IOException {

        initLocalAgent("-beanprops:on -access:private", "*(*):accept");

        Thread t = new Thread("TEST1") {
            public void run() {
                int i = 0;
                for (; i < 100; i++) {
                    iter();
                    if ((i % 10)==0) {
                        System.out.println("Iter #" + i);
                    }
                }
                System.out.format("avg(NET10)=%.2fms\n", sumNET10 / (i * 1000000.0));
                System.out.format("avg(NET20)=%.2fms\n", sumNET20 / (i * 1000000.0));
                System.out.format("avg(NET30)=%.2fms\n", sumNET30 / (i * 1000000.0));
                System.out.format("avg(NET100)=%.2fms\n", sumNET100 / (i * 1000000.0));
                System.out.format("avg(NET500)=%.2fms\n", sumNET500 / (i * 1000000.0));
            }
        };
        t.setPriority(Thread.MAX_PRIORITY);

        System.out.println("=== Starting Test Thread ====");
        t.start();
    }

    /**
     * Invokes the profiler and applies a set of rules.
     * 
     * @param opts
     * @param rules
     * @throws IOException
     */
    public static void initLocalAgent(String opts, String rules) throws IOException {
        Locale.setDefault(Locale.US);
        final int[] progress = new int[1];
        Transformer.Callback callback = new Transformer.Callback() {
            public void notifyClassTransformed(String className,
                                               int backSequence,
                                               int bachSize) {
                progress[0] = backSequence;
            }
        };
        int n = Agent.startNewSession(opts, rules, callback);
        while (progress[0] < n) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    private static void iter() {
        NET10();
        NET20();
        NET30();
        NET100();
        NET500();
    }

    private static long sumNET10;
    private static void NET10() {
        long t0 = System.nanoTime();
        sleep(10);
        sumNET10 += (System.nanoTime() - t0);
    }

    private static long sumNET20;
    private static void NET20() {
        long t0 = System.nanoTime();
        sleep(20);
        sumNET20 += (System.nanoTime() - t0);
    }

    private static long sumNET30;
    private static void NET30() {
        long t0 = System.nanoTime();
        sleep(40);
        sumNET30 += (System.nanoTime() - t0);
    }

    private static long sumNET100;
    private static void NET100() {
        long t0 = System.nanoTime();
        sleep(100);
        sumNET100 += (System.nanoTime() - t0);
    }

    private static long sumNET500;
    private static void NET500() {
        long t0 = System.nanoTime();
        sleep(500);
        sumNET500 += (System.nanoTime() - t0);
    }

    static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            // ignore
        }
    }

}
