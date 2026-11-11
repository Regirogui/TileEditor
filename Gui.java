import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

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

    private final JSlider sldrGrossura = new JSlider(JSlider.VERTICAL, 1, 100, espessuraAtual);
    //private final JLabel lblEspessura = new JLabel("Espessura");

    private enum TipoTile {
        A, B
    }

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

        pnlBarraSlider.add(sldrGrossura);
        //pnlBarraSlider.add(lblEspessura);
    }

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

    /*
     * Pressuposto: o primeiro parâmetro booleano do seu Canvas indica
     * se ele representa o tipo A (true) ou o tipo B (false).
     *
     * Se o booleano tiver outro significado na sua classe Canvas,
     * ajuste esta linha conforme o construtor real.
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

    private void tratarCliqueNoTile(int indice) {
        if (modoAtual == Ferramenta.TROCAR_TILE) {
            trocarTipoTile(indice);
        }
    }

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

    private void configurarListeners() {
        btnSeletor.addActionListener(e -> definirModo(Ferramenta.SELETOR));

        btnPincel.addActionListener(e -> definirModo(Ferramenta.PINCEL));

        btnBorracha.addActionListener(e -> definirModo(Ferramenta.BORRACHA));

        btnTrocarTile.addActionListener(e -> definirModo(Ferramenta.TROCAR_TILE));

        btnMostrarTiles.addActionListener(e -> definirModo(Ferramenta.MOSTRAR_TILES));

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

    private void definirModo(Ferramenta novoModo) {
        modoAtual = novoModo;
        for (Canvas canvas : canvasTiles) {
            canvas.setModoAtual(novoModo);
        }
    }

    private void atualizarTodosCanvas() {
        for (Canvas canvas : canvasTiles) {
            if (canvas != null) {
                canvas.repaint();
            }
        }
    }

    /**
     * Redimensiona uma imagem usada como ícone.
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
