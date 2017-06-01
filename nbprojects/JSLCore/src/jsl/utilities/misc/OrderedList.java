/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.utilities.misc;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/** This class encapsulates the holding of Requests for
 *  resources. 
 *
 * @author rossetti
 */
public class OrderedList<T extends Comparable<T>> implements Collection<T> {

    /** Holds requests that are waiting for some
     *  units of the resource
     *
     */
    protected List<T> myList;

    public OrderedList() {
        myList = new LinkedList<T>();
    }

    /** Adds the object to the list
     *  based on the Comparable interface
     *
     * @param obj
     */
    public boolean add(T obj) {
        // nothing in the list, just add to beginning
        if (myList.isEmpty()) {
            return myList.add(obj);
        }

        // might as well check for worse case, if larger than the largest
        // then put it at the end and return
        if (obj.compareTo(myList.get(myList.size() - 1)) >= 0) {
            return myList.add(obj);
        }

        // now iterate through the list
        for (ListIterator<T> i = myList.listIterator(); i.hasNext();) {
            if (obj.compareTo(i.next()) < 0) {
                // next() move the iterator forward, if it is < what was returned by next(), then it
                // must be inserted at the previous index
                myList.add(i.previousIndex(), obj);
                return true;
            }
        }
        return false;
    }

    public int size() {
        return myList.size();
    }

    public T set(int arg0, T arg1) {
        return myList.set(arg0, arg1);
    }

    public T peekNext() {
        if (myList.isEmpty()) {
            return null;
        } else {
            return myList.get(0);
        }
    }

    public T removeNext() {
         if (myList.isEmpty()) {
            return null;
        } else {
             T obj = myList.get(0);
             myList.remove(0);
            return obj;
        }
    }

    public T remove(int arg0) {
        return myList.remove(arg0);
    }

    public boolean remove(T arg0) {
        return myList.remove(arg0);
    }

    public ListIterator<T> listIterator(int arg0) {
        return myList.listIterator(arg0);
    }

    public ListIterator<T> listIterator() {
        return myList.listIterator();
    }

    public int lastIndexOf(T arg0) {
        return myList.lastIndexOf(arg0);
    }

    public Iterator<T> iterator() {
        return myList.iterator();
    }

    public boolean isEmpty() {
        return myList.isEmpty();
    }

    public int indexOf(T arg0) {
        return myList.indexOf(arg0);
    }

    public T get(int arg0) {
        return myList.get(arg0);
    }

    public boolean contains(T arg0) {
        return myList.contains(arg0);
    }

    public void clear() {
        myList.clear();
    }

    public boolean contains(Object arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object[] toArray() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <T> T[] toArray(T[] arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean remove(Object arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean containsAll(Collection<?> arg0) {
        return myList.containsAll(arg0);
    }

    public boolean addAll(Collection<? extends T> arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean removeAll(Collection<?> arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean retainAll(Collection<?> arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
