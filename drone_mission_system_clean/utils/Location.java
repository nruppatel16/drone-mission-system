package utils;

import java.util.Objects;

public class Location {
    public int row, col;

    public Location(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // same reference
        if (o == null || getClass() != o.getClass()) return false;
        Location other = (Location) o;
        return this.row == other.row && this.col == other.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    public int getX() {
        return col;
    }

    public int getY() {
        return row;
    }

    public void setX(int x) {
        this.col = x;
    }

    public void setY(int y) {
        this.row = y;
    }
}
