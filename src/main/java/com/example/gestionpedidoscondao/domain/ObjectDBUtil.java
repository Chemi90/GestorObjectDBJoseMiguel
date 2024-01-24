package com.example.gestionpedidoscondao.domain;

import lombok.Getter;
import lombok.extern.java.Log;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@Log
public class ObjectDBUtil {

    @Getter
    private final static EntityManagerFactory entityManagerFactory;

    static {
        // Aquí se debe utilizar el nombre de la unidad de persistencia definida en persistence.xml
        entityManagerFactory = Persistence.createEntityManagerFactory("data.odb");
    }
}
