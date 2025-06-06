
package com.mohamedheshsam.main.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCartItemRequest {
  @NotNull
  @Min(1)
  private Integer quantity;
}
