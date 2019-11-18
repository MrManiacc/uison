package me.jraynor.uison.parser;

public class Prop<T> {
    private String id;
    private T val;

    public Prop(String id, T val) {
        this.id = id;
        this.val = val;
    }

    public T get() {
        return (T) val;
    }

    public void set(T val) {
        this.val = val;
    }

    public String id() {
        return id;
    }
}
