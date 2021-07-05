package nudge.io.serialization.database;

import nudge.io.serialization.JsonBuilder;
import nudge.io.serialization.exceptions.DataRecreationException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nudge.io.serialization.Serializer.*;

/**
 * @author Frederik Dahl
 * XX/XX/2021
 */

public class Database extends DBEntry {

    // todo: Fix old Exception handling

    public static final String SYSTEM = "NDB"; // Nudge Database
    public static final byte[] HEADER = SYSTEM.getBytes(StandardCharsets.UTF_8);
    public static final short VERSION = 0x0100;
    private static final byte CONTAINER_TYPE = DBEntry.DATABASE;

    private short objectCount = 0;

    private final List<DBObject> objects  = new ArrayList<>();
    private Map<String, DBObject> objectMap;
    private boolean usingMap;

    protected Database() {}

    public Database(String name) {
        incHeaderSize(HEADER.length + 9);
        setName(name);
    }

    public void add(DBObject object) {
        objects.add(object);
        incDataSize(object.size());
        objectCount++;
    }

    public DBObject findObject(String key) {
        if (usingMap) return objectMap.get(key);
        for (DBObject object : objects) {
            if (object.name().equals(key))
                return object;
        }
        return null;
    }

    public short objectCount() {
        return objectCount;
    }

    public void serializeToFile(String path) throws IOException{
        FileOutputStream stream = new FileOutputStream(path);
        stream.write(getBytes());
        stream.close();
    }

    public static Database deserializeFromFile(String path) throws IOException{
        FileInputStream stream = new FileInputStream(path);
        byte[] data = new byte[stream.available()];
        int unused = stream.read(data);
        stream.close();
        return (Database) new Database().recreate(data);
    }

    @Override
    protected void byteify(byte[] dest, int[] pointer) {
        writeBytes(dest, pointer, HEADER);
        writeBytes(dest, pointer, VERSION);
        writeBytes(dest, pointer, CONTAINER_TYPE);
        writeBytes(dest,pointer,size());
        writeBytes(dest,pointer,nameSize);
        writeBytes(dest,pointer,nameData);
        writeBytes(dest, pointer, objectCount);
        for (DBObject object : objects)
            object.byteify(dest,pointer);
    }

    @Override
    protected Database recreate(byte[] data, int[] pointer) throws DataRecreationException {

        if (!readString(data,pointer, HEADER.length, StandardCharsets.UTF_8).equals(SYSTEM))
            throw new DataRecreationException("Unknown Database");

        if (readShort(data, pointer) != VERSION)
            throw new DataRecreationException("UnMatching Database Version");

        if (readByte(data,pointer) != CONTAINER_TYPE)
            throw new DataRecreationException("UnMatching ISerializable types");

        int size = readInt(data,pointer);
        nameSize = readByte(data,pointer);
        incHeaderSize(nameSize + 10 + HEADER.length);
        incDataSize(size - headerSize());
        nameData = new byte[nameSize];
        readByteArray(data,pointer, nameData);

        objectCount = readShort(data, pointer);

        usingMap = objectCount > 10;

        if (usingMap) {
            objectMap = new HashMap<>();
            for (int i = 0; i < objectCount; i++) {
                DBObject object = new DBObject().recreate(data,pointer);
                objectMap.put(object.name(),object);
            }
        }
        else for (int i = 0; i < objectCount; i++)
                objects.add(new DBObject().recreate(data,pointer));

        return this;
    }

    @Override
    protected void jsonFormat(JsonBuilder builder, int tabCount, boolean newLine, boolean comma) {

        int count = objectCount;

        if (usingMap) {
            for (DBObject object : objectMap.values())
                object.jsonFormat(builder,tabCount,true, --count != 0);
            return;
        }
        for (DBObject object : objects)
            object.jsonFormat(builder,tabCount,true, --count != 0);
    }

    @Override
    public String toString() {
        JsonBuilder builder = new JsonBuilder();
        builder.begin();
        jsonFormat(builder,1, false,false);
        builder.end();
        return builder.toString();
    }
}
