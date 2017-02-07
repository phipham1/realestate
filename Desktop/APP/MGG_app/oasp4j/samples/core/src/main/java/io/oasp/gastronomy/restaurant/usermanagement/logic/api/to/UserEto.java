package io.oasp.gastronomy.restaurant.usermanagement.logic.api.to;

import io.oasp.gastronomy.restaurant.tablemanagement.common.api.Table;
import io.oasp.gastronomy.restaurant.usermanagement.common.api.User;
import io.oasp.module.basic.common.api.to.AbstractEto;

/**
 * {@link AbstractEto ETO} for {@link Table}.
 *
 */
public class UserEto extends AbstractEto implements User {

  private static final long serialVersionUID = 1L;

  private String userId;

  private String username;

  private String firstName;

  private String lastName;

  private String password;

  private String role;

  /**
   * The constructor.
   */
  public UserEto() {

    super();
  }

  /**
   * @return userId
   */
  public String getUserId() {

    return this.userId;
  }

  /**
   * @param userId new value of {@link #getuserId}.
   */
  public void setUserId(String userId) {

    this.userId = userId;
  }

  /**
   * @return firstName
   */
  public String getFirstName() {

    return this.firstName;
  }

  /**
   * @param firstName new value of {@link #getfirstName}.
   */
  public void setFirstName(String firstName) {

    this.firstName = firstName;
  }

  /**
   * @return lastName
   */
  public String getLastName() {

    return this.lastName;
  }

  /**
   * @param lastName new value of {@link #getlastName}.
   */
  public void setLastName(String lastName) {

    this.lastName = lastName;
  }

  /**
   * @return role
   */
  public String getRole() {

    return this.role;
  }

  /**
   * @param role new value of {@link #getrole}.
   */
  public void setRole(String role) {

    this.role = role;
  }

  /**
   * @return username
   */
  @Override
  public String getUsername() {

    return this.username;
  }

  /**
   * @return password
   */
  public String getPassword() {

    return this.password;
  }

  /**
   * @param password new value of {@link #getpassword}.
   */
  public void setPassword(String password) {

    this.password = password;
  }

  /**
   * @param username new value of {@link #getusername}.
   */
  @Override
  public void setUsername(String username) {

    this.username = username;
  }

}
