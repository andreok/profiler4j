/**
 * Copyright 2009 Murat Knecht
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *      
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License. */
package net.sf.profiler4j.console.util.export;

/**
 * Generates filenames according to a pattern.
 * <p>
 * This can be used to generate names for continuously numbered files.
 * <p>
 * The pattern is an arbitrary (valid) filename string that must
 * contain precisely one "%i" substring. This is then consecutively
 * replaced by up-counting numbers until a filename is produced
 * that does not yet point to an existing file.
 */
public class FilenameGenerator {

    /** The maximum number of tries that the generator will do until it gives up. */
    private int max_attempts = 1000000;
    
    /** The maximum number of times that the generator will try to generate a valid filename. */
    private int attempts = max_attempts;
    
    /**
     * Sets the pattern that this generator is supposed to follow.
     * 
     * @param pattern the filepattern, see the class comment
     */
    public FilenameGenerator(String pattern) {
        
    }
    
    /**
     * 
     * @param max
     */
    public void setMax(int max) {
        
    }
    
    public String getNext() {
        return null;
    }
    
}
