package com.tum.ase.service;

import com.tum.ase.constant.ErrorMsg;
import com.tum.ase.constant.OrderStatus;
import com.tum.ase.constant.UserRole;
import com.tum.ase.exception.box.HasBeenAssignedException;
import com.tum.ase.exception.notfound.*;
import com.tum.ase.exception.order.IllegalOrderStatusException;
import com.tum.ase.exception.order.IllegalPickupException;
import com.tum.ase.exception.order.IllegalQrCodeException;
import com.tum.ase.exception.order.IllegalTrackCodeException;
import com.tum.ase.model.*;
import com.tum.ase.repo.BoxRepository;
import com.tum.ase.repo.OrderRepository;
import com.tum.ase.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BoxRepository boxRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll(Sort.by(Sort.Direction.ASC, "_id"));
    }

    public Order getOrderById(String id) {
        Order order = orderRepository.findOrderById(id);
        if (order == null) {
            throw new OrderNotFoundException();
        }
        return order;
    }

    public List<Order> getOrderListByCustomerId(String id) {
        AppUser user = userRepository.findAppUserById(id);
        if (user == null || !user.getRole().equals(UserRole.CUSTOMER)) {
            throw new CustomerNotFoundException();
        }

        List<Order> orders = orderRepository.findOrdersByCustomerId(user.getId());
        return orders;
    }

    public List<Order> getOrderListByDelivererId(String id) {
        AppUser user = userRepository.findAppUserById(id);
        if (user == null || !user.getRole().equals(UserRole.DELIVERER)) {
            throw new DelivererNotFoundException();
        }

        List<Order> orders = orderRepository.findOrdersByDelivererId(user.getId());
        return orders;
    }
    public Order addOrder(String boxName, Order order) {
        Box box = boxRepository.findBoxByName(boxName);
        if (box == null) {
            throw new BoxNotFoundException();
        }

        AssignUser assignCustomer = checkCustomerLegal(order.getCustomer(), box);
        AssignUser assignDeliverer = checkDelivererLegal(order.getDeliverer());
        String qrCode = checkQRCodeLegal(order.getQrCode());
        String trackCode = checkTrackCodeLegal(order.getTrackCode());
        AssignBox assignBox = new AssignBox(box.getId(), box.getName(), box.getAddr(), box.getRfid(), box.getBoxStatus());

        Order saved = new Order(assignCustomer, assignDeliverer, assignBox, qrCode, trackCode);
        orderRepository.save(saved);

        box.getOrderIds().add(saved.getId());
        boxRepository.save(box);

        return saved;
    }

    public Order updateOrder(String id, Order order) {
        Order found = orderRepository.findOrderById(id);
        if (found == null) {
            throw new OrderNotFoundException();
        }

        // qrCode, trackCode, box, deliverer, customer changed only allowed for Ordered status
        if (found.getStatus().equals(OrderStatus.Ordered)) {
            if (order.getQrCode() != null && !order.getQrCode().equals(found.getQrCode())) {
                String qrCode = checkQRCodeLegal(order.getQrCode());
                found.setQrCode(qrCode);
            }

            if (order.getDeliverer() != null && !order.getDeliverer().getEmail().equals(found.getDeliverer().getEmail())) {
                AssignUser deliverer = checkDelivererLegal(order.getDeliverer());
                found.setDeliverer(deliverer);
            }

            String foundBoxName = found.getBox().getName();
            Box foundBox = boxRepository.findBoxByName(foundBoxName);
            // if box are updated
            Box targetBox = null;
            if (order.getBox() != null && !order.getBox().getName().equals(found.getBox().getName())) {
                String targetBoxName = order.getBox().getName();
                targetBox = boxRepository.findBoxByName(targetBoxName);
                AssignBox assignBox = new AssignBox(targetBox.getId(), targetBox.getName(), targetBox.getAddr(), targetBox.getRfid(), targetBox.getBoxStatus());
                if (targetBox == null) {
                    throw new BoxNotFoundException();
                }

                AssignUser assignCustomer = found.getCustomer();
                if (order.getCustomer() != null && !order.getCustomer().getEmail().equals(found.getCustomer().getEmail())) {
                    // if customer is not changed
                    assignCustomer = order.getCustomer();
                }
                assignCustomer = checkCustomerLegal(assignCustomer, targetBox);
                found.setCustomer(assignCustomer);
                found.setBox(assignBox);
            } else {
                // box are not updated
                if (order.getCustomer() != null && !order.getCustomer().getId().equals(found.getCustomer().getId())) {
                    AssignUser assignCustomer = order.getCustomer();
                    if (foundBox == null) {
                        throw new BoxNotFoundException();
                    }

                    assignCustomer = checkCustomerLegal(assignCustomer, foundBox);
                    found.setCustomer(assignCustomer);
                }
            }

            // at last, update changed at
            found.setChangedAt(new Date());
            orderRepository.save(found);

            // if box change, move the orderId from foundBox to targetBox
            if (targetBox != null) {
                foundBox.getOrderIds().remove(id);
                targetBox.getOrderIds().add(id);
                boxRepository.save(foundBox);
                boxRepository.save(targetBox);
            }
            return found;
        } else {
            throw new IllegalOrderStatusException(String.format(ErrorMsg.ORDER_STATUS_ILLEGAL, "Ordered"));
        }
    }

    public void deleteOrders(List<String> orderIds) {
        List<Order> orders = new ArrayList<>();
        orderRepository.findAllById(orderIds).forEach(orders::add);

        if (orders.size() != orderIds.size()) {
            // some orders are not found
            throw new OrderNotFoundException(ErrorMsg.DELETE_ORDER_NOT_FOUND);
        }

        // the orders could belong to different boxes, construct the map
        HashMap<String, List<String>> boxesAndOrders = new HashMap<>();
        for (Order order : orders) {
            String boxId = order.getBox().getId();
            if (boxId == null) {
                throw new BoxNotFoundException();
            }
            if (boxesAndOrders.containsKey(boxId)) {
                boxesAndOrders.get(boxId).add(order.getId());
            } else {
                List<String> ids = new ArrayList<>();
                ids.add(order.getId());
                boxesAndOrders.put(boxId, ids);
            }
        }

        // for each box, do update
        List<Box> toBeUpdated = new ArrayList<>();
        for (String boxId : boxesAndOrders.keySet()) {
            Box box = boxRepository.findBoxById(boxId);
            List<String> ids = boxesAndOrders.get(boxId);
            if (box == null) {
                throw new BoxNotFoundException();
            }

            box.getOrderIds().removeAll(ids);
            toBeUpdated.add(box);
        }

        // if no exception, perform final delete
        for (Box box : toBeUpdated) {
            boxRepository.save(box);
        }
        orderRepository.deleteAllById(orderIds);
    }

    public Order pickUpOrder(String deliveredId, String orderId) {
        Order order = orderRepository.findOrderById(orderId);
        if (order == null) {
            throw new OrderNotFoundException();
        } else if (!order.getStatus().equals(OrderStatus.Ordered)) {
            throw new IllegalPickupException(String.format(ErrorMsg.ORDER_STATUS_ILLEGAL, "Ordered"));
        }

        AppUser deliverer = userRepository.findAppUserById(deliveredId);
        if (deliverer == null || !deliverer.getRole().equals(UserRole.DELIVERER)) {
            throw new DelivererNotFoundException();
        }
        if (!order.getDeliverer().getId().equals(deliverer.getId())) {
            throw new IllegalPickupException(ErrorMsg.DELIVERER_ILLEGAL);
        }
        if (order.getStatus() != OrderStatus.Ordered) {
            throw new IllegalPickupException(ErrorMsg.ORDER_STATUS_IS_NOT_ORDERED);
        }
        order.setStatus(OrderStatus.PickedUp);
        order.setChangedAt(new Date());
        orderRepository.save(order);
        return order;
    }

    private AssignUser checkCustomerLegal(AssignUser assignCustomer, Box box) {
        // 1. Order must be assigned with a customer
        if (assignCustomer == null) {
            throw new CustomerNotFoundException();
        }
//        AppUser customer = userRepository.findAppUserById(assignCustomer.getId());
        AppUser customer = userRepository.findAppUserByEmail(assignCustomer.getEmail());
        if (customer == null || !customer.getRole().equals(UserRole.CUSTOMER)) {
            throw new CustomerNotFoundException();
        }
        assignCustomer = new AssignUser(customer.getId(), customer.getEmail(), customer.getUsername(), customer.getRole(), customer.getToken());

        // 3. A box can only contain one or multiple deliveries for one customer
        List<String> orderIds = box.getOrderIds();
        if (orderIds.size() == 0) {
            return assignCustomer;
        } else {
            List<Order> orders = new ArrayList<>();
            orderRepository.findAllById(box.getOrderIds()).forEach(orders::add);
            if (orders.size() > 0) {
                AssignUser boxCustomer = orders.get(0).getCustomer();
                if (!boxCustomer.getId().equals(assignCustomer.getId())) {
                    throw new HasBeenAssignedException(ErrorMsg.HAS_BEEN_ASSIGNED);
                }
            }
        }
        return assignCustomer;
    }

    private AssignUser checkDelivererLegal (AssignUser assignDeliverer) {
        // 1. Order must be assigned with a deliverer
        if (assignDeliverer == null) {
            throw new DelivererNotFoundException();
        }
        //AppUser deliverer = userRepository.findAppUserById(assignDeliverer.getId());
        AppUser deliverer = userRepository.findAppUserByEmail(assignDeliverer.getEmail());
        if (deliverer == null || !deliverer.getRole().equals(UserRole.DELIVERER)) {
            throw new DelivererNotFoundException();
        }
        assignDeliverer = new AssignUser(deliverer.getId(), deliverer.getEmail(), deliverer.getUsername(), deliverer.getRole(), deliverer.getToken());

        return assignDeliverer;
    }
    private String checkQRCodeLegal (String qrCode) throws IllegalQrCodeException {
        if (qrCode == null || qrCode.isEmpty() || qrCode.isBlank()) {
            throw new IllegalQrCodeException(ErrorMsg.QR_EMPTY);
        }

        Order order = orderRepository.findOrderByQrCode(qrCode);
        if (order != null) {
            throw new IllegalQrCodeException(ErrorMsg.QR_NOT_UNIQUE);
        }
        return qrCode;
    }

    private String checkTrackCodeLegal (String trackCode) throws IllegalTrackCodeException {
        if (trackCode == null || trackCode.isEmpty() || trackCode.isBlank()) {
            throw new IllegalTrackCodeException(ErrorMsg.TRACK_CODE_EMPTY);
        }

        Order order = orderRepository.findOrderByTrackCode(trackCode);
        if (order != null) {
            throw new IllegalTrackCodeException(ErrorMsg.TRACK_CODE_NOT_UNIQUE);
        }
        return trackCode;
    }

    /**
     * Call By Box
     */
    public List<Order> findAllOrdersInBoxByUserToken(String boxRfid, String userToken) {
        Box box = boxRepository.findBoxByRfid(boxRfid);
        if (box == null) {
            throw new BoxNotFoundException();
        }

        List<Order> orders = new ArrayList<>();
        if (userToken != null) {
            AppUser user = userRepository.findAppUserByToken(userToken);
            if (user == null) {
                throw new UserNotFoundException(ErrorMsg.USER_NOT_FOUND);
            }
            if (user.getRole().equals(UserRole.DELIVERER)) {
                orders = orderRepository.findOrdersByBoxTokenAndDelivererToken(boxRfid, userToken);
                // deliverer can only open box with "Pick-up" status
                orders = orders.stream().filter(order -> order.getStatus().equals(OrderStatus.PickedUp)).collect(Collectors.toList());
            } else if (user.getRole().equals(UserRole.CUSTOMER)) {
                orders = orderRepository.findOrdersByBoxTokenAndCustomerToken(boxRfid, userToken);
                // deliverer can only open box with "Delivered" status
                orders = orders.stream().filter(order -> order.getStatus().equals(OrderStatus.Delivered)).collect(Collectors.toList());
            }
        } else {
            orders = orderRepository.findOrdersByBoxId(box.getId());
        }

        return orders;
    }

    public List<Order> updateOrderStatusByBox(String boxRfid, String userToken) {
        List<Order> orders = this.findAllOrdersInBoxByUserToken(boxRfid, userToken);
        AppUser user = userRepository.findAppUserByToken(userToken);
        List<Order> toBeUpdated = new ArrayList<>();
        for (Order order: orders) {
            if (user.getRole().equals(UserRole.DELIVERER) && order.getStatus().equals(OrderStatus.PickedUp)) {
                order.setStatus(OrderStatus.Delivered);
                order.setChangedAt(new Date());
                toBeUpdated.add(order);
            } else if (user.getRole().equals(UserRole.CUSTOMER) && order.getStatus().equals(OrderStatus.Delivered)) {
                order.setStatus(OrderStatus.Finished);
                order.setChangedAt(new Date());
                toBeUpdated.add(order);
            }
        }

        if (toBeUpdated.size() > 0) {
            orderRepository.saveAll(toBeUpdated);
        }
        return toBeUpdated;
    }
}
