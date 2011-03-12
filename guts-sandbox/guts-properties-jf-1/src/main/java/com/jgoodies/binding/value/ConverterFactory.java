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

package com.jgoodies.binding.value;

import static com.jgoodies.common.base.Preconditions.checkArgument;
import static com.jgoodies.common.base.Preconditions.checkNotNull;

import java.text.Format;
import java.text.ParseException;

/**
 * A factory that vends ValueModels that convert types, for example
 * Dates to Strings. More formally, a converting ValueModel <i>VM1</i>
 * converts the type <i>T2</i> of an object being held as a value in
 * one ValueModel <i>VM2</i> into another type <i>T1</i>.
 * When reading a value from VM1, instances of T2 are read from VM2
 * and are converted to T1. When storing a new value to VM1,
 * the type converter will perform the inverse conversion and
 * will convert an instance of T1 to T2.<p>
 *
 * Type converters should be used judiciously and only to bridge two
 * ValueModels. To bind non-Strings to a text UI component
 * you should better use a {@link javax.swing.JFormattedTextField}.
 * They provide a more powerful means to convert strings to objects
 * and handle many cases that arise around invalid input. See also the classes
 * {@link com.jgoodies.binding.adapter.Bindings} and
 * {@link com.jgoodies.binding.adapter.BasicComponentFactory} on how to
 * bind ValueModels to formatted text fields.<p>
 *
 * The inner converter implementations have a 'public' visibility
 * to enable reflection access.
 *
 * @author  Karsten Lentzsch
 * @version $Revision: 1.12 $
 *
 * @see     ValueModel
 * @see     Format
 * @see     javax.swing.JFormattedTextField
 */
public final class ConverterFactory {

    private ConverterFactory() {
        // Overrides default constructor; prevents instantiation.
    }

    // Factory Methods ********************************************************

    /**
     * Creates and returns a ValueModel that negates Booleans and leaves
     * null unchanged.<p>
     *
     * <strong>Constraints:</strong> The subject is of type {@code Boolean}.
     *
     * @param booleanSubject  a Boolean ValueModel
     * @return a ValueModel that inverts Booleans
     *
     * @throws NullPointerException if the subject is {@code null}
     */
    public static ValueModel<Boolean> createBooleanNegator(
            ValueModel<Boolean> booleanSubject) {
        return new BooleanNegator(booleanSubject);
    }


    /**
     * Creates and returns a ValueModel that converts Booleans
     * to the associated of the two specified strings, and vice versa.
     * Null values are mapped to an empty string.
     * Ignores cases when setting a text.<p>
     *
     * <strong>Constraints:</strong> The subject is of type {@code Boolean}.
     *
     * @param booleanSubject  a Boolean ValueModel
     * @param trueText      the text associated with {@code Boolean.TRUE}
     * @param falseText     the text associated with {@code Boolean.FALSE}
     *
     * @return a ValueModel that converts boolean to the associated text
     *
     * @throws NullPointerException if the subject, trueText or falseText
     *         is {@code null}
     * @throws IllegalArgumentException if the trueText equals the falseText
     */
    public static ValueModel<String> createBooleanToStringConverter(
            ValueModel<Boolean> booleanSubject,
            String trueText,
            String falseText) {
        return createBooleanToStringConverter(
                booleanSubject,
                trueText,
                falseText,
                "");
    }


    /**
     * Creates and returns a ValueModel that converts Booleans
     * to the associated of the two specified strings, and vice versa.
     * Null values are mapped to the specified text.
     * Ignores cases when setting a text.<p>
     *
     * <strong>Constraints:</strong> The subject is of type {@code Boolean}.
     *
     * @param booleanSubject  a Boolean ValueModel
     * @param trueText      the text associated with {@code Boolean.TRUE}
     * @param falseText     the text associated with {@code Boolean.FALSE}
     * @param nullText      the text associated with {@code null}
     *
     * @return a ValueModel that converts boolean to the associated text
     *
     * @throws NullPointerException if the subject, trueText, falseText
     *     or nullText is {@code null}
     * @throws IllegalArgumentException if the trueText equals the falseText
     */
    public static ValueModel<String> createBooleanToStringConverter(
            ValueModel<Boolean> booleanSubject,
            String trueText,
            String falseText,
            String nullText) {
        return new BooleanToStringConverter(booleanSubject, trueText, falseText, nullText);
    }


