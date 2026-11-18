import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

/**
 * Janela principal da aplicação "Editor de Tiles".
 * Gerencia a interface gráfica, a barra de ferramentas de desenho, o seletor de cores
 * e espessura, além da grade interativa de ladrilhos (Tiles do tipo A e B).
 *
 * @author Arthur A.
 * @version 17/11/2026
 * @since 20/10/2026
 */
public class Gui extends JFrame {

    private static final int ALTURA = 855;
    private static final int LARGURA = 855;
    private static final int LINHAS = 3;
    private static final int COLUNAS = 3;

    private int espessuraAtual = 10;
    private Ferramenta modoAtual = Ferramenta.SELETOR;
    private Color corAtual = Color.BLACK;

    private final JPanel pnlFerramentas = new JPanel(new FlowLayout());
    private JPanel pnlTiles;
    private final JPanel pnlBarraSlider = new JPanel(new FlowLayout());

    // Uma célula visual por posição do grid.
    private JPanel[] tiles;

    // Cada posição possui seu próprio Canvas.
    // Não reutilize a mesma instância de Canvas em várias células:
    // um componente Swing/AWT só pode ter um pai por vez.
    private Canvas[] canvasTiles;

    // Guarda o tipo lógico de cada posição.
    private TipoTile[] tiposTiles;

    private final JButton btnSeletor = new JButton();
    private final JButton btnPincel = new JButton();
    private final JButton btnBorracha = new JButton();
    private final JButton btnLimpar = new JButton();
    private final JButton btnTrocarTile = new JButton();
    private final JButton btnMostrarTiles = new JButton();

    private final JButton btnReta = new JButton();
    private final JButton btnRetangulo = new JButton();
    private final JButton btnCirculo = new JButton();
    private final JButton btnTriangulo = new JButton();

    private final JButton btnCores = new JButton();

    private final JLabel lblEspessura = new JLabel("Esp.");
    private final JSlider sldrGrossura = new JSlider(JSlider.VERTICAL, 1, 100, espessuraAtual);

    /**
     * Enumeração interna que identifica os dois tipos lógicos de ladrilho disponíveis na grade.
     *
     * @author Arthur A.
     * @version 17/11/2026
     * @since 17/11/2026
     */
    private enum TipoTile {
        A, B
    }

