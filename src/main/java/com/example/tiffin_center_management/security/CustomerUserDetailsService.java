package com.example.tiffin_center_management.security;

import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.tiffin_center_management.model.BaseUser;
import com.example.tiffin_center_management.repository.BaseUserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerUserDetailsService implements UserDetailsService{

	private final BaseUserRepository baseUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Sirf BaseUser se pura data nikal jayega (Clean approach)
        BaseUser user = baseUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Yahan "ROLE_" prefix lagana compulsory hai taaki SecurityConfig ka hasRole() chale
        String roleWithPrefix = "ROLE_" + user.getRole(); 

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(roleWithPrefix))
        );
    }
}