    /**
     * Creates and returns a ValueModel that converts Doubles using the
     * specified multiplier.<p>
     *
     * Examples: multiplier=100, Double(1.23) -> Double(123),
     * multiplier=1000, Double(1.23) -> Double(1230)<p>
     *
     * <strong>Constraints:</strong> The subject is of type {@code Double}.
     *
     * @param doubleSubject  a Double ValueModel
     * @param multiplier the multiplier used for the conversion
     *
     * @return a ValueModel that converts Doubles using the specified multiplier
     *
     * @throws NullPointerException if the subject is {@code null}
     *
     * @since 1.0.2
     */
    public static ValueModel<Double> createDoubleConverter(
            ValueModel<Double> doubleSubject, double multiplier) {
        return new DoubleConverter(doubleSubject, multiplier);
    }


    /**
     * Creates and returns a ValueModel that converts Doubles to Integer,
     * and vice versa.<p>
     *
     * <strong>Constraints:</strong> The subject is of type {@code Double}.
     *
     * @param doubleSubject  a Double ValueModel
     *
     * @return a ValueModel that converts Doubles to Integer
     *
     * @throws NullPointerException if the subject is {@code null}
     */
    public static ValueModel<Integer> createDoubleToIntegerConverter(
            ValueModel<Double> doubleSubject) {
        return createDoubleToIntegerConverter(doubleSubject, 1);
    }


    /**
     * Creates and returns a ValueModel that converts Doubles to Integer,
     * and vice versa. The multiplier can be used to convert Doubles
     * to percent, permill, etc. For a percentage, set the multiplier to be 100,
     * for a permill, set the multiplier to be 1000.<p>
     *
     * Examples: multiplier=100, Double(1.23) -> Integer(123),
     * multiplier=1000, Double(1.23) -> Integer(1230)<p>
     *
     * <strong>Constraints:</strong> The subject is of type {@code Double}.
     *
     * @param doubleSubject  a Double ValueModel
     * @param multiplier the multiplier used to convert the Double to Integer
     *
     * @return a ValueModel that converts Doubles to Integer
     *
     * @throws NullPointerException if the subject is {@code null}
     */
    public static ValueModel<Integer> createDoubleToIntegerConverter(
            ValueModel<Double> doubleSubject, int multiplier) {
        return new DoubleToIntegerConverter(doubleSubject, multiplier);
    }


    /**
     * Creates and returns a ValueModel that converts Floats using the
     * specified multiplier.<p>
     *
     * Examples: multiplier=100, Float(1.23) -> Float(123),
     * multiplier=1000, Float(1.23) -> Float(1230)<p>
     *
     * <strong>Constraints:</strong> The subject is of type {@code Float}.
     *
     * @param floatSubject  a Float ValueModel
     * @param multiplier the multiplier used for the conversion
     *
     * @return a ValueModel that converts Float using the specified multiplier
     *
     * @throws NullPointerException if the subject is {@code null}
     *
     * @since 1.0.2
     */
    public static ValueModel<Float> createFloatConverter(
            ValueModel<Float> floatSubject, float multiplier) {
        return new FloatConverter(floatSubject, multiplier);
    }


    /**
     * Creates and returns a ValueModel that converts Floats to Integer,
     * and vice versa.<p>
     *
     * <strong>Constraints:</strong> The subject is of type {@code Float}.
     * s
     * @param floatSubject  a Float ValueModel
     *
     * @return a ValueModel that converts Floats to Integer
     *
     * @throws NullPointerException if the subject is {@code null}
     */
    public static ValueModel<Integer> createFloatToIntegerConverter(ValueModel<Float> floatSubject) {
        return createFloatToIntegerConverter(floatSubject, 1);
    }


    /**
     * Creates and returns a ValueModel that converts Floats to Integer,
     * and vice versa. The multiplier can be used to convert Floats
     * to percent, permill, etc. For a percentage, set the multiplier to be 100,
     * for a permill, set the multiplier to be 1000.<p>
     *
     * <strong>Constraints:</strong> The subject is of type {@code Float}.
     *
     * @param floatSubject  a Float ValueModel
     * @param multiplier the multiplier used to convert the Float to Integer
     *
     * @return a ValueModel that converts Floats to Integer
     *
     * @throws NullPointerException if the subject is {@code null}
     */
    public static ValueModel<Integer> createFloatToIntegerConverter(
            ValueModel<Float> floatSubject, int multiplier) {
        return new FloatToIntegerConverter(floatSubject, multiplier);
    }


