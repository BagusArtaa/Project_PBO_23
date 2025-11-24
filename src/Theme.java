import java.awt.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class Theme {
    public static final Color DARK_GRAY = new Color(0x323437);
    public static final Color LIGHT_GRAY = new Color(0xB0B3B8);
    public static final Color YELLOW = new Color(0xFAB005);
    public static final Color GREEN = new Color(0x37B24D);
    public static final Color RED = new Color(0xF03E3E);
    public static final Color DARKER_GRAY = new Color(0x40444B);
    public static final Color WHITE = new Color(0xFFFFFF);
   
    public static final Font MAIN_FONT = new Font("Arial", Font.PLAIN, 16);
    public static final Font BOLD_FONT = new Font("Arial", Font.BOLD, 16);
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 36);
    public static final Font SUBTITLE_FONT = new Font("Arial", Font.BOLD, 24);

    public static final SimpleAttributeSet STYLE_DEFAULT;
    public static final SimpleAttributeSet STYLE_CORRECT;
    public static final SimpleAttributeSet STYLE_INCORRECT;
    public static final SimpleAttributeSet STYLE_CURRENT_BG;
    public static final SimpleAttributeSet STYLE_CURRENT_CORRECT_FG;
    public static final SimpleAttributeSet STYLE_CURRENT_INCORRECT_FG;

    static {
        STYLE_DEFAULT = new SimpleAttributeSet();
        StyleConstants.setFontFamily(STYLE_DEFAULT, "Monospaced");
        StyleConstants.setFontSize(STYLE_DEFAULT, 20);
        StyleConstants.setForeground(STYLE_DEFAULT, LIGHT_GRAY);

        STYLE_CORRECT = new SimpleAttributeSet();
        StyleConstants.setForeground(STYLE_CORRECT, GREEN);
        StyleConstants.setBold(STYLE_CORRECT, true);

        STYLE_INCORRECT = new SimpleAttributeSet();
        StyleConstants.setForeground(STYLE_INCORRECT, RED);
        StyleConstants.setBold(STYLE_INCORRECT, true);
        
        STYLE_CURRENT_BG = new SimpleAttributeSet();
        StyleConstants.setBackground(STYLE_CURRENT_BG, DARKER_GRAY);

        STYLE_CURRENT_CORRECT_FG = new SimpleAttributeSet();
        StyleConstants.setForeground(STYLE_CURRENT_CORRECT_FG, Color.WHITE);
        
        STYLE_CURRENT_INCORRECT_FG = new SimpleAttributeSet();
        StyleConstants.setForeground(STYLE_CURRENT_INCORRECT_FG, RED);
    }
}