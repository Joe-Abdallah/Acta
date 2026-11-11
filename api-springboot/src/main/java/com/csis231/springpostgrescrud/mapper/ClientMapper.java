package com.csis231.springpostgrescrud.mapper;

import com.csis231.springpostgrescrud.dto.ClientDto;
import com.csis231.springpostgrescrud.entity.Client;

/**
 * Utility class for converting between Client entities and ClientDto objects.
 *
 * All methods are static — this class is never instantiated.
 * It has no dependencies and performs pure data mapping only.
 */
public class ClientMapper {

    /**
     * Converts a Client entity to a ClientDto for use in API responses.
     * @param client the entity to convert.
     * @return a populated ClientDto.
     */
    public static ClientDto toDto(Client client) {
        if (client == null) return null;
        return new ClientDto(
                client.getId(),
                client.getUsername(),
                client.getCreatedAt(),
                client.getEmail(),
                null
        );
    }

    /**
     * Converts a ClientDto to a partial CLient entity.
     * @param dto the DTO to convert
     * @return a partially constructed Client
     */
    public static Client toEntity(ClientDto dto) {
        if (dto == null) return null;
        Client client = new Client();
        client.setUsername(dto.getUsername());
        client.setEmail(dto.getEmail());
        client.setPassword(dto.getPassword());
        return client;
    }
}