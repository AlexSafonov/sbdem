package com.entr.sbdem.security;

import com.entr.sbdem.entity.SpUser;
import com.entr.sbdem.repository.SpUserRepository;
import com.entr.sbdem.service.SpUserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SpUserRepository repository;

    public UserDetailsServiceImpl (final SpUserRepository repository){
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(final String username)
            throws UsernameNotFoundException {
        SpUser user = repository.findByUsername(username);
        if(user != null){
            return user;
        }
        throw new UsernameNotFoundException(
                "User '" + username + "' not found");
    }
}
