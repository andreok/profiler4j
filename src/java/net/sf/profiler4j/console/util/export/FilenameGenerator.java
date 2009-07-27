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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;

/**
 * Generates files named according to a pattern.
 * <p>
 * This can be used to generate names for continuously numbered files.
 * <p>
 * The pattern is an arbitrary (valid) filename string that must
 * contain at most one "%i" substring. This is then consecutively
 * replaced by up-counting numbers until a filename is produced
 * that does not yet point to an existing file.
 * <p>
 * The running number starts from {@value #RUNNING_NUMBER__START} and may increase until a maximum
 * value defined by {@link #setMaxNumber(int)}, but never above {@value #RUNNING_NUMBER__MAX}. From all
 * existing files, the highest number is used to generate the next number. This means, that the value range
 * must not be complete.
 * <p>
 * Example: Given the pattern "callgraphs-(%i).png" and there are files "callgraphs-(1).png" and
 * "callgraphs-(7).png" then the file to be generated next is named "callgraphs-(8).png".
 * 
 */
public class FilenameGenerator {

    private static final String MSG__HIGHEST_NUMBER_REACHED = "Maximum number reached, cannot generate more file names from this pattern.";

    private static final String MSG__INVALID_PATTERN = "Invalid pattern.";

    /**
     * The maximum number of tries that the generator will do until it gives up.
     * This is the hard upper limit, that cannot be overridden (i.e. extended upwards) by the user.
     */
    private static final int RUNNING_NUMBER__MAX = 1000000;
    
    /** From where the running number starts. */
    private final static int RUNNING_NUMBER__START = 0;
    
    /** The maximum number of times that the generator will try to generate a valid filename. */
    private int highest_number = RUNNING_NUMBER__MAX;
    
    /** A placeholder that may occur in the file name pattern. It specifies a running number, counting up from 0. */
    private final static String PLACEHOLDER__RUNNING_NUMBER = "%i";
    
    /** The pattern used to generate file names. */
    private String pattern = null;
    
    /**
     * Sets the pattern that this generator is supposed to follow.
     * 
     * @param pattern the file pattern, see the class comment
     */
    public FilenameGenerator(String pattern) {
        this.pattern = pattern;
    }
    
    /**
     * Sets the maximum number to substitute for the counting number.
     * <p>
     * The value cannot exceed {@value #RUNNING_NUMBER__MAX}. 
     * 
     * @param max the highest value to set
     */
    public void setMaxNumber(int max) {
        if (RUNNING_NUMBER__MAX < max) {
            highest_number = RUNNING_NUMBER__MAX;
        }
        else {
            highest_number = max;
        }
    }
    
    /**
     * Checks to see, if the given pattern is correct syntactically, i.e. if the pattern makes sense.
     * <p>
     * See the class comment for a specification of what constitutes a valid pattern.
     * <p>
     * A pattern for which this method returns {@code true} may still be unusable, e.g. if the target directory
     * is write-protected.
     * 
     * @param pattern The pattern.
     * @return {@code true}, if the pattern is syntactically correct, {@code false} otherwise.
     */
    public boolean isValidPattern(String pattern) {
        if (StringUtils.isBlank(pattern))
            return false;
        
        // It may not contain more than one placeholder for a running-number in the file name.
        String name = extractFileName(pattern);
        if (1 < StringUtils.countMatches(name, PLACEHOLDER__RUNNING_NUMBER))
            return false;
        
        // With placeholders removed, the pattern must constitute a valid filename in the current system.
        // To check this, we simply try to create the file.
        String pattern_without_placeholders = StringUtils.remove(pattern, "%i");
        if (!isPossibleToCreateFile(pattern_without_placeholders)) {
            return false;
        }
        return true;
    }
    
    /**
     * Generates a new filename and returns a corresponding File object.
     * <p>
     * This method is synchronized, so that file generation does not suffer from
     * concurrency when many snapshots are taken.
     * 
     * @return a valid file object, never {@code null}
     * 
     * @throws FileNotFoundException in case a filename could not be generated from the pattern or
     * no file object could be created. 
     */
    public synchronized File getValidFile() throws FileNotFoundException, IOException {
        if (!isValidPattern(pattern)) {
            throw new FileNotFoundException(MSG__INVALID_PATTERN);
        }
        
        // Get the next file name. If we don't have any placeholders to substitute,
        // the pattern is the filename already.
        String filename = pattern;
        if (pattern.contains(PLACEHOLDER__RUNNING_NUMBER)) {
            filename = generateNextFilename_withPlaceholderSubstitution(pattern);
        }
        
        // Create the file.
        // There is no deleting of the file in case of error, since only errors may occur
        // that would also prevent us from deleting the file.
        File nextFile = new File(filename);
        if (!nextFile.exists()) {
            try {
                nextFile.createNewFile();
            } catch(IOException exc) {
                throw new FileNotFoundException("Could not create file " + filename + ".");
            }
        }
        
        // Write-check.
        if (!nextFile.canWrite()) {
            throw new FileNotFoundException("Cannot write to file " + filename + ", no write permissions.");
        }
        
        return nextFile;
    }
    
