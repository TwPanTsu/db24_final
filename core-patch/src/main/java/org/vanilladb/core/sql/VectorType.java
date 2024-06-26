package org.vanilladb.core.sql;

import java.sql.Types;

/**
 * The type of a vector constant.
 */
public class VectorType extends Type {
    private int size;

    VectorType(int size) {
        this.size = size;
    }

    @Override
    public int getSqlType() {
        return Types.ARRAY;
    }

    @Override
    public int getArgument() {
        return size;
    }

    @Override
    public boolean isFixedSize() {
        return true;
    }

    @Override
    public boolean isNumeric() {
        return false;
    }

    @Override
    public int maxSize() {
        return size * Float.BYTES;
    }

    @Override
    public Constant maxValue() {
        // TODO Unfinished
        return new VectorConstant(size, 9999);
    }

    @Override
    public Constant minValue() {
        // TODO Unfinished
        return new VectorConstant(size, -9999);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || !(obj instanceof VectorType))
            return false;
        VectorType t = (VectorType) obj;
        return getSqlType() == t.getSqlType()
                && getArgument() == t.getArgument();
    }
}
