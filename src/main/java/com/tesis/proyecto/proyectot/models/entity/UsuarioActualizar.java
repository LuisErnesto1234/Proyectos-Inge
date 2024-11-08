package com.tesis.proyecto.proyectot.models.entity;

import jakarta.persistence.Transient;
import org.springframework.web.multipart.MultipartFile;

public class UsuarioActualizar {
    private Long id;
    private String nombre;
    private String apellido;
    private String correo;
    private String contrasennia;
    @Transient
    private MultipartFile fotoPerfil;

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasennia() {
        return contrasennia;
    }

    public void setContrasennia(String contrasennia) {
        this.contrasennia = contrasennia;
    }

    public MultipartFile getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(MultipartFile fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }
}
