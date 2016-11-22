/*
 * Copyright 2016 Daniel Skogquist Åborg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.d2ab.collection.chars;

import org.d2ab.collection.Arrayz;
import org.d2ab.function.CharConsumer;
import org.d2ab.function.CharPredicate;
import org.d2ab.function.CharUnaryOperator;
import org.d2ab.iterator.chars.CharIterator;

import java.util.Arrays;

/**
 * An {@link CharList} backed by an char-array, supporting all {@link CharList}-methods by modifying and/or replacing the
 * underlying array.
 */
public class ArrayCharList implements CharList {
	private char[] contents;
	private int size;

	public static ArrayCharList of(char... contents) {
		return new ArrayCharList(contents);
	}

	public ArrayCharList() {
		this(10);
	}

	public ArrayCharList(int capacity) {
		this.contents = new char[capacity];
	}

	public ArrayCharList(CharCollection xs) {
		this();
		addAllChars(xs);
	}

	/**
	 * Private to avoid conflict with standard int-taking capacity constructor.
	 * Use {@link #of(char...)} for public access.
	 *
	 * @see #ArrayCharList(int)
	 * @see #of(char...)
	 */
	private ArrayCharList(char... contents) {
		this.contents = Arrays.copyOf(contents, contents.length);
		this.size = contents.length;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public void clear() {
		size = 0;
	}

	@Override
	public char[] toCharArray() {
		return Arrays.copyOfRange(contents, 0, size);
	}

	@Override
	public CharIterator iterator() {
		return listIterator();
	}

	@Override
	public CharListIterator listIterator(int index) {
		rangeCheckForAdd(index);
		return new ListIter(index);
	}

	@Override
	public CharList subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void sortChars() {
		Arrays.sort(contents, 0, size);
	}

	@Override
	public int binarySearch(char x) {
		return Arrays.binarySearch(contents, 0, size, x);
	}

	@Override
	public void replaceAllChars(CharUnaryOperator operator) {
		for (int i = 0; i < size; i++)
			contents[i] = operator.applyAsChar(contents[i]);
	}

	@Override
	public char getChar(int index) {
		rangeCheck(index);
		return contents[index];
	}

	@Override
	public char setChar(int index, char x) {
		rangeCheck(index);
		char previous = contents[index];
		contents[index] = x;
		return previous;
	}

	@Override
	public void addCharAt(int index, char x) {
		rangeCheckForAdd(index);
		uncheckedAdd(index, x);
	}

	@Override
	public char removeCharAt(int index) {
		rangeCheck(index);
		char previous = contents[index];
		uncheckedRemove(index);
		return previous;
	}

	@Override
	public int lastIndexOfChar(char x) {
		for (int i = size - 1; i >= 0; i--)
			if (contents[i] == x)
				return i;

		return -1;
	}

	@Override
	public int indexOfChar(char x) {
		for (int i = 0; i < size; i++)
			if (contents[i] == x)
				return i;

		return -1;
	}

	@Override
	public boolean addChar(char x) {
		growIfNecessaryBy(1);
		contents[size++] = x;
		return true;
	}

	@Override
	public boolean addAllChars(char... xs) {
		if (xs.length == 0)
			return false;

		growIfNecessaryBy(xs.length);
		System.arraycopy(xs, 0, contents, size, xs.length);
		size += xs.length;
		return true;
	}

	@Override
	public boolean addAllChars(CharCollection xs) {
		if (xs.isEmpty())
			return false;

		if (xs instanceof ArrayCharList) {
			ArrayCharList axs = (ArrayCharList) xs;

			growIfNecessaryBy(axs.size);
			System.arraycopy(axs.contents, 0, contents, size, axs.size);
			size += axs.size;

			return true;
		} else {
			xs.forEachChar(this::addChar);
			return true;
		}
	}

	@Override
	public boolean addAllCharsAt(int index, char... xs) {
		if (xs.length == 0)
			return false;

		rangeCheckForAdd(index);
		growIfNecessaryBy(xs.length);
		System.arraycopy(contents, index, contents, index + xs.length, size - index);
		System.arraycopy(xs, 0, contents, index, xs.length);
		size += xs.length;
		return true;
	}

	@Override
	public boolean addAllCharsAt(int index, CharCollection xs) {
		if (xs.isEmpty())
			return false;

		rangeCheckForAdd(index);
		growIfNecessaryBy(xs.size());
		System.arraycopy(contents, index, contents, index + xs.size(), size - index);

		if (xs instanceof ArrayCharList) {
			ArrayCharList il = (ArrayCharList) xs;
			System.arraycopy(il.contents, 0, contents, index, il.size);
		} else {
			CharIterator iterator = xs.iterator();
			for (int i = index; i < xs.size(); i++)
				contents[i] = iterator.nextChar();
		}

		size += xs.size();

		return true;
	}

	@Override
	public boolean containsAllChars(char... xs) {
		for (char x : xs)
			if (!containsChar(x))
				return false;

		return true;
	}

	@Override
	public boolean removeChar(char x) {
		for (int i = 0; i < size; i++)
			if (contents[i] == x)
				return uncheckedRemove(i);

		return false;
	}

	@Override
	public boolean containsChar(char x) {
		for (int i = 0; i < size; i++)
			if (contents[i] == x)
				return true;

		return false;
	}

	@Override
	public boolean removeAllChars(char... xs) {
		boolean modified = false;
		for (int i = 0; i < size; i++)
			if (Arrayz.contains(xs, contents[i]))
				modified |= uncheckedRemove(i--);
		return modified;
	}

	@Override
	public boolean retainAllChars(char... xs) {
		boolean modified = false;
		for (int i = 0; i < size; i++)
			if (!Arrayz.contains(xs, contents[i]))
				modified |= uncheckedRemove(i--);
		return modified;
	}

	@Override
	public boolean removeCharsIf(CharPredicate filter) {
		boolean modified = false;
		for (int i = 0; i < size; i++)
			if (filter.test(contents[i]))
				modified |= uncheckedRemove(i--);
		return modified;
	}

	@Override
	public void forEachChar(CharConsumer consumer) {
		for (int i = 0; i < size; i++)
			consumer.accept(contents[i]);
	}

	private void growIfNecessaryBy(int grow) {
		int newSize = size + grow;
		if (newSize > contents.length) {
			int newCapacity = newSize + (newSize >> 1);
			char[] copy = new char[newCapacity];
			System.arraycopy(contents, 0, copy, 0, size);
			contents = copy;
		}
	}

	private boolean uncheckedAdd(int index, char x) {
		growIfNecessaryBy(1);
		System.arraycopy(contents, index, contents, index + 1, size++ - index);
		contents[index] = x;
		return true;
	}

	private boolean uncheckedRemove(int index) {
		System.arraycopy(contents, index + 1, contents, index, size-- - index - 1);
		return true;
	}

	private void rangeCheck(int index) {
		if (index < 0 || index >= size)
			throw new IndexOutOfBoundsException("index: " + index + " size: " + size);
	}

	private void rangeCheckForAdd(int index) {
		if (index < 0 || index > size)
			throw new IndexOutOfBoundsException("index: " + index + " size: " + size);
	}

	private class ListIter implements CharListIterator {
		protected int nextIndex;
		protected int currentIndex;
		protected boolean addOrRemove;
		protected boolean nextOrPrevious;

		private ListIter(int index) {
			this.nextIndex = index;
			this.currentIndex = index - 1;
		}

		@Override
		public boolean hasNext() {
			return nextIndex < size;
		}

		@Override
		public char nextChar() {
			addOrRemove = false;
			nextOrPrevious = true;
			return contents[currentIndex = nextIndex++];
		}

		@Override
		public boolean hasPrevious() {
			return nextIndex > 0;
		}

		@Override
		public char previousChar() {
			addOrRemove = false;
			nextOrPrevious = true;
			return contents[currentIndex = --nextIndex];
		}

		@Override
		public int nextIndex() {
			return nextIndex;
		}

		@Override
		public int previousIndex() {
			return nextIndex - 1;
		}

		@Override
		public void remove() {
			if (addOrRemove)
				throw new IllegalStateException("add() or remove() called");
			if (!nextOrPrevious)
				throw new IllegalStateException("nextChar() or previousChar() not called");

			uncheckedRemove(nextIndex = currentIndex--);
			addOrRemove = true;
		}

		@Override
		public void set(char x) {
			if (addOrRemove)
				throw new IllegalStateException("add() or remove() called");
			if (!nextOrPrevious)
				throw new IllegalStateException("nextChar() or previousChar() not called");

			contents[currentIndex] = x;
		}

		@Override
		public void add(char x) {
			uncheckedAdd(currentIndex = nextIndex++, x);
			addOrRemove = true;
		}
	}
}