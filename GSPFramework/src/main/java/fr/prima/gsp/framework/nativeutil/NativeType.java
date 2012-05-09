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
    public static final NativeType DOUBLE = simple("double");
    public static final NativeType BOOL = simple("bool");
    public static final NativeType CHAR = simple("char");
    public static final NativeType VOID = simple("void");
    public static final NativeType[] nonPointersTypes = new NativeType[]{INT, FLOAT, DOUBLE, BOOL, CHAR, VOID};

    public static NativeType stdString() {
        String[] std = new String[]{"std"};
        NativeType[] charType = new NativeType[]{CHAR};
        return struct(std, "basic_string", new NativeType[]{
            CHAR, struct(std, "char_traits", charType), struct(std, "allocator", charType)
        });
        // from c++filt => std::basic_string<char, std::char_traits<char>, std::allocator<char> >
    }
    public static NativeType stdVector(NativeType content) {
        String[] std = new String[]{"std"};
        return struct(std, "vector", new NativeType[]{content, struct(std, "allocator", new NativeType[]{content})});
    }
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
        if (t1 == null || t2 == null || t1.isPointer() ^ t2.isPointer() || t1.isCompound() ^ t2.isCompound()) {
            return false;
        }
        if (t1.isPointer()) {
            return areSame(t1.getToWhat(), t2.getToWhat());
        } else if (t1.isCompound()) {
            return t1.equals(t2);
        } else {
            return false;
        }
    }
    // moving specifications...
    static boolean areCloseEnough(NativeType t1, NativeType t2) {
        if (t1 == t2) {
            return true;
        }
        if (t1 == null || t2 == null) {
            return false;
        }
        if (!t1.isPointer() || !t2.isPointer()) {
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
    public final boolean isCompound() {
        return isCompoundImpl();
    }

    // SPI
    protected abstract boolean isPointerImpl();
    protected abstract NativeType getToWhatImpl();
    protected abstract boolean isCompoundImpl();

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

            @Override
            protected boolean isCompoundImpl() {
                return false;
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

            @Override
            protected boolean isCompoundImpl() {
                return false;
            }
        };
    }

    public static NativeType struct(final String nativeName) {
        return struct(new String[0], nativeName, null);
    }
    public static NativeType struct(final String[] ns, final String nativeName, final NativeType[] templates) {
        return new NativeType() {

            @Override
            public String toString() {
                StringBuilder b = new StringBuilder("st(");
                for (String n : ns) {
                    b.append(n).append("::");
                }
                b.append(nativeName);
                if (templates != null) {
                    b.append("< ");
                    for (NativeType t : templates) {
                        b.append(t.toString()).append(" ");
                    }
                    b.append(">");
                }
                b.append(")");
                return b.toString();
            }

            @Override
            public boolean equals(Object obj) {
                return obj != null && obj.getClass() == this.getClass() && this.toString().equals(obj.toString());
            }

            @Override
            protected boolean isPointerImpl() {
                return false;
            }

            @Override
            protected NativeType getToWhatImpl() {
                throw new UnsupportedOperationException("Can't call getToWhat on non-pointer types.");
            }

            @Override
            protected boolean isCompoundImpl() {
                return true;
            }
        };
    }


}
