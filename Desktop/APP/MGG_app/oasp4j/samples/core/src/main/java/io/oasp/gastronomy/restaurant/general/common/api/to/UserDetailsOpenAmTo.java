package io.oasp.gastronomy.restaurant.general.common.api.to;

import io.oasp.gastronomy.restaurant.general.common.api.UserProfile;
import io.oasp.gastronomy.restaurant.general.common.api.datatype.Role;
import io.oasp.module.basic.common.api.to.AbstractTo;

/**
 * This is the {@link AbstractTo TO} for the client view on the user details.
 *
 */
public class UserDetailsOpenAmTo extends AbstractTo implements UserProfile {

  /** UID for serialization. */
  private static final long serialVersionUID = 1L;

  private String userId;

  private String name;

  private String firstName;

  private String lastName;

  private Role role;

  /**
   * The constructor.
   */
  public UserDetailsOpenAmTo() {

    super();
  }

  @Override
  public Long getId() {

    return 0L;
  }

  /**
   * @return userId
   */
  public String getUserId() {

    return this.userId;
  }

  @Override
  public String getName() {

    return this.name;
  }

  @Override
  public String getFirstName() {

    return this.firstName;
  }

  @Override
  public String getLastName() {

    return this.lastName;
  }

  @Override
  public Role getRole() {

    return this.role;
  }

  /**
   * @param userId new value of {@link #getuserId}.
   */
  public void setUserId(String userId) {

    this.userId = userId;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {

    this.name = name;
  }

  /**
   * @param firstName the firstName to set
   */
  public void setFirstName(String firstName) {

    this.firstName = firstName;
  }

  /**
   * @param lastName the lastName to set
   */
  public void setLastName(String lastName) {

    this.lastName = lastName;
  }

  /**
   * @param role the role to set
   */
  public void setRole(Role role) {

    this.role = role;
  }

}
