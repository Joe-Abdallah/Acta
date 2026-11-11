package com.csis231.springpostgrescrud.service;

import com.csis231.springpostgrescrud.dto.AuthResponseDto;
import com.csis231.springpostgrescrud.dto.ClientDto;
import com.csis231.springpostgrescrud.dto.LoginDto;
import com.csis231.springpostgrescrud.entity.Client;
import com.csis231.springpostgrescrud.entity.Community;
import com.csis231.springpostgrescrud.exception.BadRequestException;
import com.csis231.springpostgrescrud.exception.ResourceNotFoundException;
import com.csis231.springpostgrescrud.mapper.ClientMapper;
import com.csis231.springpostgrescrud.repository.ClientRepository;
import com.csis231.springpostgrescrud.repository.CommunityRepository;
import com.csis231.springpostgrescrud.security.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link ClientService} that handles all client‑related business logic.
 */
@Service
@AllArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final CommunityRepository communityRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // -------------------- CREATE --------------------
    @Override
    public AuthResponseDto createClient(ClientDto clientDto) {
        validateClientDto(clientDto, true);

        String username = clientDto.getUsername().trim();
        String email = clientDto.getEmail().trim();

        if (clientRepository.existsByUsernameIgnoreCase(username)) {
            throw new BadRequestException("Username already taken: " + username);
        }
        if (clientRepository.existsByEmailIgnoreCase(email)) {
            throw new BadRequestException("Email already registered: " + email);
        }

        Client client = ClientMapper.toEntity(clientDto);
        client.setUsername(username);
        client.setEmail(email);
        client.setPassword(passwordEncoder.encode(clientDto.getPassword()));

        Client saved = clientRepository.save(client);

        String token = jwtService.generateToken(saved.getUsername());
        return new AuthResponseDto(token, ClientMapper.toDto(saved));
    }

    // -------------------- READ --------------------
    @Override
    public ClientDto getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        return ClientMapper.toDto(client);
    }

    @Override
    public List<ClientDto> getAllClients() {
        return clientRepository.findAll().stream()
                .map(ClientMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AuthResponseDto authenticate(LoginDto loginDto) {
        if (loginDto == null) {
            throw new BadRequestException("Username and password are required.");
        }
        String raw = loginDto.getUsername();
        String identifier = (raw == null || raw.trim().isEmpty()) ? null : raw.trim();
        String password = loginDto.getPassword();

        if (identifier == null || password == null || password.isBlank()) {
            throw new BadRequestException("Username and password are required.");
        }

        Client client = clientRepository.findByUsernameIgnoreCase(identifier)
                .or(() -> clientRepository.findByEmailIgnoreCase(identifier))
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        if (!passwordEncoder.matches(password, client.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        String token = jwtService.generateToken(client.getUsername());
        return new AuthResponseDto(token, ClientMapper.toDto(client));
    }

    @Override
    public ClientDto findByUsername(String username) {
        if (username == null) return null;
        return clientRepository.findByUsernameIgnoreCase(username.trim())
                .map(ClientMapper::toDto)
                .orElse(null);
    }

    @Override
    public ClientDto findByEmail(String email) {
        if (email == null) return null;
        return clientRepository.findByEmailIgnoreCase(email.trim())
                .map(ClientMapper::toDto)
                .orElse(null);
    }

    // -------------------- UPDATE --------------------
    @Override
    @Transactional
    public ClientDto updateClient(Long id, ClientDto clientDto) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

        validateClientDto(clientDto, false);

        String newUsername = clientDto.getUsername().trim();
        String newEmail = clientDto.getEmail().trim();

        if (!client.getUsername().equalsIgnoreCase(newUsername) &&
                clientRepository.existsByUsernameIgnoreCase(newUsername)) {
            throw new BadRequestException("Username already taken: " + newUsername);
        }
        if (!client.getEmail().equalsIgnoreCase(newEmail) &&
                clientRepository.existsByEmailIgnoreCase(newEmail)) {
            throw new BadRequestException("Email already registered: " + newEmail);
        }

        client.setUsername(newUsername);
        client.setEmail(newEmail);

        String newPassword = clientDto.getPassword();
        if (newPassword != null && !newPassword.isBlank()) {
            client.setPassword(passwordEncoder.encode(newPassword));
        }

        Client updated = clientRepository.save(client);
        return ClientMapper.toDto(updated);
    }

    // -------------------- DELETE --------------------
    @Override
    @Transactional
    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

        // Remove this client from all communities they have joined (many‑to‑many)
        for (Community community : client.getJoinedCommunities()) {
            community.getMembers().remove(client);
            communityRepository.save(community);
        }
        client.getJoinedCommunities().clear();

        clientRepository.delete(client);
    }

    // -------------------- HELPER METHODS --------------------
    private void validateClientDto(ClientDto dto, boolean isCreate) {
        if (dto == null) {
            throw new BadRequestException("Client DTO cannot be null");
        }
        if (dto.getUsername() == null || dto.getUsername().isBlank()) {
            throw new BadRequestException("Username is required");
        }
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new BadRequestException("Email is required");
        }
        if (isCreate && (dto.getPassword() == null || dto.getPassword().isBlank())) {
            throw new BadRequestException("Password is required for creation");
        }
        if (dto.getPassword() != null && dto.getPassword().length() < 6) {
            throw new BadRequestException("Password must be at least 6 characters long");
        }
    }
}