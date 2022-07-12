package aed.collections;
import java.util.Iterator;

public class QueueList<E> implements Iterable<E>{
	private class Node{
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
	private Node Last;
	private Node first;
	private int size;

	public QueueList() {
		this.first = null;
		this.size = 0;
		this.Last = null;
	}
	
	public void enqueue(E item) {
		Node Last = new Node(item);
		if( this.size == 0)
			this.first = Last;
		else
			this.Last.next = this.Last;
		this.Last = Last;
		++this.size;
	}
	public E dequeue() {
		if(this.size == 0)
			return null;
		E item = this.first.item;
		this.first = this.first.next;
		--this.size;
		return item;
	}
	public boolean isEmpty() {
		return this.size == 0;
	}
	public int size() {
		return this.size;
	}
	public QueueList<E> clone(){
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Iterator<E> iterator() {
		return new QueueListIterator();
	}
	
	private class QueueListIterator implements Iterator<E>{
		Node it;
		private QueueListIterator(){
			it = first;
		}
		public boolean hasNext() {
			return it != null;
		}
		public E next() {
			E out = it.item;
			it = it.next;
			return out;
		}
		public void remove() {
			throw new UnsupportedOperationException("Remove not supported");
		}
	}
}
