package org.matt.budget.service.encryption;

import javax.ejb.Stateless;

import org.mindrot.jbcrypt.BCrypt;

@Stateless
@BCryptEncryption
public class BCryptEncryptionService implements EncryptionService {

  private static final int WORKLOAD = 13;

  /**
   * This method can be used to generate a string representing an account
   * password suitable for storing in a database. It will be an OpenBSD-style
   * crypt(3) formatted hash string of length=60 The bcrypt workload is
   * specified in the above static variable, a value from 10 to 31. A workload
   * of 12 is a very reasonable safe default as of 2013. This automatically
   * handles secure 128-bit salt generation and storage within the hash.
   * 
   * @param passwordPlaintext
   *          The account's plaintext password as provided during account
   *          creation, or when changing an account's password.
   * @return String - a string of length 60 that is the bcrypt hashed password
   *         in crypt(3) format.
   */
  public String hashPassword(String passwordPlaintext) {
    String salt = BCrypt.gensalt(WORKLOAD);
    String hashedPassword = BCrypt.hashpw(passwordPlaintext, salt);

    return (hashedPassword);
  }

  /**
   * This method can be used to verify a computed hash from a plaintext (e.g.
   * during a login request) with that of a stored hash from a database. The
   * password hash from the database must be passed as the second variable.
   * 
   * @param passwordPlaintext
   *          The account's plaintext password, as provided during a login
   *          request
   * @param storedHash
   *          The account's stored password hash, retrieved from the
   *          authorization database
   * @return boolean - true if the password matches the password of the stored
   *         hash, false otherwise
   */
  public boolean checkPassword(String passwordPlaintext, String storedHash) {
    boolean passwordVerified = false;

    if (null == storedHash || !storedHash.startsWith("$2a$"))
      throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");

    passwordVerified = BCrypt.checkpw(passwordPlaintext, storedHash);

    return (passwordVerified);
  }

}
