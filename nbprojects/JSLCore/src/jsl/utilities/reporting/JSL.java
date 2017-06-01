/*
 * Copyright (c) 2007, Manuel D. Rossetti (rossetti@uark.edu)
 *
 * Contact:
 *	Manuel D. Rossetti, Ph.D., P.E.
 *	Department of Industrial Engineering
 *	University of Arkansas
 *	4207 Bell Engineering Center
 *	Fayetteville, AR 72701
 *	Phone: (479) 575-6756
 *	Email: rossetti@uark.edu
 *	Web: www.uark.edu/~rossetti
 *
 * This file is part of the JSL (a Java Simulation Library). The JSL is a framework
 * of Java classes that permit the easy development and execution of discrete event
 * simulation programs.
 *
 * The JSL is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * The JSL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the JSL (see file COPYING in the distribution);
 * if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301  USA, or see www.fsf.org
 *
 */
package jsl.utilities.reporting;

import java.awt.Desktop;
import java.io.*;
import java.util.logging.*;
import javax.swing.JOptionPane;

/** This class provides basic ability to create and write out to text files.
 *  It also provides a basic Logger.
 * 
 *  Use JSL.out just like System.out.  All data sent to JSL.out is written
 *  to the file jslOuput.txt found in the jslOutput directory of the current
 *  working directory.
 * 
 *  Also provides a globally incremented constants that can be used for "enum"
 *  like situations, via getNextEnumConstant()
 * 
 * @author rossetti
 */
public class JSL {

    /** Used to assign unique enum constants
     */
    private static int myEnumCounter_;

    private static int myFileCounter_;

    /** for logging
     */
    public static final Logger LOGGER = Logger.getLogger(JSL.class.getName());

    /** Can be used like System.out, but instead writes to a file
     *  jslOutput.txt found in the jslOutput directory
     *
     */
    public static LogPrintWriter out;

    private static FileHandler myFileHandler;

    private static String workingDirectory;

    private static StringBuilder ZEROES = new StringBuilder("000000000000");

    private static StringBuilder BLANKS = new StringBuilder("            ");

    static {
        File d = new File("jslMessages");
        d.mkdir();

        try {
            File f = new File(d, "JSLLog.txt");
            myFileHandler = new FileHandler(f.getAbsolutePath());

//			myFileHandler.setFormatter(new SimpleFormatter());
            myFileHandler.setFormatter(new LogFormatter());
            // Send logger output to our FileHandler.
            LOGGER.addHandler(myFileHandler);
        } catch (IOException e) {
            String str = "Error setting the FileHandler in JSL.LOGGER";
            LOGGER.log(Level.SEVERE, str, e);
        }

        try {
            File d1 = new File("jslOutput");
            d1.mkdir();
            out = new LogPrintWriter(new FileWriter(new File(d1, "jslOutput.txt")), true);
        } catch (IOException ex) {
            String str = "Problem opening jslOutput.txt";
            LOGGER.log(Level.SEVERE, str, ex);
        }

        try {
            workingDirectory = System.getProperty("user.dir");
        } catch (SecurityException ex) {
            String str = "Access to system information denied";
            LOGGER.log(Level.SEVERE, str, ex);
        }

    }

    /**
     * @return Returns the workingDirectory.
     */
    public static String getWorkingDirectory() {
        return workingDirectory;
    }

    /** Should be used by subclasses to get the next constant
     *  so that unique constants can be used 
     *
     * @return the constant
     */
    public static int getNextEnumConstant() {
        return (++myEnumCounter_);
    }

    /** Converts an integer to a string with a
     *  given width
     * 
     * @param val the value to format
     * @param w the width
     * @return the formatted string
     */
    public static String format(int val, int w) {
        return format(Integer.toString(val), w);
    }

    /** Converts a string to a particular width
     * 
     * @param s  String
     * @param w  width
     * @return  the formatted string
     */
    public static String format(String s, int w) {
        int w1 = Math.abs(w);
        int n = w1 - s.length();

        if (n <= 0) {
            return s;
        }
        while (BLANKS.length() < n) {
            BLANKS.append("      ");
        }
        if (w < 0) {
            return s + BLANKS.substring(0, n);
        } else {
            return BLANKS.substring(0, n) + s;
        }
    }

