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

package org.d2ab.collection.doubles;

import org.d2ab.collection.PrimitiveCollections;
import org.d2ab.iterator.doubles.DelegatingUnaryDoubleIterator;
import org.d2ab.iterator.doubles.DoubleIterator;
import org.d2ab.iterator.doubles.LimitingDoubleIterator;
import org.d2ab.iterator.doubles.SkippingDoubleIterator;
import org.d2ab.util.Doubles;
import org.d2ab.util.Strict;

import java.util.*;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * A primitive specialization of {@link List} for {@code double} values.
 */
public interface DoubleList extends List<Double>, DoubleCollection {
	/**
	 * Returns an immutable {@code DoubleList} of the given elements. The returned {@code DoubleList}'s
	 * {@link DoubleListIterator} supports forward iteration only.
	 */
	static DoubleList of(double... xs) {
		return new DoubleList.Base() {
			@Override
			public DoubleListIterator listIterator(int index) {
				return new ArrayDoubleListIterator(index, xs);
			}

			@Override
			public int size() {
				return xs.length;
			}
		};
	}

	/**
	 * @return a new empty mutable {@code DoubleList}.
	 *
	 * @since 2.1
	 */
	static DoubleList create() {
		return ArrayDoubleList.create();
	}

	/**
	 * @return a new mutable {@code DoubleList} with a copy of the given elements.
	 *
	 * @since 2.1
	 */
	static DoubleList create(double... xs) {
		return ArrayDoubleList.create(xs);
	}

	/**
	 * @return a {@code DoubleList} initialized with the members of the given {@link PrimitiveIterator.OfDouble}.
	 */
	static DoubleList copy(PrimitiveIterator.OfDouble iterator) {
		DoubleList copy = create();
		while (iterator.hasNext())
			copy.addDoubleExactly(iterator.nextDouble());
		return copy;
	}

	@Override
	default DoubleIterator iterator() {
		return listIterator();
	}

	@Override
	default DoubleListIterator listIterator() {
		return listIterator(0);
	}

	@Override
	DoubleListIterator listIterator(int index);

	default void clear() {
		iterator().removeAll();
	}

	@Override
	default boolean isEmpty() {
		return size() == 0;
	}

	@Override
	default DoubleList asList() {
		return this;
	}

	@Override
	default Double[] toArray() {
		return toArray(new Double[size()]);
	}

	@Override
	default <T> T[] toArray(T[] a) {
		return PrimitiveCollections.toArray(this, a);
	}

	@Override
	default boolean add(Double x) {
		return DoubleCollections.add(this, x);
	}

	@Override
	default boolean contains(Object o) {
		return DoubleCollections.contains(this, o);
	}

	@Override
	default boolean remove(Object o) {
		return DoubleCollections.remove(this, o);
	}

	@Override
	default boolean addAll(int index, Collection<? extends Double> c) {
		if (c instanceof DoubleCollection)
			return addAllDoublesAt(index, (DoubleCollection) c);

		Strict.check();

		if (c.isEmpty())
			return false;

		DoubleListIterator listIterator = listIterator(index);
		for (double t : c)
			listIterator.add(t);

		return true;
	}

	default boolean addAllDoublesAt(int index, double... xs) {
		if (xs.length == 0)
			return false;

		DoubleListIterator listIterator = listIterator(index);
		for (double x : xs)
			listIterator.add(x);

		return true;
	}

	default boolean addAllDoublesAt(int index, DoubleCollection xs) {
		DoubleListIterator listIterator = listIterator(index);

		boolean modified = false;
		for (DoubleIterator iterator = xs.iterator(); iterator.hasNext(); ) {
			listIterator.add(iterator.nextDouble());
			modified = true;
		}
		return modified;
	}

	@Override
	default void replaceAll(UnaryOperator<Double> operator) {
		Strict.check();

		replaceAllDoubles(operator::apply);
	}

	default void replaceAllDoubles(DoubleUnaryOperator operator) {
		DoubleListIterator listIterator = listIterator();
		while (listIterator.hasNext())
			listIterator.set(operator.applyAsDouble(listIterator.nextDouble()));
	}

	default void sortDoubles() {
		throw new UnsupportedOperationException();
	}

	default int binarySearchExactly(double x) {
		throw new UnsupportedOperationException();
	}

	@Override
	default void sort(Comparator<? super Double> c) {
		Strict.check();

		throw new UnsupportedOperationException();
	}

	@Override
	default DoubleList subList(int from, int to) {
		return new SubList(this, from, to);
	}

	@Override
	default boolean addAll(Collection<? extends Double> c) {
		return DoubleCollections.addAll(this, c);
	}

	@Override
	default boolean addDoubleExactly(double x) {
		listIterator(size()).add(x);
		return true;
	}

	@Override
	default boolean addDouble(double x, double precision) {
		return addDoubleExactly(x);
	}

	@Override
	default boolean addAllDoubles(double... xs) {
		if (xs.length == 0)
			return false;

		DoubleListIterator listIterator = listIterator(size());
		for (double x : xs)
			listIterator.add(x);

		return true;
	}

	@Override
	default boolean addAllDoubles(DoubleCollection xs) {
		DoubleListIterator listIterator = listIterator(size());

		boolean modified = false;
		for (DoubleIterator iterator = xs.iterator(); iterator.hasNext(); ) {
			listIterator.add(iterator.nextDouble());
			modified = true;
		}
		return modified;
	}

	@Override
	default boolean containsAll(Collection<?> c) {
		return DoubleCollections.containsAll(this, c);
	}

