package com.tum.ase.model;

import com.tum.ase.constant.OrderStatus;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Document("orders")
public class Order {

	@Id
	private String id;

	private OrderStatus status;

	private Date createdAt;

	private Date changedAt;

	@Indexed
	private AssignUser customer;

	@Indexed
	private AssignUser deliverer;

	@Indexed(unique = true)
	private String qrCode;

	@Indexed
	private AssignBox box;

	@Indexed
	private String trackCode;

	public Order(AssignUser customer, AssignUser deliverer, AssignBox box, String qrCode, String trackCode) {
		this.status = OrderStatus.Ordered;
		this.customer = customer;
		this.deliverer = deliverer;
		this.box = box;
		this.qrCode = qrCode;
		this.trackCode = trackCode;
		this.createdAt = new Date();
		this.changedAt = new Date();
	}
}
