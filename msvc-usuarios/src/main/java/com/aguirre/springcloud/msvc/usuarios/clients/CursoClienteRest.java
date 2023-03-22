package com.aguirre.springcloud.msvc.usuarios.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msvc-cursos", url = "localhost:8002")
public interface CursoClienteRest {

    @DeleteMapping("/eliminar-curso-usuario/{usuarioId}")
    public void eliminarCursoUsuarioPorUsuarioId(@PathVariable Long usuarioId);
}
