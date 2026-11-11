package com.csis231.springpostgrescrud.security;

import com.csis231.springpostgrescrud.entity.Client;
import com.csis231.springpostgrescrud.repository.ClientRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DbClientDetailsService implements UserDetailsService {

    private final ClientRepository clientRepository;

    public DbClientDetailsService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        if (usernameOrEmail == null) {
            throw new UsernameNotFoundException("User not found.");
        }
        String identifier = usernameOrEmail.trim();

        Client client = clientRepository.findByUsernameIgnoreCase(identifier)
                .or(() -> clientRepository.findByEmailIgnoreCase(identifier))
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        return new org.springframework.security.core.userdetails.User(
                client.getUsername(),
                client.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
