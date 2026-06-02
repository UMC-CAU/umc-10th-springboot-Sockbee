package com.example.umc10th.global.security.service;

import com.example.umc10th.domain.user.repository.UserRepository;
import com.example.umc10th.global.security.AuthMember;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(AuthMember::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}
