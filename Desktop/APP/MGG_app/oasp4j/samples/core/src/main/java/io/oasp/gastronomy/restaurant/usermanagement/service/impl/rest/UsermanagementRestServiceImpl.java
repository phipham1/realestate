package io.oasp.gastronomy.restaurant.usermanagement.service.impl.rest;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.inject.Named;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;

import io.oasp.gastronomy.restaurant.general.common.api.security.UserData;
import io.oasp.gastronomy.restaurant.general.common.api.to.UserDetailsOpenAmTo;
import io.oasp.gastronomy.restaurant.usermanagement.logic.api.to.GroupEto;
import io.oasp.gastronomy.restaurant.usermanagement.logic.api.to.UserEto;
import io.oasp.gastronomy.restaurant.usermanagement.service.api.rest.UsermanagementRestService;

/**
 * TODO akoglin This type ...
 *
 * @author akoglin
 * @since dev
 */
@Named("UsermanagementRestService")
public class UsermanagementRestServiceImpl implements UsermanagementRestService {

  private static final Logger LOG = LoggerFactory.getLogger(UsermanagementRestServiceImpl.class);

  @Value("${ldap.address}")
  private String ldapServer; // = "ldap://igovlab-dev:50389";

  boolean loginAsManagerUser = true;

  @Value("${ldap.username}")
  String ldapUsername = "cn=Directory Manager";

  @Value("${ldap.password}")
  String ldapPassword = "password";

  @Override
  public List<UserEto> getUsers() {

    final String ldapSearchBase = "dc=openam,dc=forgerock,dc=org";
    final String ldapSearchQuery = "(objectclass=inetOrgPerson)";
    ArrayList<UserEto> users = new ArrayList<>();

    LOG.warn("SEARCHING BEGAN");

    try {

      NamingEnumeration<?> namingEnum =
          ldapContext().search(ldapSearchBase, ldapSearchQuery, getSimpleSearchControls());

      while (namingEnum.hasMore()) {
        LOG.warn("SEARCH HAS MORE");

        SearchResult result = (SearchResult) namingEnum.next();
        Attributes attrs = result.getAttributes();

        UserEto user = new UserEto();
        user.setUserId(attrs.get("uid") != null ? attrs.get("uid").get().toString() : null);
        user.setUsername(attrs.get("cn") != null ? attrs.get("cn").get().toString() : null);
        user.setFirstName(attrs.get("givenName") != null ? attrs.get("givenName").get().toString() : null);
        user.setLastName(attrs.get("sn") != null ? attrs.get("sn").get().toString() : null);
        // user.setRole(attrs.get("isMemerOf").toString());
        users.add(user);
      }
      namingEnum.close();

    } catch (Exception e) {
      LOG.error("ERROR ", e);
      e.printStackTrace();
    }

    LOG.warn("SEARCHING ENDED");

    return users;
  }

  @Override
  public void addUser(UserEto user) {

    // String uid = "chief3";
    // String gn = "Charly";
    // String sn = "Chief 3";
    // String cn = "Charly Chief 3";
    // String pw = "password";

    String uid = user.getUserId();
    String gn = user.getFirstName();
    String sn = user.getLastName();
    String cn = user.getFirstName() + " " + user.getLastName();
    String pw = user.getPassword();

    String entryDN = "uid=" + uid + ",ou=people,dc=openam,dc=forgerock,dc=org";

    Attribute a_uid = new BasicAttribute("uid", uid);
    Attribute a_cn = new BasicAttribute("cn", cn);
    Attribute a_gn = new BasicAttribute("givenName", gn);
    Attribute a_sn = new BasicAttribute("sn", sn);
    Attribute a_up = new BasicAttribute("userPassword", pw);
    Attribute a_ius = new BasicAttribute("inetUserStatus", "Active");

    Attribute a_clazz = new BasicAttribute("objectClass");
    addAll(a_clazz,
        new String[] { "top", "person", "inetorgperson", "inetuser", "kbaInfoContainer", "iplanet-am-managed-person",
        "sunFMSAML2NameIdentifier", "devicePrintProfilesContainer", "sunIdentityServerLibertyPPService",
        "iplanet-am-user-service", "sunFederationManagerDataStore", "forgerock-am-dashboard-service",
        "oathDeviceProfilesContainer", "sunAMAuthAccountLockout", "organizationalperson", "iPlanetPreferences",
        "iplanet-am-auth-configuration-service" });

    Attributes entry = new BasicAttributes();
    putAll(entry, new Attribute[] { a_uid, a_cn, a_gn, a_sn, a_up, a_ius, a_clazz });

    try {
      ldapContext().createSubcontext(entryDN, entry);
    } catch (NamingException e) {
      e.printStackTrace();
    }
  }

