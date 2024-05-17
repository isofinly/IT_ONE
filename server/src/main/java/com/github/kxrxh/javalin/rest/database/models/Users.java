package com.github.kxrxh.javalin.rest.database.models;

import io.ebean.Model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Users extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Column(name = "username", unique = true)
    public String username;

    @Column(name = "password")
    public String password;

    @Override
    public String toString() {
        return "Users{" +
                "id=" + getId() +
                ", name='" + getUsername() + '\'' +
                ", password='" + getPassword() + '\'' +
                '}';
    }
}
