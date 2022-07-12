package aed.matrix;

import java.util.InputMismatchException;
import java.util.Random;

import aed.tables.OpenAddressingHashTable;

class Plot2D {
	private int x;
	private int y;
	private int hash;

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int x) {
		this.hash -= 31 * (this.x - x);
		this.x = x;
	}

	public void incrementY() {
		++this.y;
		++this.hash;
	}

	public void setY(int y) {
		this.hash -= (this.y - y);
		this.y = y;
	}

	public void incrementX() {
		++this.x;
		this.hash += 31;
	}

	public Plot2D(int x, int y) {
		this.x = x;
		this.y = y;
		this.hash = 31 * x + y;
	}

	private Plot2D(int x, int y, int hash) {
		this.x = x;
		this.y = y;
		this.hash = hash;
	}

	@Override
	public int hashCode() {
		return 31 * x + y;
	}

	public Plot2D clone() {
		return new Plot2D(this.x, this.y, this.hash);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Plot2D) {
			Plot2D that = (Plot2D) other;
			return this.x == that.x && this.y == that.y;
		}
		return false;
	}
}

public class Sparse2DMatrix {
	private int MaxLines;
	private int MaxColumns;
	OpenAddressingHashTable<Plot2D, Float> Matrix;

	public Sparse2DMatrix(int lines, int columns) {
		this.MaxLines = lines;
		this.MaxColumns = columns;
		this.Matrix = new OpenAddressingHashTable<Plot2D, Float>();
	}

	private Sparse2DMatrix(int lines, int columns, OpenAddressingHashTable<Plot2D, Float> Matrix) {
		this.MaxLines = lines;
		this.MaxColumns = columns;
		this.Matrix = Matrix;
	}

	public int getNumberNonZero() {
		return Matrix.size();
	}

	public void put(int line, int column, float value) {
		if (line >= this.MaxLines || column >= this.MaxColumns)
			throw new InputMismatchException("Posicao invalida na Matrix");
		Plot2D Coords = new Plot2D(column, line);
		if (Math.abs(value) == 0)
			this.Matrix.delete(Coords);
		else
			this.Matrix.put(Coords, value);
	}

	public float get(int line, int column) {
		Plot2D Coords = new Plot2D(column, line);
		Float Out = this.Matrix.get(Coords);
		if (Out == null)
			return 0;
		return Out;
	}

	public Sparse2DMatrix scalar(float scalar) {
		OpenAddressingHashTable<Plot2D, Float> Result = new OpenAddressingHashTable<Plot2D, Float>();
		if (Math.abs(scalar) == 0)
			return new Sparse2DMatrix(this.MaxLines, this.MaxColumns, Result);
		for (Plot2D i : this.Matrix.keys()) {
			float result = this.Matrix.get(i) * scalar;
			Result.put(i, result);
		}
		return new Sparse2DMatrix(this.MaxLines, this.MaxColumns, Result);
	}

	public Sparse2DMatrix sum(Sparse2DMatrix that) {
		if (this.MaxColumns != that.MaxColumns || this.MaxLines != that.MaxLines)
			throw new IllegalArgumentException("Tamanho de matrizes diferente.");
		OpenAddressingHashTable<Plot2D, Float> Result = new OpenAddressingHashTable<Plot2D, Float>();
		for (Plot2D i : this.Matrix.keys())
			Result.put(i, this.Matrix.get(i));

		for (Plot2D i : that.Matrix.keys()) {
			float value = Result.get(i) == null ? 0 : Result.get(i);
			value += that.Matrix.get(i);
			if (Math.abs(value) == 0)
				Result.delete(i);
			else
				Result.put(i, value);
		}

		return new Sparse2DMatrix(this.MaxLines, this.MaxColumns, Result);
	}

	public Sparse2DMatrix multiply(Sparse2DMatrix that) {
		if (this.MaxColumns != that.MaxLines)
			throw new IllegalArgumentException("Tamanho de colunas(Matriz 1) e de linhas(Matriz2) diferente.");
		OpenAddressingHashTable<Plot2D, Float> Result = new OpenAddressingHashTable<Plot2D, Float>();
		// x == column y == line
		Plot2D temp;
		for (Plot2D i : this.Matrix.keys()) {
			for (Plot2D j : that.Matrix.keys()) {
				if (i.getX() == j.getY()) {
					temp = new Plot2D(j.getX(), i.getY());
					Float LastValue = Result.get(temp);
					LastValue = LastValue == null ? 0 : LastValue;
					float result = LastValue + (this.Matrix.get(i) * that.Matrix.get(j));
					if (Math.abs(result) == 0)
						Result.delete(temp);
					else
						Result.put(temp, result);
				}
			}
		}
		return new Sparse2DMatrix(this.MaxLines, that.MaxColumns, Result);
	}