    /**
     * Creates and returns a ValueModel that converts Integers using the
     * specified multiplier.<p>
     *
     * Examples: multiplier=100, Integer(3) -> Integer(300),
     * multiplier=1000, Integer(3) -> Integer(3000)<p>
     *
     * <strong>Constraints:</strong> The subject is of type {@code Integer}.
     *
     * @param integerSubject  a Integer ValueModel
     * @param multiplier the multiplier used for the conversion
     *
     * @return a ValueModel that converts Integers using the specified multiplier
     *
     * @throws NullPointerException if the subject is {@code null}
     *
     * @since 1.0.2
     */
    public static ValueModel<Integer> createIntegerConverter(
            ValueModel<Integer> integerSubject, double multiplier) {
        return new IntegerConverter(integerSubject, multiplier);
    }


    /**
     * Creates and returns a ValueModel that converts Long using the
     * specified multiplier.<p>
     *
     * Examples: multiplier=100, Long(3) -> Long(300),
     * multiplier=1000, Long(3) -> Long(3000)<p>
     *
     * <strong>Constraints:</strong> The subject is of type {@code Long}.
     *
     * @param longSubject  a Long ValueModel
     * @param multiplier the multiplier used for the conversion
     *
     * @return a ValueModel that converts Longs using the specified multiplier
     *
     * @throws NullPointerException if the subject is {@code null}
     *
     * @since 1.0.2
     */
    public static ValueModel<Long> createLongConverter(
            ValueModel<Long> longSubject, double multiplier) {
        return new LongConverter(longSubject, multiplier);
    }


    /**
     * Creates and returns a ValueModel that converts Longs to Integer
     * and vice versa.<p>
     *
     * <strong>Constraints:</strong> The subject is of type {@code Long},
     * values written to the converter are of type {@code Integer}.
     *
     * @param longSubject  a Long ValueModel
     * @return a ValueModel that converts Longs to Integer
     *
     * @throws NullPointerException if the subject is {@code null}
     */
    public static ValueModel<Integer> createLongToIntegerConverter(ValueModel<Long> longSubject) {
        return createLongToIntegerConverter(longSubject, 1);
    }


    /**
     * Creates and returns a ValueModel that converts Longs to Integer
     * and vice versa.<p>
     *
     * <strong>Constraints:</strong> The subject is of type {@code Long},
     * values written to the converter are of type {@code Integer}.
     *
     * @param longSubject  a Long ValueModel
     * @param multiplier   used to multiply the Long when converting to Integer
     * @return a ValueModel that converts Longs to Integer
     *
     * @throws NullPointerException if the subject is {@code null}
     */
    public static ValueModel<Integer> createLongToIntegerConverter(
            ValueModel<Long> longSubject, int multiplier) {
        return new LongToIntegerConverter(longSubject, multiplier);
    }


    /**
     * Creates and returns a ValueModel that converts objects to Strings
     * and vice versa. The conversion is performed by a {@code Format}.<p>
     *
     * <strong>Constraints:</strong> The subject is of type {@code Object};
     * it must be formattable and parsable via the given {@code Format}.
     *
     * @param subject  the underlying ValueModel.
     * @param format   the {@code Format} used to format and parse
     *
     * @return a ValueModel that converts objects to Strings and vice versa
     *
     * @throws NullPointerException if the subject or the format
     *         is {@code null}
     */
    public static <T> ValueModel<String> createStringConverter(ValueModel<T> subject, Format format) {
        return new StringConverter<T>(subject, format);
    }


    // Converter Implementations **********************************************

    /**
     * Negates Booleans leaving null unchanged. Maps Boolean.TRUE
     * to Boolean.FALSE, Boolean.FALSE to Boolean.TRUE, and null to null.
     */
    public static final class BooleanNegator extends AbstractConverter<Boolean, Boolean> {

        BooleanNegator(ValueModel<Boolean> booleanSubject) {
            super(booleanSubject);
        }


        /**
         * Negates Booleans leaving null unchanged.
         * Maps Boolean.TRUE to Boolean.FALSE,
         * Boolean.FALSE to Boolean.TRUE, and null to null.
         *
         * @param subjectValue   the subject value to invert
         * @return the text that represents the subject value
         *
         * @throws ClassCastException if the subject's value is not a Boolean
         */
        @Override
        public Boolean convertFromSubject(Boolean subjectValue) {
            return negate(subjectValue);
        }


