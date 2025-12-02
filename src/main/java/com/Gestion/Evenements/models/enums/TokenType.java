package com.Gestion.Evenements.models.enums;

public enum TokenType {
    REGISTER_TOKEN("REGISTER_TOKEN"),
    ACCESS_TOKEN("ACCESS_TOKEN"),
    RESET_PASSWORD_TOKEN("RESET_PASSWORD_TOKEN"),
    CANDIDATE_ACTIVATION_TOKEN("CANDIDATE_ACTIVATION_TOKEN") ;

    private final String value;

    TokenType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


}

