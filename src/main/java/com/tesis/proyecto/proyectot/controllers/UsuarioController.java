package com.tesis.proyecto.proyectot.controllers;

import com.tesis.proyecto.proyectot.models.entity.Usuario;
import com.tesis.proyecto.proyectot.models.entity.UsuarioActualizar;
import com.tesis.proyecto.proyectot.models.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioController(UsuarioService usuarioService, PasswordEncoder passwordEncoder1) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder1;
    }

    @GetMapping("/login")
    public String mostrarFormularioDeLogin() {
        return "usuarios/login"; // Vista en templates/usuarios/login.html
    }

    @PostMapping("/login")
    public ModelAndView login(@RequestParam String correo, @RequestParam String contrasennia) {
        ModelAndView modelAndView = new ModelAndView();
        if (usuarioService.login(correo, contrasennia).isPresent()) {
            modelAndView.setViewName("redirect:/proyectos"); // Redirige a proyectos tras login exitoso
        } else {
            modelAndView.setViewName("usuarios/login");
            modelAndView.addObject("error", "Credenciales inválidas");
        }
        return modelAndView;
    }

    @GetMapping("/register")
    public String mostrarFormularioDeRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuarios/registro"; // Vista en templates/usuarios/registro.html
    }

    @PostMapping("/register")
    public String register(@ModelAttribute Usuario usuario) {
        usuarioService.register(usuario);
        return "redirect:/usuarios/login";
    }

    @GetMapping("/perfil/editar")
    public String editarPerfil(Model model, Principal principal) {
        // Obtiene el correo del usuario logueado
        String correo = principal.getName();
        // Busca el usuario por correo o lanza excepción si no existe
        Usuario usuario = usuarioService.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        // Añade el objeto usuario al modelo
        model.addAttribute("usuario", usuario);
        // Retorna la vista para editar el perfil
        return "usuarios/editar_perfil";
    }

    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(@ModelAttribute UsuarioActualizar usuarioActualizar,
                                   RedirectAttributes redirectAttributes) {
        try {
            // Recupera el usuario actual de la base de datos para obtener la imagen existente
            Usuario existingUser = usuarioService.findById(usuarioActualizar.getId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Verifica si se ha cargado una nueva imagen
            if (usuarioActualizar.getFotoPerfil() != null && !usuarioActualizar.getFotoPerfil().isEmpty()) {
                // Define la ruta de guardado
                String uploadDir = "uploads/perfil/";
                String fileName = usuarioActualizar.getFotoPerfil().getOriginalFilename();
                Path newFilePath = Paths.get(uploadDir + fileName);

                // Renombra si existe un archivo con el mismo nombre
                int counter = 1;
                while (Files.exists(newFilePath)) {
                    String newFileName = fileName.substring(0, fileName.lastIndexOf('.'))
                            + "_" + counter++
                            + fileName.substring(fileName.lastIndexOf('.'));
                    newFilePath = Paths.get(uploadDir + newFileName);
                }

                // Elimina la imagen antigua si existe
                if (existingUser.getFotoPerfil() != null) {
                    Path oldFilePath = Paths.get(existingUser.getFotoPerfil());
                    Files.deleteIfExists(oldFilePath);
                }

                // Copia el nuevo archivo a la ruta deseada
                Files.copy(usuarioActualizar.getFotoPerfil().getInputStream(), newFilePath, StandardCopyOption.REPLACE_EXISTING);

                // Actualiza la ruta de la nueva imagen en el campo fotoPerfil
                existingUser.setFotoPerfil(uploadDir + newFilePath.getFileName().toString());
            }

            // Actualiza los datos del usuario
            existingUser.setNombre(usuarioActualizar.getNombre());
            existingUser.setApellido(usuarioActualizar.getApellido());
            existingUser.setCorreo(usuarioActualizar.getCorreo());

            // Encripta la contraseña si se ha proporcionado una nueva
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (!usuarioActualizar.getContrasennia().isEmpty()) {
                existingUser.setContrasennia(passwordEncoder.encode(usuarioActualizar.getContrasennia()));
            }

            // Actualiza el usuario en la base de datos
            usuarioService.update(existingUser);

            // Muestra un mensaje de éxito y redirige al perfil
            redirectAttributes.addFlashAttribute("success", "Perfil actualizado exitosamente.");
            return "redirect:/usuarios/perfil";
        } catch (IOException e) {
            e.printStackTrace();
            // Si ocurre un error, muestra el mensaje de error y redirige al formulario
            redirectAttributes.addFlashAttribute("error", "Error al subir la imagen: " + e.getMessage());
            return "redirect:/usuarios/perfil/editar";
        }
    }

    @GetMapping("/actualizarCorreo")
    public String mostrarFormularioActualizarCorreo(Model model, Principal principal) {
        // Obtener el correo del usuario autenticado
        String correo = principal.getName();
        // Cargar el formulario para actualizar el correo y la contraseña
        model.addAttribute("correo", correo);
        return "usuarios/actualizarCorreo";  // Vista donde se carga el formulario
    }

    @PostMapping("/actualizarCorreo")
    public String actualizarCorreo(@RequestParam("correo") String nuevoCorreo,
                                   @RequestParam("contrasenia") String nuevaContrasenia,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) {
        // Obtener el correo del usuario autenticado
        String correoActual = principal.getName();

        // Buscar el usuario utilizando el correo actual
        Usuario usuario = usuarioService.findByCorreo(correoActual)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado")); // Maneja el Optional aquí

        // Actualizar el correo y la contraseña
        usuario.setCorreo(nuevoCorreo);
        usuario.setContrasennia(passwordEncoder.encode(nuevaContrasenia)); // Encriptar la nueva contraseña
        usuarioService.update(usuario); // Guardar los cambios

        // Redirigir al inicio de sesión con un mensaje de éxito
        redirectAttributes.addFlashAttribute("message", "Perfil actualizado. Por favor, inicia sesión de nuevo.");
        return "redirect:/usuarios/login"; // Redirige al login para que el usuario inicie sesión con el nuevo correo
    }

    @GetMapping("/perfil")
    public String mostrarPerfil(Model model, Principal principal) {
        String correo = principal.getName(); // Obtener el correo del usuario autenticado
        Usuario usuario = usuarioService.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        model.addAttribute("usuario", usuario);
        return "usuarios/perfil"; // Vista en templates/usuarios/perfil.html
    }

}
