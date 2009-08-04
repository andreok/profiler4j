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
package net.sf.profiler4j.agent;

/**
 * Constants used by the agent.
 * 
 * @author Antonio S. R. Gomes
 */
public class AgentConstants {

    public static final String VERSION = "1.0-alpha3";
    
    public static final int CMD_GC = 1;
    public static final int CMD_SNAPSHOT = 2;
    public static final int CMD_RESET_STATS = 3;
    public static final int CMD_DISCONNECT = 4;
    public static final int CMD_APPLY_RULES = 5;
    public static final int CMD_LIST_CLASSES = 6;
    public static final int CMD_GET_RUNTIME_INFO = 7;
    public static final int CMD_GET_MEMORY_INFO = 8;
    public static final int CMD_GET_THREAD_INFO = 9;
    public static final int CMD_SET_THREAD_MONITORING = 10;

    public static final int COMMAND_ACK = 0x00;
    public static final int STATUS_ERROR = 0x01;
    public static final int STATUS_UNKNOWN_CMD = 0x02;

}
