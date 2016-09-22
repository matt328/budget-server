package org.matt.budget.service.encryption;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptTester {
  public static void main(String[] args) {
    String salt = BCrypt.gensalt(12);
    String hashedPassword = BCrypt.hashpw(args[0], salt);

    System.out.println(hashedPassword);

  }
}
