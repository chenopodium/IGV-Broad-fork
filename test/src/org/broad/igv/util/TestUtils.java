/*
 * Copyright (c) 2007-2012 The Broad Institute, Inc.
 * SOFTWARE COPYRIGHT NOTICE
 * This software and its documentation are the copyright of the Broad Institute, Inc. All rights are reserved.
 *
 * This software is supplied without any warranty or guaranteed support whatsoever. The Broad Institute is not responsible for its use, misuse, or functionality.
 *
 * This software is licensed under the terms of the GNU Lesser General Public License (LGPL),
 * Version 2.1 which is available at http://www.opensource.org/licenses/lgpl-2.1.php.
 */

package org.broad.igv.util;

import org.broad.igv.Globals;
import org.broad.igv.PreferenceManager;
import org.broad.igv.feature.genome.Genome;
import org.broad.igv.tools.IgvTools;
import org.broad.igv.ui.IGV;
import org.broad.tribble.Feature;
import org.broad.tribble.readers.AsciiLineReader;
import org.broad.tribble.util.ftp.FTPClient;
import org.junit.Ignore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import static org.junit.Assert.*;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author jrobinso
 * @date Jul 28, 2010
 */
@Ignore
public class TestUtils {
    public static final String DATA_DIR = "test/data/";
    public static final String defaultGenome = DATA_DIR + "/genomes/hg18.unittest.genome";
    public static final String AVAILABLE_FTP_URL = "ftp://ftp.broadinstitute.org/pub/igv/TEST/test.txt";
    public static final String UNAVAILABLE_FTP_URL = "ftp://www.example.com/file.txt";
    //This is so ant can set the large data directory
    private static String LARGE_DATA_DIR_KEY = "LARGE_DATA_DIR";
    public static String LARGE_DATA_DIR = "test/largedata/";

    static {
        LARGE_DATA_DIR = System.getProperty(LARGE_DATA_DIR_KEY, LARGE_DATA_DIR);
    }

    public static void setUpTestEnvironment() {
        Globals.setTesting(true);
        PreferenceManager.getInstance().setPrefsFile("testprefs.properties");
        Globals.READ_TIMEOUT = 60 * 1000;
        Globals.CONNECT_TIMEOUT = 60 * 1000;
        FTPClient.READ_TIMEOUT = 60 * 1000;

        //Create output directory if it doesn't exist
        File outDir = new File(DATA_DIR, "out");
        if (!outDir.exists()) {
            outDir.mkdir();
        }
    }

    /**
     * Loads the session into IGV. This blocks until the session
     * is loaded.
     *
     * @param igv
     * @param sessionPath
     * @throws InterruptedException
     */
    public static void loadSession(IGV igv, String sessionPath) throws InterruptedException {
        igv.doRestoreSession(sessionPath, null, false);
    }

    /**
     * See TestUtils.createIndex(file, indexType, binSize)
     *
     * @param file
     * @throws IOException
     */
    public static void createIndex(String file) throws IOException {
        createIndex(file, IgvTools.LINEAR_INDEX, IgvTools.LINEAR_BIN_SIZE);
    }

    /**
     * Destroys index file if it exists, and creates new one under
     * the specified parameters
     *
     * @param file
     * @param indexType
     * @param binSize
     * @throws IOException
     */
    public static void createIndex(String file, int indexType, int binSize) throws IOException {
        File indexFile = new File(file + ".idx");
        if (indexFile.exists()) {
            indexFile.delete();
        }
        (new IgvTools()).doIndex(file, indexType, binSize);
        indexFile.deleteOnExit();
    }

    /**
     * Load a test genome, do some test setup
     *
     * @return
     * @throws IOException
     */
    public static Genome loadGenome() throws IOException {
        final String genomeFile = defaultGenome;
        return IgvTools.loadGenome(genomeFile, true);
    }

    public static void clearOutputDir() throws IOException {
        File outputDir = new File(DATA_DIR + "/out/");
        if (outputDir.isDirectory()) {
            File[] listFiles = outputDir.listFiles();
            for (File fi : listFiles) {
                //Keep hidden files and directories
                if (!fi.getName().startsWith(".")) {
                    fi.delete();
                }
            }
        }
    }

    /**
     * Returns either 1 or 2, representing the number of
     * bytes used to end a line. Reads only from first line of a file
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static int getBytesAtEnd(String filePath) throws IOException {
        InputStream is = new FileInputStream(filePath);
        AsciiLineReader reader = new AsciiLineReader(is);
        String line = reader.readLine();
        int bytesThisLine = (int) reader.getPosition();

        reader.close();

        return bytesThisLine - line.length();

    }

    /**
     * Check that the features are all the same. Checks size of list, chr, start, and end of
     * each feature
     *
     * @param expected
     * @param actIter
     * @throws AssertionError
     */
    public static void assertFeatureListsEqual(Iterator<Feature> expected, Iterator<Feature> actIter) throws AssertionError {
        while (expected.hasNext()) {
            Feature exp = expected.next();
            assertTrue(actIter.hasNext());
            Feature act = actIter.next();
            assertEquals(exp.getChr(), act.getChr());
            assertEquals(exp.getStart(), act.getStart());
            assertEquals(exp.getEnd(), act.getEnd());
        }
        assertFalse(actIter.hasNext());
    }

    /*
     * The FEST library finds components by name
     * We set the name property to equal the variable name.
     * Intended for testing ONLY
     * @param parent Element in which to set child names. Each field of this Object which is a Component
     *               will have it's name set
     * @param recursive Whether to set names on grand-child components. Note that this is all-or-none,
     *                  if this is true it goes recursively all the way down. Recursion is breadth-first,
     *                  stops at each level when an object has only non-Component fields
     */
    public static void setAllNames(Object parent, boolean recursive){
        Field[] fields = parent.getClass().getDeclaredFields();
        java.util.List<Component> childComponents = new ArrayList<Component>(fields.length);
        try {
            for (Field f : fields) {
                Component c = null;
                f.setAccessible(true);
                try {
                    c = (Component) f.get(parent);
                } catch (ClassCastException e) {
                    continue;
                }
                //Null valued fields don't throw a CCE
                //We don't overwrite names, this should also prevent
                //infinite recursion
                if(c == null || c.getName() != null){
                    continue;
                }
                //At this point, we've established
                //that the field in question is a Component
                c.setName(f.getName());
                childComponents.add(c);

                f.setAccessible(false);
            }

            if(recursive){
                for(Component c: childComponents){
                    setAllNames(c, recursive);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
