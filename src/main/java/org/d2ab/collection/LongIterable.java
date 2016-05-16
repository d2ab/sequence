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

package org.d2ab.collection;

import org.d2ab.iterator.longs.ArrayLongIterator;
import org.d2ab.iterator.longs.LongIterator;

import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

import static java.util.Arrays.asList;

@FunctionalInterface
public interface LongIterable extends Iterable<Long> {
	static LongIterable of(long... longs) {
		return () -> new ArrayLongIterator(longs);
	}

	static LongIterable from(Long... longs) {
		return from(asList(longs));
	}

	static LongIterable from(Iterable<Long> iterable) {
		if (iterable instanceof LongIterable)
			return (LongIterable) iterable;

		return () -> LongIterator.from(iterable.iterator());
	}

	static LongIterable once(LongIterator iterator) {
		return () -> iterator;
	}

	static LongIterable once(PrimitiveIterator.OfLong iterator) {
		return once(LongIterator.from(iterator));
	}

	@Override
	LongIterator iterator();

	/**
	 * Performs the given action for each {@code long} in this iterable.
	 */
	@Override
	default void forEach(Consumer<? super Long> consumer) {
		iterator().forEachRemaining(consumer);
	}

	/**
	 * Performs the given action for each {@code long} in this iterable.
	 */
	default void forEachLong(LongConsumer consumer) {
		iterator().forEachRemaining(consumer);
	}

	default Spliterator.OfLong spliterator() {
		return Spliterators.spliteratorUnknownSize(iterator(), Spliterator.NONNULL);
	}

	/**
	 * Collect the longs in this {@code LongIterable} into an array.
	 */
	default long[] toLongArray() {
		return iterator().toArray();
	}
}
