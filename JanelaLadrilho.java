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

/**
 * Janela secundária que exibe uma pré-visualização montada do mosaico de ladrilhos,
 * combinando os desenhos reais armazenados nos {@link Canvas} dos tipos A e B.
 *
 * @author Arthur A.
 * @version 17/11/2026
 * @since 17/11/2026
 */
public class JanelaLadrilho extends JFrame {
    private static final int MEDIDA_LADRILHO = 220;

    /**
     * Constrói e exibe a janela de pré-visualização do mosaico de ladrilhos.
     *
     * @param linhas  a quantidade de linhas da grade (deve ser maior que zero).
     * @param colunas a quantidade de colunas da grade (deve ser maior que zero).
     * @param tiposA  vetor booleano indicando o tipo de cada célula ({@code true} para tipo A,
     *                {@code false} para tipo B), cujo tamanho deve ser igual a {@code linhas * colunas}.
     * @throws IllegalArgumentException se as dimensões forem menores ou iguais a zero, se o vetor
     *                                  {@code tiposA} for nulo ou se seu tamanho não corresponder à grade.
     */
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

    /**
     * Painel interno somente de visualização que renderiza de forma escalada
     * os desenhos compartilhados armazenados na classe {@link Canvas}.
     *
     * @author Arthur A.
     * @version 17/11/2026
     * @since 17/11/2026
     */
    private static class PainelPrevia extends JPanel {
        private final boolean tipoA;

        /**
         * Constrói um painel de pré-visualização para um tipo específico de ladrilho.
         *
         * @param tipoA {@code true} para exibir os desenhos do Tile A; {@code false} para o Tile B.
         */
        PainelPrevia(boolean tipoA) {
            this.tipoA = tipoA;
            setBackground(Color.WHITE);
            setOpaque(true);
        }

        /**
         * Renderiza as figuras associadas ao tipo deste painel ajustadas às suas dimensões atuais.
         *
         * @param g o contexto gráfico {@link Graphics} fornecido pelo Swing.
         */
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