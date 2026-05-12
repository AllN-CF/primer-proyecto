package com.tortilleria.app_tortilleria.service;

import com.tortilleria.app_tortilleria.controller.UsuarioController;
import com.tortilleria.app_tortilleria.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(CustomUserDetailsService userDetailsService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public UsuarioController.AuthResponse autenticarUsuario(UsuarioController.AuthRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        ));

        UserDetails usuario = userDetailsService.loadUserByUsername(request.getEmail());

        return new UsuarioController.AuthResponse(jwtService.generarToken(usuario));
    }
}