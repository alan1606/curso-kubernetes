package org.aguirre.springcloud.msvc.cursos.controllers;

import feign.FeignException;
import feign.Response;
import jakarta.validation.Valid;
import org.aguirre.springcloud.msvc.cursos.models.Usuario;
import org.aguirre.springcloud.msvc.cursos.models.entity.Curso;
import org.aguirre.springcloud.msvc.cursos.services.CursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class CursoController {

    @Autowired
    private CursoService service;

    @GetMapping
    public ResponseEntity<?> listar(){
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> porId(@PathVariable Long id){
        Optional<Curso> c = service.porIdsConUsuarios(id);

        if(c.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(c.get());
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Curso curso, BindingResult result){
        if(result.hasErrors()){
            return validar(result);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(curso));
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> modificar(@PathVariable Long id, @Valid @RequestBody Curso curso, BindingResult result){
        if(result.hasErrors()){
            return validar(result);
        }


        Optional<Curso> c = service.porId(id);

        if(c.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Curso cursoDb = c.get();

        cursoDb.setNombre(curso.getNombre());

        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(cursoDb));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id){
        Optional<Curso> c = service.porId(id);

        if(c.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        service.eliminar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private  ResponseEntity<Map<String, String>> validar(BindingResult result) {
        Map<String,String> errores = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errores.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errores);
    }

    @PutMapping("/asignar-usuario/{cursoId}")
    public ResponseEntity<?> asignarUsuarioACurso(@RequestBody Usuario usuario, @PathVariable Long cursoId){
        Optional<Usuario> u;
        try {
            u = service.asignarUsuario(usuario, cursoId);

        }catch (FeignException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("mensaje", e.getMessage()));
        }

        if(u.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(u.get());
    }

    @PostMapping("/crear-usuario/{cursoId}")
    public ResponseEntity<?> crearUsuarioACurso(@RequestBody Usuario usuario, @PathVariable Long cursoId){
        Optional<Usuario> u;
        try {
            u = service.crearUsuario(usuario, cursoId);
        }catch (FeignException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("mensaje", e.getMessage()));
        }

        if(u.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(u.get());
    }

    @DeleteMapping("/eliminar-usuario/{cursoId}")
    public ResponseEntity<?> eliminarUsuarioACurso(@RequestBody Usuario usuario, @PathVariable Long cursoId){
        Optional<Usuario> u;
        try {
            u = service.eliminarUsuario(usuario, cursoId);

        }catch (FeignException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("mensaje", e.getMessage()));
        }

        if(u.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(u.get());
    }

    @DeleteMapping("/eliminar-curso-usuario/{usuarioId}")
    public ResponseEntity<?> eliminarCursoUsuarioPorUsuarioId(@PathVariable Long usuarioId){
        service.eliminarCursoUsuarioPorIdUsuario(usuarioId);
        return ResponseEntity.noContent().build();
    }
}
