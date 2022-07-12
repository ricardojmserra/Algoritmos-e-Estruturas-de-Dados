package aed.sorting;

import java.util.Random;
/*
 * 
 * 			RESUMOS SOBRE OS TESTES ENCONTRAM-SE NO FINAL DO FICHEIRO
 * 
 */

@SuppressWarnings({"rawtypes", "unused"})
public class QuickSort extends Sort {
	private static Random RandomNum = new Random();

	private static int partition(Comparable[] a, int low, int high) {
		int random = low + RandomNum.nextInt((high - low) + 1);
		exchange(a, low, random);
		int i = low, j = high + 1;
		Comparable v = a[low];
		while (true) {
			while (less(a[++i], v))
				if (i == high)
					break;
			while (less(v, a[--j]))
				if (j == low)
					break;

			if (i >= j)
				break;
			exchange(a, i, j);
		}
		exchange(a, low, j);
		return j;
	}

	private static void sort(Comparable[] a, int low, int high) {
		if (high <= low)
			return;
		int j = partition(a, low, high);
		sort(a, low, j - 1);
		sort(a, j + 1, high);
	}

	public static void sort(Comparable[] a) {
		sort(a, 0, a.length - 1);
	}

	private static int medianPartition(Comparable[] a, int low, int high) {
		int middle = low + (high - low) / 2;
		if (less(a[middle], a[low]))
			exchange(a, middle, low);
		if (less(a[high], a[low]))
			exchange(a, high, low);
		if (less(a[high], a[middle]))
			exchange(a, high, middle);
		exchange(a, middle, low + 1);

		int i = low + 1, j = high;
		Comparable v = a[low + 1];

		while (true) {
			while (less(a[++i], v))
				;
			while (less(v, a[--j]))
				;

			if (i >= j)
				break;
			exchange(a, i, j);
		}
		exchange(a, low + 1, j);
		return j;
	}

	private static void medianInsert(Comparable[] a, int low, int high) {
		for(int i = low+1 ; i <= high; ++i)
			for(int j = i; j > low; --j)
				if(  less(a[j], a[j-1])  )
					exchange(a, j-1, j);
				else break;
	}

	private static void medianSort(Comparable[] a, int low, int high) {
		int size = high - low;
		if (size < 10)
			medianInsert(a, low, high);
		else {
			int j = medianPartition(a, low, high);
			medianSort(a, low, j - 1);
			medianSort(a, j + 1, high);
		}
	}

	 public static void medianSort(Comparable[] a)
	    {
	        medianSort(a, 0, a.length-1);
	    }

	private static void quickSelect(Comparable[] a, int low, int high, int n) {
		while(low < high) {
			int j = partition(a,low,high);
			if(n == j)
				break;
			else if(n < j)
				high = j-1;
			else
				low = j+1;
		}
	}

	public static Comparable quickSelect(Comparable[] a, int n) {
		quickSelect(a, 0, a.length - 1, n);
		return a[n];
	}

	private static Integer[] generateExampleRandom(int n) {
		Random r = new Random();
		Integer[] examples = new Integer[n];
		for (int i = 0; i < n; i++) {
			examples[i] = r.nextInt();
		}
		return examples;
	}
	private static Integer[] generateExampleOrdenado(int n) {
		Integer[] examples = new Integer[n];
		for (int i = 0; i < n; i++) {
			examples[i] = i;
		}
		return examples;
	}

	private static Integer[] generateExampleInverso(int n) {
		Integer[] examples = new Integer[n];
		int temp = n;
		for (int i = 0; i < n; i++) {
			examples[i] = --temp;
		}
		return examples;
	}
	
	private static void Desordenar(Comparable[] a) {
		Random r = new Random();
		for(int i = 0; i < a.length/8; i++) {	//Cada troca desordena 2 elementos, logo n/8 trocas desordena n/4 elementos
			exchange( a, r.nextInt(a.length-1), r.nextInt(a.length-1) );
		}
	}

	private static double calculateAverageExecutionTime(int n) {
		int trials = 100;
		double totalTime = 0;
		for (int i = 0; i < trials; i++) {
			//int select = new Random().nextInt(n-1);			// Random para o quickSelect
			Integer[] example = generateExampleOrdenado(n);	// Mudar aqui o tipo de teste Ordenado / Random / Semi-Ordenado / Inverso
			Desordenar(example);							// Comentar / Descomentar para Semi-Ordenado
			long time = System.currentTimeMillis();
			medianSort(example); 							// Mudar aqui sort / medianSort / quickSelect
			totalTime += System.currentTimeMillis() - time;
		}
		return totalTime / trials;
	}

