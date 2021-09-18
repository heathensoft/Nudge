package com.nudge.io.serialization.database;

import com.nudge.io.serialization.JsonBuilder;
import com.nudge.io.serialization.Serializer;
import com.nudge.io.serialization.exceptions.DataRecreationException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static com.nudge.io.serialization.Serializer.*;

/**
 * @author Frederik Dahl
 * XX/XX/2021
 */

public class DBString extends DBEntry {

    private static final byte CONTAINER_TYPE = DBEntry.STRING;
    private byte format;
    private byte[] data;

    protected DBString() {}

    public DBString(String name, String string) {
        this(name,string,StandardCharsets.UTF_8);
    }

    public DBString(String name, String string, Charset charset) {
        this(name,string.getBytes(charset),charset);
    }

    public DBString(String name, byte[] data, Charset charset) {
        setName(name);
        incHeaderSize(6);
        format = Serializer.charsetToByte(charset);
        incDataSize((this.data = data).length);
    }

    public String getString() {
        return new String(data, Serializer.byteToCharset(format));
    }

    @Override
    protected void byteify(byte[] dest, int[] pointer) {
        writeBytes(dest,pointer,CONTAINER_TYPE);
        writeBytes(dest,pointer,size());
        writeBytes(dest,pointer,nameSize);
        writeBytes(dest,pointer,nameData);
        writeBytes(dest,pointer,format);
        writeBytes(dest,pointer,data);
    }

    @Override
    protected DBString recreate(byte[] data, int[] pointer) {

        if (readByte(data,pointer) != CONTAINER_TYPE)
            throw new DataRecreationException("UnMatching ISerializable types");

        int size = readInt(data,pointer);
        nameSize = readByte(data,pointer);
        incHeaderSize(nameSize + 7);
        incDataSize(size - headerSize());
        this.data = new byte[payloadSize()];
        nameData = new byte[nameSize];
        readByteArray(data,pointer, nameData);
        format = readByte(data,pointer);
        readByteArray(data,pointer,this.data);

        return this;
    }

    @Override
    protected void jsonFormat(JsonBuilder builder, int tabCount, boolean newLine, boolean comma) {

        builder.addString(name(),getString(),tabCount,newLine,comma);
    }
}
