package com.example.gestionpedidoscondao;

import com.example.gestionpedidoscondao.domain.usuario.Usuario;
import com.example.gestionpedidoscondao.domain.producto.Producto;
import com.example.gestionpedidoscondao.domain.pedido.Pedido;
import com.example.gestionpedidoscondao.domain.itemPedido.ItemPedido;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Data {
    public static List<Usuario> getUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.add(new Usuario(1L,"user1", "pass1", "user1@example.com"));
        usuarios.add(new Usuario(2L,"user2", "pass2", "user2@example.com"));
        usuarios.add(new Usuario(3L,"user3", "pass3", "user3@example.com"));
        usuarios.add(new Usuario(4L,"user4", "pass4", "user4@example.com"));
        return usuarios;
    }

    public static List<Producto> getProductos() {
        List<Producto> productos = new ArrayList<>();
        productos.add(new Producto(1L,"Bebida gaseosa", new BigDecimal("1.50"), 100));
        productos.add(new Producto(2L,"Snacks de papas", new BigDecimal("1.00"), 75));
        productos.add(new Producto(3L,"Barra de chocolate", new BigDecimal("2.00"), 50));
        productos.add(new Producto(4L,"Agua embotellada", new BigDecimal("1.25"), 120));
        productos.add(new Producto(5L,"Café instantáneo", new BigDecimal("2.50"), 40));
        productos.add(new Producto(6L,"Refresco de cola", new BigDecimal("1.75"), 90));
        productos.add(new Producto(7L,"Galletas saladas", new BigDecimal("1.20"), 60));
        productos.add(new Producto(8L,"Barra de cereales", new BigDecimal("1.50"), 70));
        productos.add(new Producto(9L,"Botella de jugo", new BigDecimal("2.25"), 80));
        productos.add(new Producto(10L, "Café en lata", new BigDecimal("2.75"), 55));
        return productos;
    }

    public static List<Pedido> getPedidos() {
        List<Pedido> pedidos = new ArrayList<>();
        // Aquí deberías crear instancias de Pedido y añadirlos a la lista
        // utilizando los datos de tu base de datos
        return pedidos;
    }

    public static List<ItemPedido> getItemsPedidos() {
        List<ItemPedido> itemsPedidos = new ArrayList<>();
        // Aquí deberías crear instancias de ItemPedido y añadirlos a la lista
        // utilizando los datos de tu base de datos
        return itemsPedidos;
    }
}
