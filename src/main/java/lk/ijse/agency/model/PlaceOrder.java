package lk.ijse.agency.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data

public class PlaceOrder {
    private Order order;

    private List<OrderDetail> odList;
    private List<OrderDetail> stlist;


}
