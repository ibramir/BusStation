package com.ibramir.busstation.users;

import com.ibramir.busstation.RetrieveListener;

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
        UserManager.getInstance().retrieve(uid, new RetrieveListener<User>() {
            @Override
            public void onRetrieve(User obj) {
                currentUser = obj;
            }
        });
    }
    public static void logout(){
        currentUser = null;
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


    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == this)
            return true;
        if(!(obj instanceof User))
            return false;
        return this.uid.equals(((User)obj).uid);
    }
}
