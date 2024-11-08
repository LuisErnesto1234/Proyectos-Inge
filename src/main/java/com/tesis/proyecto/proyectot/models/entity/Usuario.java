package com.tesis.proyecto.proyectot.models.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "usuario", uniqueConstraints = @UniqueConstraint(columnNames = "correo"))
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;
    @Column(length = 100)
    private String nombre;
    @Column(name = "apellido", length = 100)
    private String apellido;
    @Column(name = "correo", length = 100, unique = true)
    private String correo;
    @Column(name = "contrasenia")
    private String contrasennia;
    @Column(name = "foto_perfil", length = 250)
    private String fotoPerfil;  // Aquí se guarda la ruta del archivo

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

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

}
