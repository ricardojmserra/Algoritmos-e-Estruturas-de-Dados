package aed.tables;

import java.util.Iterator;
import java.util.Random;

public class OpenAddressingHashTable<Key, Value> implements Iterable<Key> {

	@SuppressWarnings("hiding")
	private class item<Key, Value> {
		Key key;
		Value value;
		boolean isActive;

		private item(Key key, Value value) {
			this.key = key;
			this.value = value;
			isActive = true;
		}

		private item(Key key, Value value, boolean isActive) {
			this.key = key;
			this.value = value;
			this.isActive = isActive;
		}
	}

	public OpenAddressingHashTable<Key, Value> clone() {
		return new OpenAddressingHashTable<Key, Value>(this.max, this.size, this.Table, this.LazyRemove,
				this.primeIndex);
	}

	@SuppressWarnings("unchecked")
	private OpenAddressingHashTable(int max, int size, item<Key, Value>[] table, int LazyRemove, int primeIndex) {
		this.LazyRemove = LazyRemove;
		this.primeIndex = primeIndex;
		this.size = size;
		this.max = max;
		this.Table = new item[max];
		for (int i = 0; i < table.length; i++) {
			if (table[i] != null)
				this.Table[i] = new item<Key, Value>(table[i].key, table[i].value, table[i].isActive);
		}
	}

	private int size;
	private item<Key, Value>[] Table;
	private int max;
	private int primeIndex;
	private int LazyRemove;

	private int hash1(Key k) {
		return (k.hashCode() & 0x7FFFFFFF) % this.max;
	}

	private int hash2(Key k) {
		//return 1;
		return primes[primeIndex - 1] - (k.hashCode() % primes[primeIndex - 1]);
	}

	private final int[] primes = { 17, 37, 79, 163, 331, 673, 1361, 2729, 5471, 10949, 21911, 43853, 87719, 175447,
			350899, 701819, 1403641, 2807303, 5614657, 11229331, 22458671, 44917381, 89834777, 179669557 };

	@SuppressWarnings("unchecked")
	public OpenAddressingHashTable() {
		this.size = 0;
		this.primeIndex = 1;
		this.max = 37;
		this.LazyRemove = 0;
		this.Table = new item[37];
	}

	public int size() {
		return this.size;
	}

	public int getCapacity() {
		return this.max;
	}

	public float getLoadFactor() {
		return (float) this.size / this.max;
	}

	public int getDeletedNotRemoved() {
		return this.LazyRemove;
	}

	public boolean containsKey(Key k) {
		int hash = hash1(k);
		int hash2 = hash2(k);
		while (this.Table[hash] != null) {
			if (this.Table[hash].isActive && this.Table[hash].key.equals(k))
				return true;
			hash = (hash + hash2) % this.max;
		}
		return false;
	}

