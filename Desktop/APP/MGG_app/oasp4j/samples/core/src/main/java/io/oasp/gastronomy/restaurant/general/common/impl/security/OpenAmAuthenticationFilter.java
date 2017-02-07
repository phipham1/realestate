package io.oasp.gastronomy.restaurant.general.common.impl.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import io.oasp.gastronomy.restaurant.general.common.api.UserProfile;
import io.oasp.gastronomy.restaurant.general.common.api.datatype.Role;
import io.oasp.gastronomy.restaurant.general.common.api.security.UserData;
import io.oasp.gastronomy.restaurant.general.common.api.to.UserDetailsOpenAmTo;
import io.oasp.module.security.common.api.accesscontrol.AccessControl;
import io.oasp.module.security.common.api.accesscontrol.AccessControlProvider;
import io.oasp.module.security.common.api.accesscontrol.PrincipalAccessControlProvider;
import io.oasp.module.security.common.base.accesscontrol.AccessControlGrantedAuthority;

/**
 * Pre-Authentication Filter doing IAM (Identity & Access Management) with OpenAM. This Pre-Authentication filter
 * extracts the OpenAM headers from the request and creates a principal that will be used in the Spring SecurityContext.
 *
 * @author akoglin
 */
public class OpenAmAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {

  private static final Role[] VALID_ROLES = { Role.CHIEF, Role.BARKEEPER, Role.COOK, Role.WAITER };

  private static final String AM_HEADER_USERID = "amuserid";

  private static final String AM_HEADER_USERNAME = "amusername";

  private static final String AM_HEADER_USERGROUPS = "amusergroups";

  private static final String AM_HEADER_FIRSTNAME = "amfirstname";

  private static final String AM_HEADER_LASTNAME = "amlastname";

  private static final Logger LOG = LoggerFactory.getLogger(OpenAmAuthenticationFilter.class);

  private PrincipalAccessControlProvider<UserProfile> principalAccessControlProvider;

  private AccessControlProvider accessControlProvider;

  /**
   * The constructor.
   */
  public OpenAmAuthenticationFilter() {

    super();
    this.principalAccessControlProvider = getPrincipalAccessControlProvider();
  }

  @Override
  protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {

    HttpServletRequest httpRequest = request;

    logHeaderAndBody(httpRequest);

    UserDetailsOpenAmTo userProfile = createUserProfile(httpRequest);

    if (userProfile != null) {
      LOG.trace("Pre-Authentication of user: " + userProfile.getUserId() + " (" + userProfile.getRole() + ")");
    }

    UserData principal = createPrincipal(userProfile);

    return principal;
  }

  /**
   * @param httpRequest
   */
  private void logHeaderAndBody(HttpServletRequest httpRequest) {

    Enumeration<String> headerNames = httpRequest.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      LOG.warn("HEADER: " + headerName + ", " + httpRequest.getHeader(headerName));
    }

    // StringBuffer jb = new StringBuffer();
    // String line = null;
    // try {
    // BufferedReader reader = httpRequest.getReader();
    // while ((line = reader.readLine()) != null) {
    // jb.append(line);
    // }
    // LOG.warn("BODY: " + jb.toString());
    // } catch (Exception e) {
    // }
  }

  /**
   * Creates the transport object for this user ("user profile")
   */
  private UserDetailsOpenAmTo createUserProfile(HttpServletRequest httpRequest) {

    Enumeration<String> headerNames = httpRequest.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      String headerValue = httpRequest.getHeader(headerName);
      LOG.trace("Header found: name=" + headerName + " / value=" + headerValue);

    }

    String amuserid = httpRequest.getHeader("amuserid");
    String amfirstname = httpRequest.getHeader("amfirstname");
    String amlastname = httpRequest.getHeader("amlastname");
    String amusername = httpRequest.getHeader("amusername");
    String amusergroups = httpRequest.getHeader("amusergroups");

    // String amfirstname = "Willy";
    // String amlastName = "Waiter";
    // String amuserid = amlastName.toLowerCase();
    // String amusername = amfirstname + " " + amlastName;
    // String amusergroups = "cn=" + amlastName + ",ou=people,dc=openam,dc=forgerock,dc=org"; // ldap groups
    // if (amuserid == null || amusername == null || amusergroups == null) {
    // LOG.debug("No valid OpenAM Pre-Authentication Headers detected");
    // throw new RuntimeException("You are not allowed to access the application");
    // }

    Role amuserrole = getMatchingRole(amusergroups);
    if (amuserrole == null) {
      LOG.warn("The OpenAM Pre-Authentication 'Role' Header did not match a role");
    }

    UserDetailsOpenAmTo userProfile = new UserDetailsOpenAmTo();
    userProfile.setUserId(amuserid);
    userProfile.setName(amusername);
    userProfile.setFirstName(amfirstname);
    userProfile.setLastName(amlastname);
    userProfile.setRole(amuserrole);
    return userProfile;
  }

  /**
   * Creates the principal for the SecurityContext ("user data")
   */
  private UserData createPrincipal(UserDetailsOpenAmTo userProfile) {

    // determine granted authorities for spring-security...
    Set<GrantedAuthority> authorities = new HashSet<>();
    Collection<String> accessControlIds = this.principalAccessControlProvider.getAccessControlIds(userProfile);
    Set<AccessControl> accessControlSet = new HashSet<>();
    for (String id : accessControlIds) {
      boolean success = this.accessControlProvider.collectAccessControls(id, accessControlSet);
      LOG.debug("Adding access control: " + id + " (" + success + ")");
      if (!success) {
        LOG.warn("Undefined access control {}.", id);
      }
    }
    for (AccessControl accessControl : accessControlSet) {
      authorities.add(new AccessControlGrantedAuthority(accessControl));
    }

    UserData userdata = new UserData(userProfile.getUserId(), "", authorities);
    userdata.setUserProfile(userProfile);

    return userdata;
  }

  /**
   * Creates an AccessControlProvider obtaining the right IDs of the user
   */
  private PrincipalAccessControlProvider<UserProfile> getPrincipalAccessControlProvider() {

    return new PrincipalAccessControlProvider<UserProfile>() {

      @Override
      public Collection<String> getAccessControlIds(UserProfile principal) {

        String roleId = null;
        if (principal.getRole() != null) {
          roleId = principal.getRole().getName();
        } else {
          roleId = "Unauthorized";
        }

        return Arrays.asList(roleId);
      }

    };
  }

  private Role getMatchingRole(String ldapGroups) {

    String firstLdapUserGroup = ldapGroups.split("\\|")[0];

    String roleName = findRoleNameFromLdapGroup(firstLdapUserGroup);
    if (roleName == null) {
      return null;
    }

    Role role = null;
    for (Role validRole : VALID_ROLES) {
      if (validRole.getName().equals(roleName)) {
        role = validRole;
        break;
      }
    }

    return role;
  }

  private String findRoleNameFromLdapGroup(String firstLdapUserGroup) {

    String roleId = null, regexp = "cn=(.*?),.*";
    Matcher matcher = Pattern.compile(regexp).matcher(firstLdapUserGroup);
    if (matcher.find()) {
      roleId = matcher.group(1);
    }

    return roleId;
  }

  @Override
  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {

    return "Dummy";
  }

  /**
   * @param accessControlProvider the {@link AccessControlProvider} to {@link Inject}.
   */
  @Inject
  public void setAccessControlProvider(AccessControlProvider accessControlProvider) {

    this.accessControlProvider = accessControlProvider;
  }

}
