package nudge.util;

import nudge.graphics.Color;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Stack;

/**
 * @author Frederik Dahl
 * XX/XX/2020
 */

public class ColorPalette {

    // Utility class for loading  color .hex files to array.

    public Color[] colors;

    public ColorPalette(String filePath) {

        Stack<String> hexStack = new Stack<>();

        File file = new File(filePath);

        try {
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {

                hexStack.push(scanner.nextLine());
            }
            scanner.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        colors = new Color[hexStack.size()];

        for (int i = colors.length - 1; i > -1; i--) {

            String hex = hexStack.pop();

            assert (hex.length() < 10)
                    : "(ColorPalette) Invalid hex-format.\n\n" +
                    "Try: 6, 8 or 9 (with #) chars," +
                    "separated by a new line";

            colors[i] = Color.fromHex(hex);
        }
    }

    public void sort() {
        Arrays.sort(colors);
    }
}