    /** Converts a floating point value (val) to a string of given size (w)
     * with a specified number (n) of decimals. If the value of W is negative,
     * then number is aligned to the left otherwise it is aligned on the right.
     *
     * @param val the value to format
     * @param n the number of decimals
     * @param w the width
     * @return the string
     */
    public static String format(double val, int n, int w) {
        //	rounding
        double incr = 0.5;
        for (int j = n; j > 0; j--) {
            incr /= 10;
        }
        val += incr;

        String s = Double.toString(val);
        int n1 = s.indexOf('.');
        int n2 = s.length() - n1 - 1;

        if (n > n2) {
            int len = n - n2;
            while (ZEROES.length() < len) {
                ZEROES.append("000000");
            }
            s = s + ZEROES.substring(0, len);
        } else if (n2 > n) {
            s = s.substring(0, n1 + n + 1);
        }

        return format(s, w);
    }

    /** Creates a PrintWriter in the jslOutput directory for use with a particular extension
     *
     * @param fileName
     * @param ext
     * @return
     */
    public static PrintWriter makePrintWriter(String fileName, String ext) {
        return makePrintWriter("jslOutput", fileName, ext);
    }

    /** Creates a PrintWriter for use with a particular extension
     *
     * @param directory
     * @param fileName
     * @param ext
     * @return
     */
    public static PrintWriter makePrintWriter(String directory, String fileName, String ext) {
        return makePrintWriter(makeFile(directory, fileName, ext));
    }

    /** Creates a PrintWriter for use with a particular extension
     *
     * @param directory
     * @param fileName
     * @param ext
     * @return
     */
    public static PrintWriter makePrintWriter(File directory, String fileName, String ext) {
        return makePrintWriter(makeFile(directory, fileName, ext));
    }

    /** Makes a PrintWriter from the given File
     *  IOExceptions are caught and logged
     * 
     * @param file  May be null if an IOException occurred
     * @return 
     */
    public static PrintWriter makePrintWriter(File file) {
        if (file == null) {
            throw new IllegalArgumentException("The supplied file was null");
        }
        try {
            return new PrintWriter(new FileWriter(file), true);
        } catch (IOException ex) {
            String str = "Problem creating PrintWriter for " + file.getAbsolutePath();
            LOGGER.log(Level.SEVERE, str, ex);
            return null;
        }
    }

    /** Makes a tFile handle in the jslOuput directory
     * 
     * @param fileName name of the file
     * @param ext extension of the file
     * @return a File reference to the file
     */
    public static File makeFile(String fileName, String ext) {
        return makeFile("jslOutput", fileName, ext);
    }

    /** Makes a directory with the given name and a file with the extension
     *  in the directory
     * 
     * @param directory
     * @param fileName
     * @param ext
     * @return 
     */
    public static File makeFile(String directory, String fileName, String ext) {
        if (directory == null) {
            directory = "jslOutput";
        }
        File d = new File(directory);
        d.mkdir();
        return makeFile(d, fileName, ext);

    }

    /** Makes a File with the provided name and extension by interpreting 
     *  the supplied File as a directory
     * 
     * @param directory
     * @param fileName
     * @param ext
     * @return 
     */
    public static File makeFile(File directory, String fileName, String ext) {
        if (directory == null) {
            directory = new File("jslOutput");
            directory.mkdir();
        }
        File f = new File(directory, makeFileName(fileName, ext));
        return f;
    }

    /** Makes a sub-directory of jslOuput
     * 
     * @param directory
     * @return
     */
    public static File makeOutputSubDirectory(String directory) {
        File d = new File("jslOutput" + File.separator + directory);
        d.mkdir();
        return d;
    }

    /** Makes a String that has the form name.csv
     * 
     * @param name
     * @return
     */
    public static String makeCSVFileName(String name) {
        return makeFileName(name, "csv");
    }

    /** Makes a String that has the form name.txt
     * 
     * @param name
     * @return
     */
    public static String makeTxtFileName(String name) {
        return makeFileName(name, "txt");
    }

