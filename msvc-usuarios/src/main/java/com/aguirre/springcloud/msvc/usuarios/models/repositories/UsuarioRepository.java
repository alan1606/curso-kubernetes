package com.aguirre.springcloud.msvc.usuarios.models.repositories;

import com.aguirre.springcloud.msvc.usuarios.models.entity.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u where u.email = ?1")
    public Optional<Usuario> findByEmail(String email);
}
