import java.awt.*;
import java.awt.Color;
import javax.swing.*;

public class Gui extends JFrame {
    // constantes
    final int ALTURA = 854;
    final int LARGURA = 854;
    
    // Atributos
    Ferramenta modoAtual = Ferramenta.SELETOR;
    Color corAtual = Color.BLACK;
    
    // Componentes de GUI
    // -Paineis
    JPanel pnlFerramentas = new JPanel(); // Painel de ferramentas (Topo)
    JPanel pnlCanvas = new JPanel(); // Painel principal de desenho e pre-visulização dos tiles
    JPanel pnlBarraSlider = new JPanel(); // Painel para controlar a grossura do pincel
    // -Botões
    // --Ferramentas
    JButton btnSeletor = new JButton();
    JButton btnPincel = new JButton();
    JButton btnBorracha = new JButton();
    JButton btnTrocarTile = new JButton();
    JButton btnMostrarTiles = new JButton();
    // --Formas
    JButton btnReta = new JButton();
    JButton btnRetangulo = new JButton();
    JButton btnCirculo = new JButton();
    JButton btnTriangulo = new JButton();
    JButton btnPoligono = new JButton();
    // -Slider
    JSlider sldrGrossura = new JSlider(JSlider.VERTICAL, 1, 100, 50);

    // CONSTRUTOR:
    public Gui() {
        super("Editor de Tiles");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setSize(LARGURA, ALTURA);
        setLayout(new BorderLayout());

        // Configuração Botões
        // TO DO: Botões escalonarem com o tamanho da tela
        btnSeletor.setIcon(new ImageIcon("./Icons/IconeSeletor.png"));
        btnSeletor.setToolTipText("Seletor");
        btnPincel.setIcon(new ImageIcon("./Icons/IconePincel.png"));
        btnPincel.setToolTipText("Pincel");
        btnBorracha.setIcon(new ImageIcon("./Icons/IconeBorracha.png"));
        btnBorracha.setToolTipText("Borracha");
        btnTrocarTile.setIcon(new ImageIcon("./Icons/IconeTrocarTile.png"));
        btnTrocarTile.setToolTipText("Trocar Tile");
        btnMostrarTiles.setIcon(new ImageIcon("./Icons/IconeMostrarTiles.png"));
        btnMostrarTiles.setToolTipText("Previsualizacao de Tiles");
        btnReta.setIcon(new ImageIcon("./Icons/IconeReta.png"));
        btnReta.setToolTipText("Reta");
        btnRetangulo.setIcon(new ImageIcon("./Icons/IconeRetangulo.png"));
        btnRetangulo.setToolTipText("Retangulo");
        btnCirculo.setIcon(new ImageIcon("./Icons/IconeCirculo.png"));
        btnCirculo.setToolTipText("Circulo");
        btnTriangulo.setIcon(new ImageIcon("./Icons/IconeTriangulo.png"));
        btnTriangulo.setToolTipText("Triangulo");
        btnPoligono.setIcon(new ImageIcon("./Icons/IconePoligono.png"));
        btnPoligono.setToolTipText("Poligono");
        // Configuração Slider

        // Configuração Paineis
        // -Layout
        pnlFerramentas.setLayout(new FlowLayout());
        pnlCanvas.setLayout(new GridLayout(3, 3));
        pnlBarraSlider.setLayout(new FlowLayout());
        // -Adicionando Componentese
        // --Centro (Tiles)
        pnlCanvas.add(new JLabel()); // JLabel → placeholder de Tiles
        pnlCanvas.add(new JLabel());
        pnlCanvas.add(new JLabel());
        pnlCanvas.add(new JLabel());
        pnlCanvas.add(new Canvas()); // Campo de Desenho
        pnlCanvas.add(new JLabel());
        pnlCanvas.add(new JLabel());
        pnlCanvas.add(new JLabel());
        pnlCanvas.add(new JLabel());
        // --Norte (Barra de ferarmentas)
        pnlFerramentas.add(btnSeletor);
        pnlFerramentas.add(btnPincel);
        pnlFerramentas.add(btnBorracha);
        pnlFerramentas.add(btnTrocarTile);
        pnlFerramentas.add(btnMostrarTiles);
        pnlFerramentas.add(btnReta);
        pnlFerramentas.add(btnRetangulo);
        pnlFerramentas.add(btnCirculo);
        pnlFerramentas.add(btnTriangulo);
        pnlFerramentas.add(btnPoligono);
        // --Oeste (Barra Deslizante)
        pnlBarraSlider.add(sldrGrossura);
        // -Tamanho
        pnlFerramentas.setPreferredSize(new Dimension(100, 50));
        pnlCanvas.setPreferredSize(new Dimension(100, 100));
        pnlBarraSlider.setPreferredSize(new Dimension(100, 100));
        // -Cores para depuração
        pnlFerramentas.setBackground(Color.red);
        pnlCanvas.setBackground(Color.green);
        pnlBarraSlider.setBackground(Color.blue);

        // Adição de Paineis
        this.add(pnlFerramentas, BorderLayout.NORTH);
        this.add(pnlCanvas, BorderLayout.CENTER);
        this.add(pnlBarraSlider, BorderLayout.WEST);
    }
    // MOVER METODO DAQUI!
    /**
     * Redimensiona as imagens ícone
     * @param imgIcon imagem-ícone original a ser redimensionada
     * @param x nova altura da imagem após o redimensionamento
     * @param y nova largura da imagem após o redimensionamento
     */
    private ImageIcon getScaledIcon(ImageIcon imgIcon, int x, int y){
        Image img = imgIcon.getImage();
        img = img.getScaledInstance(x, y, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
}