	@Override
	default boolean removeAll(Collection<?> c) {
		return DoubleCollections.removeAll(this, c);
	}

	@Override
	default boolean removeIf(Predicate<? super Double> filter) {
		Strict.check();

		return removeDoublesIf(filter::test);
	}

	@Override
	default boolean retainAll(Collection<?> c) {
		return DoubleCollections.retainAll(this, c);
	}

	@Override
	default Double get(int index) {
		Strict.check();

		return getDouble(index);
	}

	default double getDouble(int index) {
		DoubleListIterator iterator = listIterator(index);
		if (!iterator.hasNext())
			throw new IndexOutOfBoundsException(String.valueOf(index));

		return iterator.nextDouble();
	}

	@Override
	default Double set(int index, Double x) {
		Strict.check();

		return setDouble(index, x);
	}

	default double setDouble(int index, double x) {
		DoubleListIterator listIterator = listIterator(index);
		if (!listIterator.hasNext())
			throw new IndexOutOfBoundsException(String.valueOf(index));
		double previous = listIterator.nextDouble();
		listIterator.set(x);
		return previous;
	}

	@Override
	default void add(int index, Double x) {
		Strict.check();

		addDoubleAt(index, x);
	}

	default void addDoubleAt(int index, double x) {
		listIterator(index).add(x);
	}

	@Override
	default Double remove(int index) {
		Strict.check();

		return removeDoubleAt(index);
	}

	default double removeDoubleAt(int index) {
		DoubleListIterator listIterator = listIterator(index);
		if (!listIterator.hasNext())
			throw new IndexOutOfBoundsException(String.valueOf(index));
		double previous = listIterator.nextDouble();
		listIterator.remove();
		return previous;
	}

	@Override
	default int lastIndexOf(Object o) {
		Strict.check();

		return o instanceof Double ? lastIndexOfDoubleExactly((double) o) : -1;
	}

	default int lastIndexOfDoubleExactly(double x) {
		int lastIndex = -1;

		int index = 0;
		for (DoubleIterator iterator = iterator(); iterator.hasNext(); index++)
			if (iterator.nextDouble() == x)
				lastIndex = index;

		return lastIndex;
	}

	default int lastIndexOfDouble(double x, double precision) {
		int lastIndex = -1;

		int index = 0;
		for (DoubleIterator iterator = iterator(); iterator.hasNext(); index++)
			if (Doubles.eq(iterator.nextDouble(), x, precision))
				lastIndex = index;

		return lastIndex;
	}

	@Override
	default int indexOf(Object o) {
		Strict.check();

		return o instanceof Double ? indexOfDoubleExactly((double) o) : -1;
	}

	default int indexOfDoubleExactly(double x) {
		int index = 0;
		for (DoubleIterator iterator = iterator(); iterator.hasNext(); index++)
			if (iterator.nextDouble() == x)
				return index;

		return -1;
	}

	default int indexOfDouble(double x, double precision) {
		int index = 0;
		for (DoubleIterator iterator = iterator(); iterator.hasNext(); index++)
			if (Doubles.eq(iterator.nextDouble(), x, precision))
				return index;

		return -1;
	}

	@Override
	default Spliterator.OfDouble spliterator() {
		return Spliterators.spliterator(iterator(), size(), Spliterator.ORDERED | Spliterator.NONNULL);
	}

	/**
	 * Base class for {@link DoubleList} implementations.
	 */
	abstract class Base extends DoubleCollection.Base implements DoubleList {
		public static DoubleList create(double... doubles) {
			final DoubleList backing = DoubleList.create(doubles);
			return new DoubleList.Base() {
				@Override
				public DoubleListIterator listIterator(int index) {
					return backing.listIterator(index);
				}

				@Override
				public int size() {
					return backing.size();
				}
			};
		}

		public boolean equals(Object o) {
			if (o == this)
				return true;
			if (!(o instanceof List))
				return false;

			DoubleListIterator it = listIterator();
			if (o instanceof DoubleList) {
				DoubleListIterator that = ((DoubleList) o).listIterator();
				while (it.hasNext()) {
					if (!that.hasNext())
						return false;
					if (it.nextDouble() != that.nextDouble())
						return false;
				}
				return !that.hasNext();
			} else {
				ListIterator<?> that = ((List<?>) o).listIterator();
				while (it.hasNext()) {
					if (!that.hasNext())
						return false;
					double x = it.nextDouble();
					Object y = that.next();
					if (!(y instanceof Double && x == (Double) y))
						return false;
				}
				return !that.hasNext();
			}
		}

		public int hashCode() {
			int hashCode = 1;
			for (DoubleIterator iterator = iterator(); iterator.hasNext(); )
				hashCode = 31 * hashCode + Double.hashCode(iterator.nextDouble());
			return hashCode;
		}
	}

	class SubList implements IterableDoubleList {
		private final DoubleList list;

		private int from;
		private int to;

		public SubList(DoubleList list, int from, int to) {
			if (from < 0)
				throw new ArrayIndexOutOfBoundsException(from);
			if (to > list.size())
				throw new ArrayIndexOutOfBoundsException(to);
			this.list = list;
			this.from = from;
			this.to = to;
		}

		public DoubleIterator iterator() {
			return new DelegatingUnaryDoubleIterator(
					new LimitingDoubleIterator(new SkippingDoubleIterator(list.iterator(), from), to - from)) {
				@Override
				public double nextDouble() {
					return iterator.nextDouble();
				}

				@Override
				public void remove() {
					super.remove();
					to--;
				}
			};
		}

		public int size() {
			return to - from;
		}
	}
}
