package io.oasp.gastronomy.restaurant.usermanagement.service.api.rest;

import java.util.List;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.oasp.gastronomy.restaurant.general.common.api.RestService;
import io.oasp.gastronomy.restaurant.tablemanagement.logic.api.Tablemanagement;
import io.oasp.gastronomy.restaurant.usermanagement.logic.api.to.UserEto;

/**
 * The service class for REST calls in order to execute the methods in {@link Tablemanagement}.
 *
 */
@Path("/usermanagement/v1")
@Named("UsermanagementRestService")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface UsermanagementRestService extends RestService {

  @GET
  @Path("/user/")
  List<UserEto> getUsers();

  @POST
  @Path("/user/")
  void addUser(UserEto user);

  @POST
  @Path("/user/{uid}/groups")
  public void delegateGroupsToUser(@PathParam("uid") String uid);
}