  @Override
  @SuppressWarnings("javadoc")
  public void delegateGroupsToUser(String uid) {

    // füge Nutzer mit uid zu den Gruppen hinzu
    // String uid = "chief3";
    // String role = "Chief";

    String role = currentUser().getRole().getName();

    ArrayList<String> newGroupCns = new ArrayList<>();
    newGroupCns.add(role);

    // rufe bisherige Usergruppen ab
    List<GroupEto> oldGroups = getUserGroups(uid);

    // filtere aus den neuen Gruppen die heraus, die dem User bereits zugeordnet sind
    List<String> addedGroupCns = filteredNewGroups(newGroupCns, oldGroups);

    // speichere den Nutzer an jeder neu hinzugekommenen Gruppe
    for (String addedGroupCn : addedGroupCns) {
      addUserIdToGroupWithCn(uid, addedGroupCn);
    }

  }

  private List<GroupEto> getUserGroups(String uid) {

    final String ldapSearchBase = "dc=openam,dc=forgerock,dc=org";
    final String ldapSearchQuery =
        "(&(objectClass=groupofuniquenames)(uniqueMember=uid=" + uid + ",ou=people,dc=openam,dc=forgerock,dc=org))";

    ArrayList<GroupEto> groups = new ArrayList<>();

    try {

      NamingEnumeration<?> namingEnum =
          ldapContext().search(ldapSearchBase, ldapSearchQuery, getSimpleSearchControls());

      while (namingEnum.hasMore()) {
        GroupEto group = new GroupEto();
        SearchResult result = (SearchResult) namingEnum.next();
        Attributes attrs = result.getAttributes();

        String cn = attrs.get("cn").toString();

        ArrayList<String> memberDns = new ArrayList<>();
        Attribute uniqueMembersAttr = attrs.get("uniqueMember");
        NamingEnumeration<?> uniqueMembersEnum = uniqueMembersAttr.getAll();
        while (uniqueMembersEnum.hasMore()) {
          Object memberDn = uniqueMembersEnum.next();
          memberDns.add(memberDn.toString());
        }

        group.setCn(cn);
        group.setMemberDns(memberDns);
        groups.add(group);
      }
      namingEnum.close();

    } catch (Exception e) {
      e.printStackTrace();
    }

    return groups;

  }

  /**
   * @param newGroupCns
   * @param oldGroups
   * @return
   */
  private List<String> filteredNewGroups(ArrayList<String> newGroupCns, List<GroupEto> oldGroups) {

    ArrayList<String> addedGroupCns = new ArrayList<>();
    for (String newGroupCn : newGroupCns) {
      if (!containsCn(oldGroups, newGroupCn)) {
        addedGroupCns.add(newGroupCn);
      }
    }
    return addedGroupCns;
  }

  /**
   * @param userUid
   * @param addedGroupCn
   */
  private void addUserIdToGroupWithCn(String userUid, String addedGroupCn) {

    String groupDn = "cn=" + addedGroupCn + ",ou=groups,dc=openam,dc=forgerock,dc=org";

    ModificationItem[] mods = new ModificationItem[1];
    mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE,
        new BasicAttribute("uniqueMember", "uid=" + userUid + ",ou=people,dc=openam,dc=forgerock,dc=org"));

    try {
      ldapContext().modifyAttributes(groupDn, mods);
    } catch (NamingException e) {
      e.printStackTrace();
    }
  }

  private void putAll(Attributes entry, Attribute[] attributes) {

    for (Attribute attr : attributes) {
      entry.put(attr);
    }
  }

  private void addAll(Attribute attr, String[] values) {

    for (String value : values) {
      attr.add(value);
    }
  }

  private boolean containsCn(List<GroupEto> groupEtos, String cn) {

    for (GroupEto groupEto : groupEtos) {
      if (cn.equals(groupEto.getCn())) {
        return true;
      }
    }
    return false;
  }

  /**
   * @return
   * @throws NamingException
   */
  private LdapContext ldapContext() throws NamingException {

    LOG.warn("USING LDAP SERVER " + this.ldapServer);

    Hashtable<String, String> env = new Hashtable<>();
    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
    env.put(Context.PROVIDER_URL, this.ldapServer);

    if (!this.loginAsManagerUser) {
      env.put(Context.SECURITY_AUTHENTICATION, "none");
    } else {
      env.put(Context.SECURITY_PRINCIPAL, this.ldapUsername);
      env.put(Context.SECURITY_CREDENTIALS, this.ldapPassword);
    }

    LdapContext ctx = new InitialLdapContext(env, null);
    ctx.setRequestControls(null);

    return ctx;
  }

  private SearchControls getSimpleSearchControls() {

    SearchControls searchControls = new SearchControls();
    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    searchControls.setTimeLimit(30000);
    return searchControls;
  }

  private UserDetailsOpenAmTo currentUser() {

    UserData userData = (UserData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    UserDetailsOpenAmTo userProfile = (UserDetailsOpenAmTo) userData.getUserProfile();
    return userProfile;
  }

}
