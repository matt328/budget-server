package org.matt.budget.rest.common;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Credentials implements Serializable {

  private static final long serialVersionUID = 1L;

  private String userId;
  private String password;
}
