package aed.collections;

import java.util.Random;

import aed.sorting.Sort;

@SuppressWarnings("rawtypes")
public class MinPriorityQueue<T extends Comparable<T>> extends Sort {
	private static final int MIN_MAXSIZE = 64;
	private static final int START = 1;
	private Comparable<T>[] heap;
	private int size;

	@SuppressWarnings("unchecked")
	public MinPriorityQueue() {
		this.heap = new Comparable[MIN_MAXSIZE + 1];
		this.size = 0;
	}

	public MinPriorityQueue<T> clone() {
		MinPriorityQueue<T> result = new MinPriorityQueue<T>(this.heap.length);
		result.size = this.size;
		result.heap = this.heap.clone();
		return result;
	}

	@SuppressWarnings("unchecked")
	public MinPriorityQueue(int initialMaxSize) {
		if (initialMaxSize < MIN_MAXSIZE)
			initialMaxSize = MIN_MAXSIZE;
		this.heap = new Comparable[initialMaxSize + 1];
		this.size = 0;
	}

	@SuppressWarnings("unchecked")
	public MinPriorityQueue(T[] a) {
		int size = a.length * 2;
		if (size < MIN_MAXSIZE)
			size = MIN_MAXSIZE;
		this.heap = new Comparable[size + 1];
		for (int i = 0; i < a.length; i++)
			this.heap[i + 1] = a[i];
		heapify(this.heap, a.length);
		this.size = a.length;
	}

	public Comparable[] getElements() {
		return this.heap;
	}

	private static void heapifyDown(Comparable[] a, int n, int i) {
		while (true) {
			int menor = i, child1 = 2 * i, child2 = (2 * i) + 1;
			if (child1 <= n && less(a[child1], a[menor]))
				menor = child1;
			if (child2 <= n && less(a[child2], a[menor]))
				menor = child2;
			if (menor == i)
				return;
			exchange(a, i, menor);
			i = menor;
		}
	}

	private static void heapifyUp(Comparable[] a, int i) {
		while (1 < i) {
			int dad = i / 2;
			if (less(a[i], a[dad]))
				exchange(a, i, dad);
			else
				return;
			i /= 2;
		}
	}

	private static void heapify(Comparable[] a, int n) {
		for (int i = (n / 2); i > 0; i--) {
			heapifyDown(a, n, i);
		}

	}

	public static <T extends Comparable<T>> boolean isMinHeap(T[] a, int n) {
		for (int i = 1; 2 * i <= n; i++) {
			if (less(a[2 * i], a[i]) || ((2 * i) + 1 <= n && less(a[(2 * i) + 1], a[i])))
				return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private void resize_up() {
		Comparable<T>[] heap = new Comparable[((this.heap.length - 1) * 2) + 1];
		for (int i = 1; i <= this.size; i++)
			heap[i] = this.heap[i];
		this.heap = heap;
	}

	@SuppressWarnings("unchecked")
	private void resize_down() {
		int newSize = (this.heap.length - 1) / 2;
		if (newSize < MIN_MAXSIZE)
			newSize = MIN_MAXSIZE;
		Comparable<T>[] heap = new Comparable[newSize + 1];
		for (int i = 1; i <= this.size; i++)
			heap[i] = this.heap[i];
		this.heap = heap;
	}

	public void insert(T t) {
		if (this.size == heap.length - 1)
			resize_up();
		this.heap[++this.size] = t;
		heapifyUp(this.heap, this.size);
	}

	@SuppressWarnings("unchecked")
	public T peekMin() {
		if (this.isEmpty())
			return null;
		return (T) this.heap[START];
	}

	@SuppressWarnings("unchecked")
	public T removeMin() {
		if (this.isEmpty())
			return null;
		T result = (T) this.heap[START];
		this.heap[START] = this.heap[this.size--];
		heapifyDown(this.heap, this.size, 1);
		if (this.size <= (this.heap.length - 1) / 4)
			resize_down();
		return result;
	}

	public boolean isEmpty() {
		return this.size == 0;
	}

	public int size() {
		return this.size;
	}

	public int getMaxSize() {
		return heap.length - 1;
	}
	
	private static Long[] generateExample(int n){
		Random r = new Random();
		Long[] examples = new Long[n];
		for(int i = 0; i < n; i++)
		{
				examples[i] = r.nextLong();
		}
		return examples;
	}

	@SuppressWarnings("unused")
	private static double calculateAverageExecutionTimeInsert(int n) {
		Random temp = new Random();
		int trials = 50;
		double totalTime = 0;
		for (int j = 0; j < trials; j++) {
			MinPriorityQueue<Long> queue = new MinPriorityQueue<>();
			long time = System.currentTimeMillis();
			for (int i = 0; i < n; i++) {
				queue.insert(temp.nextLong());
			}
			totalTime += System.currentTimeMillis() - time;
		}
		return totalTime / trials;
	}
	
	private static double calculateAverageExecutionTimeRemove(int n){
		int trials = 50;
		double totalTime = 0;
		for(int i = 0; i < trials; i++){
			Long[] example = generateExample(n);
			MinPriorityQueue<Long> queue = new MinPriorityQueue<>(example);
			long time = System.currentTimeMillis();
			for(int j = 0; j < n; j++)
				queue.removeMin();
			totalTime += System.currentTimeMillis() - time;
		}
		return totalTime/trials;
	}


	public static void main(String[] args) {
		int n = 125;
		double previousTime = calculateAverageExecutionTimeRemove(n);	//trocar aqui entre Remove e Insert
		double newTime;
		double doublingRatio;
		for (int i = 250; true; i *= 2) {
			newTime = calculateAverageExecutionTimeRemove(i);			//trocar aqui entre Remove e Insert
			if (previousTime > 0) {
				doublingRatio = newTime / previousTime;
			} else
				doublingRatio = 0;
			previousTime = newTime;
			System.out.println(i + "\t" + newTime + "\t" + doublingRatio);
		}
	}
}

/*	TESTE RAZAO DOBRADA - METODO INSERT (Numeros a inserir , newTime, doublingRatio)
 * 
 * 		250			0.04		1.0 
 * 		500			0.08		2.0 
 * 		1000		0.12		1.5
 * 		2000		0.12		1.0
 * 		4000		0.24		2.0
 * 		8000		0.4			1.6666666666666667
 * 		16000		0.66		1.65
 * 		32000		1.44		2.1818181818181817
 * 		64000		2.86		1.9861111111111112
 * 		128000		11.42		3.9930069930069934
 * 		256000		25.02		2.190893169877408
 * 		512000		58.4		2.334132693844924
 * 		1024000		122.84		2.1034246575342466
 * 		2048000		246.98		2.010582872028655
 * 
 * 		T(N) ~ N (Linear)
 * 
 * 	TESTE RAZAO DOBRADA - METODO REMOVEMIN (Numeros a remover, newTime, doublingRatio)
 *		250			0.12		1.5
 * 		500			0.12		1.0
 * 		1000		0.12		1.0
 * 		2000		0.26		2.166666666666667
 * 		4000		0.62		2.3846153846153846
 * 		8000		1.2			1.9354838709677418
 * 		16000		2.9			2.4166666666666665
 * 		32000		6.92		2.386206896551724
 * 		64000		14.9		2.153179190751445
 * 		128000		39.94		2.680536912751678
 * 		256000		104.76		2.622934401602404
 * 		512000		301.22		2.8753340969835817
 * 		
 * 		T(N) ~ N (Linear)
 */
