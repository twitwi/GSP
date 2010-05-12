/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp;

import java.util.Collections;
import java.util.Iterator;

/**
 *
 * @author remonet
 */
public final class Option<What> implements Iterable<What> {

    public static <W> Option<W> create(W whatOrNull) {
        return new Option<W>(whatOrNull);
    }
    private What what;

    private Option(What what) {
        this.what = what;
    }

    public boolean isPresent() {
        return what != null;
    }

    public What get() {
        if (!isPresent()) {
            throw new IllegalStateException("Option");
        }
        return what;
    }

    public What getOr(What alternative) {
        return isPresent() ? what : alternative;
    }

    public Iterator<What> iterator() {
        return isPresent() ? Collections.singleton(what).iterator() : Collections.<What>emptyList().iterator();
    }
}
