package ac.za.tut.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Inline SVG icons from the Lucide icon set (the same icons React Icons
 * exposes as "react-icons/lu"), rendered server-side so no JavaScript,
 * icon font or internet connection is needed.
 *
 * Usage in a JSP:  <%= Icon.svg("house") %>
 * Icons inherit the surrounding text colour (stroke="currentColor").
 */
public final class Icon {

    private static final Map<String, String> PATHS = new HashMap<>();

    static {
        PATHS.put("house", "<path d=\"M15 21v-8a1 1 0 0 0-1-1h-4a1 1 0 0 0-1 1v8\"/><path d=\"M3 10a2 2 0 0 1 .709-1.528l7-5.999a2 2 0 0 1 2.582 0l7 5.999A2 2 0 0 1 21 10v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z\"/>");
        PATHS.put("building", "<path d=\"M6 22V4a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v18Z\"/><path d=\"M6 12H4a2 2 0 0 0-2 2v6a2 2 0 0 0 2 2h2\"/><path d=\"M18 9h2a2 2 0 0 1 2 2v9a2 2 0 0 1-2 2h-2\"/><path d=\"M10 6h4\"/><path d=\"M10 10h4\"/><path d=\"M10 14h4\"/><path d=\"M10 18h4\"/>");
        PATHS.put("key", "<path d=\"m15.5 7.5 2.3 2.3a1 1 0 0 0 1.4 0l2.1-2.1a1 1 0 0 0 0-1.4L19 4\"/><path d=\"m21 2-9.6 9.6\"/><circle cx=\"7.5\" cy=\"15.5\" r=\"5.5\"/>");
        PATHS.put("file-text", "<path d=\"M15 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7Z\"/><path d=\"M14 2v4a2 2 0 0 0 2 2h4\"/><path d=\"M10 9H8\"/><path d=\"M16 13H8\"/><path d=\"M16 17H8\"/>");
        PATHS.put("file-plus", "<path d=\"M15 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7Z\"/><path d=\"M14 2v4a2 2 0 0 0 2 2h4\"/><path d=\"M9 15h6\"/><path d=\"M12 18v-6\"/>");
        PATHS.put("wrench", "<path d=\"M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z\"/>");
        PATHS.put("clipboard-list", "<rect width=\"8\" height=\"4\" x=\"8\" y=\"2\" rx=\"1\" ry=\"1\"/><path d=\"M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2\"/><path d=\"M12 11h4\"/><path d=\"M12 16h4\"/><path d=\"M8 11h.01\"/><path d=\"M8 16h.01\"/>");
        PATHS.put("plus", "<path d=\"M5 12h14\"/><path d=\"M12 5v14\"/>");
        PATHS.put("users", "<path d=\"M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2\"/><circle cx=\"9\" cy=\"7\" r=\"4\"/><path d=\"M22 21v-2a4 4 0 0 0-3-3.87\"/><path d=\"M16 3.13a4 4 0 0 1 0 7.75\"/>");
        PATHS.put("user-plus", "<path d=\"M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2\"/><circle cx=\"9\" cy=\"7\" r=\"4\"/><line x1=\"19\" x2=\"19\" y1=\"8\" y2=\"14\"/><line x1=\"22\" x2=\"16\" y1=\"11\" y2=\"11\"/>");
        PATHS.put("door-open", "<path d=\"M13 4h3a2 2 0 0 1 2 2v14\"/><path d=\"M2 20h3\"/><path d=\"M13 20h9\"/><path d=\"M10 12v.01\"/><path d=\"M13 4.562v16.157a1 1 0 0 1-1.242.97L5 20V5.562a2 2 0 0 1 1.515-1.94l4-1A2 2 0 0 1 13 4.561Z\"/>");
        PATHS.put("chart", "<path d=\"M3 3v18h18\"/><path d=\"M18 17V9\"/><path d=\"M13 17V5\"/><path d=\"M8 17v-3\"/>");
        PATHS.put("trending-up", "<polyline points=\"22 7 13.5 15.5 8.5 10.5 2 17\"/><polyline points=\"16 7 22 7 22 13\"/>");
        PATHS.put("settings", "<path d=\"M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z\"/><circle cx=\"12\" cy=\"12\" r=\"3\"/>");
        PATHS.put("inbox", "<polyline points=\"22 12 16 12 14 15 10 15 8 12 2 12\"/><path d=\"M5.45 5.11 2 12v6a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-6l-3.45-6.89A2 2 0 0 0 16.76 4H7.24a2 2 0 0 0-1.79 1.11z\"/>");
        PATHS.put("shield", "<path d=\"M20 13c0 5-3.5 7.5-7.66 8.95a1 1 0 0 1-.67-.01C7.5 20.5 4 18 4 13V6a1 1 0 0 1 1-1c2 0 4.5-1.2 6.24-2.72a1.17 1.17 0 0 1 1.52 0C14.51 3.81 17 5 19 5a1 1 0 0 1 1 1z\"/>");
        PATHS.put("briefcase", "<path d=\"M16 20V4a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16\"/><rect width=\"20\" height=\"14\" x=\"2\" y=\"6\" rx=\"2\"/>");
        PATHS.put("alert", "<path d=\"m21.73 18-8-14a2 2 0 0 0-3.48 0l-8 14A2 2 0 0 0 4 21h16a2 2 0 0 0 1.73-3\"/><path d=\"M12 9v4\"/><path d=\"M12 17h.01\"/>");
        PATHS.put("folder-open", "<path d=\"m6 14 1.5-2.9A2 2 0 0 1 9.24 10H20a2 2 0 0 1 1.94 2.5l-1.54 6a2 2 0 0 1-1.95 1.5H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h3.9a2 2 0 0 1 1.69.9l.81 1.2a2 2 0 0 0 1.67.9H18a2 2 0 0 1 2 2v2\"/>");
        PATHS.put("check", "<path d=\"M20 6 9 17l-5-5\"/>");
        PATHS.put("check-circle", "<path d=\"M21.801 10A10 10 0 1 1 17 3.335\"/><path d=\"m9 11 3 3L22 4\"/>");
        PATHS.put("x", "<path d=\"M18 6 6 18\"/><path d=\"m6 6 12 12\"/>");
        PATHS.put("menu", "<line x1=\"4\" x2=\"20\" y1=\"12\" y2=\"12\"/><line x1=\"4\" x2=\"20\" y1=\"6\" y2=\"6\"/><line x1=\"4\" x2=\"20\" y1=\"18\" y2=\"18\"/>");
        PATHS.put("log-out", "<path d=\"M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4\"/><polyline points=\"16 17 21 12 16 7\"/><line x1=\"21\" x2=\"9\" y1=\"12\" y2=\"12\"/>");
        PATHS.put("log-in", "<path d=\"M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4\"/><polyline points=\"10 17 15 12 10 7\"/><line x1=\"15\" x2=\"3\" y1=\"12\" y2=\"12\"/>");
        PATHS.put("arrow-left", "<path d=\"m12 19-7-7 7-7\"/><path d=\"M19 12H5\"/>");
        PATHS.put("arrow-right", "<path d=\"M5 12h14\"/><path d=\"m12 5 7 7-7 7\"/>");
        PATHS.put("search", "<circle cx=\"11\" cy=\"11\" r=\"8\"/><path d=\"m21 21-4.3-4.3\"/>");
        PATHS.put("download", "<path d=\"M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4\"/><polyline points=\"7 10 12 15 17 10\"/><line x1=\"12\" x2=\"12\" y1=\"15\" y2=\"3\"/>");
        PATHS.put("printer", "<polyline points=\"6 9 6 2 18 2 18 9\"/><path d=\"M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2\"/><rect width=\"12\" height=\"8\" x=\"6\" y=\"14\"/>");
        PATHS.put("pencil", "<path d=\"M21.174 6.812a1 1 0 0 0-3.986-3.987L3.842 16.174a2 2 0 0 0-.5.83l-1.321 4.352a.5.5 0 0 0 .623.622l4.353-1.32a2 2 0 0 0 .83-.497z\"/><path d=\"m15 5 4 4\"/>");
        PATHS.put("trash", "<path d=\"M3 6h18\"/><path d=\"M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6\"/><path d=\"M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2\"/><line x1=\"10\" x2=\"10\" y1=\"11\" y2=\"17\"/><line x1=\"14\" x2=\"14\" y1=\"11\" y2=\"17\"/>");
        PATHS.put("refresh", "<path d=\"M3 12a9 9 0 0 1 9-9 9.75 9.75 0 0 1 6.74 2.74L21 8\"/><path d=\"M21 3v5h-5\"/><path d=\"M21 12a9 9 0 0 1-9 9 9.75 9.75 0 0 1-6.74-2.74L3 16\"/><path d=\"M8 16H3v5\"/>");
        PATHS.put("filter", "<polygon points=\"22 3 2 3 10 12.46 10 19 14 21 14 12.46 22 3\"/>");
        PATHS.put("calendar", "<path d=\"M8 2v4\"/><path d=\"M16 2v4\"/><rect width=\"18\" height=\"18\" x=\"3\" y=\"4\" rx=\"2\"/><path d=\"M3 10h18\"/>");
        PATHS.put("bed", "<path d=\"M2 4v16\"/><path d=\"M2 8h18a2 2 0 0 1 2 2v10\"/><path d=\"M2 17h20\"/><path d=\"M6 8v9\"/>");
        PATHS.put("ruler", "<path d=\"M8 3H5a2 2 0 0 0-2 2v3\"/><path d=\"M21 8V5a2 2 0 0 0-2-2h-3\"/><path d=\"M3 16v3a2 2 0 0 0 2 2h3\"/><path d=\"M16 21h3a2 2 0 0 0 2-2v-3\"/>");
        PATHS.put("map-pin", "<path d=\"M20 10c0 4.993-5.539 10.193-7.399 11.799a1 1 0 0 1-1.202 0C9.539 20.193 4 14.993 4 10a8 8 0 0 1 16 0\"/><circle cx=\"12\" cy=\"10\" r=\"3\"/>");
        PATHS.put("mail", "<rect width=\"20\" height=\"16\" x=\"2\" y=\"4\" rx=\"2\"/><path d=\"m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7\"/>");
        PATHS.put("phone", "<path d=\"M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z\"/>");
        PATHS.put("clock", "<circle cx=\"12\" cy=\"12\" r=\"10\"/><polyline points=\"12 6 12 12 16 14\"/>");
        PATHS.put("lock", "<rect width=\"18\" height=\"11\" x=\"3\" y=\"11\" rx=\"2\" ry=\"2\"/><path d=\"M7 11V7a5 5 0 0 1 10 0v4\"/>");
        PATHS.put("eye-off", "<path d=\"M10.733 5.076a10.744 10.744 0 0 1 11.205 6.575 1 1 0 0 1 0 .696 10.747 10.747 0 0 1-1.444 2.49\"/><path d=\"M14.084 14.158a3 3 0 0 1-4.242-4.242\"/><path d=\"M17.479 17.499a10.75 10.75 0 0 1-15.417-5.151 1 1 0 0 1 0-.696 10.75 10.75 0 0 1 4.446-5.143\"/><path d=\"m2 2 20 20\"/>");
        PATHS.put("user", "<path d=\"M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2\"/><circle cx=\"12\" cy=\"7\" r=\"4\"/>");
        PATHS.put("shield-check", "<path d=\"M20 13c0 5-3.5 7.5-7.66 8.95a1 1 0 0 1-.67-.01C7.5 20.5 4 18 4 13V6a1 1 0 0 1 1-1c2 0 4.5-1.2 6.24-2.72a1.17 1.17 0 0 1 1.52 0C14.51 3.81 17 5 19 5a1 1 0 0 1 1 1z\"/><path d=\"m9 12 2 2 4-4\"/>");
        PATHS.put("loader", "<path d=\"M21 12a9 9 0 1 1-6.219-8.56\"/>");
        PATHS.put("info", "<circle cx=\"12\" cy=\"12\" r=\"10\"/><path d=\"M12 16v-4\"/><path d=\"M12 8h.01\"/>");
        PATHS.put("eye","<path d=\"M2.062 12.348a1 1 0 0 1 0-.696 10.75 10.75 0 0 1 19.876 0 1 1 0 0 1 0 .696 10.75 10.75 0 0 1-19.876 0\"/><circle cx=\"12\" cy=\"12\" r=\"3\"/>");
    }

