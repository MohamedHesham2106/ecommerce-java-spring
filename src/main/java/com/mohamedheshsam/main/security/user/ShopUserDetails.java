package com.mohamedheshsam.main.security.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.mohamedheshsam.main.models.User;
import com.mohamedheshsam.main.enums.RoleType;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class ShopUserDetails implements UserDetails {
  private Long id;
  private String email;
  private String password;

  private Collection<GrantedAuthority> authorities;

  public static ShopUserDetails buildUserDetails(User user) {
    GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().name());
    return new ShopUserDetails(
        user.getId(),
        user.getEmail(),
        user.getPassword(),
        List.of(authority));
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return UserDetails.super.isAccountNonExpired();
  }

  @Override
  public boolean isAccountNonLocked() {
    return UserDetails.super.isAccountNonLocked();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return UserDetails.super.isCredentialsNonExpired();
  }

  @Override
  public boolean isEnabled() {
    return UserDetails.super.isEnabled();
  }
}
