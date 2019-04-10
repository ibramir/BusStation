package com.ibramir.busstation;

public interface FirestoreActions<T> {
    void delete(T obj);
    void save(T obj);
}
