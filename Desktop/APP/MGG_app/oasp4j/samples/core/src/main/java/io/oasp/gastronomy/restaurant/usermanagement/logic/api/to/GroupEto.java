package io.oasp.gastronomy.restaurant.usermanagement.logic.api.to;

import java.util.List;

/**
 * TODO akoglin This type ...
 *
 * @author akoglin
 * @since dev
 */
public class GroupEto {

  private String cn;

  private List<String> memberDns;

  /**
   * @return cn
   */
  public String getCn() {

    return this.cn;
  }

  /**
   * @param cn new value of {@link #getcn}.
   */
  public void setCn(String cn) {

    this.cn = cn;
  }

  /**
   * @return memberDns
   */
  public List<String> getMemberDns() {

    return this.memberDns;
  }

  /**
   * @param memberDns new value of {@link #getmemberDns}.
   */
  public void setMemberDns(List<String> memberDns) {

    this.memberDns = memberDns;
  }

}
