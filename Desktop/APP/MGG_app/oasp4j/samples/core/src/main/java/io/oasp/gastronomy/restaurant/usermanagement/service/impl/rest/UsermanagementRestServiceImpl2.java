package io.oasp.gastronomy.restaurant.usermanagement.service.impl.rest;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.oasp.gastronomy.restaurant.usermanagement.logic.api.to.UserEto;
import io.oasp.gastronomy.restaurant.usermanagement.service.api.rest.UsermanagementRestService;

/**
 * TODO akoglin This type ...
 *
 * @author akoglin
 * @since dev
 */
// @Named("UsermanagementRestService")
public class UsermanagementRestServiceImpl2 implements UsermanagementRestService {

  private static final Logger LOG = LoggerFactory.getLogger(UsermanagementRestServiceImpl2.class);

  @Override
  public List<UserEto> getUsers() {

    LOG.warn("SEARCHING");

    ArrayList<UserEto> users = new ArrayList<>();

    UserEto user = new UserEto();
    user.setUserId("bla");
    user.setUsername("blub");
    user.setFirstName("blib");
    user.setLastName("quip");
    // user.setRole(attrs.get("isMemerOf").toString());
    users.add(user);

    return users;
  }

  @Override
  public void addUser(UserEto user) {

    LOG.warn("ADDING");

  }

  @Override
  @SuppressWarnings("javadoc")
  public void delegateGroupsToUser(String _uid) {

    LOG.warn("DELEGATING");

  }

}
