



import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Callback;
import java.util.Arrays;

public class Main {
    public static interface FrameworkCallback extends Callback {

        void callback(String arg1, String[] arg2);
    }
    public static interface Module extends Library {
        Module INSTANCE = (Module) Native.loadLibrary("adder", Module.class);

        void injectFramework(FrameworkCallback cb);
        void loadModule();
    }

    public static void main(String[] args) {
        FrameworkCallback cb = new FrameworkCallback() {
            public void callback(String arg1, String[] arg2) {
                System.err.println(arg1 + ": " + Arrays.deepToString(arg2));
            }
        };
        Module.INSTANCE.injectFramework(cb);
        Module.INSTANCE.loadModule();
        System.err.println(cb);
    }
}
