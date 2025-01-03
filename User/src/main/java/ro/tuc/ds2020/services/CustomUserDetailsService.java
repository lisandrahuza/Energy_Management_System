package ro.tuc.ds2020.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ro.tuc.ds2020.dtos.CustomUserDetails;
import ro.tuc.ds2020.entities.User;
import ro.tuc.ds2020.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository; // Repozitoriu pentru utilizatori


    @Override
    public UserDetails loadUserByUsername(String ids) throws UsernameNotFoundException {
        System.out.println("Căutăm utilizatorul cu username-ul: " + ids);
        try {
            // Încercăm să creăm un UUID din șirul ids
            UUID id = UUID.fromString(ids);

            // Afișăm numele utilizatorului pentru debug
            System.out.println("Căutăm utilizatorul cu id-ul: " + id);

            // Căutăm utilizatorul în baza de date
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Afișăm parola stocată în baza de date (criptată)
            System.out.println("Parola stocată în baza de date (criptată): " + user.getPassword());

            // Creăm lista de autorități (roluri)
            List<GrantedAuthority> authorities = new ArrayList<>();
            if (user.getIsAdmin()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                System.out.println("Utilizatorul este administrator.");
            } else {
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                System.out.println("Utilizatorul este utilizator normal.");
            }

            // Returnăm un obiect UserDetails cu informațiile utilizatorului
            return new CustomUserDetails(
                    user.getId(),
                    user.getUsername(),
                    user.getPassword(),
                    authorities
            );
        } catch (IllegalArgumentException e) {
            System.err.println("ID-ul furnizat nu este un UUID valid: " + ids);

            // Căutăm utilizatorul în baza de date
            User user = userRepository.findByUsername(ids)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Afișăm parola stocată în baza de date (criptată)
            System.out.println("Parola stocată în baza de date (criptată): " + user.getPassword());

            // Creăm lista de autorități (roluri)
            List<GrantedAuthority> authorities = new ArrayList<>();
            if (user.getIsAdmin()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                System.out.println("Utilizatorul este administrator.");
            } else {
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                System.out.println("Utilizatorul este utilizator normal.");
            }

            // Returnăm un obiect UserDetails cu informațiile utilizatorului
            return new CustomUserDetails(
                    user.getId(),
                    user.getUsername(),
                    user.getPassword(),
                    authorities
            );
        }

    }


}
