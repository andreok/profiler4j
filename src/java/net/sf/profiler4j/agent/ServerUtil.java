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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for client-server related operations.
 * 
 * @author Antonio S. R. Gomes
 */
public class ServerUtil {

    private ServerUtil() {
        // empty;
    }

    public static ThreadInfo[] makeSerializable(java.lang.management.ThreadInfo[] mti) {
        ThreadInfo[] ti = new ThreadInfo[mti.length];
        for (int i = 0; i < ti.length; i++) {
            ti[i] = new ThreadInfo(mti[i]);
        }
        return ti;
    }

    public static void writeStringList(ObjectOutputStream out, List<String> list)
        throws IOException {
        out.writeInt(list.size());
        for (String v : list) {
            out.writeUTF(v);
        }
    }

    public static List<String> readStringList(ObjectInputStream in) throws IOException {
        int n = in.readInt();
        List<String> list = new ArrayList<String>(n);
        for (int i = 0; i < n; i++) {
            list.add(in.readUTF());
        }
        return list;
    }

    public static Map<String, String> readStringMap(ObjectInputStream in)
        throws IOException {
        int n = in.readInt();
        Map<String, String> map = new LinkedHashMap<String, String>(n);
        for (int i = 0; i < n; i++) {
            map.put(in.readUTF(), in.readUTF());
        }
        return map;
    }

    public static void writeStringMap(ObjectOutputStream out, Map<String, String> map)
        throws IOException {
        out.writeInt(map.size());
        for (String v : map.keySet()) {
            out.writeUTF(v);
            out.writeUTF(map.get(v));
        }
    }

    public static void writeMemoryUsage(ObjectOutputStream out, MemoryUsage mu)
        throws IOException {
        out.writeLong(mu.getInit());
        out.writeLong(mu.getUsed());
        out.writeLong(mu.getCommitted());
        out.writeLong(mu.getMax());
    }

    public static MemoryUsage readMemoryUsage(ObjectInputStream in) throws IOException {
        return new MemoryUsage(in.readLong(), in.readLong(), in.readLong(), in.readLong());
    }

}
