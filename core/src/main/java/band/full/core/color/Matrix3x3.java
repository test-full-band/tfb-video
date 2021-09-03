package band.full.core.color;

import static java.util.Arrays.deepToString;

public final class Matrix3x3 {
    final double[][] values;

    private Matrix3x3(double[][] values) {
        this.values = values;
    }

    public Matrix3x3(
            double r1c1, double r1c2, double r1c3,
            double r2c1, double r2c2, double r2c3,
            double r3c1, double r3c2, double r3c3) {
        values = new double[][] {
            {r1c1, r1c2, r1c3},
            {r2c1, r2c2, r2c3},
            {r3c1, r3c2, r3c3},
        };
    }

    public double get(int row, int col) {
        return values[row][col];
    }

    public Matrix3x3 invert() {
        double[][] v = values;

        double d00 = v[1][1] * v[2][2] - v[1][2] * v[2][1];
        double d01 = v[1][2] * v[2][0] - v[1][0] * v[2][2];
        double d02 = v[1][0] * v[2][1] - v[1][1] * v[2][0];

        double d = v[0][0] * d00 + v[0][1] * d01 + v[0][2] * d02;

        return new Matrix3x3(
                d00 / d,
                (v[0][2] * v[2][1] - v[0][1] * v[2][2]) / d,
                (v[0][1] * v[1][2] - v[0][2] * v[1][1]) / d,
                d01 / d,
                (v[0][0] * v[2][2] - v[0][2] * v[2][0]) / d,
                (v[0][2] * v[1][0] - v[0][0] * v[1][2]) / d,
                d02 / d,
                (v[0][1] * v[2][0] - v[0][0] * v[2][1]) / d,
                (v[0][0] * v[1][1] - v[0][1] * v[1][0]) / d);
    }

    public Matrix3x3 multiply(double multiplier) {
        double[][] m = new double[3][];

        for (int row = 0; row < 3; row++) {
            double[] dstRow = m[row] = new double[3];
            double[] srcRow = values[row];

            for (int col = 0; col < 3; col++) {
                dstRow[col] = srcRow[col] * multiplier;
            }
        }

        return new Matrix3x3(m);
    }

    public double[] multiply(double[] vector) {
        return multiply(vector, new double[3]);
    }

    public double[] multiply(double[] vector, double[] result) {
        double[] srcRow = values[0];
        double r0 = srcRow[0] * vector[0]
                + srcRow[1] * vector[1]
                + srcRow[2] * vector[2];

        srcRow = values[1];
        double r1 = srcRow[0] * vector[0]
                + srcRow[1] * vector[1]
                + srcRow[2] * vector[2];

        srcRow = values[2];
        double r2 = srcRow[0] * vector[0]
                + srcRow[1] * vector[1]
                + srcRow[2] * vector[2];

        result[0] = r0;
        result[1] = r1;
        result[2] = r2;

        return result;
    }

    public Matrix3x3 multiply(Matrix3x3 other) {
        double[][] right = other.values;
        double[][] m = new double[3][];

        for (int row = 0; row < 3; row++) {
            double[] dstRow = m[row] = new double[3];
            double[] srcRow = values[row];

            for (int col = 0; col < 3; col++) {
                double sum = 0;
                for (int i = 0; i < 3; i++) {
                    sum += srcRow[i] * right[i][col];
                }
                dstRow[col] = sum;
            }
        }

        return new Matrix3x3(m);
    }

    public Matrix3x3 multiplyRows(double[] multipliers) {
        double[][] m = new double[3][];

        for (int row = 0; row < 3; row++) {
            double[] dstRow = m[row] = new double[3];
            double[] srcRow = values[row];
            double multiplier = multipliers[row];

            for (int col = 0; col < 3; col++) {
                dstRow[col] = srcRow[col] * multiplier;
            }
        }

        return new Matrix3x3(m);
    }

    public Matrix3x3 multiplyColumns(double[] multipliers) {
        return multiplyColumns(multipliers[0], multipliers[1], multipliers[2]);
    }

    public Matrix3x3 multiplyColumns(double m0, double m1, double m2) {
        double[][] m = new double[3][];

        for (int y = 0; y < 3; y++) {
            double[] dstRow = m[y] = new double[3];
            double[] srcRow = values[y];

            dstRow[0] = srcRow[0] * m0;
            dstRow[1] = srcRow[1] * m1;
            dstRow[2] = srcRow[2] * m2;
        }

        return new Matrix3x3(m);
    }

    @Override
    public String toString() {
        return deepToString(values);
    }
}
