/*
 * Copyright (c) 2009-2010 JGoodies Karsten Lentzsch. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of JGoodies Karsten Lentzsch nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jgoodies.common.base;

import java.util.logging.Logger;

/**
 * Provides convenience behavior to determine the operating system
 * and Java version.
 *
 * @author Karsten Lentzsch
 * @version $Revision: 1.6 $
 */
public class SystemUtils {

    // Internal Constants *****************************************************

    /**
     * The {@code os.name} System Property. Operating system name.<p>
     *
     * Defaults to {@code null}, if the runtime does not have security
     * access to read this property or the property does not exist.
     */
    protected static final String OS_NAME = getSystemProperty("os.name");


    /**
     * The {@code os.version} System Property. Operating system version.<p>
     *
     * Defaults to {@code null}, if the runtime does not have security
     * access to read this property or the property does not exist.
     */
    protected static final String OS_VERSION = getSystemProperty("os.version");


    /**
     * The {@code os.name} System Property. Operating system name.<p>
     *
     * Defaults to {@code null}, if the runtime does not have security
     * access to read this property or the property does not exist.
     */
    protected static final String JAVA_VERSION = getSystemProperty("java.version");


    // Requesting the OS and OS Version ***************************************

    /**
     * Is true if this is Linux.
     */
    public static final boolean IS_OS_LINUX =
        startsWith(OS_NAME, "Linux") || startsWith(OS_NAME, "LINUX");


    /**
     * True if this is the Mac OS.
     */
    public static final boolean IS_OS_MAC =
        startsWith(OS_NAME, "Mac OS");


    /**
     * True if this is Solaris.
     */
    public static final boolean IS_OS_SOLARIS =
        startsWith(OS_NAME, "Solaris");


    /**
     * True if this is Windows.
     */
    public static final boolean IS_OS_WINDOWS =
        startsWith(OS_NAME, "Windows");


    /**
     * True if this is Windows 98.
     */
    public static final boolean IS_OS_WINDOWS_98 =
        startsWith(OS_NAME, "Windows 9") && startsWith(OS_VERSION, "4.1");


    /**
     * True if this is Windows ME.
     */
    public static final boolean IS_OS_WINDOWS_ME =
        startsWith(OS_NAME, "Windows") && startsWith(OS_VERSION, "4.9");


    /**
     * True if this is Windows 2000.
     */
    public static final boolean IS_OS_WINDOWS_2000 =
        startsWith(OS_NAME, "Windows") && startsWith(OS_VERSION, "5.0");


    /**
     * True if this is Windows XP.
     */
    public static final boolean IS_OS_WINDOWS_XP =
        startsWith(OS_NAME, "Windows") && startsWith(OS_VERSION, "5.1");


    /**
     * True if this is Windows Vista or Server 2008.
     */
    public static final boolean IS_OS_WINDOWS_VISTA =
        startsWith(OS_NAME, "Windows") && startsWith(OS_VERSION, "6.0");


    /**
     * True if this is Windows Vista/Server 2008/7/2008 R2.
     */
    public static final boolean IS_OS_WINDOWS_6_OR_LATER =
        startsWith(OS_NAME, "Windows") && startsWith(OS_VERSION, "6.");


    // Requesting the Java Version ********************************************

    /**
     * True if this is Java 1.4.
     */
    public static final boolean IS_JAVA_1_4 =
        startsWith(JAVA_VERSION, "1.4");



    /**
     * True if this is Java 5.x. We check for a prefix of 1.5.
     */
    public static final boolean IS_JAVA_5 =
        startsWith(JAVA_VERSION, "1.5");


    /**
     * True if this is Java 5.x or later. Since we support only Java 5 or later,
     * this is always true.
     */
    public static final boolean IS_JAVA_5_OR_LATER =
        !IS_JAVA_1_4;


    /**
     * True if this is Java 6. We check for a prefix of 1.6.
     */
    public static final boolean IS_JAVA_6 =
        startsWith(JAVA_VERSION, "1.6");

    /**
     * True if this is Java 6.x or later. We check that it's
     * neither 1.4 nor 1.5.
     */
    public static final boolean IS_JAVA_6_OR_LATER =
           !IS_JAVA_1_4
        && !IS_JAVA_5;


    /**
     * True if this is Java 7. We check for a prefix of 1.7.
     */
    public static final boolean IS_JAVA_7 =
        startsWith(JAVA_VERSION, "1.7");


    /**
     * True if this is Java 6.x or later. We check that it's neither
     * 1.4 nor 1.5 nor 1.6.
     */
    public static final boolean IS_JAVA_7_OR_LATER =
           !IS_JAVA_1_4
        && !IS_JAVA_5
        && !IS_JAVA_6;


    // Internal ***************************************************************

    protected SystemUtils() {
        // Override default constructor; prevents instantiation.
    }


    /**
     * Tries to look up the System property for the given key.
     * In untrusted environments this may throw a SecurityException.
     * In this case we catch the exception and answer an empty string.
     *
     * @param key   the name of the system property
     * @return the system property's String value, or {@code null} if there's
     *     no such value, or an empty String when
     *     a SecurityException has been caught
     */
    protected static String getSystemProperty(String key) {
        try {
            return System.getProperty(key);
        } catch (SecurityException e) {
            Logger.getLogger(SystemUtils.class.getName()).warning(
                  "Can't access the System property " + key + ".");
            return "";
        }
    }


    protected static boolean startsWith(String str, String prefix) {
        return str != null && str.startsWith(prefix);
    }


}
