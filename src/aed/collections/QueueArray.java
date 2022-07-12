package aed.collections;

import java.util.Iterator;

public class QueueArray<E> implements Iterable<E>{
	
	private class QueueArrayIterator implements Iterator<E>{
		private E[] it;
		private int size;
		private int i;
		private int next;
		private int maxSize;
		QueueArrayIterator() {
			it = Items;
			size = tamanho;
			i = 0;
			next = offset;
			maxSize = tamanho_maximo;
		}
		public boolean hasNext() {
			return i < size;
		}
		public E next() {
			E OutPut = it[next];
			next = (next+1)%maxSize;
			i++;
			return OutPut;
		}
		public void remove() {
			throw new UnsupportedOperationException("Remove not supported");
		}
	}
	
	
	public E[] Items;
	private int offset;
	private int tamanho;
	private int tamanho_maximo;
	
	@SuppressWarnings("unchecked")
	public QueueArray(int maxSize) {
		this.Items = (E[]) new Object[maxSize];
		this.tamanho = 0;
		this.offset = 0;
		this.tamanho_maximo = maxSize;
		}
	private QueueArray(E[] Items, int size, int maxSize,int offset) {
		this.Items = Items;
		this.tamanho = size;
		this.tamanho_maximo = maxSize;
		this.offset = offset;
		}
	
	public void enqueue(E addQueue) {
		if(tamanho == this.tamanho_maximo)
			throw new OutOfMemoryError("Limite da queue chegado");
		this.Items[ ( this.tamanho++ + this.offset ) % this.tamanho_maximo] = addQueue;
	}
	
	public E dequeue() {
		if(isEmpty())
			return null;
		E OutPut = this.Items[this.offset];
		this.Items[this.offset] = null;
		this.offset = (this.offset+1)%this.tamanho_maximo;
		this.tamanho--;
		return OutPut;
	}
	
	public E peek() {
		if(isEmpty())
			return null;
		return this.Items[this.offset];
	}
	public int size() {
		return this.tamanho;
	}
	public boolean isEmpty() {
		return this.tamanho == 0;
	}
	
	public QueueArray<E> shallowCopy(){
		@SuppressWarnings("unchecked")
		E[] Output = (E[]) new Object[this.tamanho_maximo];
		int temp = 0;
		for(E i : Items)
			Output[temp++] = i;
		return new QueueArray<E>(Output, this.tamanho, this.tamanho_maximo,this.offset);
	}
	public Iterator<E> iterator(){
		return new QueueArrayIterator();
	}
}