    /**
     * Constrói e exibe a janela principal do editor, inicializando os botões,
     * a grade de tiles, os painéis laterais e os ouvintes de eventos.
     */
    public Gui() {
        super("Editor de Tiles");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(LARGURA, ALTURA);
        setLayout(new BorderLayout());

        configurarBotoes();
        criarGrid();
        configurarPainelFerramentas();
        configurarListeners();

        pnlFerramentas.setPreferredSize(new Dimension(100, 50));
        pnlBarraSlider.setPreferredSize(new Dimension(100, 100));

        add(pnlFerramentas, BorderLayout.NORTH);
        add(pnlTiles, BorderLayout.CENTER);
        add(pnlBarraSlider, BorderLayout.WEST);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Carrega, redimensiona e atribui os ícones e dicas de ferramentas (tooltips)
     * a todos os botões da barra superior.
     */
    private void configurarBotoes() {
        int size = (int)(ALTURA*0.03);
        String path = "";
        ImageIcon icone;

        path = "./Icons/IconeSeletor.png";
        icone = new ImageIcon(path);
        icone = getScaledIcon(icone, size, size);
        btnSeletor.setIcon(icone);
        btnSeletor.setToolTipText("Seletor");

        path = "./Icons/IconePincel.png";
        icone = new ImageIcon(path);
        icone = getScaledIcon(icone, size, size);
        btnPincel.setIcon(icone);
        btnPincel.setToolTipText("Pincel");

        path = "./Icons/IconeBorracha.png";
        icone = new ImageIcon(path);
        icone = getScaledIcon(icone, size, size);
        btnBorracha.setIcon(icone);
        btnBorracha.setToolTipText("Borracha");

        path = "./Icons/IconeLimpar.png";
        icone = new ImageIcon(path);
        icone = getScaledIcon(icone, size, size);
        btnLimpar.setIcon(icone);
        btnLimpar.setToolTipText("Limpar Tela");

        path = "./Icons/IconeTrocarTile.png";
        icone = new ImageIcon(path);
        icone = getScaledIcon(icone, size, size);
        btnTrocarTile.setIcon(icone);
        btnTrocarTile.setToolTipText("Trocar Tile");

        path = "./Icons/IconeMostrarTiles.png";
        icone = new ImageIcon(path);
        icone = getScaledIcon(icone, size, size);
        btnMostrarTiles.setIcon(icone);
        btnMostrarTiles.setToolTipText("Mostrar Tile");

        path = "./Icons/IconeReta.png";
        icone = new ImageIcon(path);
        icone = getScaledIcon(icone, size, size);
        btnReta.setIcon(icone);
        btnReta.setToolTipText("Reta");

        path = "./Icons/IconeRetangulo.png";
        icone = new ImageIcon(path);
        icone = getScaledIcon(icone, size, size);
        btnRetangulo.setIcon(icone);
        btnRetangulo.setToolTipText("Retangulo");

        path = "./Icons/IconeCirculo.png";
        icone = new ImageIcon(path);
        icone = getScaledIcon(icone, size, size);
        btnCirculo.setIcon(icone);
        btnCirculo.setToolTipText("Circulo");

        path = "./Icons/IconeTriangulo.png";
        icone = new ImageIcon(path);
        icone = getScaledIcon(icone, size, size);
        btnTriangulo.setIcon(icone);
        btnTriangulo.setToolTipText("Triângulo");

        path = "./Icons/IconeSeletorCores.png";
        icone = new ImageIcon(path);
        icone = getScaledIcon(icone, size, size);
        btnCores.setIcon(icone);
        btnCores.setToolTipText("Seletor de Cores");
    }

    /**
     * Adiciona os botões de ferramentas ao painel superior e os controles de espessura ao painel lateral.
     */
    private void configurarPainelFerramentas() {
        pnlFerramentas.add(btnSeletor);
        pnlFerramentas.add(btnPincel);
        pnlFerramentas.add(btnBorracha);
        pnlFerramentas.add(btnLimpar);
        pnlFerramentas.add(btnTrocarTile);
        pnlFerramentas.add(btnMostrarTiles);
        pnlFerramentas.add(btnReta);
        pnlFerramentas.add(btnRetangulo);
        pnlFerramentas.add(btnCirculo);
        pnlFerramentas.add(btnTriangulo);
        pnlFerramentas.add(btnCores);

        pnlBarraSlider.add(lblEspessura);
        pnlBarraSlider.add(sldrGrossura);
    }

    /**
     * Inicializa a grade de células, alternando inicialmente os tipos de tile (A e B)
     * e instanciando um componente {@link Canvas} independente para cada posição.
     */
    private void criarGrid() {
        pnlTiles = new JPanel(
                new GridLayout(LINHAS, COLUNAS, 5, 5)
        );

        int quantidade = LINHAS * COLUNAS;

        tiles = new JPanel[quantidade];
        canvasTiles = new Canvas[quantidade];
        tiposTiles = new TipoTile[quantidade];

        for (int i = 0; i < quantidade; i++) {
            final int indice = i;

            tiles[i] = new JPanel(new BorderLayout());
            tiles[i].setBorder(
                    BorderFactory.createLineBorder(Color.DARK_GRAY)
            );

            // Distribui os tipos alternadamente: A, B, A, B...
            tiposTiles[i] = (i % 2 == 0) ? TipoTile.A : TipoTile.B;

            canvasTiles[i] = criarCanvas(tiposTiles[i]);
            tiles[i].add(canvasTiles[i], BorderLayout.CENTER);

            // O clique é registrado no Canvas, que ocupa a área da célula.
            canvasTiles[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    tratarCliqueNoTile(indice);
                }
            });

            pnlTiles.add(tiles[i]);
        }
    }

    /**
     * Cria e configura uma nova instância de {@link Canvas} correspondente ao tipo de ladrilho informado.
     *
     * @param tipo o {@link TipoTile} (A ou B) que determina o conjunto de desenhos compartilhados.
     * @return uma nova instância configurada de {@link Canvas}.
     */
    private Canvas criarCanvas(TipoTile tipo) {
        boolean ehTipoA = (tipo == TipoTile.A);

        Canvas canvas = new Canvas(
                ehTipoA,
                modoAtual,
                corAtual,
                espessuraAtual
        );

        canvas.setAoFinalizarDesenho(this::atualizarTodosCanvas);

        return canvas;
    }

    /**
     * Verifica se a ferramenta ativa é a troca de tile e executa a alternância na célula clicada.
     *
     * @param indice a posição da célula clicada no vetor da grade.
     */
    private void tratarCliqueNoTile(int indice) {
        if (modoAtual == Ferramenta.TROCAR_TILE) {
            trocarTipoTile(indice);
        }
    }

    /**
     * Alterna o tipo lógico de uma célula específica entre {@code TipoTile.A} e {@code TipoTile.B},
     * substituindo seu componente {@link Canvas} por um novo vinculado ao tipo atualizado.
     *
     * @param indice o índice da célula na grade a ser alterada.
     */
    private void trocarTipoTile(int indice) {
        if (indice < 0 || indice >= tiles.length) {
            return;
        }

        // Alterna o tipo lógico do tile.
        tiposTiles[indice] = (tiposTiles[indice] == TipoTile.A)
                ? TipoTile.B
                : TipoTile.A;

        // Cria uma nova representação visual para o tipo atualizado.
        Canvas novoCanvas = criarCanvas(tiposTiles[indice]);

        // Substitui somente o Canvas desta posição.
        JPanel celula = tiles[indice];
        celula.removeAll();
        celula.add(novoCanvas, BorderLayout.CENTER);

        canvasTiles[indice] = novoCanvas;

        // O novo Canvas também precisa receber o listener do mouse.
        final int indiceFinal = indice;
        novoCanvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tratarCliqueNoTile(indiceFinal);
            }
        });

        celula.revalidate();
        celula.repaint();
    }

    /**
     * Registra os ouvintes de ação (ActionListeners) e de mudança (ChangeListeners)
     * para todos os botões de ferramentas, seletor de cores e controle deslizante de espessura.
     */
    private void configurarListeners() {
        btnSeletor.addActionListener(e -> definirModo(Ferramenta.SELETOR));

        btnPincel.addActionListener(e -> definirModo(Ferramenta.PINCEL));

        btnBorracha.addActionListener(e -> definirModo(Ferramenta.BORRACHA));

        btnLimpar.addActionListener(e -> {
            int escolha = JOptionPane.showConfirmDialog(null, "limpar TODOS os ladrilhos?",
                    "Limpar Tela", JOptionPane.YES_NO_OPTION);
            if (escolha == JOptionPane.YES_OPTION) {
                // As listas de figuras são compartilhadas: limpa A e B.
                Canvas.limparTodasFiguras();

                // Também descarta desenhos incompletos (ex.: triângulo
                // que ainda recebeu apenas um ou dois cliques).
                for (Canvas canvas : canvasTiles) {
                    canvas.cancelarDesenhoEmAndamento();
                    canvas.repaint();
                }
            }
        });

        btnTrocarTile.addActionListener(e -> definirModo(Ferramenta.TROCAR_TILE));

        btnMostrarTiles.addActionListener(e -> abrirJanelaLadrilho());

        btnReta.addActionListener(e -> definirModo(Ferramenta.RETA));

        btnRetangulo.addActionListener(e -> definirModo(Ferramenta.RETANGULO));

        btnCirculo.addActionListener(e -> definirModo(Ferramenta.CIRCULO));

        btnTriangulo.addActionListener(e -> definirModo(Ferramenta.TRIANGULO));

        // TO DO: Ajeitar metodo !
        btnCores.addActionListener(e -> {
            Color c = JColorChooser.showDialog(null, "Escolha uma cor", Color.WHITE);
            if (c != null){
                corAtual = c; // pega do chooserColor
                for (Canvas canvas : canvasTiles) {
                    canvas.setCorAtual(c);
                }
            }

            //Canvas.setCorAtual(corAtual); // cor atual
            // Tlz fosse legal o background do botão de cor mostrar a cor atual
            btnCores.setBackground(corAtual);
            btnCores.revalidate();
            btnCores.repaint();
        });

        sldrGrossura.addChangeListener(e -> {
            espessuraAtual = sldrGrossura.getValue();
            for (Canvas canvas : canvasTiles) {
                canvas.setEspessura(espessuraAtual);
            }
        });
    }

    /**
     * Atualiza a ferramenta ativa na interface e propaga a mudança para todos os {@link Canvas} da grade.
     *
     * @param novoModo a nova {@link Ferramenta} selecionada pelo usuário.
     */
    private void definirModo(Ferramenta novoModo) {
        modoAtual = novoModo;
        for (Canvas canvas : canvasTiles) {
            canvas.setModoAtual(novoModo);
        }
    }

    /**
     * Converte a configuração atual de tipos de ladrilhos da grade e abre uma instância
     * de {@link JanelaLadrilho} para pré-visualizar o mosaico montado.
     */
    private void abrirJanelaLadrilho() {
        boolean[] tipos = new boolean[tiposTiles.length];
        for (int i = 0; i < tiposTiles.length; i++) {
            tipos[i] = (tiposTiles[i] == TipoTile.A);
        }

        new JanelaLadrilho(LINHAS, COLUNAS, tipos);
    }

    /**
     * Solicita a repintura de todos os componentes {@link Canvas} da grade para sincronizar
     * visualmente os desenhos compartilhados.
     */
    private void atualizarTodosCanvas() {
        for (Canvas canvas : canvasTiles) {
            if (canvas != null) {
                canvas.repaint();
            }
        }
    }

    /**
     * Redimensiona suavemente um ícone para as dimensões especificadas.
     *
     * @param imgIcon o {@link ImageIcon} original a ser redimensionado.
     * @param largura a largura desejada em pixels.
     * @param altura  a altura desejada em pixels.
     * @return um novo {@link ImageIcon} redimensionado, ou {@code null} se o ícone original for nulo.
     */
    private ImageIcon getScaledIcon(ImageIcon imgIcon, int largura, int altura) {
        if (imgIcon == null) {
            return null;
        }

        Image img = imgIcon.getImage().getScaledInstance(
                largura,
                altura,
                Image.SCALE_SMOOTH
        );

        return new ImageIcon(img);
    }

}