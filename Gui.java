import java.awt.*;
import javax.swing.*;

public class Gui extends JFrame{
    final int ALTURA = 480;
    final int LARGURA = 854;


    // CONSTRUTOR:
    public Gui(){
        super("Editor de Tiles");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setSize(LARGURA, ALTURA);
        setLayout(new BorderLayout());
    }
}