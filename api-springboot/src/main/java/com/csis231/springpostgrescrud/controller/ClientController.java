package com.csis231.springpostgrescrud.controller;

import com.csis231.springpostgrescrud.dto.AuthResponseDto;
import com.csis231.springpostgrescrud.dto.ClientDto;
import com.csis231.springpostgrescrud.dto.LoginDto;
import com.csis231.springpostgrescrud.service.ClientService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing {@link com.csis231.springpostgrescrud.entity.Client} entities.
 */
@RestController
@RequestMapping("/api/clients")
@AllArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<AuthResponseDto> createClient(@RequestBody ClientDto clientDto) {
        AuthResponseDto created = clientService.createClient(clientDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> getClientById(@PathVariable Long id) {
        ClientDto client = clientService.getClientById(id);
        return ResponseEntity.ok(client);
    }

    @GetMapping
    public ResponseEntity<List<ClientDto>> getAllClients() {
        List<ClientDto> clients = clientService.getAllClients();
        return ResponseEntity.ok(clients);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientDto> updateClient(@PathVariable Long id, @RequestBody ClientDto clientDto) {
        ClientDto updated = clientService.updateClient(id, clientDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponseDto> authenticate(@RequestBody LoginDto loginDto) {
        AuthResponseDto client = clientService.authenticate(loginDto);
        return ResponseEntity.ok(client);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<ClientDto> findByUsername(@PathVariable String username) {
        ClientDto client = clientService.findByUsername(username);
        return client != null ? ResponseEntity.ok(client) : ResponseEntity.notFound().build();
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ClientDto> findByEmail(@PathVariable String email) {
        ClientDto client = clientService.findByEmail(email);
        return client != null ? ResponseEntity.ok(client) : ResponseEntity.notFound().build();
    }
}