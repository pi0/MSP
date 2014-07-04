package framework;

//=======================================================================================
//  Game debugger
//=======================================================================================

public class GDB {

    public static void i(String message) {
        log("info", message);
    }

    public static void e(String s) {
        log("error", s);
    }

    private static void log(String tag, String message) {
        StackTraceElement myCaller = Thread.currentThread().getStackTrace()[3];
        String trace=String.format("%s(%s:%d)",myCaller.getClassName(),myCaller.getFileName(),myCaller.getLineNumber());
        System.out.printf("[%-5s] %-60s  %s\r\n", tag,trace, message);
    }

}