	public static void main(String[] args) {
		int n = 125;
		double previousTime = calculateAverageExecutionTime(n);
		double newTime;
		double doublingRatio;
		for (int i = 250; true; i *= 2) {
			newTime = calculateAverageExecutionTime(i);
			if (previousTime > 0) {
				doublingRatio = newTime / previousTime;
			} else
				doublingRatio = 0;
			previousTime = newTime;
			System.out.println(i + "\t" + newTime + "\t" + doublingRatio);
		}
	}
}
/*
	Teste Random --------------------------------------------------------
				Melhor == medianSort
					Pois este consegue ter um melhor valor para a partição do que o Sort
						pois esse é necessário ter sorte(escolher sempre um bom pivot)
						para ser mais eficiente que o median
						Para além da implementação do insertionSort que causa partições mais pequenas para
						serem mais rápidas que o normal.
			
			Sort			T(N) ~ N
		250			0.03333333333333333		0.3333333333333333
		500			0.06666666666666667		2.0
		1000		0.16666666666666666		2.5
		2000		1.3333333333333333		8.0
		4000		1.5						1.125
		8000		0.8666666666666667		0.5777777777777778
		16000		1.7						1.9615384615384615
		32000		3.8333333333333335		2.2549019607843137
		64000		9.433333333333334		2.4608695652173913
		128000		19.233333333333334		2.03886925795053
		256000		44.3					2.3032928942807622
		512000		107.86666666666666		2.434913468773514
		1024000		243.6					2.258343634116193
		
			medianSort 		T(N) ~ N
		250			0.0						0.0
		500			0.16666666666666666		0.0
		1000		0.5666666666666667		3.4
		2000		0.6						1.0588235294117647
		4000		1.1						1.8333333333333335
		8000		2.0						1.8181818181818181
		16000		1.8333333333333333		0.9166666666666666
		32000		4.0						2.181818181818182
		64000		9.066666666666666		2.2666666666666666
		128000		18.833333333333332		2.077205882352941
		256000		46.53333333333333		2.470796460176991
		512000		104.96666666666667		2.255730659025788
		1024000		243.26666666666668		2.3175611305176247
		
	Teste Ordenado -----------------------------------------------------
				Melhor == medianSort
					O medianSort vai sempre ter o melhor pivot possivel para cada iteração
						escolhendo sempre o elemento do meio com pivot fazendo com que este
						tenha sempre o melhor caso.
	
			Sort			T(N) ~ N
		250			0.03333333333333333		1.0
		500			0.13333333333333333		4.0
		1000		0.26666666666666666		2.0
		2000		1.2333333333333334		4.625
		4000		0.5333333333333333		0.4324324324324324
		8000		0.7						1.3125
		16000		1.1333333333333333		1.619047619047619
		32000		1.7333333333333334		1.5294117647058825
		64000		3.7						2.1346153846153846
		128000		7.7						2.081081081081081
		256000		18.066666666666666		2.346320346320346
		512000		36.43333333333333		2.01660516605166
		1024000		74.26666666666667		2.038426349496798
		2048000		153.23333333333332		2.0632854578096946
	
			medianSort 		T(N) ~ N
		250		0.0	0.0
		500		0.0	0.0
		1000	0.06666666666666667	0.0
		2000	0.2	3.0
		4000	0.36666666666666664	1.833333333333333
		8000	0.7666666666666667	2.0909090909090913
		16000	0.3	0.3913043478260869
		32000	0.23333333333333334	0.7777777777777778
		64000	0.9333333333333333	4.0
		128000	1.9666666666666666	2.107142857142857
		256000	5.033333333333333	2.5593220338983054
		512000	13.366666666666667	2.6556291390728477
		1024000	24.4	1.8254364089775559
		2048000	54.86666666666667	2.248633879781421
		4096000	112.76666666666667	2.055285540704739
		
		Teste Inverso --------------------------------------------------------
					Melhor == medianSort
						Igualmente ao teste anterior o median irá ter sempre o melhor pivot
							possivel para a partição fazendo com que o caso médio seja igual
							ao melhor caso possivel.
							
			Sort 			T(N) ~ N
		250			0.03333333333333333		1.0
		500			0.06666666666666667		2.0
		1000		0.16666666666666666		2.5
		2000		0.6666666666666666		4.0
		4000		1.7333333333333334		2.6
		8000		0.5						0.28846153846153844
		16000		0.9666666666666667		1.9333333333333333
		32000		1.9666666666666666		2.0344827586206895
		64000		4.066666666666666		2.0677966101694913
		128000		8.0						1.9672131147540985
		256000		20.133333333333333		2.5166666666666666
		512000		40.666666666666664		2.019867549668874
		1024000		86.03333333333333		2.115573770491803
		2048000		174.26666666666668		2.025571483920961
		
			medianSort 		T(N) ~ N
		250			0.03333333333333333		0.5
		500			0.06666666666666667		2.0
		1000		0.13333333333333333		2.0
		2000		0.36666666666666664		2.75
		4000		1.2						3.272727272727273
		8000		2.7						2.2500000000000004
		16000		0.4						0.14814814814814814
		32000		1.0						2.5
		64000		2.2						2.2
		128000		4.433333333333334		2.015151515151515
		256000		13.1					2.9548872180451125
		512000		27.533333333333335		2.1017811704834606
		1024000		59.733333333333334		2.169491525423729
		2048000		125.93333333333334		2.1082589285714284
		
		Teste Semi-Ordenado ----------------------------------------------------------
						Melhor == medianSort
							Devido a 1/4 do array estar desordenado o medianSort nem sempre irá fazer a melhor mediana
								pois basta apenas que sejam feitas poucas trocas para causar várias chamadas ao
								InsertionSort, mas pela implementação do InsertionSort este é mais rápido que
								o Sort
								
			Sort 			T(N) ~ N
		250			0.03333333333333333		1.0
		500			0.06666666666666667		2.0
		1000		0.1						1.5
		2000		0.9333333333333333		9.333333333333332
		4000		0.7333333333333333		0.7857142857142857
		8000		0.7						0.9545454545454546
		16000		1.3333333333333333		1.9047619047619049
		2000		2.9						2.1750000000000003
		64000		6.066666666666666		2.0919540229885056
		128000		12.1					1.9945054945054945
		256000		30.9					2.553719008264463
		512000		66.7					2.1585760517799355
		1024000		151.9					2.2773613193403297
		
			medianSort 		T(N) ~ N
		250			0.03					1.5
		500			0.11					3.666666666666667
		1000		0.23					2.090909090909091
		2000		0.61					2.652173913043478
		4000		0.18					0.29508196721311475
		8000		0.3						1.6666666666666667
		16000		0.76					2.5333333333333337
		32000		1.54					2.026315789473684
		64000		3.08					2.0
		128000		6.31					2.0487012987012987
		256000		15.69					2.4865293185419968
		512000		35.38					2.254939451880179
		1024000		78.65					2.2230073487846242
		
		----------------------------------------------------------------------------------
		Teste QuickSelect	T(N) ~ N
		
		250			0.0						0.0
		500			0.03333333333333333		0.0
		1000		0.06666666666666667		2.0
		2000		0.13333333333333333		2.0
		4000		0.1						0.75
		8000		0.6666666666666666		6.666666666666666
		16000		1.3666666666666667		2.0500000000000003
		32000		0.6666666666666666		0.4878048780487805
		64000		1.1						1.6500000000000001
		128000		2.0						1.8181818181818181
		256000		4.633333333333334		2.316666666666667
		512000		10.833333333333334		2.338129496402878
		1024000		24.333333333333332		2.2461538461538457
		2048000		46.53333333333333		1.9123287671232876
		4096000		109.76666666666667		2.3588825214899716
		
		
		Visto que o tamanho do array vai sempre multiplicando por dois, estes vão ter que ordenar 2 vezes mais
		elementos que o anterior fazendo com que também façam 2 vezes mais comparações e 2 vezes mais trocas
		(dependendo de cada caso, mas para casos iguais as trocas/comparações irão ser as mesmas)
		Logo é normal que T(N) ~ N para todos os casos que o crescimento temporar seja apenas o dobro que o anterior.
*/