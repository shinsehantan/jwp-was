package model;

import java.util.Collection;

public class Users {
    private final Collection<User> users;

    public Users(Collection<User> users) {
        this.users = users;
    }

    public Collection<User> getUsers() {
        return users;
    }
}
