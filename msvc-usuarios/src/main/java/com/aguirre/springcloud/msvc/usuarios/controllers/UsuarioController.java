package com.aguirre.springcloud.msvc.usuarios.controllers;

import com.aguirre.springcloud.msvc.usuarios.models.entity.Usuario;
import com.aguirre.springcloud.msvc.usuarios.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @GetMapping
    public ResponseEntity<?> obtenerTodo(){
        return  ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id){
        Optional<Usuario> u = service.porId(id);
        if(u.isEmpty()){
            return  ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(u.get());
    }

    @PostMapping
    public ResponseEntity<?> guardar(@Valid @RequestBody Usuario usuario, BindingResult result){

        if(result.hasErrors()){
            return validar(result);
        }

        if(service.porEmail(usuario.getEmail()).isPresent()){
            return ResponseEntity.badRequest()
                    .body(Collections
                            .singletonMap("error", "Ya existe un registrado con ese correo"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(usuario));
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> modificar(@PathVariable Long id, @Valid @RequestBody Usuario usuario, BindingResult result){

        if(result.hasErrors()){
            return validar(result);
        }

        Optional<Usuario> u = service.porId(id);

        if(u.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Usuario usuarioDb = u.get();

        if(!usuario.getEmail().equalsIgnoreCase(usuarioDb.getEmail()) && service.porEmail(usuario.getEmail()).isPresent()){
            return ResponseEntity.badRequest()
                    .body(Collections
                            .singletonMap("error", "Ya existe un registrado con ese correo"));
        }


        usuarioDb.setEmail(usuario.getEmail());
        usuarioDb.setNombre(usuario.getNombre());
        usuarioDb.setPassword(usuario.getPassword());

        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(usuarioDb));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPorId(@PathVariable Long id){
        Optional<Usuario> u = service.porId(id);
        if(u.isEmpty()){
            return  ResponseEntity.notFound().build();
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    private  ResponseEntity<Map<String, String>> validar(BindingResult result) {
        Map<String,String> errores = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errores.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errores);
    }

    @GetMapping("/usuarios-por-curso")
    public ResponseEntity<?> obtenerUsuariosPorCurso(@RequestParam List<Long> ids){
        return ResponseEntity.ok(service.listarPorIds(ids));
    }

}