    private Icon() { }

    /** The REMS brand mark (an "R" whose stem rises into a roofline), gold tile for dark backgrounds. */
    public static String logo() {
        return logo(true);
    }

    /**
     * The REMS brand mark.
     * @param onDark true = gold tile with navy mark (for dark backgrounds),
     *               false = navy tile with gold mark (for light backgrounds)
     */
    public static String logo(boolean onDark) {
        String id = onDark ? "remsLogoGold" : "remsLogoNavy";
        String tile = onDark ? "url(#" + id + ")" : "#0b1b2b";
        String mark = onDark ? "#0b1b2b" : "url(#" + id + ")";
        return "<svg class=\"brand-logo\" xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 40 40\" role=\"img\" aria-label=\"REMS logo\">"
                + "<defs><linearGradient id=\"" + id + "\" gradientUnits=\"userSpaceOnUse\" x1=\"0\" y1=\"0\" x2=\"40\" y2=\"40\">"
                + "<stop offset=\"0\" stop-color=\"#ecd08f\"/><stop offset=\"1\" stop-color=\"#b48a35\"/></linearGradient></defs>"
                + "<rect width=\"40\" height=\"40\" rx=\"11\" fill=\"" + tile + "\"/>"
                + "<g fill=\"none\" stroke=\"" + mark + "\" stroke-width=\"3.3\" stroke-linecap=\"round\" stroke-linejoin=\"round\">"
                + "<path d=\"M10.5 31V17.6L20 9.6l9.5 8\"/>"
                + "<path d=\"M16 31V20.4h5.4a3.7 3.7 0 0 1 0 7.4H16\"/>"
                + "<path d=\"M21.6 27.8 26.4 31\"/></g></svg>";
    }

