package solver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    private static Complex[][] getInputData(File inFile) {
        float numOfVariables = 0;
        float numberOfEquations;
        String tmpIn;
        String[] tmpStrArr;
        int countRow = 0;
        int countColumn = 0;
        Complex[][] matrix = new Complex[0][];
        Complex tmpMn;
        int matrixSize = 0;
        int readElementsNum = 0;
        assert inFile != null;
        try (Scanner scanner = new Scanner(inFile)) {
            if (scanner.hasNext()) {
                numOfVariables = scanner.nextFloat();
                numberOfEquations = scanner.nextFloat();
                matrix = new Complex[(int) numberOfEquations][(int) (numOfVariables + 1)];
                matrixSize = (int) (numberOfEquations * (numOfVariables + 1));
                scanner.skip("\\s+");
            }
            while (scanner.hasNextLine() || readElementsNum != matrixSize) {
                tmpIn = scanner.nextLine();
                tmpStrArr = tmpIn.split(" ");
                for(String s : tmpStrArr) {
                    tmpMn = Complex.parseComplex(s);
                    matrix[countRow][countColumn] = tmpMn;
                    readElementsNum++;
                    countColumn++;
                    if (countColumn == numOfVariables + 1) {
                        countRow++;
                        countColumn = 0;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No file found: " + inFile.getPath());
            return null;
        }
        return matrix;
    }

    private static void writeSolutionInFile(File outFile, String[] solutionResult) {
        assert outFile != null;
        try (PrintWriter printWriter = new PrintWriter(outFile)) {
            for (String s : solutionResult) {
                printWriter.println(s);
            }
            System.out.println("Saved to file " + outFile.getName());
        } catch (IOException e) {
            System.out.printf("An exception occurs %s", e.getMessage());
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
         if (args.length != 4) {
             System.out.println("Error! Not all argument present! Usage: -in in.txt -out out.txt");
             return;
         }
        File inFile = null;
        File outFile = null;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-in":
                    inFile = new File(args[i + 1]);
                    break;
                case "-out":
                    outFile = new File(args[i + 1]);
                    break;
                default:
                    break;
            }
        }

        System.out.println("Start solving the equation.");
        Matrix matrix;
        Complex[][] tmpMatrix = getInputData(inFile);
        if (tmpMatrix != null) {
            matrix = new Matrix(tmpMatrix);
            Solver solver = new Solver();
            EliminateCommand ec = new EliminateCommand(matrix);
            PrintSolutionCommand psc = new PrintSolutionCommand(matrix);
            solver.setCommand(ec);
            solver.executeCommand();
            solver.setCommand(psc);
            solver.executeCommand();
            writeSolutionInFile(outFile,matrix.getResult());
        }
    }
}

class Solver {
    private SolverCommand command;
    void setCommand(SolverCommand command) { this.command = command; }
    void executeCommand() { command.executeCommand(); }
}

interface SolverCommand { void executeCommand();}

class EliminateCommand implements SolverCommand {
    private final Matrix matrix;
    EliminateCommand(Matrix matrix) { this.matrix = matrix; }
    @Override
    public void executeCommand() { matrix.gjElimination(); }
}

class PrintSolutionCommand implements SolverCommand {
    private final Matrix matrix;
    PrintSolutionCommand(Matrix matrix) { this.matrix = matrix; }
    @Override
    public void executeCommand() { matrix.printSolution(); }
}