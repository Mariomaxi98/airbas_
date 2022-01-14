package com.afm.authservice.service;

import com.afm.authservice.exception.UsernameNotFoundException;
import com.afm.authservice.repository.UserBasRepository;
import com.afm.authservice.security.JWTAuthenticationManager;
import lombok.RequiredArgsConstructor;

import model.auth.AuthProvider;
import model.auth.ERole;
import model.auth.UserBas;
import model.auth.UserBasDetail;
import model.utils.LoginRequest;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @RequiredArgsConstructor generates a constructor with 1 parameter for each field that requires special handling.
 * All non-initialized final fields get a parameter, as well as any fields that are marked as @NonNull that aren't
 * initialized where they are declared. For those fields marked with @NonNull, an explicit null check is also generated.
 * The constructor will throw a NullPointerException if any of the parameters intended for the fields marked with
 * @NonNull contain null. The order of the parameters match the order in which the fields appear in your class.
 */

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserBasRepository userBasRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTAuthenticationManager jwtAuthenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public String authenticateUser(LoginRequest credentials) throws UsernameNotFoundException,
            BadCredentialsException {

        if (findUser(credentials.getEmail()) == null)
            throw new UsernameNotFoundException("Email - " + credentials.getEmail());

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    credentials.getEmail(),
                    credentials.getPassword()));
        }catch (UsernameNotFoundException e){
            throw new BadCredentialsException("Invalid Credentials - Password not valid");
        }
        return jwtAuthenticationManager.generateJwtToken(credentials.getEmail());
    }

    public UserBas createUser(LoginRequest request, AuthProvider provider) throws Exception {
        if (userBasRepository.findByEmail(request.getEmail()) != null)
            throw new Exception("Email already exists");

        UserBas newUser = new UserBas();
        //UserBasDetail detail = new UserBasDetail();
        //necessario aggiornare entrambe le referenze
        //detail.setUserbas(newUser);

        newUser.setEmail(request.getEmail());
        newUser.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        newUser.setRole(ERole.ROLE_USER);
        newUser.setProvider(provider);
        //newUser.setUserbasdetail(detail);

        userBasRepository.save(newUser);
        //salvando newUser salvo il relativo UserBasDetail associato
        return newUser;
    }

    public List<UserBas> findAll(){
        return userBasRepository.findAll();
    }

    public UserBas findUser(String email){
        return userBasRepository.findByEmail(email);
    }

    public boolean exisitUser(String email){
        return userBasRepository.findByEmail(email) != null;
    }




}
