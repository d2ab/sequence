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

package org.d2ab.iterator;

import java.util.*;
import java.util.function.Predicate;

/**
 * An {@link Iterator} that can batch up another iterator by comparing two items in sequence and deciding whether
 * to split up in a batch on those items.
 */
public abstract class SplittingIterator<T, S> extends DelegatingMappingIterator<T, S> {
	private final Predicate<? super T> predicate;

	public SplittingIterator(Iterator<T> iterator, T element) {
		this(iterator, e -> Objects.equals(e, element));
	}

	public SplittingIterator(Iterator<T> iterator, Predicate<? super T> predicate) {
		super(iterator);
		this.predicate = predicate;
	}

	@Override
	public S next() {
		if (!hasNext())
			throw new NoSuchElementException();

		List<T> buffer = new ArrayList<>();
		while (iterator.hasNext()) {
			T next = iterator.next();
			if (predicate.test(next))
				break;
			buffer.add(next);
		}

		return toSequence(buffer);
	}

	protected abstract S toSequence(List<T> list);

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
