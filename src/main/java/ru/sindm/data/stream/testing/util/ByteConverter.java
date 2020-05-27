package ru.sindm.data.stream.testing.util;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ByteConverter {

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectStream = new ObjectOutputStream(outputStream);
        objectStream.writeObject(obj);
        return outputStream.toByteArray();
    }

    public static Object deserialize(byte[] data, String className) throws IOException, ClassNotFoundException {
        Class clazz = Class.forName(className);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        ObjectInputStream objectStream = new ObjectInputStream(inputStream);
        return clazz.cast(objectStream.readObject());
    }
}