    /** Makes a String that has the form name.ext
     *  If an extension already exists it is replaced.
     *
     * @param name
     * @param ext
     * @return
     */
    public static String makeFileName(String name, String ext) {
        if (name == null) {
            myFileCounter_ = myFileCounter_ + 1;
            name = "Temp" + myFileCounter_;
        }

        if (ext == null) {
            ext = "txt";
        }
        String s;
        int dot = name.lastIndexOf(".");

        if (dot == -1) {
            // no period found
            s = name + "." + ext;
        } else {
            // period found
            s = name.substring(dot) + ext;
        }

        return (s);
    }

    /** Uses Desktop.getDesktop() to open the file
     * 
     * @param file
     * @throws IOException  
     */
    public static void openFile(File file) throws IOException {
        if (file == null) {
            JOptionPane.showMessageDialog(null,
                    "Cannot open the supplied file because it was null",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!file.exists()) {
            JOptionPane.showMessageDialog(null,
                    "Cannot open the supplied file because it does not exist.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(file);
        } else {
            JOptionPane.showMessageDialog(null,
                    "Cannot open the supplied file because it \n AWT Desktop is not supported!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
    }

    /** Creates a PDF representation of a LaTeX file within the 
     *  provided directory with the given name. Uses /usr/texbin/pdflatex if it
     *  exists
     * 
     * @param dirname must not be null
     * @param filename  must not be null, must have .tex extension
     * @return
     * @throws IOException if file does not exist or end with .tex
     * @throws InterruptedException 
     */
    public static int makePDFFromLaTeX(String dirname, String filename)
            throws IOException, InterruptedException {
        return makePDFFromLaTeX("/usr/texbin/pdflatex", dirname, filename);
    }

    /** Creates a PDF representation of a LaTeX file within the 
     *  provided directory with the given name. 
     * 
     * @param pdfcmd the command for making the pdf within the OS
     * @param dirname must not be null
     * @param filename  must not be null, must have .tex extension
     * @return
     * @throws IOException if file does not exist or end with .tex
     * @throws InterruptedException 
     */
    public static int makePDFFromLaTeX(String pdfcmd, String dirname, String filename)
            throws IOException, InterruptedException {
        if (dirname == null) {
            throw new IllegalArgumentException("The directory name was null");
        }
        if (filename == null) {
            throw new IllegalArgumentException("The file name was null");
        }
        File d = new File(dirname);
        d.mkdir();
        File f = new File(d, filename);
        if (!f.exists()) {
            d.delete();
            throw new IOException("The file did not exist");
        }
        if (f.length() == 0) {
            d.delete();
            throw new IOException("The file was empty");
        }
        String fn = f.getName();
        String[] g = fn.split("\\.");
        if (!g[1].equals("tex")) {
            throw new IllegalArgumentException("The file was not a tex file");
        }

        ProcessBuilder b = new ProcessBuilder();
        b.command(pdfcmd, f.getName());
        b.directory(d);
        Process process = b.start();
        process.waitFor();
        return process.exitValue();
    }

    /** Creates a PDF representation of a LaTeX file within the 
     *  with the given name. Uses pdflatex if it
     *  exists
     *
     * @param file  must not be null, must have .tex extension
     * @return
     * @throws IOException if file does not exist or end with .tex
     * @throws InterruptedException 
     */
    public static int makePDFFromLaTeX(File file) throws IOException, InterruptedException {
        if (file == null) {
            throw new IllegalArgumentException("The file was null");
        }
        if (!file.exists()) {
            throw new IOException("The file did not exist");
        }
        if (file.length() == 0) {
            throw new IOException("The file was empty");
        }

        String fn = file.getName();
        String[] g = fn.split("\\.");
        if (!g[1].equals("tex")) {
            throw new IllegalArgumentException("The file was not a tex file");
        }
        ProcessBuilder b = new ProcessBuilder();
        b.command("/usr/texbin/pdflatex", file.getName());
        Process process = b.start();
        process.waitFor();
        return process.exitValue();
    }
    
    /** Turns the supplied array into comma separated values
     * 
     * @param x
     * @return 
     */
    public static StringBuilder makeCSV(double[] x){
        if (x == null){
            throw new IllegalArgumentException("The array was null0");
        }
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< x.length; i++){
            sb.append(x[i]);
            if (i < x.length - 1){
                sb.append(",");
            }
        }
        sb.append(System.lineSeparator());
        return sb;
    }
}
