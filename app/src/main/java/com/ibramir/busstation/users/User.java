package com.ibramir.busstation.users;

import javax.annotation.Nullable;

public abstract class User {
    private String uid;
    private String email;
    private String name;

    private static User currentUser = null;
    public static User getCurrentUser() {
        return currentUser;
    }

    User(String uid) {
        this(uid, null);
    }
    User(String uid, String email) {
        this.uid = uid;
        this.email = email;
    }

    public static void login(String uid) {
        //TODO login
    }

    public String getUid() {
        return uid;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public void setName(String name) {
        this.name = name;
    }

    public abstract boolean saveInfo();

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == this)
            return true;
        if(!(obj instanceof User))
            return false;
        return this.uid.equals(((User)obj).uid);
    }
}
