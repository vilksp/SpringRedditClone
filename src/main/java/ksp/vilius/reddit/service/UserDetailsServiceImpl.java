package ksp.vilius.reddit.service;

import ksp.vilius.reddit.model.SecurityUser;
import ksp.vilius.reddit.model.User;
import ksp.vilius.reddit.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(s);
        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("Theres no such user"));

        return new SecurityUser(user);
    }


}
