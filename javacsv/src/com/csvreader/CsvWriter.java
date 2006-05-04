/*
 * Java CSV is a stream based library for reading and writing
 * CSV and other delimited data.
 *   
 * Copyright (C) Bruce Dunwiddie bruce@csvreader.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */
package com.csvreader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * A stream based writer for writing delimited text data to a file or a stream.
 */
public class CsvWriter {
    private static final char LF = '\n';

    private static final char CR = '\r';

    private static final char QUOTE = '"';

    private static final char COMMA = ',';

    private static final char SPACE = ' ';

    private static final char TAB = '\t';

    private static final char POUND = '#';

    private static final char BACKSLASH = '\\';

    private static final char NULL_CHAR = '\0';

    private PrintWriter outputStream = null;

    private String fileName = null;

    private boolean firstColumn = true;

    private char delimiter = COMMA;

    private char recordDelimiter = NULL_CHAR;

    private boolean useCustomRecordDelimiter = false;

    private char comment = POUND;

    private boolean forceQualifier = false;

    private int escapeMode = ESCAPE_MODE_DOUBLED;

    private char textQualifier = QUOTE;

    private boolean useTextQualifier = true;

    private Charset charset = null;

    private String singleQualifier = null;

    private String doubleQualifier = null;

    private boolean initialized = false;

    private boolean closed = false;

    /**
     * Double up the text qualifier to represent an occurance of the text
     * qualifier.
     */
    public static final int ESCAPE_MODE_DOUBLED = 1;

    /**
     * Use a backslash character before the text qualifier to represent an
     * occurance of the text qualifier.
     */
    public static final int ESCAPE_MODE_BACKSLASH = 2;

    /**
     * Creates a {@link com.csvreader.CsvWriter CsvWriter} object using a file
     * as the data destination.
     * 
     * @param fileName
     *            The path to the file to output the data.
     * @param delimiter
     *            The character to use as the column delimiter.
     * @param charset
     *            The {@link java.nio.charset.Charset Charset} to use while
     *            writing the data.
     */
    public CsvWriter(String fileName, char delimiter, Charset charset) {
        this();

        if (fileName == null) {
            throw new IllegalArgumentException("File name can not be null.");
        }

        if (charset == null) {
            throw new IllegalArgumentException("Charset can not be null.");
        }

        this.fileName = fileName;
        this.delimiter = delimiter;
        this.charset = charset;
    }

    /**
     * Creates a {@link com.csvreader.CsvWriter CsvWriter} object using a file
     * as the data destination.&nbsp;Uses a comma as the column delimiter and
     * ISO-8859-1 as the {@link java.nio.charset.Charset Charset}.
     * 
     * @param fileName
     *            The path to the file to output the data.
     */
    public CsvWriter(String fileName) {
        this(fileName, COMMA, Charset.forName("ISO-8859-1"));
    }

    /**
     * Creates a {@link com.csvreader.CsvWriter CsvWriter} object using a Writer
     * to write data to.
     * 
     * @param outputStream
     *            The stream to write the column delimited data to.
     * @param delimiter
     *            The character to use as the column delimiter.
     */
    public CsvWriter(Writer outputStream, char delimiter) {
        this();

        if (outputStream == null) {
            throw new IllegalArgumentException("Output stream can not be null.");
        }

        this.outputStream = new PrintWriter(outputStream);
        this.delimiter = delimiter;
        initialized = true;
    }

    /**
     * Creates a {@link com.csvreader.CsvWriter CsvWriter} object using an
     * OutputStream to write data to.
     * 
     * @param outputStream
     *            The stream to write the column delimited data to.
     * @param delimiter
     *            The character to use as the column delimiter.
     * @param charset
     *            The {@link java.nio.charset.Charset Charset} to use while
     *            writing the data.
     */
    public CsvWriter(OutputStream outputStream, char delimiter, Charset charset) {
        this(new OutputStreamWriter(outputStream, charset), delimiter);
    }

    /**
     * 
     */
    private CsvWriter() {
        initTextQualifier();
    }

    /**
     * Gets the character being used as the column delimiter.
     * 
     * @return The character being used as the column delimiter.
     */
    public char getDelimiter() {
        return delimiter;
    }

