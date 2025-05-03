package com.mohamedheshsam.main.security.user;

import com.mohamedheshsam.main.enums.RoleType;
import com.mohamedheshsam.main.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShopUserDetails implements UserDetails {
  private Long id;
  private String email;
  private String password;
  private RoleType role;
  private Collection<GrantedAuthority> authorities;

  public static ShopUserDetails build(User user) {
    GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
    return new ShopUserDetails(
        user.getId(),
        user.getEmail(),
        user.getPassword(),
        user.getRole(),
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
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
