package com.tum.ase.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignBox {
    private String id;

    private String name;

    private String addr;

    private String rfid;

    private int boxStatus;
}
