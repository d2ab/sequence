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

import org.d2ab.function.CharConsumer;
import org.d2ab.iterator.IterationException;
import org.d2ab.iterator.chars.ArrayCharIterator;
import org.d2ab.iterator.chars.CharIterator;
import org.d2ab.iterator.chars.ReaderCharIterator;

import java.io.IOException;
import java.io.Reader;
import java.util.function.Consumer;

import static java.util.Arrays.asList;

@FunctionalInterface
public interface CharIterable extends Iterable<Character> {
	/**
	 * Create a {@code CharIterable} from a {@link Reader} which iterates over the characters provided in the reader.
	 * The {@link Reader} must support {@link Reader#reset} or the {@code CharIterable} will only be available to
	 * iterate over once. The {@link Reader} will be reset in between iterations, if possible. If an
	 * {@link IOException} occurs during iteration, an {@link IterationException} will be thrown. The {@link Reader}
	 * will not be closed by the {@code CharIterable} when iteration finishes, it must be closed externally when
	 * iteration is finished.
	 *
	 * @since 1.2
	 */
	static CharIterable read(Reader reader) {
		return new CharIterable() {
			boolean started;

			@Override
			public CharIterator iterator() {
				if (started)
					try {
						reader.reset();
					} catch (IOException e) {
						// do nothing, let reader exhaust itself
					}
				else
					started = true;

				return new ReaderCharIterator(reader);
			}
		};
	}

	@Override
	CharIterator iterator();

	/**
	 * Perform the given action for each {@code char} in this iterable.
	 */
	default void forEachChar(CharConsumer consumer) {
		iterator().forEachRemaining(consumer);
	}

	/**
	 * Perform the given action for each {@code char} in this iterable.
	 */
	@Override
	default void forEach(Consumer<? super Character> consumer) {
		iterator().forEachRemaining(consumer);
	}

	static CharIterable of(char... characters) {
		return () -> new ArrayCharIterator(characters);
	}

	static CharIterable from(Character... characters) {
		return from(asList(characters));
	}

	static CharIterable from(Iterable<Character> iterable) {
		return () -> CharIterator.from(iterable.iterator());
	}

	static CharIterable once(CharIterator iterator) {
		return () -> iterator;
	}

	/**
	 * @return this {@code CharIterable} as a {@link Reader}. Mark and reset is supported, by re-traversing
	 * the iterator to the mark position.
	 *
	 * @since 1.2
	 */
	default Reader asReader() {
		return new CharIterableReader(this);
	}
}
