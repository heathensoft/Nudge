package nudge.io.serialization.database;

import nudge.io.serialization.JsonBuilder;
import nudge.io.serialization.exceptions.DataRecreationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nudge.io.serialization.Serializer.*;

/**
 * @author Frederik Dahl
 * XX/XX/2021
 */

public class DBObject extends DBEntry {

    private static final byte CONTAINER_TYPE = DBEntry.OBJECT;

    private byte  fieldCount  = 0;
    private byte  stringCount = 0;
    private byte  arrayCount  = 0;
    private short objectCount = 0;

    private final List<DBField>   fields   = new ArrayList<>();
    private final List<DBString>  strings  = new ArrayList<>();
    private final List<DBArray>   arrays   = new ArrayList<>();
    private final List<DBObject>  objects  = new ArrayList<>();

    private Map<String, DBObject> objectMap;
    private boolean usingMap;

    protected DBObject() {}

    public DBObject(String name) {
        incHeaderSize(10);
        setName(name);
    }

    public <T extends DBEntry>void add(T entry) {

        if (entry instanceof DBField) {
            fields.add((DBField) entry);
            incDataSize(entry.size());
            fieldCount++;
        }
        else if (entry instanceof DBString) {
            strings.add((DBString) entry);
            incDataSize(entry.size());
            stringCount++;
        }
        else if (entry instanceof DBArray) {
            arrays.add((DBArray) entry);
            incDataSize(entry.size());
            arrayCount++;
        }
        else if (entry instanceof DBObject) {
            objects.add((DBObject) entry);
            incDataSize(entry.size());
            objectCount++;
        }
    }

    public DBObject findObject(String key) {
        if (usingMap) {
            return objectMap.get(key);
        }
        for (DBObject object : objects) {
            if (object.name().equals(key))
                return object;
        }
        return null;
    }

    public DBArray findArray(String key) {
        for (DBArray array : arrays) {
            if (array.name().equals(key))
                return array;
        }
        return null;
    }

    public DBString findString(String key) {
        for (DBString string : strings) {
            if (string.name().equals(key))
                return string;
        }
        return null;
    }

    public DBField findField(String key) {
        for (DBField field : fields) {
            if (field.name().equals(key))
                return field;
        }
        return null;
    }

    public byte arrayCount() {
        return arrayCount;
    }

    public byte fieldCount() {
        return fieldCount;
    }

    public byte stringCount() {
        return stringCount;
    }

    public short objectCount() {
        return objectCount;
    }

    @Override
    protected void byteify(byte[] dest, int[] pointer) {

        writeBytes(dest,pointer,CONTAINER_TYPE);
        writeBytes(dest,pointer,size());
        writeBytes(dest,pointer,nameSize);
        writeBytes(dest,pointer,nameData);

        writeBytes(dest,pointer,fieldCount);
        for (DBField field : fields)
            field.byteify(dest,pointer);

        writeBytes(dest,pointer,stringCount);
        for (DBString string : strings)
            string.byteify(dest,pointer);

        writeBytes(dest,pointer,arrayCount);
        for (DBArray array : arrays)
            array.byteify(dest,pointer);

        writeBytes(dest,pointer,objectCount);
        for (DBObject object : objects)
            object.byteify(dest,pointer);
    }

    @Override
    protected DBObject recreate(byte[] data, int[] pointer) throws DataRecreationException{

        if (readByte(data,pointer) != CONTAINER_TYPE)
            throw new DataRecreationException("UnMatching ISerializable types");

        int size = readInt(data,pointer);
        nameSize = readByte(data,pointer);
        incHeaderSize(nameSize + 11);
        incDataSize(size - headerSize());
        nameData = new byte[nameSize];
        readByteArray(data,pointer, nameData);

        fieldCount = readByte(data, pointer);

        for (int i = 0; i < fieldCount; i++) {
            DBField field = new DBField().recreate(data,pointer);
            fields.add(field);
        }

        stringCount = readByte(data, pointer);

        for (int i = 0; i < stringCount; i++) {
            DBString string = new DBString().recreate(data,pointer);
            strings.add(string);
        }

        arrayCount = readByte(data, pointer);

        for (int i = 0; i < arrayCount; i++) {
            DBArray array = new DBArray().recreate(data,pointer);
            arrays.add(array);
        }

        objectCount = readShort(data, pointer);

        usingMap = objectCount > 10;

        if (usingMap) {
            objectMap = new HashMap<>();
            for (int i = 0; i < objectCount; i++) {
                DBObject object = new DBObject().recreate(data,pointer);
                objectMap.put(object.name(),object);
            }
        }
        else {
            for (int i = 0; i < objectCount; i++) {
                DBObject object = new DBObject().recreate(data,pointer);
                objects.add(object);
            }
        }
        return this;
    }

    @Override
    protected void jsonFormat(JsonBuilder builder, int tabCount, boolean newLine, boolean comma) {

        builder.beginObject(name(),tabCount);

        int count = fieldCount + stringCount + arrayCount + objectCount;
        for (DBField field : fields)
            field.jsonFormat(builder,tabCount+1,true,--count != 0);
        for (DBString string : strings)
            string.jsonFormat(builder,tabCount+1,true,--count != 0);
        for (DBArray array : arrays)
            array.jsonFormat(builder,tabCount+1,true,--count != 0);
        if (usingMap) {
            for (DBObject object : objectMap.values())
                object.jsonFormat(builder,tabCount+1,true,--count != 0);
            return;
        }
        for (DBObject object : objects)
            object.jsonFormat(builder,tabCount+1,true,--count != 0);

        builder.endObject(tabCount,newLine,comma);
    }
}
