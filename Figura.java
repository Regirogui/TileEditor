import java.awt.Color;

/**
 * Estrutura de Dados para o armazenamento de figuras primitivas
 *
 * @author Arthur Anon
 * @version 31-10-2026
 */
public class Figura {
    Ferramenta tipo;
    int x1, y1, x2, y2, x3, y3;
    int raio;
    Color cor;
    int espessura;

    public Figura(Ferramenta tipo, int x1, int x2, int y2, int x3, int y3, int raio,
                           Color cor, int espessura) {
        this.tipo = tipo;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.x3 = x3;
        this.y3 = y3;
        this.raio = raio;
        this.cor = cor;
        this.espessura = espessura;
    }
    public String toString(){
        return "TIPO: "+ this.tipo+"\nP1: "+this.x1+", "+this.y1+"\nP2: "+this.x2+", "+this.y2+"\nP3: "+this.x3+", "+this.y3+"\nRAIO:"+this.raio+"\nCor: "+this.cor.getRGB()+"\nEspessura: "+this.espessura;
    }
}