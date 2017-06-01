/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jsl.utilities.misc;

/** Implementors promise to nullify all internal references
 *  in preparation for garbage collection
 *
 * @author rossetti
 */
public interface Nullifiable {

    void nullify();
    
}
