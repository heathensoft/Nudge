package com.nudge.io.serialization.database;

import com.nudge.io.serialization.JsonBuilder;
import com.nudge.io.serialization.exceptions.DataRecreationException;

import static com.nudge.io.serialization.Serializer.*;
import static com.nudge.io.serialization.database.DBEntry.JavaPrimitive.*;

/**
 * @author Frederik Dahl
 * XX/XX/2021
 */

public class DBField extends DBEntry {

    private static final byte CONTAINER_TYPE = DBEntry.FIELD;
    private byte primitiveType;
    private byte[] data;

    protected DBField() {}

    public DBField(String name, boolean value) {
        incHeaderSize(2);
        primitiveType = JavaPrimitive.BOOLEAN;
        setName(name);
        incDataSize(JavaPrimitive.size(primitiveType));
        data = new byte[payloadSize()];
        writeBytes(data, value);
    }

    public DBField(String name, byte value) {
        incHeaderSize(2);
        primitiveType = JavaPrimitive.BYTE;
        setName(name);
        incDataSize(JavaPrimitive.size(primitiveType));
        data = new byte[payloadSize()];
        writeBytes(data, value);
    }

    public DBField(String name, short value) {
        incHeaderSize(2);
        primitiveType = JavaPrimitive.SHORT;
        setName(name);
        incDataSize(JavaPrimitive.size(primitiveType));
        data = new byte[payloadSize()];
        writeBytes(data, value);
    }

    public DBField(String name, char value) {
        incHeaderSize(2);
        primitiveType = JavaPrimitive.CHAR;
        setName(name);
        incDataSize(JavaPrimitive.size(primitiveType));
        data = new byte[payloadSize()];
        writeBytes(data, value);
    }

    public DBField(String name, int value) {
        incHeaderSize(2);
        primitiveType = JavaPrimitive.INTEGER;
        setName(name);
        incDataSize(JavaPrimitive.size(primitiveType));
        data = new byte[payloadSize()];
        writeBytes(data, value);
    }

    public DBField(String name, long value) {
        incHeaderSize(2);
        primitiveType = JavaPrimitive.LONG;
        setName(name);
        incDataSize(JavaPrimitive.size(primitiveType));
        data = new byte[payloadSize()];
        writeBytes(data, value);
    }

    public DBField(String name, float value) {
        incHeaderSize(2);
        primitiveType = JavaPrimitive.FLOAT;
        setName(name);
        incDataSize(JavaPrimitive.size(primitiveType));
        data = new byte[payloadSize()];
        writeBytes(data, value);
    }

    public DBField(String name, double value) {
        incHeaderSize(2);
        primitiveType = JavaPrimitive.DOUBLE;
        setName(name);
        incDataSize(JavaPrimitive.size(primitiveType));
        data = new byte[payloadSize()];
        writeBytes(data, value);
    }

    public boolean getBool() { return readBoolean(data); }

    public byte getByte() { return data[0]; }

    public short getShort() { return readShort(data); }

    public char getChar() { return readChar(data); }

    public int getInt() { return readInt(data); }

    public long getLong() { return readLong(data); }

    public double getDouble() { return readDouble(data); }

    public float getFloat() { return readFloat(data); }

    @Override
    protected void byteify(byte[] dest, int[] pointer) {
        writeBytes(dest,pointer,CONTAINER_TYPE);
        writeBytes(dest,pointer,nameSize);
        writeBytes(dest,pointer,nameData);
        writeBytes(dest,pointer,primitiveType);
        writeBytes(dest,pointer,data);
    }

    @Override
    protected DBField recreate(byte[] data, int[] pointer) throws DataRecreationException {

        if (readByte(data,pointer) != CONTAINER_TYPE)
            throw new DataRecreationException("UnMatching ISerializable types");

        nameSize = readByte(data,pointer);
        incHeaderSize(nameSize + 3);
        nameData = new byte[nameSize];
        readByteArray(data,pointer, nameData);
        primitiveType = readByte(data,pointer);
        incDataSize(JavaPrimitive.size(primitiveType));
        this.data = new byte[payloadSize()];
        readByteArray(data,pointer, this.data);

        return this;
    }

    @Override
    protected void jsonFormat(JsonBuilder builder, int tabCount, boolean newLine, boolean comma) {

        switch (primitiveType) {

            case BOOLEAN: builder.addVar(name(),getBool(),tabCount,newLine,comma);     break;
            case BYTE:    builder.addVar(name(),getByte(),tabCount,newLine,comma);     break;
            case SHORT:   builder.addVar(name(),getShort(),tabCount,newLine,comma);    break;
            case CHAR:    builder.addVar(name(),getChar(),tabCount,newLine,comma);     break;
            case INTEGER: builder.addVar(name(),getInt(),tabCount,newLine,comma);      break;
            case LONG:    builder.addVar(name(),getLong(),tabCount,newLine,comma);     break;
            case FLOAT:   builder.addVar(name(),getFloat(),tabCount,newLine,comma);    break;
            case DOUBLE:  builder.addVar(name(),getDouble(),tabCount,newLine,comma);   break;
            default:                                                                   break;
        }

    }
}
