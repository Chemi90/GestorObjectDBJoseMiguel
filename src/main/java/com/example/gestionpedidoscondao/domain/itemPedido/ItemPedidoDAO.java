package com.example.gestionpedidoscondao.domain.itemPedido;

import com.example.gestionpedidoscondao.domain.DAO;
import com.example.gestionpedidoscondao.domain.ObjectDBUtil;
import com.example.gestionpedidoscondao.domain.producto.Producto;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

public class ItemPedidoDAO implements DAO<ItemPedido> {

    public List<ItemPedido> findItemsByPedidoCodigo(String codPedido) {
        javax.persistence.EntityManager em = ObjectDBUtil.getEntityManagerFactory().createEntityManager();
        List<ItemPedido> items = new ArrayList<>();
        try {
            TypedQuery<ItemPedido> query = em.createQuery(
                    "SELECT ip FROM ItemPedido ip JOIN ip.pedido p JOIN ip.producto prod WHERE p.codigo = :codPedido", ItemPedido.class);
            query.setParameter("codPedido", codPedido);
            items = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return items;
    }

    @Override
    public List<Producto> getAll() {
        // Implementación para obtener todos los ItemPedido
        return null; // Modifica esto según la implementación deseada
    }

    @Override
    public ItemPedido get(Long id) {
        // Implementación para obtener un ItemPedido por su ID
        return null; // Modifica esto según la implementación deseada
    }

    @Override
    public ItemPedido save(ItemPedido data) {
        EntityManager em = ObjectDBUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(data);
            transaction.commit();
            return data;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void update(ItemPedido itemPedido) {
        // Implementación para actualizar un ItemPedido
    }

    @Override
    public void delete(ItemPedido data) {
        // Implementación para eliminar un ItemPedido
    }

    @Override
    public boolean remove(ItemPedido item) {
        javax.persistence.EntityManager em = ObjectDBUtil.getEntityManagerFactory().createEntityManager();
        javax.persistence.EntityTransaction transaction = em.getTransaction();
        boolean salida = false;
        try {
            transaction.begin();
            ItemPedido itemToRemove = em.find(ItemPedido.class, item.getId());
            if (itemToRemove != null) {
                em.remove(itemToRemove);
                salida = true;
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
        return salida;
    }
}
