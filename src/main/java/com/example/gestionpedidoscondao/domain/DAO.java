package com.example.gestionpedidoscondao.domain;

import com.example.gestionpedidoscondao.domain.producto.Producto;
import com.example.gestionpedidoscondao.domain.usuario.Usuario;

import java.util.List;

/**
 * Interfaz DAO.
 * Define las operaciones CRUD (Crear, Leer, Actualizar, Eliminar) para un tipo genérico T.
 * Esta interfaz es utilizada por clases DAO para implementar operaciones de base de datos estándar.
 *
 * @param <T> Tipo genérico que representa la entidad para la cual se implementa el DAO.
 * @author Author Name
 * @version 1.0
 * @since 2023-11-21
 */
public interface DAO<T> {

    public List<Producto> getAll();
    public T get(Long id);
    public T save(T data);
    public void update(T data);
    public void delete(T data);
    boolean remove(T t);
}
