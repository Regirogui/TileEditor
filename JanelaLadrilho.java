import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/** Janela que monta uma prévia do mosaico com os desenhos reais dos Canvas. */
public class JanelaLadrilho extends JFrame {
    private static final int MEDIDA_LADRILHO = 220;

    public JanelaLadrilho(int linhas, int colunas, boolean[] tiposA) {
        super("Ladrilho montado");
        if (linhas <= 0 || colunas <= 0 || tiposA == null
                || tiposA.length != linhas * colunas) {
            throw new IllegalArgumentException("Dimensões ou tipos de ladrilho inválidos.");
        }

        JPanel grade = new JPanel(new GridLayout(linhas, colunas, 2, 2));
        grade.setBackground(Color.DARK_GRAY);

        for (boolean tipoA : tiposA) {
            JPanel celula = new JPanel(new BorderLayout());
            celula.setBackground(Color.WHITE);
            celula.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            celula.add(new PainelPrevia(tipoA), BorderLayout.CENTER);
            grade.add(celula);
        }

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(grade);
        setSize(colunas * MEDIDA_LADRILHO + 20,
                linhas * MEDIDA_LADRILHO + 40);
        setMinimumSize(new Dimension(300, 250));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /** Painel somente de visualização; os desenhos continuam guardados no Canvas. */
    private static class PainelPrevia extends JPanel {
        private final boolean tipoA;

        PainelPrevia(boolean tipoA) {
            this.tipoA = tipoA;
            setBackground(Color.WHITE);
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            Canvas.desenharFiguras(g2, tipoA, getWidth(), getHeight());
            g2.dispose();
        }
    }
}
