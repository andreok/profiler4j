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
package net.sf.profiler4j.console.client;

/**
 * Interface that must be implemented by classes interested in monitor the progress of a
 * long-running operation.
 * 
 * @author Antonio S. R. Gomes
 */
public interface ProgressCallback {

    /**
     * Called to indicate that the operation has just started
     * @param maxValue Max value of the progress (starting from 0)
     */
    void operationStarted(int maxValue);

    /**
     * Called to update the current progress.
     * @param value current value
     */
    void update(int value);
}
