package aed.collections;

import java.util.Iterator;

public class StackList<E> implements Iterable<E> {

	private class Node {
		private E item;
		private Node next;

		private Node(E item, Node next) {
			this.item = item;
			this.next = next;
		}

		private Node(E item) {
			this.item = item;
			this.next = null;
		}

		private Node() {
		}
	}

	private class StackListIterator implements Iterator<E> {
		Node it;

		StackListIterator() {
			it = Primeiro;
		}

		public boolean hasNext() {
			return it != null && it.item != null;
		}

		public E next() {
			E OutPut = it.item;
			it = it.next;
			return OutPut;
		}

		public void remove() {
			throw new UnsupportedOperationException("Remove not supported");
		}
	}

	private Node Primeiro;
	private int tamanho;

	@SuppressWarnings("unused")
	private StackList(Node Primeiro, int tamanho) {
		this.Primeiro = Primeiro;
		this.tamanho = tamanho;
	}

	public StackList() {
		this.Primeiro = null;
		this.tamanho = 0;
	}

	public void push(E InputAdd) {
		this.Primeiro = new Node(InputAdd, this.Primeiro);
		this.tamanho++;
	}

	public E peek() {
		if (isEmpty())
			return null;
		return this.Primeiro.item;
	}

	public E pop() {
		if (isEmpty())
			return null;
		E OutPut = this.Primeiro.item;
		this.Primeiro = this.Primeiro.next;
		this.tamanho--;
		return OutPut;
	}

	public boolean isEmpty() {
		return this.Primeiro == null;
	}

	public int size() {
		return this.tamanho;
	}

	public StackList<E> shallowCopy() {
		StackList<E> copia = new StackList<E>();
		if (isEmpty() == true)
			return copia;
		copia.Primeiro = new Node();
		Node temp = copia.Primeiro;
		for (E i : this) {
			temp.item = i;
			temp.next = new Node();
			temp = temp.next;
		}
		return copia;
	}

	public Iterator<E> iterator() {
		return new StackListIterator();
	}

		
	public static int somaMultiplos(int a, int n, int b) {
		int result = 0;
		int j = 1;
		for (int i = 0; i < n;) {
			int mult = a * j;
			j++;
			if(b < mult) {
				i++;
				result += mult;
			}
		}
		return result;
	}
	
	public static void main(String[] args) {
		System.out.println(somaMultiplos(4, 3, 10));
	}

}
