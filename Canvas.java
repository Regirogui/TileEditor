import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;

/**
 * Area de desenho de um Tile.
 * Os desenhos são compartilhados por todos os Canvas do mesmo tipo (A ou B).
 */
public class Canvas extends JPanel implements MouseListener, MouseMotionListener {

    private static final ArrayList<FiguraCanvas> figurasA = new ArrayList<>();
    private static final ArrayList<FiguraCanvas> figurasB = new ArrayList<>();
    private final ArrayList<FiguraCanvas> figuras;

    public boolean ativo = true;
    public Ferramenta modoAtual;
    private Color corAtual;
    private int espessura;

    private int x1, y1, x2, y2, x3, y3;
    private int pontosTriangulo = 0;
    private boolean desenhando = false;
    private Shape figuraTemporaria;
    private Path2D caminhoAtual;

    /**
     * @param tipoA true para Tile A e false para Tile B.
     */
    public Canvas(boolean tipoA, Ferramenta tipo, Color corAtual, int esp) {
        figuras = tipoA ? figurasA : figurasB;
        modoAtual = tipo;
        this.corAtual = corAtual;
        espessura = Math.max(1, esp);

        setBackground(Color.WHITE);
        setOpaque(true);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void setModoAtual(Ferramenta modo) {
        modoAtual = modo;
        cancelarDesenhoTemporario();
    }

    public void setCorAtual(Color cor) {
        corAtual = cor;
    }

    public void setEspessura(int esp) {
        espessura = Math.max(1, esp);
    }

    private void cancelarDesenhoTemporario() {
        desenhando = false;
        figuraTemporaria = null;
        caminhoAtual = null;
        pontosTriangulo = 0;
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!ativo || !SwingUtilities.isLeftMouseButton(e)) return;

        x1 = e.getX();
        y1 = e.getY();

        if (modoAtual == Ferramenta.PINCEL || modoAtual == Ferramenta.BORRACHA) {
            caminhoAtual = new Path2D.Double();
            caminhoAtual.moveTo(x1, y1);
            desenhando = true;
        } else if (modoAtual == Ferramenta.RETA
                || modoAtual == Ferramenta.RETANGULO
                || modoAtual == Ferramenta.CIRCULO) {
            x2 = x1;
            y2 = y1;
            desenhando = true;
            atualizarFiguraTemporaria();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!ativo || !desenhando) return;

        x2 = e.getX();
        y2 = e.getY();

        if (modoAtual == Ferramenta.PINCEL || modoAtual == Ferramenta.BORRACHA) {
            caminhoAtual.lineTo(x2, y2);
            figuraTemporaria = caminhoAtual;
        } else {
            atualizarFiguraTemporaria();
        }
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!ativo || !desenhando) return;

        x2 = e.getX();
        y2 = e.getY();

        if (modoAtual == Ferramenta.PINCEL || modoAtual == Ferramenta.BORRACHA) {
            caminhoAtual.lineTo(x2, y2);
            Color cor = modoAtual == Ferramenta.BORRACHA ? getBackground() : corAtual;
            figuras.add(new FiguraCanvas(caminhoAtual, cor, espessura));
        } else {
            atualizarFiguraTemporaria();
            if (figuraTemporaria != null) {
                figuras.add(new FiguraCanvas(figuraTemporaria, corAtual, espessura));
            }
        }

        desenhando = false;
        caminhoAtual = null;
        figuraTemporaria = null;
        repaint();
        aoFinalizarDesenho.run();
    }

    private void atualizarFiguraTemporaria() {
        switch (modoAtual) {
            case RETA:
                figuraTemporaria = new Line2D.Double(x1, y1, x2, y2);
                break;
            case RETANGULO:
                figuraTemporaria = criarRetangulo(x1, y1, x2, y2);
                break;
            case CIRCULO:
                figuraTemporaria = criarElipse(x1, y1, x2, y2);
                break;
            default:
                figuraTemporaria = null;
        }
        repaint();
    }

    private Rectangle2D criarRetangulo(int xa, int ya, int xb, int yb) {
        return new Rectangle2D.Double(
                Math.min(xa, xb), Math.min(ya, yb),
                Math.abs(xb - xa), Math.abs(yb - ya));
    }

    private Ellipse2D criarElipse(int xa, int ya, int xb, int yb) {
        return new Ellipse2D.Double(
                Math.min(xa, xb), Math.min(ya, yb),
                Math.abs(xb - xa), Math.abs(yb - ya));
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!ativo || !SwingUtilities.isLeftMouseButton(e)) return;

        if (modoAtual == Ferramenta.TRIANGULO) {
            adicionarPontoTriangulo(e.getPoint());
        } 
    }

    private void adicionarPontoTriangulo(Point p) {
        if (pontosTriangulo == 0) {
            x1 = p.x; y1 = p.y;
            pontosTriangulo = 1;
        } else if (pontosTriangulo == 1) {
            x2 = p.x; y2 = p.y;
            pontosTriangulo = 2;
        } else {
            x3 = p.x; y3 = p.y;
            Path2D triangulo = new Path2D.Double();
            triangulo.moveTo(x1, y1);
            triangulo.lineTo(x2, y2);
            triangulo.lineTo(x3, y3);
            triangulo.closePath();
            figuras.add(new FiguraCanvas(triangulo, corAtual, espessura));
            pontosTriangulo = 0;
            repaint();
        }
    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        for (FiguraCanvas figura : figuras) figura.desenhar(g2);

        if (figuraTemporaria != null) {
            desenharForma(g2, figuraTemporaria, corAtual, espessura);
        }
        if (caminhoAtual != null) {
            Color cor = modoAtual == Ferramenta.BORRACHA ? getBackground() : corAtual;
            desenharForma(g2, caminhoAtual, cor, espessura);
        }
        g2.dispose();
    }



    private void desenharForma(Graphics2D g2, Shape forma, Color cor, int grossura) {
        g2.setColor(cor);
        g2.setStroke(new BasicStroke(grossura, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));
        g2.draw(forma);
    }

    private static class FiguraCanvas {
        private final Shape forma;
        private final Color cor;
        private final int espessura;

        FiguraCanvas(Shape forma, Color cor, int espessura) {
            this.forma = forma;
            this.cor = cor;
            this.espessura = espessura;
        }

        void desenhar(Graphics2D g2) {
            g2.setColor(cor);
            g2.setStroke(new BasicStroke(espessura, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));
            g2.draw(forma);
        }
    }

    @Override public void mouseEntered(MouseEvent e) { }
    @Override public void mouseExited(MouseEvent e) { }
    @Override public void mouseMoved(MouseEvent e) { }

    private Runnable aoFinalizarDesenho = () -> {};

    public void setAoFinalizarDesenho(Runnable callback) {
        this.aoFinalizarDesenho = callback;
    }
}
