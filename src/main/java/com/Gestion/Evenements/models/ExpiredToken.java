package com.Gestion.Evenements.models;

import com.Gestion.Evenements.models.enums.TokenType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "expired_tokens")
public class ExpiredToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenType tokenType;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Date expirationDate;

    @Column(nullable = false)
    private Date addedAt;

    // Constructors
    public ExpiredToken() {}

    public ExpiredToken(String token, TokenType tokenType, String email, Date expirationDate) {
        this.token = token;
        this.tokenType = tokenType;
        this.email = email;
        this.expirationDate = expirationDate;
        this.addedAt = new Date();
    }

}