	public Value get(Key k) {
		int hash = hash1(k);
		int hash2 = hash2(k);
		while (this.Table[hash] != null) {
			if (this.Table[hash].isActive && this.Table[hash].key.equals(k))
				return this.Table[hash].value;
			hash = (hash + hash2) % this.max;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private void resizeUp() {
		this.LazyRemove = 0;
		this.size = 0;
		item<Key, Value>[] temp = this.Table;
		this.max = this.primes[++this.primeIndex];
		this.Table = new item[this.max];
		for (int i = 0; i < temp.length; i++) {
			if (temp[i] != null && temp[i].isActive)
				this.put(temp[i].key, temp[i].value);
		}
	}

	public void put(Key k, Value v) {
		if (this.getLoadFactor() >= 0.5)
			resizeUp();
		int hash = hash1(k);
		int hash2 = hash2(k);
		int index = -1;
		
		while(this.Table[hash] != null) {
			if(this.Table[hash].key.equals(k)) {
				index = hash;//Encontra key igual, guarda a hash e break
				break;
			}
			else if(index != -1 && !this.Table[hash].isActive)
				index = hash;//Guarda o sitio do primeiro valido caso n encontre Key igual
			hash = (hash + hash2) % this.max;
		}
		
		if(this.Table[hash] == null) { // Primeiro era logo nulo
			if(v == null) return;
			++this.size;
			index = hash;
		}
		else if(!this.Table[index].isActive) { //Primeiro era marcado para Apagar
			++this.size;
			--this.LazyRemove;
		}
		this.Table[index] = new item<Key, Value>(k, v);
		if(v == null) {
			this.Table[hash].isActive = false;
			--this.size;
			++this.LazyRemove;
			if (this.primeIndex != 1 && this.getLoadFactor() < 0.125)
				resizeDown();
			if (0.2 * this.max < this.LazyRemove)
				removeLazy();
		}
	}

	@SuppressWarnings("unchecked")
	private void resizeDown() {
		this.LazyRemove = 0;
		this.size = 0;
		item<Key, Value>[] temp = this.Table;
		this.max = this.primes[--this.primeIndex];
		this.Table = new item[this.max];
		for (int i = 0; i < temp.length; i++) {
			if (temp[i] != null && temp[i].isActive)
				this.put(temp[i].key, temp[i].value);
		}
	}

	public void delete(Key k) {
		int hash = hash1(k);
		int hash2 = hash2(k);
		while (this.Table[hash] != null) {
			if (this.Table[hash].key.equals(k))
				break;
			hash = (hash + hash2) % this.max;
		}

		if (!(this.Table[hash] == null || !this.Table[hash].isActive)) {
			this.Table[hash].isActive = false;
			--this.size;
			++this.LazyRemove;
		}

		if (this.primeIndex != 1 && this.getLoadFactor() < 0.125)
			resizeDown();
		if (0.2 * this.max < this.LazyRemove)
			removeLazy();
	}

	@SuppressWarnings("unchecked")
	private void removeLazy() {
		this.LazyRemove = 0;
		this.size = 0;
		item<Key, Value>[] temp = this.Table;
		this.Table = new item[this.max];
		for (int i = 0; i < temp.length; i++) {
			if (temp[i] != null && temp[i].isActive)
				this.put(temp[i].key, temp[i].value);
		}
	}

	public Iterable<Key> keys() {
		return this;
	}

	@Override
	public Iterator<Key> iterator() {
		return new TableIterator();
	}

	private class TableIterator implements Iterator<Key> {
		int i;
		private item<Key, Value>[] it;

		TableIterator() {
			this.it = Table;
			this.i = 0;
			while (this.i < this.it.length) {
				if (this.it[i] != null && this.it[i].isActive)
					break;
				++this.i;
			}
		}

		@Override
		public boolean hasNext() {
			return i < this.it.length;
		}

		@Override
		public Key next() {
			Key next = this.it[i].key;
			++this.i;
			while (this.i < this.it.length) {
				if (this.it[i] != null && this.it[i].isActive)
					break;
				++this.i;
			}
			return next;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Remove not supported");
		}
	}

	@SuppressWarnings("unused")
	private static void BasicTest() {
		int seed = new Random().nextInt();
		// seed = -926081565; //seed para loop infinito na hash2
		OpenAddressingHashTable<Integer, Integer> temp = new OpenAddressingHashTable<Integer, Integer>();
		Random test = new Random(seed);
		for (int i = 0; i < 20; i++) {
			temp.put(test.nextInt(), test.nextInt());
		}
		for (Integer i : temp)
			System.out.print(i + " ");
		System.out.println("\n lazy: " + temp.LazyRemove + " max: " + temp.max + " size: " + temp.size + " load: "
				+ temp.getLoadFactor());
		System.out.println("\n-------\n");
		test = new Random(seed);

		for (int i = 0; i < 500; i++) {
			int key = test.nextInt();
			test.nextInt(); // value
			if (Math.random() < 0.5)
				temp.put(key, null);
		}
		for (Integer i : temp.clone())
			System.out.print(i + " ");
		System.out.println("\n lazy: " + temp.LazyRemove + " max: " + temp.max + " size: " + temp.size + " load: "
				+ temp.getLoadFactor() + " prime: " + temp.primeIndex);
		temp = new OpenAddressingHashTable<Integer, Integer>();
		for (int i = 0; i < 15; i++)
			temp.put(i * 37, i);
		for (int i = 7; 0 < i; i--)
			temp.put(i * 37, null);
		for (int i = 15; i < 20; i++)
			temp.put(i, i);
		temp.delete(15);
		temp.put(123, null);
		System.out.println(temp.size + " " + temp.LazyRemove);
	}

	private static void EmpiricTests() {
		int n = 125;
		double previousTime = CalcPut(n);
		double newTime;
		double doublingRatio;
		for (int i = 210545; true;) {
			newTime = CalcPut(i);
			if (previousTime > 0) {
				doublingRatio = newTime / previousTime;
			} else
				doublingRatio = 0;
			previousTime = newTime;
			System.out.println(i + "\t" + newTime + "\t" + doublingRatio);
		}

	}

	public static OpenAddressingHashTable<Integer, Integer> generateExample(int n) {
		Random r = new Random();
		OpenAddressingHashTable<Integer, Integer> examples = new OpenAddressingHashTable<Integer, Integer>();
		for (int i = 0; i < n; i++) {
			examples.put(r.nextInt(), 0);
		}
		return examples;
	}

	public static double CalcDelete(int n) {
		int trials = 30;
		double totalTime = 0;
		for (int i = 0; i < trials; i++) {
			Integer remove = null;
			OpenAddressingHashTable<Integer, Integer> example = generateExample(n);
			for(Integer j : example.keys()) {
				remove = j;
				if(Math.random() < 0.5) break;
			}
			long time = System.nanoTime();
			example.delete(remove);;
			totalTime += System.nanoTime() - time;
		}
		return totalTime / trials;
	}

	public static double CalcPut(int n) {
		int trials = 30;
		Random r = new Random();
		double totalTime = 0;
		for (int i = 0; i < trials; i++) {
			OpenAddressingHashTable<Integer, Integer> example = generateExample(n);
			int put = r.nextInt();
			long time = System.nanoTime();
			example.put(put, 1);
			totalTime += System.nanoTime() - time;
		}
		return totalTime / trials;
	}

	public static void main(String[] args) {
		//BasicTest();
		EmpiricTests();
	}
}
/*
 * Foi analisado a razao dobrada e tempo de execucao da Double Hashing e Hashing Linear para os LoadFactos de 0.499; 0.2; 0.37 e 0.3
 * 
 * A partir desta analise de dados e possivel concluir que a Double Hashing e mais consistente, mas para tamanhos grandes de arrays a diferenca
 * temporal entre a Linear e a Double Hashing e bastante pequena mas existe
 * Pois para alem da Double Hashing ser vais eficiente, caso as chaves nao sejam completamente aleatorios (o que favorece linear Hashing)
 * a double Hashing ira demonstrar sempre maior eficiencia
 * 
 */


//
//			DOUBLE HASHING LoadFactor = 0.499     PUT
//
//		Tamanho antes de introduzir;       tempo (nano);         razao
//87720		2544.4666666666667		5.06260777291418
//87720		2459.0333333333333		0.966423874027301
//87720		26674.8					10.847677271556574
//87720		1582.3333333333333		0.05931940758068789
//87720		1539.5333333333333		0.9729513376869602
//87720		1678.5333333333333		1.0902870999870091
//87720		1518.0666666666666		0.9044006672491858
//87720		1421.9					0.9366518817794565
//87720		1646.5					1.157957662282861
//87720		1774.7333333333333		1.0778823767587813
//87720		1796.1					1.012039367416701
//87720		1454.1					0.8095874394521463
//87720		1528.8					1.0513719826696926
//87720		1817.4666666666667		1.188819117390546
//87720		1881.7333333333333		1.0353605751595627
//87720		1625.1333333333334		0.8636363636363636
//87720		2480.366666666667		1.526254256061041
//87720		1860.4					0.7500503957748182

//
//										PUT 0.25
//			
//90065		2223.8				3.1511973926597703
//90065		2459.0333333333333	1.1057798962736456
//90065		2341.6				0.95224410676282
//90065		2897.4				1.2373590707208748
//90065		1828.2666666666667	0.6310025079956743
//90065		2587.266666666667	1.41514731621937
//90065		1881.8				0.7273312891339637
//90065		1796.1				0.9544584971835477
//90065		1689.2				0.9404821557819721

//
//										PUT 0.37
//
//122814	2298.5666666666666	3.1621497684229833
//122814	2138.266666666667	0.9302608872195718
//122814	2298.633333333333	1.0749984411049447
//122814	2138.266666666667	0.9302339071042215
//122814	1892.4666666666667	0.8850470786306666
//122814	2020.7				1.0677598900905345
//122814	2277.1666666666665	1.1269197142904273


//
//							PUT 0.3
//
//210545	5078.5				6.883301707779887
//210545	3870.2				0.7620754159692822
//210545	30234.966666666667	7.812249151637298
//210545	4436.933333333333	0.14674841160731117
//210545	3774.0				0.8505874928629383
//210545	4244.433333333333	1.1246511217099453
//210545	3741.9666666666667	0.8816174911452648

//
//										REMOVE 0.499
//
//87720		3902.3				2.723801768264309
//87720		3528.1666666666665	0.9041249177835293
//87720		29507.9				8.363522131418584
//87720		3078.9666666666667	0.10434380849422245
//87720		3431.866666666667	1.1146163756238565
//87720		4661.4				1.3582695520416488
//87720		3346.3333333333335	0.7178816092447191
//87720		2875.9333333333334	0.8594282299033769
//87720		3870.3				1.3457544217529382
//87720		3132.5333333333333	0.8093773953784805
//87720		3014.866666666667	0.9624372180131098
//87720		4575.966666666666	1.5178006766468388

//
//										REMOVE 0.25
//
//90065		3848.8333333333335	5.1425199305215346
//90065		3357.0333333333333	0.872221019356515
//90065		28930.733333333334	8.617946401088263
//90065		2769.0666666666666	0.09571367012243036
//90065		3079.2				1.1119992295839753
//90065		3036.3333333333335	0.9860786351433274
//90065		3228.766666666667	1.0633768800087826
//90065		4875.3				1.5099573624603821
//90065		5623.5				1.1534674789243737
//90065		5377.666666666667	0.9562846388666608
//90065		7494.566666666667	1.3936465629455153
//90065		6724.933333333333	0.8973078274483293

//
//										REMOVE 0.37
//
//122814	2801.233333333333	0.09472554648920209
//122814	2865.1666666666665	1.0228232802218071
//122814	3325.0333333333333	1.1605025885637834
//122814	3806.133333333333	1.1446902787941975
//122814	3602.8				0.9465774539340014
//122814	3870.3333333333335	1.0742570593242293

//
//								REMOVE 0.3
//
//210545	2886.6666666666665	6.136186494721179
//210545	3100.5				1.0740762124711316
//210545	2619.266666666667	0.8447884749771543
//210545	2448.3				0.9347272773549848
//210545	2394.766666666667	0.9781344878759411
//210545	2191.8				0.9152457441921968
//210545	2298.766666666667	1.0488031146394137
//210545	2405.5333333333333	1.0464451952496265


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//
//			LINEAR HASHING LoadFactor = 0.499    PUT
//
//87720		2394.866666666667		4.665930640342902
//87720		2138.3333333333335		0.8928819976059906
//87720		2138.2					0.9999376461418549
//87720		2106.133333333333		0.9850029619929536
//87720		2042.0					0.9695492529754369
//87720		1700.0					0.8325171400587659
//87720		1560.8666666666666		0.918156862745098
//87720		1689.4333333333334		1.0823687694870372
//87720		2213.133333333333		1.3099855967484164
//87720		2084.8333333333335		0.9420278940868152
//87720		2074.0					0.9948037413062594
//87720		1860.3333333333333		0.8969784635165541
//87720		1454.1333333333334		0.7816520336857194
//87720		1614.4333333333334		1.110237483953787
//87720		1764.1					1.0927053868230339
//87720		1913.7666666666667		1.0848402395933716
//87720		1667.8666666666666		0.8715099367739013
//87720		1518.2					0.9102646094811736
//87720		1657.0					1.0914240548017389
//87720		1560.9333333333334		0.9420237376785355

//
//				PUT 0.25
//
//90065		2298.6				3.6441367647835965
//90065		2555.233333333333	1.1116476695959858
//90065		2149.0333333333333	0.8410321301381479
//90065		1603.6				0.7461959640768717
//90065		2405.6				1.5001247193813918
//90065		1956.4666666666667	0.8132967520230574
//90065		1753.3				0.8961563362524279
//90065		2052.5333333333333	1.1706686438905682


//
//					PUT 0.37
//
//122814	2330.733333333333	6.410157682434909
//122814	2063.366666666667	0.8852864620577215
//122814	2769.1666666666665	1.3420623253259234
//122814	1860.1666666666667	0.6717424014444779
//122814	2341.233333333333	1.2586148194606217
//122814	1721.3666666666666	0.7352392613579737
//122814	1860.3666666666666	1.0807497918320714
//122814	1828.2333333333333	0.9827274193259394

//
//					PUT 0.3
//		
//210545	2480.4				4.732985625238519
//210545	2384.1666666666665	0.9612024942213621
//210545	2063.5333333333333	0.8655155540020972
//210545	1774.8333333333333	0.8600943365748069
//210545	1977.8666666666666	1.1143957179077848
//210545	1977.9666666666667	1.0000505595254146
//210545	2074.1				1.0486020997994574


//
//				REMOVE 0.499
//
//87720		3506.8					3.452594269961603
//87720		3004.2					0.8566784532907493
//87720		2843.9					0.9466413687504162
//87720		3271.7					1.1504272302120326
//87720		3079.0666666666666		0.941121333455594
//87720		3560.2					1.1562594725674447
//87720		4458.3					1.252261108926465
//87720		3645.766666666667		0.8177481700797763
//87720		3057.6					0.8386713357044242
//87720		3292.866666666667		1.076944880516309
//87720		3004.1666666666665		0.9123256331868888
//87720		3143.366666666667		1.0463356449375867

//	
//				REMOVE 0.25
//
//90065		3560.266666666667	3.397849462365592
//90065		2769.1				0.7777788180660624
//90065		29198.066666666666	10.54424421893997
//90065		2822.6				0.09667078413867584
//90065		2651.4				0.9393467016226175
//90065		2790.3333333333335	1.0523999899424203
//90065		2940.133333333333	1.0536853422530164
//90065		4672.3				1.5891456169788218
//90065		5377.666666666667	1.150967760346439


//
//					REMOVE 0.37
//
//122814	3528.0666666666666	2.7731286189640265
//122814	3431.866666666667	0.9727329415543925
//122814	33078.86666666667	9.638738878744318
//122814	3282.233333333333	0.09922447967786077
//122814	3570.9666666666667	1.0879685579940488
//122814	2854.5				0.7993633843310401

//
//				REMOVE 0.3
//
//210545	4383.466666666666	3.1538756715272447
//210545	3998.6				0.9122003893417692
//210545	42423.03333333333	10.609471648410278
//210545	3250.1				0.07661168343297783
//210545	3367.8				1.0362142703301438
//210545	3827.5666666666666	1.13651839974662
//210545	3517.366666666667	0.9189563430203698