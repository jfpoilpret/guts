/*
 * Copyright (c) 2002-2010 JGoodies Karsten Lentzsch. All Rights Reserved.
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

package com.jgoodies.binding.adapter;

import static com.jgoodies.common.base.Preconditions.checkArgument;
import static com.jgoodies.common.base.Preconditions.checkNotNull;

import java.util.prefs.Preferences;

import com.jgoodies.binding.value.AbstractValueModel;

/**
 * A ValueModel implementation that reads and writes values from/to a key
 * of a given {@code Preferences} node under a specified key.
 * Write changes fire value changes.<p>
 *
 * <strong>Example:</strong><pre>
 * String  prefsKey = "isShowing";
 * Boolean defaultValue = Boolean.TRUE;
 * Preferences prefs = Workbench.userPreferences();
 * ValueModel model = new PreferencesAdapter(prefs, prefsKey, defaultValue);
 * JCheckBox showingBox = new JCheckBox("Show tips");
 * showingBox.setModel(new ToggleButtonAdapter(model));
 * </pre>
 *
 * @author Karsten Lentzsch
 * @version $Revision: 1.11 $
 *
 * @see java.util.prefs.Preferences
 */
public final class PreferencesAdapter<T> extends AbstractValueModel<T> {

    private static final String ERROR_MSG =
        "Value must be a Boolean, Double, Float, Integer, Long, or String.";

    /**
     * Refers to the preferences node that is used to persist the bound data.
     */
    private final Preferences prefs;

    /**
     * Holds the preferences key that is used to access the stored value.
     */
    private final String key;

    /**
     * Refers to the type of accepted values.
     */
    private final Class<T> type;

    /**
     * Holds the default value that is used if the preferences
     * do not yet store a value.
     */
    private final T defaultValue;


    //Instance Creation *******************************************************

    /**
     * Constructs a PreferencesAdapter on the given {@code Preferences}
     * using the specified key and default value, all which must be
     * non-{@code null}.
     *
     * @param prefs   the {@code Preferences} used to store and retrieve
     * @param key     the key used to get and set values  in the Preferences
     * @param defaultValue    the default value
     *
     * @throws NullPointerException if the Preferences, key, or default value
     *     is {@code null}
     * @throws IllegalArgumentException if the default value is of a type other
     *     than Boolean, Double, Float, Integer, Long, or String.
     */
    @SuppressWarnings("unchecked") 
    public PreferencesAdapter(
        Preferences prefs,
        String key,
        T defaultValue) {
        this.prefs = checkNotNull(prefs, "The Preferences must not be null.");
        this.key = checkNotNull(key, "The key must not be null.");
        this.defaultValue = checkNotNull(defaultValue, "The default value must not be null.");
        checkArgument(isBackedType(defaultValue), "The Default " + ERROR_MSG);
        this.type = (Class<T>) defaultValue.getClass();
    }


    // ValueModel Implementation **********************************************

    /**
     * Looks up and returns the value from the preferences. The value is
     * look up under this adapter's key. It will be converted before it is
     * returned.
     *
     * @return the retrieved and converted value
     * @throws ClassCastException if the type of the default value
     *     cannot be read from the preferences
     */
    @SuppressWarnings("unchecked") 
    @Override public T getValue() {
        if (type == Boolean.class) {
            return (T) Boolean.valueOf(getBoolean());
        } else if (type == Double.class) {
            return (T) Double.valueOf(getDouble());
        } else if (type == Float.class) {
            return (T) Float.valueOf(getFloat());
        } else if (type == Integer.class) {
            return (T) Integer.valueOf(getInt());
        } else if (type == Long.class) {
            return (T) Long.valueOf(getLong());
        } else if (type == String.class) {
            return (T) getString();
        } else {
            throw new ClassCastException(ERROR_MSG);
        }
    }

    /**
     * Converts the given value to a string and puts it into the preferences.
     *
     * @param newValue   the object to be stored
     * @throws IllegalArgumentException if the new value cannot be stored
     *      in the preferences due to an illegal type
     */
    @Override public void setValue(T newValue) {
        checkNotNull(newValue, "The value must not be null.");
        Class<?> valueType = newValue.getClass();
        if (type != valueType)
            throw new IllegalArgumentException(
                    "The type of the value set must be consistent "
                  + "with the default value type " + type);

        T oldValue = getValue();
        if (newValue instanceof Boolean) {
            setBoolean(((Boolean) newValue).booleanValue());
        } else if (newValue instanceof Double) {
            setDouble(((Double) newValue).doubleValue());
        } else if (newValue instanceof Float) {
            setFloat(((Float) newValue).floatValue());
        } else if (newValue instanceof Integer) {
            setInt(((Integer) newValue).intValue());
        } else if (newValue instanceof Long) {
            setLong(((Long) newValue).longValue());
        } else if (newValue instanceof String) {
            setString((String) newValue);
        }
        fireValueChange(oldValue, newValue);
    }


