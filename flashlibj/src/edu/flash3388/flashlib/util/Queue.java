package edu.flash3388.flashlib.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * A collection designed for holding elements prior to processing. 
 * Besides basic Collection operations, queues provide additional insertion, extraction, and inspection operations. 
 * Queue order elements in a FIFO (first-in-first-out) manner. The head can be removed by calling {@link #dequeue()}.
 * 
 * @since FlashLib 1.0.0
 * @author Tom Tzook 
 * @param <T> the type of elements held in this collection
 */
@SuppressWarnings("unchecked")
public class Queue<T> implements java.util.Queue<T>{

	private Object[] elements;
	private int nextIndex = 0;
	
	/**
	 * Creates a new empty Queue with an initial array size.
	 * 
	 * @param size the initial size of the queue
	 * @throws IllegalArgumentException if the size is smaller than 1
	 */
	public Queue(int size){
		if(size < 1)
			throw new IllegalArgumentException("Start size must be atleast 1");
		elements = new Object[size];
	}
	/**
	 * Creates a new empty Queue with an initial array size of 10 elements.
	 */
	public Queue(){
		this(10);
	}
	
	/**
	 * Fills the queue with elements from an array. Elements will be inserted by order into the queue and will override any
	 * existing elements. 
	 * 
	 * @param elements an array of elements 
	 */
	public void fill(T[] elements){
		if(elements.length >= this.elements.length)
			expend(5 + elements.length);
		if(nextIndex > elements.length){
			for (int i = elements.length; i < nextIndex; i++)
				this.elements[i] = null;
		}
		System.arraycopy(elements, 0, this.elements, 0, elements.length);
		nextIndex = elements.length;
	}
	
	/**
	 * Gets the amount of elements in the queue.
	 * @return the amount of elements in the queue.
	 */
	public int size(){
		return nextIndex;
	}
	/**
	 * Gets the size of the array holding the elements in the queue. The array is never the save size as the amount of
	 * elements in the queue.
	 * @return the length of the array holding elements
	 */
	public int capacity(){
		return elements.length;
	}
	/**
	 * Gets whether this queue is empty of elements.
	 * @return true if the queue is empty, false otherwise
	 */
	public boolean isEmpty(){
		return size() == 0;
	}
	
	/**
	 * Adds a new element into the end of the queue. if the array holding the elements is not big enough, a new one
	 * is created with extra 10 places.
	 * 
	 * @param element the element to add
	 */
	public void enqueue(T element){
		checkSize();
		elements[nextIndex++] = element;
	}
	/**
	 * Removes and gets the head of the queue. Elements are shifted one place towards the head.
	 * 
	 * @return the head of the queue
	 */
	public T dequeue(){
		if(isEmpty()) return null;
		T element = (T) elements[0];
		shift();
		nextIndex--;
		return element;
	}
	/**
	 * Gets the head of the queue without removing it/
	 * @return the head of the queue.
	 */
	public T peek(){
		if(isEmpty()) return null;
		return (T) elements[0];
	}
	/**
	 * Removes all elements in the queue.
	 */
	public void clear(){
		for (int i = 0; i < size(); i++) 
			elements[i] = null;
		nextIndex = 0;
	}
	
	private void shift(){
		shift(0);
	}
	private void shift(int index){
		for(int i = 0; i < size()-1; i++){
			elements[i] = elements[i+1];
			elements[i+1] = null;
		}
	}
	private void checkSize(){
		if(size() < capacity()) return;
		expend(10);
	}
	private void expend(int size){
		Object[] newarr = new Object[capacity() + size];
		System.arraycopy(elements, 0, newarr, 0, capacity());
		elements = newarr;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(Object o) {
		for(int i = 0; i < size(); i++){
			if(o.equals(elements[i]))
				return true;
		}
		return false;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<T> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] toArray() {
		return Arrays.copyOfRange(elements, 0, size());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E> E[] toArray(E[] a) {
		if (a.length < nextIndex)
            return (E[]) Arrays.copyOf(elements, nextIndex, a.getClass());

        System.arraycopy(elements, 0, a, 0, nextIndex);

        if (a.length > nextIndex)
            a[nextIndex] = null;

        return a;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean remove(Object o) {
		for (int i = 0; i < size(); i++) {
			if(elements[i].equals(o)){
				shift(i);
				return true;
			}
		}
		return false;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		return false;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addAll(Collection<? extends T> c) {
		return false;
	}
	@Override
	public boolean removeAll(Collection<?> c) {
		return false;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		return false;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean add(T e) {
		if(size() < capacity()){
			elements[nextIndex++] = e;
			return true;
		}
		throw new IllegalStateException("Queue is full");
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean offer(T e) {
		if(size() < capacity()){
			elements[nextIndex++] = e;
			return true;
		}
		return false;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public T remove() {
		if (isEmpty())
			throw new IllegalStateException("Queue is empty");
		return dequeue();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public T poll() {
		return dequeue();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public T element() {
		return peek();
	}
	/**
	 * Creates an {@link java.util.Enumeration} of elements in the queue and returns it.
	 * @return an {@link java.util.Enumeration} of elements in the queue.
	 */
	public Enumeration<T> elements(){
		T[] ele;
		synchronized (elements) {
			ele = (T[]) new Object[size()];
			System.arraycopy(elements, 0, ele, 0, size());
		}
		return new Enumeration<T>(){
			private T[] elem = ele;
			private int index = 0;
			
			@Override
			public boolean hasMoreElements() {
				return index < elem.length;
			}
			@Override
			public T nextElement() {
				if(index >= elem.length)
					return null;
				return elem[index++];
			}
		};
	}
}
