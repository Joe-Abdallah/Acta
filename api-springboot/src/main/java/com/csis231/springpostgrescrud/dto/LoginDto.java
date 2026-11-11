package com.csis231.springpostgrescrud.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * Data Transfer Object for User login
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
    /** The user's name. */
    private String username;
    /** The user's password. */
    private String password;
}
