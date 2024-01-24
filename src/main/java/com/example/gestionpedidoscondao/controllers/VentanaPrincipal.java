package com.example.gestionpedidoscondao.controllers;

import com.example.gestionpedidoscondao.App;
import com.example.gestionpedidoscondao.Session;
import com.example.gestionpedidoscondao.domain.ObjectDBUtil;
import com.example.gestionpedidoscondao.domain.carrito.Carrito;
import com.example.gestionpedidoscondao.domain.itemPedido.ItemPedido;
import com.example.gestionpedidoscondao.domain.pedido.Pedido;
import com.example.gestionpedidoscondao.domain.pedido.PedidoDAO;
import com.example.gestionpedidoscondao.domain.producto.Producto;
import com.example.gestionpedidoscondao.domain.producto.ProductoDAO;
import com.example.gestionpedidoscondao.domain.usuario.Usuario;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Clase controladora de la interfaz de usuario principal de la aplicación.
 * Gestiona la interacción con el usuario y realiza la navegación entre diferentes vistas.
 *
 * @author José Miguel Ruiz Guevara
 * @version 1.0
 * @since 2023-11-21
 */
public class VentanaPrincipal extends Application implements Initializable {

    @javafx.fxml.FXML
    private TableView tbPedidos;
    @javafx.fxml.FXML
    private TableColumn cCodigo;
    @javafx.fxml.FXML
    private TableColumn cFecha;
    @javafx.fxml.FXML
    private TableColumn cTotal;
    @javafx.fxml.FXML
    private TableView tbCarrito;
    @javafx.fxml.FXML
    private TableColumn cNombre;
    @javafx.fxml.FXML
    private TableColumn cCantidadCarrito;
    @javafx.fxml.FXML
    private TableColumn cPrecioTotal;
    @javafx.fxml.FXML
    private Button btnAceptar;
    @javafx.fxml.FXML
    private Button btnCancelar;
    @javafx.fxml.FXML
    private Label lbNombreUsuario;
    @javafx.fxml.FXML
    private ComboBox cbItem;
    @javafx.fxml.FXML
    private Label lbPrecio;
    @javafx.fxml.FXML
    private ComboBox cbCantidad;
    @javafx.fxml.FXML
    private Button bntAñadir;
    @javafx.fxml.FXML
    private MenuItem mbLogout;
    @javafx.fxml.FXML
    private MenuItem mbClose;
    private ProductoDAO productoDAO = new ProductoDAO();
    private PedidoDAO pedidoDAO = new PedidoDAO();


    public static void main(String[] args) {
        launch(args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage primaryStage) {}

    /**
     * Maneja el evento de clic en el botón "Comprar".
     * Este método procesa los ítems en el carrito de compras y registra un nuevo pedido.
     *
     * @param event El evento que desencadena esta acción.
     */
    @javafx.fxml.FXML
    protected void onComprarClick(ActionEvent event) {
        ObservableList<Carrito> itemsCarrito = tbCarrito.getItems();

        if (itemsCarrito.isEmpty()) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Carrito Vacío");
            alert.setHeaderText("No se puede realizar la compra");
            alert.setContentText("No hay artículos en el carrito. Por favor, añade algunos productos antes de comprar.");
            alert.showAndWait();
            return;
        }

        var totalCompra = calcularTotalCompra(itemsCarrito);
        var codigoPedido = generateUniqueCode();
        var fechaPedido = new Date(System.currentTimeMillis());
        var usuario = Session.getUser();

        EntityManager em = ObjectDBUtil.getEntityManagerFactory().createEntityManager();
        try  {
            var tx = em.getTransaction();
            tx.begin();

            var pedido = new Pedido();
            pedido.setCodigo(codigoPedido);
            pedido.setFecha(fechaPedido);
            pedido.setUsuario(usuario);
            pedido.setTotal(totalCompra);

            em.persist(pedido);

            for (var item : itemsCarrito) {
                var productos = productoDAO.findByName(item.getNombre()); // Método para buscar Productos por nombre
                Producto producto = null;

// Verificar si se encontraron productos
                if (!productos.isEmpty()) {
                    for (Producto p : productos) {
                        producto = p; // Obtener el primer producto si existe
                        break; // Salir del bucle después de obtener el primer producto
                    }
                }

                if (producto != null) {
                    var itemPedido = new ItemPedido();
                    itemPedido.setPedido(pedido);
                    itemPedido.setProducto(producto);
                    itemPedido.setCantidad(item.getCantidad());
                    em.persist(itemPedido);
                } else {
                    // Manejar el caso de que no se encontraron productos
                    var alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Producto no encontrado");
                    alert.setHeaderText(null);
                    alert.setContentText("El producto con nombre " + item.getNombre() + " no se encontró en la base de datos.");
                    alert.showAndWait();
                }
            }


            tx.commit();

            itemsCarrito.clear();
            tbCarrito.getItems().clear();

            actualizarTablaPedidos();

            var alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Compra Exitosa");
            alert.setHeaderText(null);
            alert.setContentText("Tu pedido ha sido registrado exitosamente.");
            alert.showAndWait();
        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            em.close(); // Asegurarse de cerrar el EntityManager
        }
    }