    // Convenience Accessors **************************************************

    /**
     * Looks up, converts and returns the stored value from the preferences.
     * Returns the default value if no value has been stored before.
     *
     * @return the stored value or the default
     */
    private boolean getBoolean() {
        return prefs.getBoolean(key, ((Boolean) defaultValue).booleanValue());
    }

    /**
     * Looks up, converts and returns the stored value from the preferences.
     * Returns the default value if no value has been stored before.
     *
     * @return the stored value or the default
     */
    private double getDouble() {
        return prefs.getDouble(key, ((Double) defaultValue).doubleValue());
    }

    /**
     * Looks up, converts and returns the stored value from the preferences.
     * Returns the default value if no value has been stored before.
     *
     * @return the stored value or the default
     */
    private float getFloat() {
        return prefs.getFloat(key, ((Float) defaultValue).floatValue());
    }

    /**
     * Looks up, converts and returns the stored value from the preferences.
     * Returns the default value if no value has been stored before.
     *
     * @return the stored value or the default
     */
    private int getInt() {
        return prefs.getInt(key, ((Integer) defaultValue).intValue());
    }

    /**
     * Looks up, converts and returns the stored value from the preferences.
     * Returns the default value if no value has been stored before.
     *
     * @return the stored value or the default
     */
    private long getLong() {
        return prefs.getLong(key, ((Long) defaultValue).longValue());
    }

    /**
     * Looks up, converts and returns the stored value from the preferences.
     * Returns the default value if no value has been stored before.
     *
     * @return the stored value or the default
     */
    private String getString() {
        return prefs.get(key, (String) defaultValue);
    }


    /**
     * Converts the given value to an Object and stores it in this
     * adapter's Preferences under this adapter's preferences key.
     *
     * @param newValue   the value to put into the Preferences
     *
     * @throws ClassCastException   if the default value is not a Boolean
     */
    private void setBoolean(boolean newValue) {
        prefs.putBoolean(key, newValue);
    }

    /**
     * Converts the given value to an Object and stores it in this
     * adapter's Preferences under this adapter's preferences key.
     *
     * @param newValue   the value to put into the Preferences
     *
     * @throws ClassCastException   if the default value is not a Double
     */
    private void setDouble(double newValue) {
        prefs.putDouble(key, newValue);
    }

    /**
     * Converts the given value to an Object and stores it in this
     * adapter's Preferences under this adapter's preferences key.
     *
     * @param newValue   the value to put into the Preferences
     *
     * @throws ClassCastException   if the default value is not a Float
     */
    private void setFloat(float newValue) {
        prefs.putFloat(key, newValue);
    }

    /**
     * Converts the given value to an Object and stores it in this
     * adapter's Preferences under this adapter's preferences key.
     *
     * @param newValue   the value to put into the Preferences
     *
     * @throws ClassCastException   if the default value is not an Integer
     */
    private void setInt(int newValue) {
        prefs.putInt(key, newValue);
    }

    /**
     * Converts the given value to an Object and stores it in this
     * adapter's Preferences under this adapter's preferences key.
     *
     * @param newValue   the value to put into the Preferences
     *
     * @throws ClassCastException   if the default value is not a Long
     */
    private void setLong(long newValue) {
        prefs.putLong(key, newValue);
    }

    /**
     * Converts the given value to an Object and stores it in this
     * adapter's Preferences under this adapter's preferences key.
     *
     * @param newValue   the value to put into the Preferences
     *
     * @throws ClassCastException   if the default value is not a String
     */
    @SuppressWarnings("unchecked") 
    private void setString(String newValue) {
        String oldValue = getString();
        prefs.put(key, newValue);
        fireValueChange((T) oldValue, (T) newValue);
    }


    // Helper Code ************************************************************

    /**
     * Used to check the default value type during construction.
     */
    private boolean isBackedType(Object value) {
        Class<?> aClass = value.getClass();
        return aClass == Boolean.class
             || aClass == Double.class
             || aClass == Float.class
             || aClass == Integer.class
             || aClass == Long.class
             || aClass == String.class;
    }

}
