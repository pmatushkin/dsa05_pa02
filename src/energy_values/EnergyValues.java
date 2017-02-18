import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

class Equation {
    Equation(double a[][], double b[]) {
        this.a = a;
        this.b = b;
    }

    double a[][];
    double b[];
}

class Position {
    Position(int column, int row) {
        this.column = column;
        this.row = row;
    }

    int column;
    int row;
}

class EnergyValues {
    static Equation ReadEquation() throws IOException {
        Scanner scanner = new Scanner(System.in);
        int size = scanner.nextInt();

        double a[][] = new double[size][size];
        double b[] = new double[size];
        for (int row = 0; row < size; ++row) {
            for (int column = 0; column < size; ++column)
                a[row][column] = scanner.nextInt();
            b[row] = scanner.nextInt();
        }
        return new Equation(a, b);
    }

    static Position SelectPivotElement(double a[][], int column) {
        // This algorithm selects the first free element.
        // You'll need to improve it to pass the problem.
        int free = -1;
        int size = a.length;

        // Take leftmost non-zero entry
        for (int i = column; i < size; i++) {
            if (a[i][column] != 0) {
                free = i;

                break;
            }
        }

        return new Position(column, free);
    }

    static void SwapLines(double a[][], double b[], boolean used_rows[], Position pivot) {
        int size = a.length;

        for (int column = 0; column < size; ++column) {
            double tmpa = a[pivot.column][column];
            a[pivot.column][column] = a[pivot.row][column];
            a[pivot.row][column] = tmpa;
        }

        double tmpb = b[pivot.column];
        b[pivot.column] = b[pivot.row];
        b[pivot.row] = tmpb;

        boolean tmpu = used_rows[pivot.column];
        used_rows[pivot.column] = used_rows[pivot.row];
        used_rows[pivot.row] = tmpu;

        pivot.row = pivot.column;
    }

    static void ProcessPivotElement(double a[][], double b[], Position pivot) {
        // Write your code here
        int size = a.length;
        double pivot_value = a[pivot.row][pivot.column];

        for (int i = pivot.row + 1; i < size; i++) {
            double scale = a[i][pivot.column] / pivot_value;

            // Subtract row from rows below to make other entries in pivot column equal to 0
            for (int j = pivot.column; j < size; j++) {
                a[i][j] -= a[pivot.row][j] * scale;
            }

            b[i] -= b[pivot.row] * scale;
        }
    }

    static void MarkPivotElementUsed(Position pivot_element, boolean used_rows[], boolean used_columns[]) {
        used_rows[pivot_element.row] = true;
        used_columns[pivot_element.column] = true;
    }

    static double[] SolveEquation(Equation equation) {
        double a[][] = equation.a;
        double b[] = equation.b;
        int size = a.length;

        boolean[] used_columns = new boolean[size];
        boolean[] used_rows = new boolean[size];
        for (int step = 0; step < size; ++step) {
            Position pivot_element = SelectPivotElement(a, step);
            SwapLines(a, b, used_rows, pivot_element);
            ProcessPivotElement(a, b, pivot_element);
            MarkPivotElementUsed(pivot_element, used_rows, used_columns);
        }

        // build the answer starting from the last row
        // now in every row i pivot element is at a[i][i]
        for (int i = size - 1; i >= 0; i--) {

            for (int j = i + 1; j < size; j++) {
                // at this step we know the solutions for the rows below i
                // so we use them to reduce the solutions for the row i
                b[i] -= b[j] * a[i][j];
            }

            // scale down the solution for the row i by the value of the pivot
            b[i] /= a[i][i];
        }

        return b;
    }

    static void PrintColumn(double column[]) {
        int size = column.length;
        for (int row = 0; row < size; ++row)
            System.out.printf("%.20f\n", column[row]);
    }

    public static void main(String[] args) throws IOException {
        Equation equation = ReadEquation();
        double[] solution = SolveEquation(equation);
        PrintColumn(solution);
    }
}