	public float[] getNonZeroElements() {
		float[] elements = new float[Matrix.size()];
		int j = 0;
		for (Plot2D i : this.Matrix.keys())
			elements[j++] = this.Matrix.get(i);
		return elements;
	}

	public float[][] getNonSparseMatrix() {
		float[][] NonSparse = new float[this.MaxLines][this.MaxColumns];
		for (Plot2D i : this.Matrix.keys())
			NonSparse[i.getY()][i.getX()] = this.Matrix.get(i);
		return NonSparse;
	}

	private static void BasicTest() {
		System.out.println("Start BasicTest");
		Sparse2DMatrix test = new Sparse2DMatrix(3, 3);
		test.put(2, 0, 1);
		test.put(0, 2, 234);
		test.put(1, 1, (float) 34.523);
		test.put(0, 2, 3);
		System.out.println(test.getNumberNonZero());
		for (float i : test.getNonZeroElements())
			System.out.print(i + " ");
		System.out.println("\n");
		float[][] temp = test.getNonSparseMatrix();
		for (int i = 0; i < temp.length; i++) {
			for (int j = 0; j < temp[i].length; j++) {
				System.out.print(temp[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println("End BasicTest");
		System.out.println();
	}

	private static void SimpleMultiply() {
		System.out.println("Start Multiply");
		Sparse2DMatrix one = new Sparse2DMatrix(3, 2);
		Sparse2DMatrix two = new Sparse2DMatrix(2, 3);
		one.put(0, 0, 2);
		one.put(0, 1, 3);
		one.put(1, 0, -9);
		one.put(1, 1, 0);
		one.put(2, 0, 0);
		one.put(2, 1, 4);

		two.put(0, 0, 3);
		two.put(0, 1, -2);
		two.put(0, 2, 5);
		two.put(1, 0, 3);
		two.put(1, 1, 0);
		two.put(1, 2, 4);
		Sparse2DMatrix mult = one.multiply(two);
		float[][] temp = mult.getNonSparseMatrix();
		// float[][] temp = multiplicar(one.getNonSparseMatrix(),
		// two.getNonSparseMatrix());
		for (int i = 0; i < temp.length; i++) {
			for (int j = 0; j < temp[i].length; j++) {
				System.out.print(temp[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println("End Multiply");
		System.out.println();
	}

	private static Sparse2DMatrix GenerateSecond(int FixedLines, int MaxColumns) {
		Random r = new Random();
		int ActualColumns = r.nextInt(MaxColumns + 1);
		Sparse2DMatrix First = new Sparse2DMatrix(FixedLines, ActualColumns);
		int TotalPoints = FixedLines * ActualColumns;
		for (int i = 0; i <= TotalPoints * 0.01; i++) {
			First.put(r.nextInt(FixedLines), r.nextInt(ActualColumns), r.nextFloat());
		}
		return First;
	}

	private static Sparse2DMatrix GenerateFirst(int MaxLines, int FixedColumns) {
		Random r = new Random();
		int ActualLines = r.nextInt(MaxLines + 1);
		Sparse2DMatrix First = new Sparse2DMatrix(ActualLines, FixedColumns);
		int TotalPoints = ActualLines * FixedColumns;
		for (int i = 0; i <= Math.ceil(TotalPoints * 0.01); i++) {
			First.put(r.nextInt(ActualLines), r.nextInt(FixedColumns), r.nextFloat());
		}
		return First;
	}

	private static float[][] multiplicar(float[][] A, float[][] B) {
		int aRows = A.length;
		int aColumns = A[0].length;
		int bRows = B.length;
		int bColumns = B[0].length;

		if (aColumns != bRows)
			throw new IllegalArgumentException("Tamanho de colunas(Matriz 1) e de linhas(Matriz2) diferente.");

		float[][] C = new float[aRows][bColumns];
		for (int i = 0; i < aRows; i++)
			for (int j = 0; j < bColumns; j++)
				C[i][j] = 0;

		for (int i = 0; i < aRows; i++)// aRow
			for (int j = 0; j < bColumns; j++)// bColumn
				for (int k = 0; k < aColumns; k++)// aColumn
					C[i][j] += A[i][k] * B[k][j];

		return C;
	}

	private static double Ratio(Sparse2DMatrix First, Sparse2DMatrix Second) {
		float[][] First_notSparse = First.getNonSparseMatrix();
		float[][] Second_notSparse = Second.getNonSparseMatrix();
		long finalSparse = 0;
		long finalNormal = 0;

		for (int i = 0; i < 30; i++) {
			long timeSparse = System.nanoTime();
			First.multiply(Second);
			finalSparse += (System.nanoTime() - timeSparse);

			long timeNormal = System.nanoTime();
			multiplicar(First_notSparse, Second_notSparse);
			finalNormal += (System.nanoTime() - timeNormal);
		}

		finalSparse /= 30;
		finalNormal /= 30;

		System.out.println("TimeSparse(nano) : " + finalSparse + "  TimeNormal(nano) : " + finalNormal);
		return finalNormal / (double) finalSparse;
	}

	private static void MultiplyTest() {
		for (int i = 700; true; i += 100) {
			Sparse2DMatrix First = GenerateFirst(i, i);
			Sparse2DMatrix Second = GenerateSecond(i, i);
			System.out.println();
			System.out.println("Matriz 1 = " + First.MaxLines + "x" + First.MaxColumns);
			System.out.println("Matriz 2 = " + Second.MaxLines + "x" + Second.MaxColumns);
			System.out.println("Normal is " + Ratio(First, Second) + " times slower than sparse.");
		}

	}

	public static void main(String[] args) {
		BasicTest();
		SimpleMultiply();
		MultiplyTest();
	}
}

/*
 * Matriz 1 = 49x100 Matriz 2 = 100x61 TimeSparse(nano) : 281769
 * TimeNormal(nano) : 647475 Normal is 2.297892954867285 times slower than
 * sparse.
 * 
 * Matriz 1 = 158x200 Matriz 2 = 200x3 TimeSparse(nano) : 83071 TimeNormal(nano)
 * : 108366 Normal is 1.3044985614715123 times slower than sparse.
 * 
 * Matriz 1 = 60x300 Matriz 2 = 300x102 TimeSparse(nano) : 1183387
 * TimeNormal(nano) : 2170344 Normal is 1.8340103448829503 times slower than
 * sparse.
 * 
 * Matriz 1 = 121x400 Matriz 2 = 400x200 TimeSparse(nano) : 2507665
 * TimeNormal(nano) : 13779812 Normal is 5.495076894242254 times slower than
 * sparse.
 * 
 * Matriz 1 = 227x500 Matriz 2 = 500x478 TimeSparse(nano) : 14971197
 * TimeNormal(nano) : 87357174 Normal is 5.83501599771882 times slower than
 * sparse.
 * 
 * Matriz 1 = 249x600 Matriz 2 = 600x474 TimeSparse(nano) : 31940952
 * TimeNormal(nano) : 113519874 Normal is 3.554054180977449 times slower than
 * sparse.
 * 
 * Matriz 1 = 278x700 Matriz 2 = 700x679 TimeSparse(nano) : 79501037
 * TimeNormal(nano) : 237164677 Normal is 2.9831645718030066 times slower than
 * sparse.
 * 
 * Matriz 1 = 23x800 Matriz 2 = 800x735 TimeSparse(nano) : 12492922
 * TimeNormal(nano) : 29537932 Normal is 2.3643733627729366 times slower than
 * sparse.
 * 
 * Matriz 1 = 70x900 Matriz 2 = 900x355 TimeSparse(nano) : 17982872
 * TimeNormal(nano) : 37218177 Normal is 2.0696458830380373 times slower than
 * sparse.
 * 
 * Matriz 1 = 230x1000 Matriz 2 = 1000x288 TimeSparse(nano) : 46642867
 * TimeNormal(nano) : 100099322 Normal is 2.1460799568774362 times slower than
 * sparse.
 */