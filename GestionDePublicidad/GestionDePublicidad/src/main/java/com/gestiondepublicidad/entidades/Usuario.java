package com.gestiondepublicidad.entidades;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
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
    
    @Basic
    private String nombre;
    private String email;
    private String contrasenia;

//    Relacion: private Foto foto;
    //Relacion: List<Proyecto> proyecto
    //Relacion: List<Calendario>
    //Enum PuestoEmpresa: puestoEmpresa
}
