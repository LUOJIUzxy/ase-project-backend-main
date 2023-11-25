package com.tum.ase.service;

import com.tum.ase.constant.ErrorMsg;
import com.tum.ase.constant.OrderStatus;
import com.tum.ase.constant.UserRole;
import com.tum.ase.exception.notfound.BoxNotFoundException;
import com.tum.ase.exception.box.AddressIllegalException;
import com.tum.ase.exception.box.HasBeenAssignedException;
import com.tum.ase.exception.box.IllegalBoxNameException;
import com.tum.ase.exception.box.IllegalRfidException;
import com.tum.ase.exception.user.FailUpdateCredentialException;
import com.tum.ase.exception.user.IllegalTokenException;
import com.tum.ase.model.AppUserCredential;
import com.tum.ase.model.AssignBox;
import com.tum.ase.model.Box;

import com.tum.ase.model.Order;
import com.tum.ase.repo.BoxRepository;
import com.tum.ase.repo.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoxService {

	//@Autowired
	private final BoxRepository boxRepository;

    private final OrderRepository orderRepository;

    private final AuthService authService;

    private final OrderService orderService;

    @Autowired
    public BoxService(BoxRepository boxRepository, OrderRepository orderRepository, AuthService authService, OrderService orderService) {
        this.boxRepository = boxRepository;
        this.orderRepository = orderRepository;
        this.authService = authService;
        this.orderService = orderService;
    }

    public List<Box> getAllBoxes(){
        return boxRepository.findAll(Sort.by(Sort.Direction.ASC, "_id"));
    }

    public List<Box> getAllBoxesAssignedToDeliverer(String userId) {
        List<Order> ordersAssignedToDeliverer = orderService.getOrderListByDelivererId(userId);
        List<Box> boxes = new ArrayList<>();
        for (Order order : ordersAssignedToDeliverer) {
            AssignBox assignBox = order.getBox();
            Box box = boxRepository.findBoxById(assignBox.getId());
            if (box == null) {
                throw new BoxNotFoundException();
            }
            boolean contain = boxes.stream().filter(b -> b.getId().equals(box.getId())).findFirst().isPresent();
            if (!contain) {
                boxes.add(box);
            }
        }
        return boxes;
    }

    public Box saveBox(Box box, String jwt) throws IllegalBoxNameException, IllegalRfidException, AddressIllegalException {
        String name = checkNameLegal(box.getName());
        String address = checkAddressLegal(box.getAddr());
        String rfid = checkRfidLegal(box.getRfid());

        Box saved = new Box(name, address, rfid);
        // save box credential to authentication service
        AppUserCredential credential = authService.saveUserCredential(new AppUserCredential(box.getRfid(), box.getRfid(), "VeryStrongPassword", UserRole.BOX), jwt);
        if (credential != null) {
            // make sure the same user in two db has the same id
            saved.setId(credential.getId());
            boxRepository.save(saved);
            return saved;
        } else {
            throw new FailUpdateCredentialException(ErrorMsg.FAIL_TO_UPDATE_CREDENTIAL);
        }
    }

    public Box getBoxById(String id) {
        Box box = boxRepository.findBoxById(id);
        if (box == null) {
            throw new BoxNotFoundException();
        }
        return box;
    }

    public Box updateBox(String id, Box box, String jwt) throws BoxNotFoundException {
        Box found = boxRepository.findBoxById(id);
        if (found == null) {
            throw new BoxNotFoundException();
        }

        // if this box is assigned to an active order, does not allow to update
        checkIfAssignedToActiveOrder(found);

        if (box.getName() != null && !box.getName().equals(found.getName())) {
            String name = checkNameLegal(box.getName());
            found.setName(name);
        }

        // street address is fixed
//        if (box.getAddr() != null && !box.getAddr().equals(found.getAddr())) {
//            String address = checkAddressLegal(box.getAddr());
//            found.setAddr(address);
//        }

        if (box.getRfid() != null && !box.getRfid().equals(found.getRfid())) {
            String rfid = checkRfidLegal(box.getRfid());
            found.setRfid(rfid);
        }
        found.setBoxStatus(box.getBoxStatus());

        // update credential first
//        AppUserCredential credential = authService.updateUserCredential(id,
//                new AppUserCredential(found.getRfid(), found.getRfid(), found.getName(), UserRole.BOX), jwt);
//        if (credential != null) {
//            boxRepository.save(found);
//        } else {
//            throw new FailUpdateCredentialException(ErrorMsg.FAIL_TO_UPDATE_CREDENTIAL);
//        }
        boxRepository.save(found);
        return found;
    }

    public void deleteBoxes(List<String> ids, String jwt) {
        List<Box> boxes = new ArrayList<>();
        boxRepository.findAllById(ids).forEach(boxes::add);

        if (boxes.size() != ids.size()) {
            // some boxes are not found
            throw new BoxNotFoundException(ErrorMsg.BOX_NOT_FOUND);
        }

        for (Box box : boxes) {
            checkIfAssignedToActiveOrder(box);
        }

        authService.deleteUserCredentials(ids, jwt);
        boxRepository.deleteAllById(ids);
    }

    private String checkNameLegal(String name) throws IllegalBoxNameException {
        // box name must not be empty
        if (name == null || name.isEmpty() || name.isBlank()) {
            throw new IllegalBoxNameException(ErrorMsg.BOX_NAME_EMPTY);
        }
        // box name must be unique
        Box found = boxRepository.findBoxByName(name);
        if (found == null) {
            return name;
        } else {
            throw new IllegalBoxNameException(String.format(ErrorMsg.BOX_NAME_NOT_UNIQUE, name));
        }
    }

    private String checkRfidLegal(String rfid) throws IllegalRfidException {
        // rfid must not be empty
        if (rfid == null || rfid.isEmpty() || rfid.isBlank()) {
            throw new IllegalTokenException(ErrorMsg.TOKEN_EMPTY);
        }
        // rfid must be unique
        Box found = boxRepository.findBoxByRfid(rfid);
        if (found == null) {
            return rfid;
        } else {
            throw new IllegalTokenException(String.format(ErrorMsg.TOKEN_NOT_UNIQUE, rfid));
        }
    }

    private String checkAddressLegal(String address) throws AddressIllegalException {
        if (address == null || address.isEmpty() || address.isBlank()) {
            throw new AddressIllegalException(ErrorMsg.ADDRESS_EMPTY);
        }
        return address;
    }

    private void checkIfAssignedToActiveOrder(Box box) {
        List<Order> orders = orderRepository.findOrdersByBoxId(box.getId());
        // find active orders
        orders = orders.stream().filter(order -> !order.getStatus().equals(OrderStatus.Finished)).collect(Collectors.toList());
        if (orders.size() > 0) {
            throw new HasBeenAssignedException(String.format(ErrorMsg.BOX_HAS_BEEN_ASSIGNED, box.getName()));
        }
    }
}
