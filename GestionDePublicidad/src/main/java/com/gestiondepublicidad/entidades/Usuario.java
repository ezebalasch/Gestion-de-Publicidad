package com.gestiondepublicidad.entidades;
import com.gestiondepublicidad.enumeraciones.PuestoEmpresa;
import com.gestiondepublicidad.enumeraciones.Rol;
import java.util.List;
import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@Table(name = "Usuario")
public class Usuario {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id_usuario;

    private String nombre;
    private String email;
    private String contrasenia;

    @OneToOne
    private Foto foto;

    @ManyToMany
    private List<Proyecto> proyecto;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evento> eventos;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Nota> notas;

    @Enumerated(EnumType.STRING)
    private Rol rol;
    
    @Enumerated(EnumType.STRING)
    private PuestoEmpresa puestoEmpresa;


    
}
