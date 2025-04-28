package com.mohamedheshsam.main.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;

import com.mohamedheshsam.main.enums.RoleType;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Role {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, unique = true)
  private RoleType name;

  public Role(RoleType name) {
    this.name = name;
  }

  @ManyToMany(mappedBy = "roles")
  private Collection<User> users = new HashSet<>();
}
