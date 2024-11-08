package com.tesis.proyecto.proyectot.models.service;

import com.tesis.proyecto.proyectot.models.entity.Proyecto;
import com.tesis.proyecto.proyectot.models.repository.ProyectoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;

    public ProyectoService(ProyectoRepository proyectoRepository) {
        this.proyectoRepository = proyectoRepository;
    }

    public Proyecto buscarPorId(Long id) {
        return proyectoRepository.findById(id).orElse(null);
    }

    public List<Proyecto> getAllProyectos() {
        return proyectoRepository.findAll();
    }

    public Optional<Proyecto> getProyectoById(Long id) {
        return proyectoRepository.findById(id);
    }

    public Proyecto saveProyecto(Proyecto proyecto) {
        return proyectoRepository.save(proyecto);
    }

    public void deleteProyecto(Long id) {
        Proyecto proyecto = proyectoRepository.findById(id).orElse(null);

        if (proyecto != null) {
            // Eliminar imágenes del sistema de archivos
            if (proyecto.getImagenes() != null) {
                for (String imagenUrl : proyecto.getImagenes()) {
                    try {
                        Path rutaImagen = Paths.get(imagenUrl);
                        Files.deleteIfExists(rutaImagen);  // Elimina el archivo si existe
                    } catch (IOException e) {
                        e.printStackTrace();  // Manejo de errores
                    }
                }
            }

            // Eliminar el proyecto de la base de datos
            proyectoRepository.delete(proyecto);
        }
    }
}
