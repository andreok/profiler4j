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
package net.sf.profiler4j.console;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.UIManager;

/**
 * CLASS_COMMENT
 * 
 * @author Antonio S. R. Gomes
 */
public class Prefs {

    private static final String TIP_VERSION_KEY = "tip_version";
    private static final String TIP_CURRENT_KEY = "tip_current";
    private static final String TIP_SHOW_KEY = "tip_show";
    private static final String ANTIALIASING_KEY = "antialiasing";
    private static final String LAF_KEY = "laf";
    private static final String HTTP_PROXY_SERVER_KEY = "http_proxy_server";
    private static final String HTTP_PROXY_PORT_KEY = "http_proxy_port";
    private static final String CHECK_UPDATES_KEY = "check_updates";
    private static final String WIN_XPOS = "win_xpos";
    private static final String WIN_YPOS = "win_ypos";
    private static final String WIN_W = "win_w";
    private static final String WIN_H = "win_h";

    private Preferences prefs = Preferences.userNodeForPackage(Prefs.class);

    public void flush() throws BackingStoreException {
        prefs.flush();
    }

    public void setTipVersion(int ver) {
        prefs.putInt(TIP_VERSION_KEY, ver);
    }

    public int getTipVersion() {
        return prefs.getInt(TIP_VERSION_KEY, 0);
    }

    public void setCurrentTip(int i) {
        prefs.putInt(TIP_CURRENT_KEY, i);
    }

    public int getCurrentTip() {
        return prefs.getInt(TIP_CURRENT_KEY, 0);
    }

    public void setShowTip(boolean v) {
        prefs.putBoolean(TIP_SHOW_KEY, v);
    }

    public boolean isShowTip() {
        return prefs.getBoolean(TIP_SHOW_KEY, true);
    }

    public void setLaf(String laf) {
        prefs.put(LAF_KEY, laf);
    }

    public String getLaf() {
        return prefs.get(LAF_KEY, UIManager.getCrossPlatformLookAndFeelClassName());
    }
    
    public void setAntialiasing(boolean v) {
        prefs.putBoolean(ANTIALIASING_KEY, v);
    }
    
    public boolean isAntialiasing() {
        return prefs.getBoolean(ANTIALIASING_KEY, true);
    }
}
