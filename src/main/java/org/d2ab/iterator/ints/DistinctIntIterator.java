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

package org.d2ab.iterator.ints;

import org.d2ab.collection.ints.BitIntSet;
import org.d2ab.collection.ints.IntSet;

import java.util.NoSuchElementException;

public class DistinctIntIterator extends DelegatingUnaryIntIterator {
	private final IntSet seen = new BitIntSet();

	private int next;
	private boolean hasNext;

	public DistinctIntIterator(IntIterator iterator) {
		super(iterator);
	}

	@Override
	public int nextInt() {
		if (!hasNext())
			throw new NoSuchElementException();

		hasNext = false;
		return next;
	}

	@Override
	public boolean hasNext() {
		if (hasNext)
			return true;

		while (!hasNext && iterator.hasNext()) {
			int maybeNext = iterator.nextInt();
			if (hasNext = seen.addInt(maybeNext))
				next = maybeNext;
		}

		return hasNext;
	}
}
