package com.example.gestionpedidoscondao.domain.pedido;

import com.example.gestionpedidoscondao.domain.DAO;
import com.example.gestionpedidoscondao.domain.ObjectDBUtil;
import com.example.gestionpedidoscondao.domain.itemPedido.ItemPedido;
import com.example.gestionpedidoscondao.domain.producto.Producto;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

public class PedidoDAO implements DAO<Pedido> {


    public void actualizarPrecioTotalPedido(Long pedidoId) {
        EntityManager entityManager = ObjectDBUtil.getEntityManagerFactory().createEntityManager();
        try {
            entityManager.getTransaction().begin();

            Pedido pedido = entityManager.find(Pedido.class, pedidoId);
            Double nuevoTotal = calcularNuevoTotalPedido(pedido);
            pedido.setTotal(nuevoTotal);

            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }
    }

    private Double calcularNuevoTotalPedido(Pedido pedido) {
        // Calcula el nuevo total del pedido en función de los ItemPedido
        // Puedes iterar sobre los ItemPedido y sumar sus precioTotal aquí

        // Ejemplo de cómo calcular el nuevo total:
        double nuevoTotal = pedido.getItemsPedidos().stream()
                .mapToDouble(ItemPedido::getPrecioTotal)
                .sum();

        return nuevoTotal;
    }

    public List<Pedido> findByUsuarioId(int usuarioId) {
        EntityManager em = ObjectDBUtil.getEntityManagerFactory().createEntityManager();
        List<Pedido> pedidos;
        try {
            TypedQuery<Pedido> query = em.createQuery("SELECT p FROM Pedido p WHERE p.usuario.id = :usuarioId", Pedido.class);
            query.setParameter("usuarioId", usuarioId);
            pedidos = query.getResultList();

        } finally {
            em.close();
        }
        return pedidos;
    }

    // Otros métodos...

    @Override
    public List<Producto> getAll() {
        return null;
    }

    @Override
    public Pedido get(Long id) {
        return null;
    }

    @Override
    public Pedido save(Pedido data) {
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
    public void update(Pedido data) {

    }

    @Override
    public void delete(Pedido data) {

    }

    @Override
    public boolean remove(Pedido pedido) {
        return false;
    }

    public Pedido findByCodigo(String codigo) {
        EntityManager em = ObjectDBUtil.getEntityManagerFactory().createEntityManager();
        Pedido pedido = null;
        try {
            TypedQuery<Pedido> query = em.createQuery("SELECT p FROM Pedido p WHERE p.codigo = :codigo", Pedido.class);
            query.setParameter("codigo", codigo);
            pedido = query.getSingleResult();

            // Calcular total
            double total = pedido.getItemsPedidos().stream()
                    .mapToDouble(ItemPedido::getPrecioTotal)
                    .sum();
            pedido.setTotal(total);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return pedido;
    }

    public void deleteByCodigo(String codigoPedido) {
        EntityManager em = ObjectDBUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            TypedQuery<Pedido> query = em.createQuery("SELECT p FROM Pedido p WHERE p.codigo = :codigo", Pedido.class);
            query.setParameter("codigo", codigoPedido);
            Pedido pedido = query.getSingleResult();
            if (pedido != null) {
                // Elimina los ítems de pedido asociados antes de eliminar el pedido
                for (ItemPedido itemPedido : pedido.getItemsPedidos()) {
                    em.remove(itemPedido);
                }
                em.remove(pedido);
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
    }

}
