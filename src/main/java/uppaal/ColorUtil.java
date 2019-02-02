package uppaal;

import org.jdom.Element;

import java.awt.Color;

class ColorUtil {
    /**
     * Tries to find the color of the XML `element`.
     * Returns `null` if a color is not found.
     * @param element The XML element with a possible `color` attribute.
     * @return Color
     */
    static Color findColor(Element element) {
        String color = element.getAttributeValue("color");
        try {
            return Color.decode(color);
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

    /**
     * Converts a `Color` to a hexadecimal color string (`#RRGGBB`)
     * @param color The Color to convert.
     * @return String
     * @throws NullPointerException if `color` is `null`
     */
    static String toHexString(Color color) throws NullPointerException {
        String hexColor = Integer.toHexString(color.getRGB() & 0xffffff);
        if (hexColor.length() < 6) {
            hexColor = "000000".substring(0, 6 - hexColor.length()) + hexColor;
        }
        return "#" + hexColor;
    }
}