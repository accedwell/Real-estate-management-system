package ac.za.tut.util;

/**
 * Small helpers used by the JSP views.
 */
public final class Html {

    private Html() { }

    /** Escapes text for safe output inside HTML content or quoted attributes. */
    public static String esc(Object value) {
        if (value == null) {
            return "";
        }
        String s = String.valueOf(value);
        StringBuilder out = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '<': out.append("&lt;"); break;
                case '>': out.append("&gt;"); break;
                case '&': out.append("&amp;"); break;
                case '"': out.append("&quot;"); break;
                case '\'': out.append("&#39;"); break;
                default: out.append(c);
            }
        }
        return out.toString();
    }

    /** Escapes a value, falling back to a placeholder when it is null or blank. */
    public static String escOr(Object value, String fallback) {
        if (value == null || String.valueOf(value).trim().isEmpty()) {
            return esc(fallback);
        }
        return esc(value);
    }

    /** CSS modifier for a status/priority badge, e.g. "Available" becomes "badge-available". */
    public static String badge(String status) {
        if (status == null) {
            return "badge";
        }
        return "badge badge-" + status.trim().toLowerCase().replaceAll("[^a-z]", "");
    }

    /** Picks a stock photo for a property card so listings look consistent. */
    public static String propertyImage(String type, int id) {
        String[] houses = {"images/feature-listings.jpg", "images/hero.jpg", "images/login-bg.jpg", "images/signup-bg.jpg"};
        if (type != null && (type.equalsIgnoreCase("Apartment") || type.equalsIgnoreCase("Condo"))) {
            return id % 2 == 0 ? "images/feature-tenants.jpg" : "images/cta-bg.jpg";
        }
        if (type != null && type.equalsIgnoreCase("Office")) {
            return "images/dashboard-bg.jpg";
        }
        return houses[Math.abs(id) % houses.length];
    }

    /** First letter of a name for the avatar bubble. */
    public static String initial(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "U";
        }
        return esc(name.trim().substring(0, 1).toUpperCase());
    }
}
