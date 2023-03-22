package org.aguirre.springcloud.msvc.cursos.services;

import org.aguirre.springcloud.msvc.cursos.clients.UsuarioClientRest;
import org.aguirre.springcloud.msvc.cursos.models.Usuario;
import org.aguirre.springcloud.msvc.cursos.models.entity.Curso;
import org.aguirre.springcloud.msvc.cursos.models.entity.CursoUsuario;
import org.aguirre.springcloud.msvc.cursos.models.repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CursoServiceImpl implements  CursoService{

    @Autowired
    private CursoRepository repository;


    @Autowired
    private UsuarioClientRest client;

    @Override
    @Transactional(readOnly = true)
    public List<Curso> listar() {
        return (List<Curso>) repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> porId(Long id) {
        return  repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> porIdsConUsuarios(Long id) {
        Optional<Curso> o = repository.findById(id);
        if(o.isEmpty()){
            return Optional.empty();
        }
        Curso curso = o.get();
        if(!curso.getCursoUsuarios().isEmpty()){
            List<Long> ids = curso.getCursoUsuarios().stream().map(c -> c.getId())
                    .collect(Collectors.toList());
            curso.setUsuarios(client.buscarUsuarios(ids));
        }
        return Optional.of(curso);
    }

    @Override
    @Transactional
    public Curso guardar(Curso curso) {
        return repository.save(curso);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public Optional<Usuario> asignarUsuario(Usuario usuario, Long cursoId) {
        Optional<Curso> c = repository.findById(cursoId);

        if(c.isEmpty()){
            return Optional.empty();
        }

        Usuario usuarioMsvc = client.buscarPorId(usuario.getId());
        Curso curso = c.get();
        CursoUsuario cursoUsuario = new CursoUsuario();
        cursoUsuario.setUsuarioId(usuarioMsvc.getId());

        curso.addCursoUsario(cursoUsuario);
        repository.save(curso);

        return Optional.of(usuarioMsvc);
    }

    @Override
    @Transactional
    public Optional<Usuario> crearUsuario(Usuario usuario, Long cursoId) {
        Optional<Curso> c = repository.findById(cursoId);

        if(c.isEmpty()){
            return Optional.empty();
        }

        Usuario usuarioNvoMsvc = client.guardar(usuario);
        Curso curso = c.get();
        CursoUsuario cursoUsuario = new CursoUsuario();
        cursoUsuario.setUsuarioId(usuarioNvoMsvc.getId());

        curso.addCursoUsario(cursoUsuario);
        repository.save(curso);

        return Optional.of(usuarioNvoMsvc);
    }

    @Override
    @Transactional
    public Optional<Usuario> eliminarUsuario(Usuario usuario, Long cursoId) {
        Optional<Curso> c = repository.findById(cursoId);

        if(c.isEmpty()){
            return Optional.empty();
        }

        Usuario usuarioNvoMsvc = client.buscarPorId(usuario.getId());
        Curso curso = c.get();
        CursoUsuario cursoUsuario = new CursoUsuario();
        cursoUsuario.setUsuarioId(usuarioNvoMsvc.getId());

        curso.removeCursoUsario(cursoUsuario);
        repository.save(curso);

        return Optional.of(usuarioNvoMsvc);
    }

    @Override
    @Transactional
    public void eliminarCursoUsuarioPorIdUsuario(Long id) {
        repository.eliminarCursoUsuarioPorUsuarioId(id);
    }
}
