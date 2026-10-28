package com.example.wayuunext_app.model;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "\"user\"")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String email;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer progress = 0;

    // USER o ADMIN; solo se cambia desde la base de datos
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'USER'")
    private String role = "USER";

    public Usuario() {}

    public Usuario(Long id) {
        this.id = id;
    }

    public Usuario(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
