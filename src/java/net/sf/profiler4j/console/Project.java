/*
 * Copyright 2006 Antonio S. R. Gomes
 * Copyright 2009 Murat Knecht
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
package net.sf.profiler4j.console;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * POJO that represents a profiler project.
 * 
 * @author Antonio S. R. Gomes
 */
public class Project {

    /** Specifies that test classes from the profiler4j project are to be instrumented and profiled. */
//    private static final Rule RULE__ACCEPT_PROFILER4J_TEST_CLASSES = new Rule("net.sf.profiler4j.test.*(*)", Rule.Action.ACCEPT);
    // TODO (mk) remove me on project finish
    private static final Rule RULE__ACCEPT_PROFILER4J_TEST_CLASSES = new Rule("org.gudy.azureus2.ui.*(*)", Rule.Action.ACCEPT);

    private boolean changed;

    private File file;
    private String hostname = "localhost";
    private int port = 7890;
    private boolean beanprops = true;
    private Rule.AccessOption access = Rule.AccessOption.PRIVATE;

    private List<Rule> rules = new ArrayList<Rule>();
    
    private boolean exportAutomaticallyEnabled = false;
    
    /** The image filename pattern for exporting purposes. */
    private String exportPattern = null;

    public Project() {
        // Set default rules
        
        // rules.add(new Rule("org.apache.*(*)", Rule.Action.REJECT));
        // rules.add(new Rule("org.jboss.*(*)", Rule.Action.REJECT));
        // rules.add(new Rule("net.sf.jasperreports.*(*)", Rule.Action.REJECT));
        // rules.add(new Rule("bsh.*(*)", Rule.Action.REJECT));
        // rules.add(new Rule("EDU.oswego.*(*)", Rule.Action.REJECT));
        // rules.add(new Rule("org.eclipse.*(*)", Rule.Action.REJECT));
        // rules.add(new Rule("org.hsqldb.*(*)", Rule.Action.REJECT));
        //rules.add(new Rule("*(*)", Rule.Action.REJECT));
        
        rules.add(RULE__ACCEPT_PROFILER4J_TEST_CLASSES);
    }

    public String getHostname() {
        return this.hostname;
    }

    public void setHostname(String hostname) {
        changed = true;
        this.hostname = hostname;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        changed = true;
        this.port = port;
    }

    public Rule.AccessOption getAccess() {
        return this.access;
    }
    public void setAccess(Rule.AccessOption access) {
        changed = true;
        this.access = access;
    }
    public boolean isBeanprops() {
        return this.beanprops;
    }
    public void setBeanprops(boolean beanprops) {
        this.beanprops = beanprops;
    }
    public List<Rule> getRules() {
        return this.rules;
    }
    public void setRules(List<Rule> rules) {
        changed = true;
        this.rules = rules;
    }
    public File getFile() {
        return this.file;
    }
    public void setFile(File file) {
        changed = true;
        this.file = file;
    }

    /**
     * @return Returns the changed.
     */
    public boolean isChanged() {
        return this.changed;
    }

    public void clearChanged() {
        changed = false;
    }

    public String formatOptions() {
        StringBuilder out = new StringBuilder();
        out.append("-beanprops:" + (isBeanprops() ? "on" : "off"));
        out.append(" -access:" + (getAccess().toString().toLowerCase()));
        return out.toString();
    }

    public String formatRules() {
        StringBuilder out = new StringBuilder();
        for (net.sf.profiler4j.console.Rule r : getRules()) {
            out.append(r.getPattern() + " : " + r.getAction().toString().toLowerCase()
                    + "; ");
        }
        return out.toString();
    }

    /**
     * Sets the pattern from which a name for export images is to be derived.
     * <p>
     * The value may be {@code null} or otherwise invalid, since there is no chance
     * to check the pattern for usefulness when it is specified. (I.e. there may
     * be no write-access to the directory anymore, when exporting begins.)
     * <p>
     * Note that the pattern only is used, if {@link #isExportAutomaticallyEnabled()} return {@code true}.
     * 
     * @param exportPattern the filename pattern to set, may be {@code null}
     */
    public void setExportPattern(String exportPattern) {
        this.exportPattern = exportPattern;
    }

    /**
     * Gets the pattern from which a name for export images is to be derived.
     * <p>
     * May be {@code null} or otherwise invalid, see {@link #setExportPattern(String)}.
     * <p>
     * Note that the pattern only is used, if {@link #isExportAutomaticallyEnabled()} return {@code true}.
     * 
     * @return the currently used filename pattern
     */
    public String getExportPattern() {
        return exportPattern;
    }

    /**
     * Determine, if the call graph is to be exported automatically after each snapshot.
     * 
     * @param exportAutomaticallyEnabled 
     * 
     * @see #setExportPattern(String)
     */
    public void setExportAutomaticallyEnabled(boolean exportAutomaticallyEnabled) {
        this.exportAutomaticallyEnabled = exportAutomaticallyEnabled;
    }

    /**
     * Determine, if the call graph is to be exported automatically after each snapshot.
     *
     * @return the exportAutomaticallyEnabled
     * 
     * @see #getExportPattern()
     */
    public boolean isExportAutomaticallyEnabled() {
        return exportAutomaticallyEnabled;
    }

}
