package io.oasp.gastronomy.restaurant.general.common.impl.security;

import io.oasp.gastronomy.restaurant.general.common.api.security.UserData;

import javax.inject.Named;

import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

/**
 * This class is responsible for providing the principal to the SecurityContext.
 */
@Named("OpenAmAuthenticationProvider")
public class OpenAmAuthenticationProvider extends PreAuthenticatedAuthenticationProvider {

  /**
   * The constructor.
   */
  public OpenAmAuthenticationProvider() {

    super();
    setPreAuthenticatedUserDetailsService(new OpenAmUserDetailsService());
  }

  private class OpenAmUserDetailsService implements
      AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException {

      return (UserData) token.getPrincipal();
    }

  }

}
