package com.ibramir.busstation.users;

public class Manager extends User {

    public Manager(String uid) {
        this(uid, null);
    }

    public Manager(String uid, String email) {
        super(uid, email);
    }

}
