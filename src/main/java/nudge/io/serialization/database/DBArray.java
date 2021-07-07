package nudge.io.serialization.database;

import nudge.io.serialization.JsonBuilder;
import nudge.io.serialization.exceptions.DataRecreationException;

import static nudge.io.serialization.Serializer.*;
import static nudge.io.serialization.database.DBEntry.JavaPrimitive.*;

/**
 * @author Frederik Dahl
 * XX/XX/2021
 */

public class DBArray extends DBEntry {

    private static final byte CONTAINER_TYPE = DBEntry.ARRAY;
    private byte primitiveType;
    private int count;

    private boolean[] boolData;
    private byte[] byteData;
    private short[] shortData;
    private char[] charData;
    private int[] intData;
    private long[] longData;
    private float[] floatData;
    private double[] doubleData;

    protected DBArray() {}

    public DBArray(String name, boolean[] data) {
        incHeaderSize(6);
        setName(name);
        primitiveType = JavaPrimitive.BOOLEAN;
        count = data.length;
        boolData = data;
        incDataSize((int)(Math.ceil(count/8f)* JavaPrimitive.size(primitiveType)));
    }

    public DBArray(String name, byte[] data) {
        incHeaderSize(6);
        setName(name);
        primitiveType = JavaPrimitive.BYTE;
        count = data.length;
        byteData = data;
        incDataSize(count * JavaPrimitive.size(primitiveType));
    }

    public DBArray(String name, short[] data) {
        incHeaderSize(6);
        setName(name);
        primitiveType = JavaPrimitive.SHORT;
        count = data.length;
        shortData = data;
        incDataSize(count * JavaPrimitive.size(primitiveType));
    }

    public DBArray(String name, char[] data) {
        incHeaderSize(6);
        setName(name);
        primitiveType = JavaPrimitive.CHAR;
        count = data.length;
        charData = data;
        incDataSize(count * JavaPrimitive.size(primitiveType));
    }

    public DBArray(String name, int[] data) {
        incHeaderSize(6);
        setName(name);
        primitiveType = JavaPrimitive.INTEGER;
        count = data.length;
        intData = data;
        incDataSize(count * JavaPrimitive.size(primitiveType));
    }

    public DBArray(String name, long[] data) {
        incHeaderSize(6);
        setName(name);
        primitiveType = JavaPrimitive.LONG;
        count = data.length;
        longData = data;
        incDataSize(count * JavaPrimitive.size(primitiveType));
    }

    public DBArray(String name, float[] data) {
        incHeaderSize(6);
        setName(name);
        primitiveType = JavaPrimitive.FLOAT;
        count = data.length;
        floatData = data;
        incDataSize(count * JavaPrimitive.size(primitiveType));
    }

    public DBArray(String name, double[] data) {
        incHeaderSize(6);
        setName(name);
        primitiveType = JavaPrimitive.DOUBLE;
        count = data.length;
        doubleData = data;
        incDataSize(count * JavaPrimitive.size(primitiveType));
    }

    public boolean[] boolData() {
        return boolData;
    }

    public byte[] byteData() {
        return byteData;
    }

    public short[] shortData() {
        return shortData;
    }

    public char[] charData() {
        return charData;
    }

    public int[] intData() {
        return intData;
    }

    public long[] longData() {
        return longData;
    }

    public float[] floatData() {
        return floatData;
    }

    public double[] doubleData() {
        return doubleData;
    }

    @Override
    protected void byteify(byte[] dest, int[] pointer) {

        writeBytes(dest,pointer,CONTAINER_TYPE);
        writeBytes(dest,pointer,nameSize);
        writeBytes(dest,pointer, nameData);
        writeBytes(dest,pointer,count);
        writeBytes(dest,pointer, primitiveType);

        switch (primitiveType) {
            case BOOLEAN:   writeBytes(dest,pointer,boolData);   break;
            case BYTE:      writeBytes(dest,pointer,byteData);   break;
            case SHORT:     writeBytes(dest,pointer,shortData);  break;
            case CHAR:      writeBytes(dest,pointer,charData);   break;
            case INTEGER:   writeBytes(dest,pointer,intData);    break;
            case LONG:      writeBytes(dest,pointer,longData);   break;
            case FLOAT:     writeBytes(dest,pointer,floatData);  break;
            case DOUBLE:    writeBytes(dest,pointer,doubleData); break;
        }
    }

    @Override
    protected DBArray recreate(byte[] data, int[] pointer) throws DataRecreationException {

        if (readByte(data,pointer) != CONTAINER_TYPE)
            throw new DataRecreationException("UnMatching Serializable types");

        nameSize = readByte(data,pointer);
        nameData = new byte[nameSize];
        incHeaderSize(nameSize + 7);
        readByteArray(data,pointer, nameData);
        count = readInt(data,pointer);
        primitiveType = readByte(data,pointer);

        switch (primitiveType) {
            case BOOLEAN:
                boolData = new boolean[count];
                readBoolArray(data,pointer,boolData);
                incDataSize((int)(Math.ceil(count/8f)* JavaPrimitive.size(primitiveType)));
                break;
            case BYTE:
                byteData = new byte[count];
                readByteArray(data,pointer,byteData);
                incDataSize(count * JavaPrimitive.size(primitiveType));
                break;
            case SHORT:
                shortData = new short[count];
                readShortArray(data,pointer,shortData);
                incDataSize(count * JavaPrimitive.size(primitiveType));
                break;
            case CHAR:
                charData = new char[count];
                readCharArray(data,pointer,charData);
                incDataSize(count * JavaPrimitive.size(primitiveType));
                break;
            case INTEGER:
                intData = new int[count];
                readIntArray(data,pointer,intData);
                incDataSize(count * JavaPrimitive.size(primitiveType));
                break;
            case LONG:
                longData = new long[count];
                readLongArray(data,pointer,longData);
                incDataSize(count * JavaPrimitive.size(primitiveType));
                break;
            case FLOAT:
                floatData = new float[count];
                readFloatArray(data,pointer,floatData);
                incDataSize(count * JavaPrimitive.size(primitiveType));
                break;
            case DOUBLE:
                doubleData = new double[count];
                readDoubleArray(data,pointer,doubleData);
                incDataSize(count * JavaPrimitive.size(primitiveType));
                break;
            default: throw new DataRecreationException("Unknown primitive");
        }

        return this;
    }

    @Override
    protected void jsonFormat(JsonBuilder builder, int tabCount, boolean newLine, boolean comma) {

        switch (primitiveType) {

            case BOOLEAN: builder.addArray(name(),boolData,tabCount,newLine,comma);    break;
            case BYTE:    builder.addArray(name(),byteData,tabCount,newLine,comma);    break;
            case SHORT:   builder.addArray(name(),shortData,tabCount,newLine,comma);   break;
            case CHAR:    builder.addArray(name(),charData,tabCount,newLine,comma);    break;
            case INTEGER: builder.addArray(name(),intData,tabCount,newLine,comma);     break;
            case LONG:    builder.addArray(name(),longData,tabCount,newLine,comma);    break;
            case FLOAT:   builder.addArray(name(),floatData,tabCount,newLine,comma);   break;
            case DOUBLE:  builder.addArray(name(),doubleData,tabCount,newLine,comma);  break;
            default:                                                                   break;
        }
    }
}
