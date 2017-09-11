package org.mini.rx;

import java.io.Serializable;

public class Var<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private T value;

    public Var() {
    }

    public Var(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Var<?> var = (Var<?>) o;

        return value != null ? value.equals(var.value) : var.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Var{" +
                "value=" + value +
                '}';
    }
}
