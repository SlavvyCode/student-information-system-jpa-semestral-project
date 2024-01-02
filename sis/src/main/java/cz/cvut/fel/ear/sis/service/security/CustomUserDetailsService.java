package cz.cvut.fel.ear.sis.service.security;

import cz.cvut.fel.ear.sis.model.Person;
import cz.cvut.fel.ear.sis.repository.PersonRepository;
import cz.cvut.fel.ear.sis.security.model.CustomUserDetails;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final PersonRepository personRepository;

    @Autowired
    public CustomUserDetailsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Person user = personRepository.findByUserName(username);

        if (user == null) {
            throw new UsernameNotFoundException("User with username " + username + " not found.");
        }

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole()));

        CustomUserDetails customUserDetail = new CustomUserDetails(user);
        customUserDetail.setAuthorities(authorities);

        return customUserDetail;
    }
}
