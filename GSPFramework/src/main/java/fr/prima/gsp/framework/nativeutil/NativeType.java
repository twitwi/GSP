/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.gsp.framework.nativeutil;

/**
 *
 * @author remonet
 */
public abstract class NativeType {

    //
    public static final NativeType INT = simple("int");
    public static final NativeType FLOAT = simple("float");
    public static final NativeType BOOL = simple("bool");
    public static final NativeType CHAR = simple("char");
    public static final NativeType VOID = simple("void");
    public static final NativeType[] nonPointersTypes = new NativeType[]{INT, FLOAT, BOOL, CHAR, VOID};

    // predefined helpers
    public static final NativeType CHAR_POINTER = pointer(CHAR);
    public static final NativeType VOID_POINTER = pointer(VOID);


    public static NativeType pointer(NativeType toWhat) {
        return pointerTo(toWhat);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NativeType && areSame(this, (NativeType) obj);
    }

    @Override
    public int hashCode() {
        return isPointer() ? 1 + getToWhat().hashCode() : super.hashCode();
    }


    static boolean areSame(NativeType t1, NativeType t2) {
        if (t1 == t2) {
            return true;
        }
        if (t1 == null || !t1.isPointer() || t2 == null || !t2.isPointer()) {
            return false;
        }
        return areSame(t1.getToWhat(), t2.getToWhat());
    }
    // moving specifications...
    static boolean areCloseEnough(NativeType t1, NativeType t2) {
        if (t1 == t2) {
            return true;
        }
        if (t1 == null || !t1.isPointer() || t2 == null || !t2.isPointer()) {
            return false;
        }
        return true;
    }

    // API
    public final boolean isPointer() {
        return isPointerImpl();
    }
    public final NativeType getToWhat() {
        return getToWhatImpl();
    }

    // SPI
    protected abstract boolean isPointerImpl();
    protected abstract NativeType getToWhatImpl();

    //
    private static NativeType simple(final String desc) {
        return new NativeType() {

            @Override
            public String toString() {
                return "simple(" + desc + ")";
            }

            @Override
            protected boolean isPointerImpl() {
                return false;
            }

            @Override
            protected NativeType getToWhatImpl() {
                throw new UnsupportedOperationException("Can't call getToWhat on non-pointer types.");
            }
        };
    }
    private static NativeType pointerTo(final NativeType toWhat) {
        return new NativeType() {

            @Override
            public String toString() {
                return "p(" + toWhat.toString() + ")";
            }

            @Override
            protected boolean isPointerImpl() {
                return true;
            }

            @Override
            protected NativeType getToWhatImpl() {
                return toWhat;
            }
        };
    }


}
