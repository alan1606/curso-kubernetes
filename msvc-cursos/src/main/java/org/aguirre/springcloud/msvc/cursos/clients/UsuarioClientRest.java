package org.aguirre.springcloud.msvc.cursos.clients;

import jakarta.validation.Valid;
import org.aguirre.springcloud.msvc.cursos.models.Usuario;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "msvc-usuarios", url = "localhost:8001")
public interface UsuarioClientRest {

    @GetMapping("/{id}")
    public Usuario buscarPorId(@PathVariable Long id);

    @PostMapping("/")
    public Usuario guardar( @RequestBody Usuario usuario);

    @GetMapping("/usuarios-por-curso")
    public List<Usuario> buscarUsuarios(@RequestParam Iterable<Long> ids);

}
