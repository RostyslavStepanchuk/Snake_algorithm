package com.codenjoy.dojo.snake.solver;

import com.codenjoy.dojo.snake.model.Elements;

public final class SymbolsMatchKey {
    private final Elements element;
    private final int offsetDirection;

    public SymbolsMatchKey(Elements element, int offsetDirection) {
        if (offsetDirection != 1 && offsetDirection !=0) throw new IllegalArgumentException("Invalid offset direction %d " + offsetDirection);
        this.element = element;
        this.offsetDirection = offsetDirection;
    }

    public Elements getElement() {
        return element;
    }

    public int getOffsetDirection() {
        return offsetDirection;
    }

    @Override
    public int hashCode() {
        return element.hashCode() + offsetDirection * 17;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        SymbolsMatchKey se = (SymbolsMatchKey) obj;
        return (this.offsetDirection == se.offsetDirection && element.equals(se.element));
    }
}
