package com.mohamedheshsam.main.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mohamedheshsam.main.enums.Gender;
import com.mohamedheshsam.main.enums.RoleType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private Gender gender;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private Cart cart;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "image_id", nullable = true)
  @OnDelete(action = OnDeleteAction.SET_NULL)
  private Image image;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<Order> orders;

  private RoleType role = RoleType.USER;
}
