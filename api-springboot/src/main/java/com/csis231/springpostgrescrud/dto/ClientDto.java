package com.csis231.springpostgrescrud.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

/**
 * Data Transfer Object for Client.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {

    /** The client's unique identifier. */
    private Long id;

    /** The client's public display name. */
    private String username;

    /** The timestamp when the client account was created. */
    private Instant createdAt;

    /** The client's email */
    private String email;

    /** The client's password */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}