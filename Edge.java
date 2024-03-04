// PROG2 VT2022, Inlämningsuppgift, del 2
// Grupp 064
// abal 7627
// vela 1859
// guhe 8938


import java.io.Serializable;

public class Edge<T> implements Serializable {

    private static final int MINIMUM_WEIGHT = 0;
    private T destination;
    private T source;
    private String name;
    private int weight;


    public Edge(T destination, String name, int weight, T source) {
        this.destination = destination;
        this.name = name;
        this.weight = weight;
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }

    public T getDestination() {
        return destination;
    }


    public void setWeight(int weight) {
        if (weight < MINIMUM_WEIGHT) {
            throw new IllegalArgumentException("Invalid weight");
        } else {
            this.weight = weight;
        }
    }

    public String toString() {
        return String.format(" till " + destination +" med %s tar %d", name, weight);
    }
}