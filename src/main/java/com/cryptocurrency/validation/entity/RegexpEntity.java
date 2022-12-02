package com.cryptocurrency.validation.entity;

import lombok.Data;

@Data
public class RegexpEntity {
    private String password;
    private String username;
    private String email;
    private String phone;

    public RegexpEntity(String password, String username, String email, String phone) {
        this.password = password;
        this.username = username;
        this.email = email;
        this.phone = phone;
    }

    public static final class RegexpValidationBuilder {
        private String password;
        private String username;
        private String email;
        private String phone;

        public RegexpValidationBuilder() {
        }

        public static RegexpValidationBuilder anApiError() {
            return new RegexpValidationBuilder();
        }

        public RegexpValidationBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public RegexpValidationBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public RegexpValidationBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public RegexpValidationBuilder withPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public RegexpEntity build() {
            return new RegexpEntity(password, username, email, phone);
        }
    }
}
