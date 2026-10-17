package controller;

/**
 * Log Controller (Lightweight decoupled version)
 * Used as a replacement for System.out.println.
 */
public class LogController {

    public static void log(String msg)
    {
        System.out.println(msg);
        // Can be extended later:
        // - UI log panel
        // - File logging
        // - Animation notifications
    }
}