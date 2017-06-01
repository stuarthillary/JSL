/*
* Copyright (c) 2007, Manuel D. Rossetti (rossetti@uark.edu)
*
* Contact:
*	Manuel D. Rossetti, Ph.D., P.E. 
*	Department of Industrial Engineering 
*	University of Arkansas 
*	4207 Bell Engineering Center 
*	Fayetteville, AR 72701 
*	Phone: (479) 575-6756 
*	Email: rossetti@uark.edu 
*	Web: www.uark.edu/~rossetti
*
* This file is part of the JSL (a Java Simulation Library). The JSL is a framework
* of Java classes that permit the easy development and execution of discrete event
* simulation programs.
*
* The JSL is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.
*
* The JSL is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with the JSL (see file COPYING in the distribution); 
* if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, 
* Boston, MA  02110-1301  USA, or see www.fsf.org
* 
*/
package jsl.utilities.misc;

import java.util.*;

public class GenericMultiMap extends AbstractMap {
    
  private Map map;

  public GenericMultiMap() {
    this(null);
  }

  public GenericMultiMap(Map copy) {
    map = new HashMap();
    if (copy != null) {
      Iterator iter = copy.entrySet().iterator();
      while(iter.hasNext()) {
        Map.Entry entry = (Map.Entry)iter.next();
        add(entry.getKey(), entry.getValue());
      }
    }
  }

  public boolean containsKey(Object key) {
    Collection values = (Collection)map.get(key);
    return ((values != null) && (values.size() != 0));
  }
    
  public boolean containsValue(Object value) {
    Iterator iter = map.entrySet().iterator();
    boolean found = false;
    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry)iter.next();
      Collection values = (Collection)entry.getValue();
      if (values.contains(value)) {
        found = true;
        break;
      }
    }
    return found;
  }


  public Object get(Object key) {
    return map.get(key);
  }

  public Object put(Object key, Object value) {
    if (!(value instanceof Collection)) {
      throw new IllegalArgumentException(value.getClass().toString());
    }
    Object original = get(key);
    map.put(key, value);
    return original;
  }

  public boolean add(Object key, Object value) {
    return getValues(key).add(value);
  }
    
  public boolean addAll(Object key, Collection values) {
    return getValues(key).addAll(values);
  }

  private Collection getValues(Object key) {
    Collection col = (Collection)map.get(key);
    if (col == null) {
      col = new HashSet();
      map.put(key, col);
    }
    return col;
  }

  public Object remove(Object key) {
    Object original = get(key);
    map.remove(key);
    return original;
  }

  public boolean remove(Object key, Object value) {
    Collection values = (Collection)map.get(key);
    if (values == null) {
      return false;
    } else {
      return values.remove(value);
    }
  }

  public void clear() {
    map.clear();
  }

  public String toString() {
    StringBuffer buff = new StringBuffer();
    buff.append("{");
    Iterator keys = map.keySet().iterator();
    boolean first = true;
    while (keys.hasNext()) {
      if (first) {
        first = false;
      } else {
        buff.append(", ");
      }
      Object key = keys.next();
      Collection values = getValues(key);
      buff.append("[" + key + ": " + values + "]");
    }
    buff.append("}");
    return buff.toString();
  }

  public Set entrySet() {
    int size = 0;
    Iterator iterKeys = map.entrySet().iterator();
    while (iterKeys.hasNext()) {
      Map.Entry entry = (Map.Entry)iterKeys.next();
      Collection values = (Collection)entry.getValue();
      Iterator iterValues = values.iterator();
      while (iterValues.hasNext()) { 
        size++;
        iterValues.next();
      }
    }

    final int finalSize = size;

    final Iterator entries = map.entrySet().iterator();

    return new AbstractSet() {
      int pos = 0;
      Map.Entry entry;
      Iterator values;

      public Iterator iterator() { 
        return new Iterator() {
          public void remove() {
            throw new UnsupportedOperationException();
          }
          public boolean hasNext() {
            return pos != finalSize;
          }
          public Object next() {
            while(true) {
              if (entry == null) {
                entry = (Map.Entry)entries.next();
                values = ((Collection)entry.getValue()).iterator();
              }
              Object key = entry.getKey();
              if (values.hasNext()) {
                Object value = values.next();
                pos++;
                return new Entry(key, value);
              } else {
                entry = null;
              }
            }
          }
        };
      }
      public int size() {
        return finalSize;
      }
    };
  }

  private static class Entry implements Map.Entry {
    Object key;
    Object value;

    Entry(Object key, Object value) {
      this.key = key;
      this.value = value;
    }

    public Object getKey() {
      return key;
    }

    public Object getValue() {
      return value;
    }

    public Object setValue(Object value) {
      Object oldValue = this.value;
      this.value = value;
      return oldValue;
    }

    public boolean equals(Object o) {
      if (!(o instanceof Map.Entry)) {
        return false;
      } else {
        Map.Entry e = (Map.Entry)o;
        return (key==null ? e.getKey()==null : key.equals(e.getKey())) &&
          (value==null ? e.getValue()==null : value.equals(e.getValue()));
      }
    }

    public int hashCode() {
      return ((value==null) ? 0 : value.hashCode());
    }

    public String toString() {
      return key+"="+value;
    }
  }

}