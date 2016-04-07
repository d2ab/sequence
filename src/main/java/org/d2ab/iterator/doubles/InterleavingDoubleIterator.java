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

package org.d2ab.iterator.doubles;

import org.d2ab.iterable.doubles.DoubleIterable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * An {@link Iterator} that interleaves the streams of two other {@link Iterator}s with each other.
 */
public class InterleavingDoubleIterator implements DoubleIterator {
	private final List<DoubleIterator> iterators = new ArrayList<>();

	private int current;

	public InterleavingDoubleIterator(DoubleIterable... iterables) {
		for (DoubleIterable iterable : iterables)
			iterators.add(iterable.iterator());
	}

	@Override
	public double nextDouble() {
		if (!hasNext())
			throw new NoSuchElementException();

		while (!iterators.get(current).hasNext())
			advance();

		DoubleIterator iterator = iterators.get(current);
		advance();
		return iterator.nextDouble();
	}

	@Override
	public boolean hasNext() {
		for (Iterator iterator : iterators)
			if (iterator.hasNext())
				return true;
		return false;
	}

	private void advance() {
		current = (current + 1) % iterators.size();
	}
}
