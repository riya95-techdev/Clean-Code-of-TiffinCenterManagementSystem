package com.example.tiffin_center_management.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name="admin")
@Data
@EqualsAndHashCode(callSuper = true) // Lombok fix for inheritance
@DiscriminatorValue("ADMIN")
public class Admin extends BaseUser{

}
