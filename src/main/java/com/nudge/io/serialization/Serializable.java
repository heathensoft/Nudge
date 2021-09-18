package com.nudge.io.serialization;

public abstract class Serializable {


    public final byte[] getBytes() {

        byte[] data = new byte[size()];
        byteify(data,new int[1]);
        return data;

    }

    public final Serializable recreate(byte[] data) {

        return recreate(data, new int[1]);

    }

    public final int size() {

        return headerSize() + payloadSize();

    }

    protected abstract void byteify(byte[] dest, int[] pointer);

    protected abstract Serializable recreate(byte[] data, int[] pointer);

    public abstract int headerSize();

    public abstract int payloadSize();
}
