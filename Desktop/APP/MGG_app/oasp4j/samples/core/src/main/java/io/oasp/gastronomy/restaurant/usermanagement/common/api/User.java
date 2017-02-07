package io.oasp.gastronomy.restaurant.usermanagement.common.api;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import io.oasp.gastronomy.restaurant.general.common.api.ApplicationEntity;
import io.oasp.gastronomy.restaurant.tablemanagement.common.api.datatype.TableState;

/**
 * This is the interface for a table of the restaurant. It has a unique {@link #getNumber() number} can be
 * {@link TableState#isReserved() reserved}, {@link TableState#isOccupied() occupied} and may have a
 * {@link #getWaiterId() waiter} assigned.
 *
 */
public interface User extends ApplicationEntity {

  @NotNull
  @Min(0)
  String getUsername();

  void setUsername(String username);

}