        /**
         * Inverts the given Boolean and sets it as the subject's new value.
         *
         * @param newValue the value to be inverted and set as new subject value
         * @throws ClassCastException if the new value is not a Boolean
         * @throws IllegalArgumentException if the new value does neither match
         *     the trueText nor the falseText
         */
        @Override public void setValue(Boolean newValue) {
            subject.setValue(negate(newValue));
        }


        /**
         * Negates Booleans leaving null unchanged.
         * Maps Boolean.TRUE to Boolean.FALSE ,
         * Boolean.FALSE to Boolean.TRUE, and null to null.
         *
         * @param value   the value to invert
         * @return the inverted Boolean value, or null if value is null
         *
         * @throws ClassCastException if the value is not a Boolean
         */
        private Boolean negate(Boolean value) {
            if (value == null) {
                return null;
            } else if (Boolean.TRUE.equals(value)) {
                return Boolean.FALSE;
            } else if (Boolean.FALSE.equals(value)) {
                return Boolean.TRUE;
            } else {
                throw new ClassCastException("The value must be a Boolean.");
            }
        }

    }


    /**
     * Converts Booleans to Strings and vice-versa using given texts for
     * true, false, and null. Throws a ClassCastException if the value
     * to convert is not a Boolean, or not a String for the reverse conversion.
     */
    public static final class BooleanToStringConverter extends AbstractConverter<Boolean, String> {

        private final String trueText;
        private final String falseText;
        private final String nullText;

        BooleanToStringConverter(
                ValueModel<Boolean> booleanSubject,
                String trueText,
                String falseText,
                String nullText) {
            super(booleanSubject);
            this.trueText  = checkNotNull(trueText, "The trueText must not be null.");
            this.falseText = checkNotNull(falseText, "The falseText must not be null.");
            this.nullText  = checkNotNull(nullText, "The nullText must not be null.");
            checkArgument(!trueText.equals(falseText),
                    "The trueText and falseText must be different.");
        }


        /**
         * Converts the subject value to associated text representation.
         * Rejects non-Boolean values.
         *
         * @param subjectValue the subject's new value
         * @return the text that represents the subject value
         *
         * @throws ClassCastException if the subject's value is not a Boolean
         */
        @Override
        public String convertFromSubject(Boolean subjectValue) {
            if (Boolean.TRUE.equals(subjectValue)) {
                return trueText;
            } else if (Boolean.FALSE.equals(subjectValue)) {
                return falseText;
            } else if (subjectValue == null) {
                return nullText;
            } else {
                throw new ClassCastException(
                "The subject value must be of type Boolean.");
            }
        }


        /**
         * Converts the given String and sets the associated Boolean as
         * the subject's new value. In case the new value equals neither
         * this class' trueText, nor the falseText, nor the nullText,
         * an IllegalArgumentException is thrown.
         *
         * @param newValue  the value to be converted and set as new subject value
         * @throws ClassCastException if the new value is not a String
         * @throws IllegalArgumentException if the new value does neither match
         *     the trueText nor the falseText nor the nullText
         */
        @Override public void setValue(String newValue) {
            if (trueText.equalsIgnoreCase(newValue)) {
                subject.setValue(Boolean.TRUE);
            } else if (falseText.equalsIgnoreCase(newValue)) {
                subject.setValue(Boolean.FALSE);
            } else if (nullText.equalsIgnoreCase(newValue)) {
                subject.setValue(null);
            } else {
                throw new IllegalArgumentException(
                        "The new value must be one of: "
                      + trueText + '/'
                      + falseText + '/'
                      + nullText);
            }
        }

    }


    /**
     * Converts Doubles using a given multiplier.
     */
    public static final class DoubleConverter extends AbstractConverter<Double, Double> {

        private final double multiplier;

        DoubleConverter(
                ValueModel<Double> doubleSubject, double multiplier) {
            super(doubleSubject);
            this.multiplier = multiplier;
        }

        /**
         * Converts the subject's value and returns a
         * corresponding {@code Double} using the multiplier.
         *
         * @param subjectValue  the subject's value
         * @return the converted subjectValue
         * @throws ClassCastException if the subject value is not of type
         *     {@code Double}
         */
        @Override
        public Double convertFromSubject(Double subjectValue) {
            return Double.valueOf(subjectValue * multiplier);
        }

        /**
         * Converts a {@code Double} using the multiplier
         * and sets it as new value.
         *
         * @param newValue  the {@code Double} object that shall be converted
         * @throws ClassCastException if the new value is not of type
         *     {@code Double}
         */
        @Override public void setValue(Double newValue) {
            subject.setValue(newValue / multiplier);
        }

    }

