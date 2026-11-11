package com.csis231.springpostgrescrud.service;

import com.csis231.springpostgrescrud.dto.AuthResponseDto;
import com.csis231.springpostgrescrud.dto.ClientDto;
import com.csis231.springpostgrescrud.dto.LoginDto;

import java.util.List;

/**
 * Service interface for managing {@link com.csis231.springpostgrescrud.entity.Client} entities.
 * Provides CRUD operations and authentication‑related lookups.
 */
public interface ClientService {

    /**
     * Creates a new client after validating and hashing the password.
     *
     * @param clientDto DTO containing the client data (username, email, password)
     * @return the saved client as a DTO (password excluded)
     * @throws com.csis231.springpostgrescrud.exception.BadRequestException if username/email already exist or validation fails
     */
    AuthResponseDto createClient(ClientDto clientDto);

    /**
     * Retrieves a client by its unique ID.
     *
     * @param id the client ID
     * @return the client DTO
     * @throws com.csis231.springpostgrescrud.exception.ResourceNotFoundException if no client exists with the given ID
     */
    ClientDto getClientById(Long id);

    /**
     * Retrieves all clients.
     *
     * @return list of all client DTOs
     */
    List<ClientDto> getAllClients();

    /**
     * Updates an existing client. Only updatable fields: username, email, password.
     *
     * @param id         the ID of the client to update
     * @param clientDto  DTO containing updated values
     * @return the updated client DTO
     * @throws com.csis231.springpostgrescrud.exception.ResourceNotFoundException if client not found
     * @throws com.csis231.springpostgrescrud.exception.BadRequestException        if the new username/email conflicts with another client
     */
    ClientDto updateClient(Long id, ClientDto clientDto);

    /**
     * Deletes a client by ID. Also removes the client from all joined communities
     * (many‑to‑many side) before deletion to preserve referential integrity.
     *
     * @param id the client ID
     * @throws com.csis231.springpostgrescrud.exception.ResourceNotFoundException if client not found
     */
    void deleteClient(Long id);

    /**
     * Authenticates a client using username/email and password.
     *
     * @param loginDto the client's username or email and password
     * @return the authenticated client DTO
     * @throws com.csis231.springpostgrescrud.exception.BadRequestException if credentials are invalid
     */
    AuthResponseDto authenticate(LoginDto loginDto);

    /**
     * Finds a client by exact username (case‑insensitive).
     *
     * @param username the username
     * @return the client DTO, or {@code null} if not found
     */
    ClientDto findByUsername(String username);

    /**
     * Finds a client by exact email (case‑insensitive).
     *
     * @param email the email address
     * @return the client DTO, or {@code null} if not found
     */
    ClientDto findByEmail(String email);
}