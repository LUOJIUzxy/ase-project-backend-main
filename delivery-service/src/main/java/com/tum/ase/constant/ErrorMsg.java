package com.tum.ase.constant;

public class ErrorMsg {

    public static String USERNAME_NOT_UNIQUE = "%s: Username already exist";
    public static String USERNAME_EMPTY = "Username cannot be empty";

    public static String EMAIL_NOT_UNIQUE = "%s: Email already exist";
    public static String EMAIL_EMPTY = "Email cannot be empty";

    public static String PASSWORD_EMPTY = "Password cannot be empty";

    public static String TOKEN_NOT_UNIQUE = "Token already exist";
    public static String TOKEN_EMPTY = "Token cannot be empty";

    public static String TOKEN_MALFORMED = "Token should be 8 characters long, each character is a letter or digit";

    public static String ROLE_NOT_LEGAL = "%s: Role is not legal";

    public static String BOX_NAME_NOT_UNIQUE = "%s: Box name already exist";
    public static String BOX_NAME_EMPTY = "Box name cannot be empty";

    public static String ADDRESS_EMPTY = "Box address cannot be empty";

    public static String QR_NOT_UNIQUE = "QR already exist";
    public static String QR_EMPTY = "QR cannot be empty";

    public static String TRACK_CODE_NOT_UNIQUE = "Track code already exist";

    public static String TRACK_CODE_EMPTY = "Track code cannot be empty";

    public static String HAS_BEEN_ASSIGNED = "The box has already been assigned to another customer";

    public static String ORDER_STATUS_ILLEGAL = "Cannot update status because the current status is not %s";

    public static String DELIVERER_ILLEGAL = "Deliverer is not assigned to this order";

    public static String USER_HAS_BEEN_ASSIGNED = "The user %s has been assigned to an active order, only free user can be updated or deleted";

    public static String BOX_HAS_BEEN_ASSIGNED = "The box %s has been assigned to an active order, only free box can be updated or deleted";

    public static String ORDER_HAS_STARTED = "The order is already %s, cannot change the deliverer/customer/box";

    public static String ORDER_HAS_FINISHED = "The order is finished, cannot change the deliverer/customer/box";

    public static String USER_NOT_FOUND = "User not found";

    public static String DELETE_USER_NOT_FOUND = "Delete operation failed due to some users not found";

    public static String DELETE_ORDER_NOT_FOUND = "Delete operation failed due to some orders not found";

    public static String DELETE_BOX_NOT_FOUND = "Delete operation failed due to some boxes not found";

    public static String BOX_NOT_FOUND = "Box not found";

    public static String ORDER_NOT_FOUND = "Order not found";

    public static String FAIL_TO_UPDATE_CREDENTIAL = "Fail to update authentication credential";

    public static String ORDER_STATUS_IS_NOT_ORDERED = "You can just pick up a delivery in Ordered status!";
}
