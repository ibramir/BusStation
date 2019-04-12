package com.ibramir.busstation.users;

public class Manager extends User {

    Manager(String uid) {
        this(uid, null);
    }

    Manager(String uid, String email) {
        super(uid, email);
    }

}
