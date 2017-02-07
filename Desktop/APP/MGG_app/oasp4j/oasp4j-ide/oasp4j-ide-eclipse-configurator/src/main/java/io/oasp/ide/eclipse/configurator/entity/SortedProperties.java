package io.oasp.ide.eclipse.configurator.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * {@link Properties} that are sorted ascending by their keys.
 * @author trippl
 *
 */
public class SortedProperties extends Properties {

  /**
   * Generated UUID.
   */
  private static final long serialVersionUID = -8046778843992665187L;

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public synchronized Enumeration keys() {

    Enumeration keysEnum = super.keys();
    List keyList = new ArrayList();
    while (keysEnum.hasMoreElements()) {
      keyList.add(keysEnum.nextElement());
    }
    Collections.sort(keyList);
    return Collections.enumeration(keyList);
  }

  /**
   * {@inheritDoc}
   */
  public Set<Object> keySet() {

    SortedSet<Object> set = new TreeSet<Object>(super.keySet());
    return set;
  }

}
