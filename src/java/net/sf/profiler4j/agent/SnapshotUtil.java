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
package net.sf.profiler4j.agent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import net.sf.profiler4j.commons.Snapshot.Method;

public final class SnapshotUtil {

    public static final Comparator<Method> byNetTimeComparator = new Comparator<Method>() {
        public int compare(Method g1, Method g2) {
            return (int) -Math.signum(g1.getNetTime() - g2.getNetTime());
        }

        @Override
        public String toString() {
            return "BY_TOTAL_TIME";
        }
    };

    public static final Comparator<Method> bySelfTimeComparator = new Comparator<Method>() {
        public int compare(Method g1, Method g2) {
            return (int) -Math.signum(g1.getSelfTime() - g2.getSelfTime());
        }

        @Override
        public String toString() {
            return "BY_TOTAL_LOCAL_TIME";
        }
    };

    public static final Comparator<Method> byHitsComparator = new Comparator<Method>() {
        public int compare(Method g1, Method g2) {
            return -compareLong(g1.getHits(), g2.getHits());
        }

        @Override
        public String toString() {
            return "BY_HITS";
        }
    };

    public static void saveSnapshot() {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd_HHmmss");
        File file = new File("snapshot_" + fmt.format(new Date()) + ".p4j");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ThreadProfiler.createSnapshot(fos);
            fos.close();
            Log.print(0, "Written snapshot to " + file.getAbsolutePath());
        } catch (Exception e) {
            Log.print(0, "Could not write snapshot", e);
        }
    }
    
    public static void saveSnapshotToXML(String destination) {
        XStream xstream = new XStream(new DomDriver());
        try {
        String xml = xstream.toXML(ThreadProfiler.createSnapshot());
            FileWriter fwriter = new FileWriter(destination);
            fwriter.write(xml);
            fwriter.close();
            Log.print(0, "Written XML snapshot to " + destination);
        } catch (IOException exc) {
            throw new Profiler4JError("I/O Error when dumping final snapshot", exc);
        }
    }
    
    private static int compareLong(long l1, long l2) {
        return (l1 > l2) ? 1 : ((l1 < l2) ? -1 : 0);
    }

}
