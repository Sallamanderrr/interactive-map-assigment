// PROG2 VT2022, Inlämningsuppgift, del 2
// Grupp 064
// abal 7627
// vela 1859
// guhe 8938


import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


public class City extends Circle {

    private final String name;
    private boolean saved;

    public City(String name, double x, double y) {
        super(x, y, 7, Color.BLUE);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isSaved() {
        return saved;
    }

    public void save(){
        saved = true;
    }

    @Override
    public Node getStyleableNode() {
        return super.getStyleableNode();
    }

}