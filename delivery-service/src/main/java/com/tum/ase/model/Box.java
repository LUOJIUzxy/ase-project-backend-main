package com.tum.ase.model;

import com.mongodb.lang.NonNull;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document("boxes")
public class Box {
	@Id
	private String id;

	@Indexed(unique = true, sparse = true)
	@NonNull
	private String name;
	@NonNull
	private String addr;
	@NonNull
	@Indexed(unique = true, sparse = true)
	private String rfid;
	@NonNull
	private int boxStatus;

	private List<String> orderIds;

	public Box(String name, String addr, String rfid) {
		this.name = name;
		this.addr = addr;
		this.rfid = rfid;
		this.boxStatus = 0;
		this.orderIds = new ArrayList<>();
	}

}
