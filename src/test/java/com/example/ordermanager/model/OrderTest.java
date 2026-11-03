package com.example.ordermanager.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Order / OrderItem - Pruebas unitarias del dominio")
class OrderTest {

    @Test
    @DisplayName("Order: nace en PENDING con total 0 y nombre recortado")
    void shouldStartPendingWithZeroTotal() {
        Order order = new Order("  Juan  ");

        assertEquals("Juan", order.getCustomerName());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(BigDecimal.ZERO, order.getTotalAmount());
        assertTrue(order.canBeModified());
    }

    @Test
    @DisplayName("Order: rechaza nombre de cliente vacío o nulo")
    void shouldRejectBlankCustomerName() {
        assertThrows(IllegalArgumentException.class, () -> new Order("  "));
        assertThrows(IllegalArgumentException.class, () -> new Order(null));
    }

    @Test
    @DisplayName("addItem: recalcula el total sumando cantidad x precio")
    void shouldRecalculateTotalWhenAddingItems() {
        Order order = new Order("Ana");

        order.addItem(new OrderItem("Teclado", 2, new BigDecimal("10.50")));
        order.addItem(new OrderItem("Mouse", 1, new BigDecimal("5.00")));

        assertEquals(2, order.getItems().size());
        assertEquals(0, new BigDecimal("26.00").compareTo(order.getTotalAmount()));
    }

    @Test
    @DisplayName("canBeModified: solo los pedidos PENDING se pueden modificar")
    void shouldOnlyAllowModificationWhenPending() {
        Order order = new Order("Luis");
        order.setStatus(OrderStatus.CONFIRMED);

        assertFalse(order.canBeModified());
    }

    @Test
    @DisplayName("OrderItem: calcula el subtotal")
    void shouldCalculateSubtotal() {
        OrderItem item = new OrderItem("Cable", 3, new BigDecimal("2.50"));

        assertEquals(0, new BigDecimal("7.50").compareTo(item.getSubtotal()));
    }

    @Test
    @DisplayName("OrderItem: rechaza datos inválidos")
    void shouldRejectInvalidItems() {
        assertThrows(IllegalArgumentException.class,
                () -> new OrderItem(" ", 1, BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class,
                () -> new OrderItem("X", 0, BigDecimal.ONE));
        assertThrows(IllegalArgumentException.class,
                () -> new OrderItem("X", 1, new BigDecimal("-1")));
        assertThrows(IllegalArgumentException.class,
                () -> new OrderItem("X", 1, null));
    }

    @Test
    @DisplayName("OrderItem: igualdad por valor")
    void shouldCompareItemsByValue() {
        OrderItem a = new OrderItem("X", 1, BigDecimal.TEN);
        OrderItem b = new OrderItem("X", 1, BigDecimal.TEN);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
