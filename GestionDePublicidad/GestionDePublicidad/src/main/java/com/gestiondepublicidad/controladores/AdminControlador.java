package com.gestiondepublicidad.controladores;

import com.gestiondepublicidad.entidades.Usuario;
import com.gestiondepublicidad.enumeraciones.Rol;
import com.gestiondepublicidad.excepciones.MiException;
import com.gestiondepublicidad.servicios.UsuarioServicio;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin")
public class AdminControlador {

    private final Logger log = LoggerFactory.getLogger(AdminControlador.class);

    @Autowired
    UsuarioServicio usuarioServicio;

    @GetMapping("/dashboard")
    public String panelAdministrativo(ModelMap modelo) {
        return "dashboard.html";
    }

    @GetMapping("/tablaUsuarios")
    public String listarUsuarios(ModelMap modelo) {
        List<Usuario> listaUsuarios = usuarioServicio.listarUsuarios();
        modelo.addAttribute("usuarios", listaUsuarios);
        return "tablaClientes.html";
    }

    //LISTAR
    @GetMapping("/tablaClientes")
    public String listarClientes(ModelMap modelo) {
        List<Usuario> listaUsuarios = usuarioServicio.buscarPorRol("CLIENTE");
        modelo.addAttribute("usuarios", listaUsuarios);
        return "tablaClientes.html";
    }
    @PostMapping("/tablaClientes/search")
    public String buscarClientePorNombre(@RequestParam String nombre, ModelMap modelo) throws Exception {
        try{
            List<Usuario> usuarios = usuarioServicio.usuariosPorNombreYRol(nombre, "CLIENTE");
            modelo.addAttribute("usuarios", usuarios);
        }catch(Exception e){
            modelo.put("error", e.getMessage());

        }finally {
            return "tablaTrabajadores.html";
        }
    }
    @GetMapping("/tablaTrabajadores")
    public String listarTrabajadores(ModelMap modelo) {
        List<Usuario> listaUsuarios = usuarioServicio.buscarPorRol("USER");
        modelo.addAttribute("usuarios", listaUsuarios);
        return "tablaTrabajadores.html";
    }
    @PostMapping("/tablaTrabajadores/search")
    public String buscarTrabajadorPorNombre(@RequestParam String nombre, ModelMap modelo) throws Exception {
        try{
            List<Usuario> usuarios = usuarioServicio.usuariosPorNombreYRol(nombre, "USER");
            modelo.addAttribute("usuarios", usuarios);
        }catch(Exception e){
            modelo.put("error", e.getMessage());

        }finally {
            return "tablaClientes.html";
        }
    }


    //MODIFICAR ROL USUARIO
    @GetMapping("/modificarRol/{id}")
    public String modificarRol(ModelMap modelo, @PathVariable String id) {
        Usuario usuario = usuarioServicio.getOne(id);
        modelo.put("usuario", usuario);
        return "editar_usuario.html";
    }

    @PostMapping("/modificarRol/{id}")
    public String modificarRol(@RequestParam String id, String rol, ModelMap modelo) throws MiException {

        Usuario usuario = usuarioServicio.getOne(id);
        modelo.put("usuario", usuario);

        try {
            if (usuario.getRol().toString().equals(rol)){
                throw new MiException("El usuario ya tiene este rol");
            }else if (rol.equals("CLIENTE")){
                usuarioServicio.cambiarRol(id, Rol.CLIENTE);
            } else if (rol.equals("USER")) {
                usuarioServicio.cambiarRol(id, Rol.USER);
            }else if (rol.equals("ADMIN")) {
                usuarioServicio.cambiarRol(id, Rol.ADMIN);
            }

            modelo.put("exito", "Usuario actualizado correctamente!");
            return "editar_usuario.html";
        } catch (MiException ex) {
            modelo.put("error", ex.getMessage());
            modelo.put("usuario", usuario);
            return "editar_usuario.html";
        }
    }

    //ELIMINAR USUARIO
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable String id, ModelMap modelo) {
        usuarioServicio.eliminarUsuario(id);
        return "redirect:/admin/usuarios";

    }
}