    /**
     * Sets the character to use as the column delimiter.
     * 
     * @param delimiter
     *            The character to use as the column delimiter.
     */
    public void setDelimiter(char delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Gets the character to use as a text qualifier in the data.
     * 
     * @return The character to use as a text qualifier in the data.
     */
    public char getTextQualifier() {
        return textQualifier;
    }

    /**
     * Sets the character to use as a text qualifier in the data.
     * 
     * @param textQualifier
     *            The character to use as a text qualifier in the data.
     */
    public void setTextQualifier(char textQualifier) {
        this.textQualifier = textQualifier;
    }

    /**
     * Whether text qualifiers will be used while writing data or not.
     * 
     * @return Whether text qualifiers will be used while writing data or not.
     */
    public boolean getUseTextQualifier() {
        return useTextQualifier;
    }

    /**
     * Sets whether text qualifiers will be used while writing data or not.
     * 
     * @param useTextQualifier
     *            Whether to use a text qualifier while writing data or not.
     */
    public void setUseTextQualifier(boolean useTextQualifier) {
        this.useTextQualifier = useTextQualifier;
    }

    /**
     * Sets the character to use as the record delimiter.
     * 
     * @param recordDelimiter
     *            The character to use as the record delimiter. Default is
     *            combination of standard end of line characters for Windows,
     *            Unix, or Mac.
     */
    public void setRecordDelimiter(char recordDelimiter) {
        useCustomRecordDelimiter = true;
        this.recordDelimiter = recordDelimiter;
    }

    public void setEscapeMode(int escapeMode) {
        this.escapeMode = escapeMode;
    }

    public int getEscapeMode() {
        return escapeMode;
    }

    public void setComment(char comment) {
        this.comment = comment;
    }

    public char getComment() {
        return comment;
    }

    /**
     * Use this to force all fields to be surrounded by the text qualifier even
     * if the qualifier is not necessarily needed to escape this field. Default
     * is false.
     * 
     * @param forceQualifier
     *            Whether to force the fields to be qualified or not.
     */
    public void setForceQualifier(boolean forceQualifier) {
        this.forceQualifier = forceQualifier;
    }

    /**
     * Whether fields will be surrounded by the text qualifier even if the
     * qualifier is not necessarily needed to escape this field.
     * 
     * @return Whether fields will be forced to be qualified or not.
     */
    public boolean getForceQualifier() {
        return forceQualifier;
    }

    /**
     * Writes another column of data to this record.
     * 
     * @param content
     *            The data for the new column.
     * @param preserveSpaces
     *            Whether to preserve leading and trailing whitespace in this
     *            column of data.
     * @exception IOException
     *                Thrown if an error occurs while writing data to the
     *                destination stream.
     */
    public void write(String content, boolean preserveSpaces)
            throws IOException {
        checkClosed();

        checkInit();

        if (content == null) {
            content = "";
        }

        if (!firstColumn) {
            outputStream.write(delimiter);
        }

        boolean textQualify = forceQualifier;

        if (!preserveSpaces && content.length() > 0) {
            content = content.trim();
        }

        if (!textQualify
                && useTextQualifier
                && (content.indexOf(textQualifier) > -1
                        || content.indexOf(delimiter) > -1
                        || (!useCustomRecordDelimiter && (content.indexOf(LF) > -1 || content
                                .indexOf(CR) > -1))
                        || (useCustomRecordDelimiter && content
                                .indexOf(recordDelimiter) > -1)
                        || (firstColumn && content.length() > 0 && content
                                .charAt(0) == comment) ||
                // check for empty first column, which if on its own line must
                // be qualified or the line will be skipped
                (firstColumn && content.length() == 0))) {
            textQualify = true;
        }

        if (useTextQualifier && !textQualify && content.length() > 0
                && preserveSpaces) {
            char firstLetter = content.charAt(0);

            if (firstLetter == SPACE || firstLetter == TAB) {
                textQualify = true;
            }

            if (!textQualify && content.length() > 1) {
                char lastLetter = content.charAt(content.length() - 1);

                if (lastLetter == SPACE || lastLetter == TAB) {
                    textQualify = true;
                }
            }
        }

        if (textQualify) {
            outputStream.write(textQualifier);

            if (escapeMode == ESCAPE_MODE_BACKSLASH) {
                content = content.replaceAll("" + BACKSLASH,
                        "" + BACKSLASH + BACKSLASH).replaceAll(
                        "" + textQualifier, "" + BACKSLASH + textQualifier);
            } else {
                content = content.replaceAll("" + textQualifier, ""
                        + textQualifier + textQualifier);
            }
        } else if (escapeMode == ESCAPE_MODE_BACKSLASH) {
            content = content.replaceAll("" + BACKSLASH,
                    "" + BACKSLASH + BACKSLASH).replaceAll("" + delimiter,
                    "" + BACKSLASH + delimiter);

            if (useCustomRecordDelimiter) {
                content = content.replaceAll("" + recordDelimiter, ""
                        + BACKSLASH + recordDelimiter);
            } else {
                content = content.replaceAll("" + CR, "" + BACKSLASH + CR)
                        .replaceAll("" + LF, "" + BACKSLASH + LF);
            }

            if (firstColumn && content.length() > 0
                    && content.charAt(0) == comment) {
                if (content.length() > 1) {
                    content = "" + BACKSLASH + comment + content.substring(1);
                } else {
                    content = "" + BACKSLASH + comment;
                }
            }
        }

        if (textQualify) {
            outputStream.write(textQualifier);

            content = content.replaceAll(singleQualifier, doubleQualifier);
        }

        outputStream.write(content);

        if (textQualify) {
            outputStream.write(textQualifier);
        }

        firstColumn = false;
    }

    /**
     * Writes another column of data to this record.&nbsp;Does not preserve
     * leading and trailing whitespace in this column of data.
     * 
     * @param content
     *            The data for the new column.
     * @exception IOException
     *                Thrown if an error occurs while writing data to the
     *                destination stream.
     */
    public void write(String content) throws IOException {
        write(content, false);
    }

    /**
     * Writes a new record using the passed in array of values.
     * 
     * @param values
     *            Values to be written.
     * 
     * @throws IOException
     *             Thrown if an error occurs while writing data to the
     *             destination stream.
     */
    public void writeRecord(String[] values) throws IOException {
        writeRecord(values, false);
    }

    /**
     * Writes a new record using the passed in array of values.
     * 
     * @param values
     *            Values to be written.
     * 
     * @param preserveSpaces
     *            Whether to preserver leading and trailing spaces in columns
     *            while writing out to the record or not.
     * 
     * @throws IOException
     *             Thrown if an error occurs while writing data to the
     *             destination stream.
     */
    public void writeRecord(String[] values, boolean preserveSpaces)
            throws IOException {
        if (values != null && values.length > 0) {
            for (int i = 0; i < values.length; i++) {
                write(values[i], preserveSpaces);
            }

            endRecord();
        }
    }

    /**
     * 
     */
    private void initTextQualifier() {
        singleQualifier = "";
        singleQualifier += textQualifier;
        doubleQualifier = singleQualifier + singleQualifier;
    }

    /**
     * Ends the current record by sending the record delimiter.
     * 
     * @exception IOException
     *                Thrown if an error occurs while writing data to the
     *                destination stream.
     */
    public void endRecord() throws IOException {
        checkClosed();

        checkInit();

        if (useCustomRecordDelimiter) {
            outputStream.write(recordDelimiter);
        } else {
            outputStream.println();
        }

        firstColumn = true;
    }

    /**
     * 
     */
    private void checkInit() throws IOException {
        if (!initialized) {
            if (fileName != null) {
                outputStream = new PrintWriter(new OutputStreamWriter(
                        new FileOutputStream(fileName), charset));
            }

            initialized = true;
        }
    }

    /**
     * Clears all buffers for the current writer and causes any buffered data to
     * be written to the underlying device.
     */
    public void flush() {
        outputStream.flush();
    }

    /**
     * Closes and releases all related resources.
     */
    public void close() {
        if (!closed) {
            close(true);

            closed = true;
        }
    }

    /**
     * 
     */
    private void close(boolean closing) {
        if (!closed) {
            if (closing) {
                charset = null;
            }

            try {
                if (initialized) {
                    outputStream.close();
                }
            } catch (Exception e) {
                // just eat the exception
            }

            outputStream = null;

            closed = true;
        }
    }

    /**
     * 
     */
    private void checkClosed() throws IOException {
        if (closed) {
            throw new IOException();
        }
    }

    /**
     * 
     */
    protected void finalize() {
        close(false);
    }
}