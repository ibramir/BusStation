package com.ibramir.busstation.users;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
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

    public enum Type {
        CUSTOMER,
        DRIVER,
        MANAGER
    }
    public static User fromDocument(DocumentSnapshot d) {
        if(d == null || !d.exists())
            return null;
        String uid = d.getString("name"), email = d.getString("email"), name = d.getString("name");
        switch(Type.valueOf(d.getString("type"))) {
            case CUSTOMER:
                return new Customer(uid, email, name);
            case DRIVER:
                return new Driver(uid, email, name);
        }
        return null;
    }
    User(String uid) {
        this(uid, null);
    }
    User(String uid, String email) {
        this.uid = uid;
        this.email = email;
    }
    public User(String uid, String email, String name) {
        this.uid = uid;
        this.email = email;
        this.name = name;
    }

    private static void register(FirebaseUser user, Type type, LoginListener listener) {
        switch (type) {
            case CUSTOMER:
                currentUser = new Customer(user.getUid(), user.getEmail());
                break;
            case DRIVER:
                currentUser = new Driver(user.getUid(), user.getEmail());
                break;
            case MANAGER:
                currentUser = new Manager(user.getUid(), user.getEmail());
                break;
        }
        currentUser.setName(user.getDisplayName());
        UserManager.getInstance().save(currentUser);
        if(listener != null)
            listener.onLogin();
    }
    public static void login(String uid, final Type type, final LoginListener listener) {
        UserManager.getInstance().retrieve(uid, new RetrieveListener<User>() {
            @Override
            public void onRetrieve(User obj) {
                if(obj == null)
                    register(FirebaseAuth.getInstance().getCurrentUser(),type, listener);
                else {
                    currentUser = obj;
                    if(listener != null)
                        listener.onLogin();
                }
            }
        });
    }
    public static void login(String uid, final Type type) {
        login(uid, type, null);
    }
    public static void logout(){
        currentUser = null;
    }
    public static boolean isLoggedIn() {
        return currentUser == null;
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
