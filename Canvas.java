import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;

/**
 * Área de desenho interativa de um Tile (ladrilho).
 * Os desenhos realizados são armazenados em listas estáticas e compartilhados
 * entre todas as instâncias de {@code Canvas} do mesmo tipo lógico (A ou B).
 *
 * @author Arthur A.
 * @version 17/11/2026
 * @since 20/10/2026
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

    private Runnable aoFinalizarDesenho = () -> {};

    /**
     * Constrói uma nova área de desenho associada a um tipo de ladrilho.
     *
     * @param tipoA    {@code true} para vincular ao conjunto de figuras do Tile A;
     *                 {@code false} para vincular ao Tile B.
     * @param tipo     a ferramenta de desenho inicialmente selecionada.
     * @param corAtual a cor inicial do traço.
     * @param esp      a espessura inicial do traço em pixels (mínimo de 1).
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

    /**
     * Define a ferramenta de desenho ativa e cancela qualquer forma temporária em progresso.
     *
     * @param modo a nova {@link Ferramenta} a ser utilizada.
     */
    public void setModoAtual(Ferramenta modo) {
        modoAtual = modo;
        cancelarDesenhoTemporario();
    }

    /**
     * Define a cor atual utilizada para novos desenhos.
     *
     * @param cor a nova {@link Color} do traço.
     */
    public void setCorAtual(Color cor) {
        corAtual = cor;
    }

    /**
     * Define a espessura do traço para novos desenhos.
     *
     * @param esp a nova espessura em pixels (valores menores que 1 são ajustados para 1).
     */
    public void setEspessura(int esp) {
        espessura = Math.max(1, esp);
    }

    /**
     * Remove permanentemente todos os desenhos compartilhados tanto do tipo A quanto do tipo B.
     */
    public static void limparTodasFiguras() {
        figurasA.clear();
        figurasB.clear();
    }

    /**
     * Cancela e descarta qualquer desenho que ainda esteja em andamento neste {@code Canvas}.
     */
    public void cancelarDesenhoEmAndamento() {
        cancelarDesenhoTemporario();
    }

    /**
     * Redefine o estado interno de desenho temporário e solicita a repintura do componente.
     */
    private void cancelarDesenhoTemporario() {
        desenhando = false;
        figuraTemporaria = null;
        caminhoAtual = null;
        pontosTriangulo = 0;
        repaint();
    }

    /**
     * Inicia o processo de desenho ao pressionar o botão esquerdo do mouse,
     * capturando as coordenadas iniciais para pincel, borracha e formas geométricas.
     *
     * @param e o evento de mouse capturado.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (!ativo || !SwingUtilities.isLeftMouseButton(e)) return;

        // As coordenadas iniciais só são atualizadas para ferramentas
        // que desenham enquanto o botão do mouse está pressionado.
        // O triângulo guarda seus vértices separadamente, em mouseClicked.
        if (modoAtual == Ferramenta.PINCEL || modoAtual == Ferramenta.BORRACHA) {
            x1 = e.getX();
            y1 = e.getY();
            caminhoAtual = new Path2D.Double();
            caminhoAtual.moveTo(x1, y1);
            desenhando = true;
        } else if (modoAtual == Ferramenta.RETA
                || modoAtual == Ferramenta.RETANGULO
                || modoAtual == Ferramenta.CIRCULO) {
            x1 = e.getX();
            y1 = e.getY();
            x2 = x1;
            y2 = y1;
            desenhando = true;
            atualizarFiguraTemporaria();
        }
    }

    /**
     * Atualiza o traçado livre ou a pré-visualização da forma geométrica enquanto o mouse é arrastado.
     *
     * @param e o evento de movimento de arrasto do mouse.
     */
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

    /**
     * Finaliza o desenho ao soltar o botão do mouse, persistindo a figura na lista
     * compartilhada e disparando o callback de conclusão.
     *
     * @param e o evento de liberação do botão do mouse.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (!ativo || !desenhando) return;

        x2 = e.getX();
        y2 = e.getY();

        if (modoAtual == Ferramenta.PINCEL || modoAtual == Ferramenta.BORRACHA) {
            caminhoAtual.lineTo(x2, y2);
            Color cor = modoAtual == Ferramenta.BORRACHA ? getBackground() : corAtual;
            figuras.add(new FiguraCanvas(caminhoAtual, cor, espessura, getWidth(), getHeight()));
        } else {
            atualizarFiguraTemporaria();
            if (figuraTemporaria != null) {
                figuras.add(new FiguraCanvas(figuraTemporaria, corAtual, espessura, getWidth(), getHeight()));
            }
        }

        desenhando = false;
        caminhoAtual = null;
        figuraTemporaria = null;
        repaint();
        aoFinalizarDesenho.run();
    }

    /**
     * Atualiza a forma geométrica temporária (reta, retângulo ou círculo) durante o arrasto do mouse.
     */
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

    /**
     * Cria um retângulo normalizado a partir de dois pontos opostos quaisquer.
     *
     * @param xa coordenada X do primeiro ponto.
     * @param ya coordenada Y do primeiro ponto.
     * @param xb coordenada X do segundo ponto.
     * @param yb coordenada Y do segundo ponto.
     * @return um objeto {@link Rectangle2D} com dimensões positivas.
     */
    private Rectangle2D criarRetangulo(int xa, int ya, int xb, int yb) {
        return new Rectangle2D.Double(
                Math.min(xa, xb), Math.min(ya, yb),
                Math.abs(xb - xa), Math.abs(yb - ya));
    }

    /**
     * Cria uma elipse inscrita no retângulo delimitador definido por dois pontos opostos.
     *
     * @param xa coordenada X do primeiro ponto.
     * @param ya coordenada Y do primeiro ponto.
     * @param xb coordenada X do segundo ponto.
     * @param yb coordenada Y do segundo ponto.
     * @return um objeto {@link Ellipse2D} com dimensões positivas.
     */
    private Ellipse2D criarElipse(int xa, int ya, int xb, int yb) {
        return new Ellipse2D.Double(
                Math.min(xa, xb), Math.min(ya, yb),
                Math.abs(xb - xa), Math.abs(yb - ya));
    }

    /**
     * Trata cliques do mouse, utilizado especificamente para marcar os vértices da ferramenta Triângulo.
     *
     * @param e o evento de clique do mouse.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (!ativo || !SwingUtilities.isLeftMouseButton(e)) return;

        if (modoAtual == Ferramenta.TRIANGULO) {
            adicionarPontoTriangulo(e.getPoint());
        }
    }

    /**
     * Registra sequencialmente os três vértices de um triângulo. Ao registrar o terceiro ponto,
     * fecha o polígono, adiciona-o à lista de figuras e notifica a conclusão do desenho.
     *
     * @param p o ponto ({@link Point}) correspondente ao clique atual.
     */
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
            figuras.add(new FiguraCanvas(triangulo, corAtual, espessura, getWidth(), getHeight()));
            pontosTriangulo = 0;
            repaint();
            aoFinalizarDesenho.run();
        }
    }

    /**
     * Renderiza todas as figuras persistidas do tipo deste tile, bem como a forma
     * temporária ou o traçado livre que esteja sendo desenhado no momento.
     *
     * @param g o contexto gráfico {@link Graphics} fornecido pelo Swing.
     */
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

    /**
     * Renderiza todas as figuras armazenadas de um determinado tipo de ladrilho (A ou B)
     * escaladas proporcionalmente para as dimensões especificadas.
     *
     * @param g2      o contexto gráfico {@link Graphics2D} de destino.
     * @param tipoA   {@code true} para desenhar as figuras do tipo A; {@code false} para o tipo B.
     * @param largura a largura de destino em pixels.
     * @param altura  a altura de destino em pixels.
     */
    public static void desenharFiguras(Graphics2D g2, boolean tipoA, int largura, int altura) {
        ArrayList<FiguraCanvas> lista = tipoA ? figurasA : figurasB;
        for (FiguraCanvas figura : lista) {
            figura.desenharEscalado(g2, largura, altura);
        }
    }

    /**
     * Desenha uma forma vetorial no contexto gráfico aplicando cor e traço arredondado.
     *
     * @param g2       o contexto gráfico {@link Graphics2D}.
     * @param forma    a forma ({@link Shape}) a ser desenhada.
     * @param cor      a cor do traço.
     * @param grossura a espessura do traço em pixels.
     */
    private void desenharForma(Graphics2D g2, Shape forma, Color cor, int grossura) {
        g2.setColor(cor);
        g2.setStroke(new BasicStroke(grossura, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));
        g2.draw(forma);
    }

    /**
     * Representa uma figura vetorial individual armazenada no canvas,
     * guardando suas propriedades visuais e dimensões originais para reescalonamento.
     *
     * @author Arthur A.
     * @version 17/11/2026
     * @since 17/11/2026
     */
    private static class FiguraCanvas {
        private final Shape forma;
        private final Color cor;
        private final int espessura;
        private final int larguraOrigem;
        private final int alturaOrigem;

        /**
         * Constrói um registro imutável de figura desenhada.
         *
         * @param forma         a geometria {@link Shape} da figura.
         * @param cor           a cor utilizada no desenho.
         * @param espessura     a espessura do traço.
         * @param larguraOrigem a largura do canvas no momento da criação.
         * @param alturaOrigem  a altura do canvas no momento da criação.
         */
        FiguraCanvas(Shape forma, Color cor, int espessura, int larguraOrigem, int alturaOrigem) {
            this.forma = forma;
            this.cor = cor;
            this.espessura = espessura;
            this.larguraOrigem = Math.max(1, larguraOrigem);
            this.alturaOrigem = Math.max(1, alturaOrigem);
        }

        /**
         * Desenha a figura em sua escala original.
         *
         * @param g2 o contexto gráfico {@link Graphics2D}.
         */
        void desenhar(Graphics2D g2) {
            desenharEscalado(g2, larguraOrigem, alturaOrigem);
        }

        /**
         * Desenha a figura ajustando sua escala proporcionalmente às dimensões informadas.
         *
         * @param g2      o contexto gráfico {@link Graphics2D}.
         * @param largura a largura alvo para renderização.
         * @param altura  a altura alvo para renderização.
         */
        void desenharEscalado(Graphics2D g2, int largura, int altura) {
            Graphics2D copia = (Graphics2D) g2.create();
            copia.scale((double) largura / larguraOrigem,
                    (double) altura / alturaOrigem);
            copia.setColor(cor);
            copia.setStroke(new BasicStroke(espessura, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));
            copia.draw(forma);
            copia.dispose();
        }
    }

    /**
     * Evento não utilizado da interface {@link MouseListener}.
     *
     * @param e o evento de mouse.
     */
    @Override public void mouseEntered(MouseEvent e) { }

    /**
     * Evento não utilizado da interface {@link MouseListener}.
     *
     * @param e o evento de mouse.
     */
    @Override public void mouseExited(MouseEvent e) { }

    /**
     * Evento não utilizado da interface {@link MouseMotionListener}.
     *
     * @param e o evento de movimento do mouse.
     */
    @Override public void mouseMoved(MouseEvent e) { }

    /**
     * Define uma ação de retorno (callback) a ser executada sempre que um desenho for concluído.
     *
     * @param callback o {@link Runnable} a ser invocado após a conclusão de um traço ou forma.
     */
    public void setAoFinalizarDesenho(Runnable callback) {
        this.aoFinalizarDesenho = callback;
    }
}