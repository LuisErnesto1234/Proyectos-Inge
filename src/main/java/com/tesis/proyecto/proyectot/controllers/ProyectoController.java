package com.tesis.proyecto.proyectot.controllers;

import com.tesis.proyecto.proyectot.models.entity.Proyecto;
import com.tesis.proyecto.proyectot.models.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;

@Controller
@RequestMapping("/proyectos")
public class ProyectoController {

    private final static String rutaSubida = "uploads/";

    @Autowired
    private ProyectoService proyectoService;

    @GetMapping
    public String listarProyectos(Model model) {
        model.addAttribute("proyectos", proyectoService.getAllProyectos());
        return "proyectos/listar"; // Asegúrate de que esta vista exista
    }

    @GetMapping("/nuevo")
    public String nuevoProyecto(Model model) {
        Proyecto proyecto = new Proyecto();
        proyecto.setFechaCreacion(LocalDate.now());
        model.addAttribute("proyecto", proyecto);
        return "proyectos/formulario";
    }

    @PostMapping
    public String guardarProyecto(@RequestParam(value = "id", required = false) Long id,
                                  @RequestParam("titulo") String titulo,
                                  @RequestParam("descripcion") String descripcion,
                                  @RequestParam(value = "imagenes", required = false) MultipartFile[] imagenes,
                                  RedirectAttributes redirectAttributes) {

        Proyecto proyecto;

        if (id != null) {
            proyecto = proyectoService.buscarPorId(id);
            if (proyecto == null) {
                redirectAttributes.addFlashAttribute("error", "Proyecto no encontrado");
                return "redirect:/proyectos";
            }

            if (imagenes != null && imagenes.length > 0) {
                for (String imagenUrl : proyecto.getImagenes()) {
                    Path rutaImagenExistente = Paths.get(imagenUrl);
                    try {
                        Files.deleteIfExists(rutaImagenExistente);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                proyecto.getImagenes().clear();
            }
        } else {
            proyecto = new Proyecto();
            proyecto.setImagenes(new ArrayList<>());
        }

        proyecto.setTitulo(titulo);
        proyecto.setDescripcion(descripcion);
        proyecto.setFechaCreacion(LocalDate.now());

        if (imagenes != null) {
            for (MultipartFile imagen : imagenes) {
                if (!imagen.isEmpty()) {
                    String nombreImagen = imagen.getOriginalFilename();
                    Path rutaCompleta = Paths.get(rutaSubida + nombreImagen);

                    int contador = 1;
                    while (Files.exists(rutaCompleta)) {
                        String nuevoNombre = nombreImagen.substring(0, nombreImagen.lastIndexOf('.'))
                                + "_" + contador++
                                + nombreImagen.substring(nombreImagen.lastIndexOf('.'));
                        rutaCompleta = Paths.get(rutaSubida + nuevoNombre);
                    }

                    try {
                        Files.copy(imagen.getInputStream(), rutaCompleta);
                        proyecto.getImagenes().add(rutaCompleta.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        proyectoService.saveProyecto(proyecto);
        redirectAttributes.addFlashAttribute("mensaje", "Proyecto guardado exitosamente!");
        return "redirect:/proyectos";
    }

    @GetMapping("/editar/{id}")
    public String editarProyecto(@PathVariable Long id, Model model) {
        Proyecto proyecto = proyectoService.buscarPorId(id);
        model.addAttribute("proyecto", proyecto);
        return "proyectos/formulario"; // Asegúrate de que esta vista exista
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProyecto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Proyecto proyecto = proyectoService.buscarPorId(id);
        if (proyecto != null) {
            // Eliminar las imágenes del sistema de archivos
            for (String imagen : proyecto.getImagenes()) {
                Path file = Paths.get(imagen);
                try {
                    Files.deleteIfExists(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            proyectoService.deleteProyecto(id);
            redirectAttributes.addFlashAttribute("mensaje", "Proyecto eliminado exitosamente");
        } else {
            redirectAttributes.addFlashAttribute("error", "Proyecto no encontrado");
        }
        return "redirect:/proyectos";
    }

    @GetMapping("/ver/{id}")
    public String verProyecto(@PathVariable Long id, Model model) {
        Proyecto proyecto = proyectoService.buscarPorId(id);
        model.addAttribute("proyecto", proyecto);
        return "proyectos/proyecto"; // Asegúrate de que esta vista exista
    }
}