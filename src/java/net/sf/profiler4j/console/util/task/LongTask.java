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
package net.sf.profiler4j.console.util.task;

import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract class that is implemented by long-running tasks that want to interact with the
 * user.
 * 
 * @author Antonio S. R. Gomes
 */
abstract public class LongTask {

    private Log log = LogFactory.getLog(getClass());

    private Throwable error;
    private Object value;
    private LongTaskExecutorDialog dialog;

    /**
     * Executes the task, which can take any desired time. In order to update the status
     * in the UI during the process the implementation may either call
     * {@link #setTaskProgress(int)} or {@link #setTaskMessage(String)}.
     * 
     * @throws Exception If any error happens during the execution.
     */
    abstract public void executeInBackground() throws Exception;

    /**
     * Sets the {@link LongTaskExecutorDialog} instance that will execute this task.
     * @param dialog The dialog to set.
     */
    public void setDialog(LongTaskExecutorDialog dialog) {
        this.dialog = dialog;
    }

    /**
     * @param error The error to set.
     */
    public void setError(Throwable error) {
        this.error = error;
    }

    /**
     * @return Returns the error.
     */
    public Throwable getError() {
        return this.error;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

//    public void failed() {
//
//    }

    /**
     * Called by the task itself from the method {@link #executeInBackground()} to update
     * the current progress.
     * @param percent
     */
    protected void setTaskProgress(final int percent) {
        log.debug("Current task progress is " + percent);
        if (dialog.getProgressBar().isIndeterminate()) {
            dialog.getProgressBar().setIndeterminate(false);
            dialog.getProgressBar().setMaximum(100);
            dialog.getProgressBar().setStringPainted(true);
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                dialog.getProgressBar().setValue(percent);
                dialog.getProgressBar().setString(String.valueOf(percent) + "%");
            }
        });
    }

    /**
     * Called to update the message that appears in the dialog. This method may be called
     * during the execution of the task.
     * @param s New message
     */
    protected void setTaskMessage(final String s) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                dialog.setMessage(s);
            }
        });
    }
}
