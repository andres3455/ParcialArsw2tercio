package edu.eci.arsw.myrestaurant.restcontrollers;

import edu.eci.arsw.myrestaurant.model.Order;
import edu.eci.arsw.myrestaurant.model.RestaurantProduct;
import edu.eci.arsw.myrestaurant.services.OrderServicesException;
import edu.eci.arsw.myrestaurant.services.RestaurantOrderServices;
import edu.eci.arsw.myrestaurant.beans.impl.BasicBillCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * 
 * @author hcadavid
 * Editado por Andrés Rodriguez
 */
@RestController
@RequestMapping("/orders")
public class OrdersAPIController {


    private RestaurantOrderServices ros;

    @Autowired
    private BasicBillCalculator basicBillCalculator;


    @GetMapping(produces = "application/json")
    public ResponseEntity<?> getAllOrders() {
        try {
            List<Order> orders = ros.getTablesWithOrders().stream()
                .map(orderId -> {
                    Order order = ros.getTableOrder(orderId);
                    // Calculamos el total de la cuenta usando BasicBillCalculator
                    Map<String, RestaurantProduct> productsMap = ros.getProductsMap();
                    int totalBill = basicBillCalculator.calculateBill(order, productsMap);
                    
                    order.setTotalBill(totalBill);
                    return order;
                })
                .collect(Collectors.toList());

            // Devolvemos la lista de órdenes con su total en formato JSON
            return ResponseEntity.ok(orders);

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while retrieving orders: " + ex.getMessage());
        }
    }
}
