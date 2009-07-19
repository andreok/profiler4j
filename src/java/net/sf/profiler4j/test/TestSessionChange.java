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

import net.sf.profiler4j.agent.ThreadProfiler;

/**
 * @author Antonio S. R. Gomes
 */
public class TestSessionChange {

    public static void main(String[] args) {
        while (true) {
            foo();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    private static int n = 0;
    public static void foo() {
        System.err.println("inside TestSessionChange.foo()  count=" + n + "  session="
                + ThreadProfiler.getSessionId());
        n++;
    }

}
