package com.gestiondepublicidad.servicios;

import com.gestiondepublicidad.entidades.Foto;
import com.gestiondepublicidad.entidades.Proyecto;
import com.gestiondepublicidad.entidades.Usuario;
import com.gestiondepublicidad.enumeraciones.Rol;
import com.gestiondepublicidad.excepciones.MiException;
import com.gestiondepublicidad.repositorios.FotoRepositorio;
import com.gestiondepublicidad.repositorios.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private FotoServicio fotoServicio;
    
    @Autowired
    private FotoRepositorio fotoRepositorio;

    //CREA/REGISTRA UN NUEVO USUARIO
    @Transactional
    public Usuario registrar(String nombre, String email, String contrasenia,
            String contrasenia2) throws MiException {

        validar(nombre, email, contrasenia, contrasenia2);

        Usuario usuario = new Usuario();

        usuario.setNombre(nombre);
        usuario.setEmail(email);

        usuario.setContrasenia(new BCryptPasswordEncoder().
                encode(contrasenia));

        usuario.setRol(Rol.USER);
/*        
        Foto foto = new Foto();
                        
        foto.setMime("image/jpeg");
                
        foto.setNombre("ImagenPorDefecto");
                
        foto.setContenido(fotoRepositorio.buscarPorNombre("ImagenPorDefecto1")
                .getContenido());

        fotoRepositorio.save(foto);

        usuario.setFoto(foto);
*/
        return usuarioRepositorio.save(usuario);
    }

    //MODIFICA LOS DATOS EXISTENTES DE UN USUARIO
    @Transactional
    public void actualizar(MultipartFile archivo, String id_usuario, String nombre, String email,
            String password, String password2) throws MiException {

        validar(nombre, email, password, password2);

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id_usuario);

        if (respuesta.isPresent()) {

            Usuario usuario = respuesta.get();
            usuario.setNombre(nombre);
            usuario.setEmail(email);

            usuario.setContrasenia(new BCryptPasswordEncoder().encode(password));

            usuario.setRol(Rol.USER);

            String idFoto = null;

            if (usuario.getFoto() != null) {
                idFoto = usuario.getFoto().getId_foto();
            }
            Foto foto = fotoServicio.actualizar(archivo, idFoto);
            usuario.setFoto(foto);
            usuarioRepositorio.save(usuario);
        }

    }

    //DEVUELVE UN USUARIO POR SU ID
    public Usuario getOne(String id_usuario) {
        return usuarioRepositorio.getOne(id_usuario);
    }

    //DEVUELVE LOS USUARIO CON EL MISMO NOMBRE
    public List<Usuario> usuariosPorNombre(String nombre) {

        return  usuarioRepositorio.buscarPorNombre(nombre);
    }

    //DEVUELVE UN USUARIO BUSCADO POR SU EMAIL
    public Usuario usuariosPorEmail(String email) {
        return usuarioRepositorio.buscarPorEmail(email);
    }

    //DEVUELVE LOS USUARIOS CONECTADOS AL MISMO PROYECTO
    public List<Usuario> usuariosPorProyecto(Proyecto proyecto) {

        return usuarioRepositorio.buscarPorProyecto(proyecto);
    }

    //DEVUELVE UNA LISTA DE TODOS LOS USUARIOS
    public List<Usuario> listarUsuarios() {

        return usuarioRepositorio.findAll();
    }

    public List<Usuario> listarClientesPorNombre(String nombre) {
        List<Usuario> listaClientes = new ArrayList();
        return listaClientes = usuarioRepositorio.buscarPorNombre(nombre);
    }
    
    //CAMBIA ROLES ENTRE USER, CLIENTE Y ADMIN
    @Transactional
    public void cambiarRol(String id_usuario, Rol rol) {
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id_usuario);

        if (respuesta.isPresent()) {

            Usuario usuario = respuesta.get();
            usuario.setRol(rol);
            usuarioRepositorio.save(usuario);
        }
    }

    //ELIMINA UN USUARIO SEGÚN SU ID
    @Transactional
    public void eliminarUsuario(String id_usuario) {
        usuarioRepositorio.deleteById(id_usuario);
    }

    //IMPIDE QUE SE INGRESEN DATOS NULOS
    private void validar(String nombre, String email, String contrasenia, String contrasenia2)
            throws MiException {

        if (nombre.isEmpty() || nombre == null) {
            throw new MiException("Debe ingresar un nombre");
        }
        if (email.isEmpty() || email == null) {
            throw new MiException("Debe ingresar un email válido");
        }
        if (contrasenia.isEmpty() || contrasenia == null || contrasenia.length() <= 5) {
            throw new MiException("La contraseña no puede estar vacía, y debe tener más de 5 dígitos");
        }

        if (!contrasenia.equals(contrasenia2)) {
            throw new MiException("Las contraseñas ingresadas deben ser iguales");
        }
        //IMPIDE QUE SE INGRESEN EMAILS EXISTENTES
        if(usuarioRepositorio.buscarPorEmail(email)!= null) {
            throw new MiException("El email ingresado ya esta registrado");
        }
        //VERIFICA QUE SE INGRESEN EMAILS VÁLIDOS
        if(!email.contains("@") || !email.contains(".com")){
            throw new MiException("No se ha ingresado un email válido");
        }

    }

    public List<Usuario> buscarPorRol(String rol){
        return usuarioRepositorio.buscarPorRol(rol);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepositorio.buscarPorEmail(email);

        if (usuario != null) {

            List<GrantedAuthority> permisos = new ArrayList();

            GrantedAuthority p = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString());

            permisos.add(p);

            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

            HttpSession session = attr.getRequest().getSession(true);

            session.setAttribute("usuariosession", usuario);

            return new User(usuario.getEmail(), usuario.getContrasenia(), permisos);
        } else {
            return null;
        }

    }

}
