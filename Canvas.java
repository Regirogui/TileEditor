import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class Canvas extends JPanel implements MouseListener, MouseMotionListener {

    public boolean ativo = false;
    public Ferramenta modoAtual;
    Color corAtual;
    int espessura;
    int raio;
    int x1, x2, x3;
    int y1, y2, y3;
    int pontosTriangulo = 0;
    boolean primeiraVez = true;
    boolean dragged = false;
    public ArrayList<Figura> figuras = new ArrayList<>();

    // Construtores
    public Canvas(boolean desenhavel, Ferramenta tipo, Color corAtual, int esp){
        // Adiciona "ouvidor" de eventos de mouse
        this.ativo = desenhavel;
        this.modoAtual = tipo;
        this.corAtual = corAtual;
        this.espessura = esp;
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    // Eventos listeners de Mouse
    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("Mouse Clicked!");
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