    /**
     * Converts Doubles to Integers and vice-versa.
     */
    public static final class DoubleToIntegerConverter extends AbstractConverter<Double, Integer> {

        private final int multiplier;

        DoubleToIntegerConverter(
                ValueModel<Double> doubleSubject, int multiplier) {
            super(doubleSubject);
            this.multiplier = multiplier;
        }

        /**
         * Converts the subject's value and returns a
         * corresponding {@code Integer} value using the multiplier.
         *
         * @param subjectValue  the subject's value
         * @return the converted subjectValue
         * @throws ClassCastException if the subject value is not of type
         *     {@code Double}
         */
        @Override
        public Integer convertFromSubject(Double subjectValue) {
            double doubleValue = subjectValue;
            if (multiplier != 1) {
                doubleValue *= multiplier;
            }
            return Integer.valueOf((int) Math.round(doubleValue));
        }

        /**
         * Converts a {@code Double} using the multiplier
         * and sets it as new value.
         *
         * @param newValue  the {@code Integer} object that shall
         *     be converted
         * @throws ClassCastException if the new value is not of type
         *     {@code Integer}
         */
        @Override public void setValue(Integer newValue) {
            double doubleValue = newValue.doubleValue();
            if (multiplier != 1) {
                doubleValue /= multiplier;
            }
            subject.setValue(Double.valueOf(doubleValue));
        }

    }

    /**
     * Converts Floats using a given multiplier.
     */
    public static final class FloatConverter extends AbstractConverter<Float, Float> {

        private final float multiplier;

        FloatConverter(
                ValueModel<Float> floatSubject, float multiplier) {
            super(floatSubject);
            this.multiplier = multiplier;
        }

        /**
         * Converts the subject's value and returns a
         * corresponding {@code Float} using the multiplier.
         *
         * @param subjectValue  the subject's value
         * @return the converted subjectValue
         * @throws ClassCastException if the subject value is not of type
         *     {@code Float}
         */
        @Override
        public Float convertFromSubject(Float subjectValue) {
            return Float.valueOf(subjectValue * multiplier);
        }

        /**
         * Converts a {@code Float} using the multiplier
         * and sets it as new value.
         *
         * @param newValue  the {@code Float} object that shall be converted
         * @throws ClassCastException if the new value is not of type
         *     {@code Float}
         */
        @Override public void setValue(Float newValue) {
            subject.setValue(Float.valueOf(newValue / multiplier));
        }

    }

    /**
     * Converts Floats to Integers and vice-versa.
     */
    public static final class FloatToIntegerConverter extends AbstractConverter<Float, Integer> {

        private final int multiplier;

        FloatToIntegerConverter(
                ValueModel<Float> floatSubject, int multiplier) {
            super(floatSubject);
            this.multiplier = multiplier;
        }


        /**
         * Converts the subject's value and returns a
         * corresponding {@code Integer} using the multiplier.
         *
         * @param subjectValue  the subject's value
         * @return the converted subjectValue
         * @throws ClassCastException if the subject value is not of type
         *     {@code Float}
         */
        @Override
        public Integer convertFromSubject(Float subjectValue) {
            float floatValue = subjectValue;
            if (multiplier != 1) {
                floatValue *= multiplier;
            }
            return Integer.valueOf(Math.round(floatValue));
        }


        /**
         * Converts a {@code Float} using the multiplier and
         * sets it as new value.
         *
         * @param newValue  the {@code Integer} object that shall be converted
         * @throws ClassCastException if the new value is not of type
         *     {@code Integer}
         */
        @Override public void setValue(Integer newValue) {
            float floatValue = newValue.floatValue();
            if (multiplier != 1) {
                floatValue /= multiplier;
            }
            subject.setValue(Float.valueOf(floatValue));
        }

    }


    /**
     * Converts Longs using a given multiplier.
     */
    public static final class LongConverter extends AbstractConverter<Long, Long> {

        private final double multiplier;

        LongConverter(
                ValueModel<Long> longSubject, double multiplier) {
            super(longSubject);
            this.multiplier = multiplier;
        }

        /**
         * Converts the subject's value and returns a
         * corresponding {@code Long} using the multiplier.
         *
         * @param subjectValue  the subject's value
         * @return the converted subjectValue
         * @throws ClassCastException if the subject value is not of type
         *     {@code Long}
         */
        @Override
        public Long convertFromSubject(Long subjectValue) {
            return Long.valueOf((long) (subjectValue * multiplier));
        }

