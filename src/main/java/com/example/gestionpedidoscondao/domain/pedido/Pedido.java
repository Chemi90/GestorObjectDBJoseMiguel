package com.example.gestionpedidoscondao.domain.pedido;

import com.example.gestionpedidoscondao.domain.itemPedido.ItemPedido;
import com.example.gestionpedidoscondao.domain.usuario.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Clase Pedido.
 * Representa un pedido en la base de datos.
 * Contiene información sobre el pedido, incluyendo un identificador único, código, fecha, usuario asociado, total del pedido,
 * y un conjunto de ítems asociados al pedido.
 *
 * @author Author Name
 * @version 1.0
 * @since 2023-11-21
 */
@Entity
@Table(name = "Pedidos")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Pedido implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigo;

    @Temporal(TemporalType.DATE)
    private Date fecha;

    @ManyToOne
    @JoinColumn(name = "usuario", referencedColumnName = "id")
    private Usuario usuario;

    private double total;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private Set<ItemPedido> itemsPedidos = new HashSet<>();

    @Override
    public String toString() {
        // Incluir solo los campos primitivos o Strings, excluir referencias a otras entidades
        return "Pedido{" +
                "id=" + id +
                ", codigo='" + codigo + '\'' +
                ", fecha=" + fecha +
                // ... otros campos
                '}';
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, codigo, fecha, total);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pedido pedido = (Pedido) o;

        return id != null && id.equals(pedido.id) &&
                Objects.equals(codigo, pedido.codigo) &&
                Objects.equals(fecha, pedido.fecha) &&
                Objects.equals(total, pedido.total);
    }

}
