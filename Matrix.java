package solver;

public class Matrix {
    private final int numOfVariables;
    private final int numOfEquations;
    private final int numOfColumns;
    private final Complex[][] matrix;
    private final Complex[] solution;
    private final int[] columnChanges;
    private int statusSolve = -1;//0 - no solution, 1 - one solution, 2 - many solutions
    private String[] result;

    public Matrix(Complex[][] matrix) {
        numOfColumns = matrix[0].length;
        numOfVariables = matrix[0].length - 1;
        numOfEquations = matrix.length;
        this.matrix = matrix.clone();
        solution = new Complex[numOfVariables];
        result = new String[numOfVariables];
        columnChanges = new int[numOfVariables];
        for (int i = 0; i < columnChanges.length; i++) {
            columnChanges[i] = i;
        }
    }

    public String[] getResult() {
        return result;
    }

    private  void printCurrentMatrix() {
        for (int i = 0; i < numOfEquations; i++) {
            for (int j = 0; j < numOfColumns; j++) {
                System.out.print(matrix[i][j] + "  ");
            }
            System.out.println();
        }
    }

    private  void swapRow(int r1, int r2) {
        for (int i = 0; i < numOfColumns; i++) {
            Complex temp = matrix[r1][i];
            matrix[r1][i] = matrix[r2][i];
            matrix[r2][i] = temp;
        }
    }

    private  void swapColumn(int c1, int c2) {
        for (int i = 0; i < numOfEquations; i++) {
            Complex temp = matrix[i][c1];
            matrix[i][c1] = matrix[i][c2];
            matrix[i][c2] = temp;
        }
        int tmp = columnChanges[c1];        //save new positions columns
        columnChanges[c1] = columnChanges[c2];
        columnChanges[c2] = tmp;
    }

    boolean findPivotElement(int index) {
        boolean result = false;
        int c = 1;
        int r = 1;
        while ((index + c) < numOfEquations && matrix[index + c][index].isZero()) {//search  in this column first non-zero element
            c++;
        }
        if (index + c == numOfEquations) {       //if didnt find in column, search in row right of column
            c = 1;
            while ((index + c) < numOfVariables && matrix[index][index + c].isZero()) {//search  in this row first non-zero element
                c++;
            }
            if (index + c == numOfVariables) {  // if didnt find and in row, searching in bottom-right corner
                c = 1;
                while (index + c < numOfEquations && matrix[index + r][index + c].isZero()) { //search  in this column first non-zero element
                    r++;
                    if (index + r == numOfEquations) {       //didnt find, go to next column
                        c++;
                        r = 1;
                    } else {            //found, swap row and column so the non-zero element be a pivot element
                        System.out.println("Swap R"+ (index + 1)+" <--> R" + (index + r + 1));
                        swapRow(index, index + r);
                        System.out.println("Swap С"+ (index + 1)+" <--> С" + (index + c + 1));
                        swapColumn(index, index + c);
                        result = true;
                    }
                }
            } else {    //if found, swap columns so the non-zero element be a pivot element
                System.out.println("Swap С"+ (index + 1)+" <--> С" + (index + c + 1));
                swapColumn(index, index + c);
                result = true;
            }
        } else {    //if found, swap rows so the non-zero element be a pivot element
            System.out.println("Swap R"+ (index + 1)+" <--> R" + (index + c + 1));
            swapRow(index, index + c);
            result = true;
        }
        return result;
    }

    void gjElimination() {
        for (int i = 0; i < numOfVariables; i++) {   //go by each column matrix
            if (i < numOfEquations) {               // for case when num equations < num variables!
                if (matrix[i][i].isZero()) {            //check diagonal element of column (pivot)
                    if (!findPivotElement(i)) {     //if have not found a replacement for it, then there is nothing more to count (there are only zeros left)
                        break;
                    }
                }

                for (int j = 0; j < numOfEquations; j++) {   //for each row (in current column)
                    if (i != j) {               //for non pivot elements (current column)
                        //count coefficient: divide first non diagonal element in this column (this we want to zeroed)
                        //by diagonal element (pivot) of this column Hes precisely non zero, see check above.
                        Complex p = matrix[j][i].divides(matrix[i][i]);          //now let's go through all the elements of the row where the
                        for (int z = 0; z <= numOfVariables; z++) {     // element that is currently being zeroed is located
                            //Complex tmp = matrix[i][z].times(p);
                            matrix[j][z] = matrix[j][z].minus(matrix[i][z].times(p));//and substract from them is first row values (corresponding to column), multiplied by coefficient
                        }
                        if (!p.isZero()) {
                            System.out.println(p + " * R" + (i +1) + " - R" + (j +1 ) + " -> R" + (j + 1));
                        }
                    }
                }
            }
        }
        checkSolution();
    }

    void checkSolution() {
        boolean zeroRow;
        int countSignificantEquations = 0;
        for (int i = 0; i < numOfEquations; i++) {
            zeroRow = false;
            for (int j = 0; j < numOfVariables; j++) {
                if (!matrix[i][j].isZero()) {
                    zeroRow = true;
                    break;
                }
            }
            if (!zeroRow && !matrix[i][numOfColumns - 1].isZero()) {
                statusSolve = 0; // No solution
                result = new String[]{"No solutions"};
                break;
            } else if (zeroRow) {
                countSignificantEquations++;
            }
        }

        if (statusSolve != 0) {
            if (countSignificantEquations == numOfVariables) {
                statusSolve = 1;    // One solution
                for (int j = 0; j < numOfVariables; j++) {
                    solution[j] = matrix[j][numOfVariables].divides(matrix[j][j]);
                }
                for (int z = 0; z < result.length; z++) {      //if the columns were swapped then rearrange the result accordingly
                    result[z] = String.valueOf(solution[columnChanges[z]]);
                }
            } else if (countSignificantEquations < numOfVariables) {
                statusSolve = 2;    // Infinitely many solutions
                //TODO: get common solution
                result = new String[]{"Infinitely many solutions"};
            }
        }
    }

    public void printSolution()
    {
        if (statusSolve == 2) {
            System.out.println("Infinitely many solutions");
        }
        if (statusSolve == 0) {
            System.out.println("No Solution");
        } else if (statusSolve == 1) {
            System.out.print("The solution is: (");
            for (int i = 0; i < numOfVariables; i++) {
                if (i < numOfVariables - 1) {
                    System.out.print(solution[i] + ", ");
                } else {
                    System.out.print(solution[i]);
                }
            }
            System.out.println(")");
        }
    }
}