        /**
         * Converts a {@code Long} using the multiplier
         * and sets it as new value.
         *
         * @param newValue  the {@code Long} object that shall be converted
         * @throws ClassCastException if the new value is not of type
         *     {@code Long}
         */
        @Override public void setValue(Long newValue) {
            double doubleValue = newValue.doubleValue();
            subject.setValue(Long.valueOf((long) (doubleValue / multiplier)));
        }

    }

    /**
     * Converts Integers using a given multiplier.
     */
    public static final class IntegerConverter extends AbstractConverter<Integer, Integer> {

        private final double multiplier;

        IntegerConverter(
                ValueModel<Integer> integerSubject, double multiplier) {
            super(integerSubject);
            this.multiplier = multiplier;
        }

        /**
         * Converts the subject's value and returns a
         * corresponding {@code Integer} using the multiplier.
         *
         * @param subjectValue  the subject's value
         * @return the converted subjectValue
         * @throws ClassCastException if the subject value is not of type
         *     {@code Integer}
         */
        @Override
        public Integer convertFromSubject(Integer subjectValue) {
            return Integer.valueOf((int) (subjectValue * multiplier));
        }

        /**
         * Converts a {@code Integer} using the multiplier
         * and sets it as new value.
         *
         * @param newValue  the {@code Integer} object that shall be converted
         * @throws ClassCastException if the new value is not of type
         *     {@code Integer}
         */
        @Override public void setValue(Integer newValue) {
            double doubleValue = newValue.doubleValue();
            subject.setValue(Integer.valueOf((int) (doubleValue / multiplier)));
        }

    }

    /**
     * Converts Longs to Integers and vice-versa.
     */
    public static final class LongToIntegerConverter extends AbstractConverter<Long, Integer> {

        private final int multiplier;

        LongToIntegerConverter(
                ValueModel<Long> longSubject, int multiplier) {
            super(longSubject);
            this.multiplier = multiplier;
        }

        /**
         * Converts the subject's value and returns a
         * corresponding {@code Integer}.
         *
         * @param subjectValue  the subject's value
         * @return the converted subjectValue
         * @throws ClassCastException if the subject value is not of type
         *     {@code Float}
         */
        @Override
        public Integer convertFromSubject(Long subjectValue) {
            int intValue = subjectValue.intValue();
            if (multiplier != 1) {
                intValue *= multiplier;
            }
            return Integer.valueOf(intValue);
        }

        /**
         * Converts an Integer to Long and sets it as new value.
         *
         * @param newValue  the {@code Integer} object that represents
         *     the percent value
         * @throws ClassCastException if the new value is not of type
         *     {@code Integer}
         */
        @Override public void setValue(Integer newValue) {
            long longValue = newValue.longValue();
            if (multiplier != 1) {
                longValue /= multiplier;
            }
            subject.setValue(Long.valueOf(longValue));
        }

    }


    /**
     * Converts Values to Strings and vice-versa using a given Format.
     */
    public static final class StringConverter<T> extends AbstractConverter<T, String> {

        /**
         * Holds the {@code Format} used to format and parse.
         */
        private final Format format;


        // Instance Creation **************************************************

        /**
         * Constructs a {@code StringConverter} on the given
         * subject using the specified {@code Format}.
         *
         * @param subject  the underlying ValueModel.
         * @param format   the {@code Format} used to format and parse
         * @throws NullPointerException if the subject or the format is null.
         */
        StringConverter(ValueModel<T> subject, Format format) {
            super(subject);
            this.format = checkNotNull(format, "The format must not be null.");
        }


        // Implementing Abstract Behavior *************************************

        /**
         * Formats the subject value and returns a String representation.
         *
         * @param subjectValue  the subject's value
         * @return the formatted subjectValue
         */
        @Override
        public String convertFromSubject(T subjectValue) {
            return format.format(subjectValue);
        }


        // Implementing ValueModel ********************************************

        /**
         * Parses the given String encoding and sets it as the subject's
         * new value. Silently catches {@code ParseException}.
         *
         * @param value  the value to be converted and set as new subject value
         */
        @SuppressWarnings("unchecked") 
        @Override public void setValue(String value) {
            try {
                subject.setValue((T) format.parseObject(value));
            } catch (ParseException e) {
                // Do not change the subject value
            }
        }

    }


}