    /**
     * Generate a new filename by subsituting the placeholder for the next higher number.
     * <p>
     * The next higher number one plus the highest in use.
     * 
     * @param pattern The pattern that contains a placeholder.
     * @return a file name, never {@code null}.
     */
    private String generateNextFilename_withPlaceholderSubstitution(String pattern) throws FileNotFoundException {
        // First, determine the highest number in use so far.
        int highest_in_use = determineHighestNumberInUse(pattern);
        if (highest_number <= highest_in_use) {
            throw new FileNotFoundException(MSG__HIGHEST_NUMBER_REACHED);
        }
        return pattern.replace(PLACEHOLDER__RUNNING_NUMBER, String.valueOf(highest_in_use + 1));
    }
    
    /**
     * Get the highest number currently in use for the given pattern.
     * <p>
     * This means checking if there are files adhering to the pattern. If so, determine the highest
     * number from all these files.
     *  
     * @param pattern the pattern
     * @return the number currently in use, or {@link #RUNNING_NUMBER__START}-1.
     */
    private int determineHighestNumberInUse(String pattern) {
        String base = extractDirectory(pattern);
        String name = extractFileName(pattern);
        
        PatternFilter filter = new PatternFilter(name);
        new File(base).list(filter);
          
        return filter.getHighest();
    }
    
    /**
     * Checks the validity of the given file name by creating it.
     * <p>
     * The file name is valid, if a file with the given name could be created <i>or</i>
     * a file with the given name exists.
     * 
     * @param filename the filename to check, must not be {@code null}.
     * @return {@code true} in case the name is valid, {@code false} otherwise.
     */
    protected boolean isPossibleToCreateFile(String filename) {
        File file = null;
        boolean file_created = false;

        try {
            // Try to create it.
            file = new File(filename);
            file_created = file.createNewFile();
            
            // Check the file's existence. It does matter not, if it could be created or not:
            // If its there, the filename should be usable with high enough certainty.
            if (!file.exists()) {
                return false;
            }
            
        } catch (IOException e) {
            return false;
        } finally {
            // Clean up.
            if (file_created) {
                // If the file has been created, file is definitely not null.
                file.delete();
            }
        }
            
        return true;
    }
    
    /**
     * Returns the part of the pattern that denotes the file's name, i.e. the pattern exclusive an optional path information.
     * 
     * @param pattern from which to extract the path
     * @return a new string containing the file name
     */
    private String extractFileName(String pattern) {
        if (!pattern.contains(File.separator)) {
            return pattern;
        }
        
        return pattern.substring(pattern.lastIndexOf(File.separator)+1);
    }
    
    /**
     * Returns the part of the pattern that denotes the directory's name, i.e. the path information in the pattern, if any.
     * 
     * @param pattern from which to extract the optional path
     * @return a new string containing the path, or empty
     */
    private String extractDirectory(String pattern) {
        if (!pattern.contains(File.separator)) {
            return "";
        }
        
        return pattern.substring(0, pattern.lastIndexOf(File.separator));
    }
    
    /**
     * Theoretically a file filter, but is in this case (mis)used to determine
     * the highest number in use. This is done to avoid iterating all files twice.
     * 
     * @author murat
     */
    private class PatternFilter implements FilenameFilter
    {
        private String begin = null;
        private String end = null;
        private int highest = RUNNING_NUMBER__START - 1;
        
        PatternFilter(String file_pattern) {
            begin = file_pattern.substring(0, file_pattern.indexOf(PLACEHOLDER__RUNNING_NUMBER));
            end = file_pattern.substring(file_pattern.indexOf(PLACEHOLDER__RUNNING_NUMBER) + PLACEHOLDER__RUNNING_NUMBER.length());
        }
        
        public boolean accept(File dir, String s)
        {
            if (!s.startsWith(begin) || !s.endsWith(end)) {
                return false;
            }
            
            String middle = s.substring(begin.length(), s.length() - end.length());
            if (!StringUtils.isNumeric(middle)) {
                return false;
            }
            
            highest = Math.max(highest, Integer.valueOf(middle).intValue());
            
            return true;
        }

        public int getHighest() {
            return this.highest;
        }
        
      }

    
}