    /** The logo as a data: URI for the browser-tab icon. */
    public static final String FAVICON = "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 40 40'%3E"
            + "%3Crect width='40' height='40' rx='11' fill='%23c9a24a'/%3E"
            + "%3Cg fill='none' stroke='%230b1b2b' stroke-width='3.6' stroke-linecap='round' stroke-linejoin='round'%3E"
            + "%3Cpath d='M10.5 31V17.6L20 9.6l9.5 8'/%3E%3Cpath d='M16 31V20.4h5.4a3.7 3.7 0 0 1 0 7.4H16'/%3E"
            + "%3Cpath d='M21.6 27.8 26.4 31'/%3E%3C/g%3E%3C/svg%3E";

    /** An icon at the default size (1.15em, follows the text size). */
    public static String svg(String name) {
        return svg(name, "i");
    }

    /** An icon with extra CSS classes, e.g. svg("house", "i i-lg"). */
    public static String svg(String name, String cssClass) {
        String paths = PATHS.get(name);
        if (paths == null) {
            paths = PATHS.get("x");
        }
        return "<svg class=\"" + cssClass + "\" xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 24 24\" fill=\"none\""
                + " stroke=\"currentColor\" stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\""
                + " aria-hidden=\"true\" focusable=\"false\">" + paths + "</svg>";
    }
}