    /**
     * Actualiza la tabla de pedidos con la información más reciente del usuario actual.
     * Este método se llama para refrescar la vista de pedidos después de realizar una compra o al iniciar la sesión.
     */
    private void actualizarTablaPedidos() {

        var pedidosActualizados = pedidoDAO.findByUsuarioId(Math.toIntExact(Session.getUser().getId()));
        tbPedidos.setItems(FXCollections.observableArrayList(pedidosActualizados));
    }

    /**
     * Calcula el total de la compra a partir de los ítems en el carrito.
     *
     * @param itemsCarrito La lista observable de ítems en el carrito.
     * @return El valor total de la compra.
     */
    private double calcularTotalCompra(ObservableList<Carrito> itemsCarrito) {
        double total = 0;
        for (var item : itemsCarrito) {
            total += item.getPrecioTotal();
        }
        return total;
    }

    /**
     * Maneja el evento de clic en el botón "Cancelar".
     * Este método limpia el carrito de compras.
     *
     * @param actionEvent El evento que desencadena esta acción.
     */
    @javafx.fxml.FXML
    public void onCancelarClick(ActionEvent actionEvent) {
        tbCarrito.getItems().clear();
    }

    /**
     * Maneja el evento de clic en el botón "Añadir" para añadir un producto al carrito.
     * Este método agrega un nuevo ítem al carrito basado en la selección del usuario.
     *
     * @param actionEvent El evento que desencadena esta acción.
     */
    @javafx.fxml.FXML
    public void onAñadirClick(ActionEvent actionEvent) {
        var productoSeleccionado = cbItem.getValue() != null ? cbItem.getValue().toString() : null;
        var cantidadSeleccionada = cbCantidad.getValue() != null ? (int) cbCantidad.getValue() : null;

        if (productoSeleccionado == null || cantidadSeleccionada == null || cantidadSeleccionada <= 0) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error en selección");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, selecciona un producto y una cantidad válida.");
            alert.showAndWait();
            return;
        }

        Double precioUnitario = Double.parseDouble(lbPrecio.getText().replace("Precio: ", ""));
        Double total = cantidadSeleccionada * precioUnitario; // Calcular el total

        var carritoItem = new Carrito(productoSeleccionado, cantidadSeleccionada, total);

        tbCarrito.getItems().add(carritoItem);

        if (cNombre.getCellData(0) == null) {
            cNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            cCantidadCarrito.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
            cPrecioTotal.setCellValueFactory(new PropertyValueFactory<>("precioTotal"));
            tbCarrito.getColumns().setAll(cNombre, cCantidadCarrito, cPrecioTotal);
        }
    }

    /**
     * Genera un código único para identificar un pedido.
     * Este método utiliza la hora actual y un valor aleatorio para asegurar la unicidad.
     *
     * @return Un código de pedido único.
     */
    public String generateUniqueCode() {
        var timestamp = System.currentTimeMillis();
        var randomValue = (int) (Math.random() * 1000);
        var uniqueCode = "PEDIDO_" + timestamp + "_" + randomValue;
        return uniqueCode;
    }

    /**
     * Maneja el evento de clic en la opción de menú "Cerrar sesión".
     * Este método cambia la vista a la pantalla de inicio de sesión.
     *
     * @param actionEvent El evento que desencadena esta acción.
     */
    @javafx.fxml.FXML
    public void onLogoutClick(ActionEvent actionEvent) {
        try {
            App.changeScene("ventanaLogin.fxml", "Login");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Maneja el evento de clic en la opción de menú "Cerrar".
     * Este método cierra la aplicación.
     *
     * @param actionEvent El evento que desencadena esta acción.
     */
    @javafx.fxml.FXML
    public void onCloseClick(ActionEvent actionEvent) {
        var stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lbNombreUsuario.setText(Session.getUser().getNombre());
        loadNombresProductosIntoComboBox();
        loadPedidosUsuario();
        chageSceneToItemsPedidos();
    }

    /**
     * Cambia la escena a la ventana de gestión de ítems de pedidos.
     * Este método se activa al seleccionar un pedido de la tabla de pedidos.
     */
    private void chageSceneToItemsPedidos() {
        tbPedidos.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                var pedidoSeleccionado = (Pedido) newValue;
                var codigoPedido = pedidoSeleccionado;
                Session.setPedido(codigoPedido); // Guardar el código del pedido en la sesión
                try {
                    App.changeScene("ventanaItemPedido.fxml", "Items del Pedido " + codigoPedido);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * Carga los pedidos del usuario actual y los muestra en la tabla.
     * Este método llena la tabla de pedidos con los pedidos realizados por el usuario.
     */
    private void loadPedidosUsuario() {
        var pedidos = pedidoDAO.findByUsuarioId(Math.toIntExact(Session.getUser().getId()));

        cCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        cFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        cTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        // Asegúrate de que la lista de pedidos no esté vacía
        if (!pedidos.isEmpty()) {
            tbPedidos.setItems(FXCollections.observableArrayList(pedidos));
        } else {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error pedidos");
            alert.setHeaderText(null);
            alert.setContentText("Tabla vacia.");
            alert.showAndWait();
        }
    }

    /**
     * Actualiza la etiqueta de precio con el precio del producto seleccionado.
     * Este método se llama cuando el usuario selecciona un producto del ComboBox.
     *
     * @param producto El producto seleccionado.
     */
    private void loadPrecioProductosIntoLabel(Producto producto){
        if (producto != null) {
            lbPrecio.setText(String.valueOf(producto.getPrecio()));
        } else {
            lbPrecio.setText("$0.00");
        }
    }

    /**
     * Carga los nombres de los productos en el ComboBox de la interfaz de usuario.
     * Este método se utiliza para llenar la lista desplegable con los nombres de los productos disponibles.
     */
    private void loadNombresProductosIntoComboBox() {
        var productos = productoDAO.getAll();

        if (productos == null || productos.isEmpty()) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Carga");
            alert.setHeaderText(null);
            alert.setContentText("No se pudieron cargar los productos. Por favor, verifica la conexión con la base de datos.");
            alert.showAndWait();
            return;
        }

        List<String> nombreProductos = new ArrayList<>();
        for (var producto : productos) {
            nombreProductos.add(producto.getNombre());
        }
        cbItem.getItems().addAll(nombreProductos);
        listenerProductoSeleccionado(productos);
    }


    /**
     * Agrega un listener al ComboBox de productos.
     * Este listener actualiza la interfaz de usuario cuando se selecciona un producto diferente.
     *
     * @param productos Lista de productos disponibles para selección.
     */
    private void listenerProductoSeleccionado(List<Producto> productos) {
        cbItem.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            var selectedProducto = productos.stream()
                    .filter(producto -> producto.getNombre().equals(newValue))
                    .findFirst()
                    .orElse(null);
            if (selectedProducto != null) {
                updateCantidadComboBox(selectedProducto);
                loadPrecioProductosIntoLabel(selectedProducto);
            }
        });
    }

    /**
     * Actualiza el ComboBox de cantidades basado en la disponibilidad del producto seleccionado.
     * Este método rellena el ComboBox de cantidades con números desde 1 hasta la cantidad disponible del producto.
     *
     * @param producto El producto seleccionado en el ComboBox.
     */
    private void updateCantidadComboBox(Producto producto) {
        cbCantidad.getItems().clear();
        if (producto != null) {
            for (var i = 1; i <= producto.getCantidadDisponible(); i++) {
                cbCantidad.getItems().add(i);
            }
        }
    }
}