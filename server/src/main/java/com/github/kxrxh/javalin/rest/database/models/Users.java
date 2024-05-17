package com.github.kxrxh.javalin.rest.database.models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("users")
public class Users extends Model {

    public void setName(String name) {
        setString("username", name);
    }

    public String getName() {
        return getString("username");
    }

    public void setPassword(String password) {
        setString("password", password);
    }

    public String getPassword() {
        return getString("password");
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", password='" + getPassword() + '\'' +
                '}';
    }
}
