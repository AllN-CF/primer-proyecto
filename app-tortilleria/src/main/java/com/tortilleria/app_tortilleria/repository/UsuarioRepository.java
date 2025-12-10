package com.tortilleria.app_tortilleria.repository;
import com.tortilleria.app_tortilleria.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
