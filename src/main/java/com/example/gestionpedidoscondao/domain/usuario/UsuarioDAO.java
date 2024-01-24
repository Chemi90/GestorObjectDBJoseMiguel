package com.example.gestionpedidoscondao.domain.usuario;

import com.example.gestionpedidoscondao.domain.DAO;
import com.example.gestionpedidoscondao.domain.ObjectDBUtil;
import com.example.gestionpedidoscondao.domain.producto.Producto;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;

public class UsuarioDAO implements DAO<Usuario> {

    @Override
    public List<Producto> getAll() {
        // Implementación para obtener todos los usuarios
        return null; // Modifica esto según la implementación deseada
    }

    public void saveAll(List<Usuario> usuarios) {
        EntityManager em = ObjectDBUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            for (Usuario usuario : usuarios) {
                em.persist(usuario);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public Usuario get(Long id) {
        EntityManager em = ObjectDBUtil.getEntityManagerFactory().createEntityManager();
        Usuario usuario = null;
        try {
            usuario = em.find(Usuario.class, id);
        } finally {
            em.close();
        }
        return usuario;
    }

    @Override
    public Usuario save(Usuario data) {
        EntityManager em = ObjectDBUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(data);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
        return data;
    }

    @Override
    public void update(Usuario data) {
        // Implementación para actualizar un usuario
    }

    @Override
    public void delete(Usuario data) {
        // Implementación para eliminar un usuario
    }

    @Override
    public boolean remove(Usuario o) {
        // Implementación para eliminar un usuario
        return false;
    }

    public Usuario validateUser(String username, String password){
        EntityManager em = ObjectDBUtil.getEntityManagerFactory().createEntityManager();
        Usuario result = null;
        try {
            TypedQuery<Usuario> q = em.createQuery("SELECT u FROM Usuario u WHERE u.nombre = :u AND u.contraseña = :p", Usuario.class);
            q.setParameter("u", username);
            q.setParameter("p", password);
            result = q.getSingleResult();
        } catch (Exception e) {
            System.out.println("Error al validar el usuario: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
        return result;
    }
}
