/*
 * Copyright 2015 Daniel Skogquist Åborg
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

import org.d2ab.function.ints.ForwardPeekingIntFunction;

import java.util.NoSuchElementException;

/**
 * An iterator over ints that also maps each element by looking at the current AND the next element.
 */
public class ForwardPeekingIntIterator extends UnaryIntIterator {
	private final ForwardPeekingIntFunction mapper;

	private boolean hasCurrent;
	private int current = -1;
	private boolean started;

	public ForwardPeekingIntIterator(ForwardPeekingIntFunction mapper) {
		this.mapper = mapper;
	}

	@Override
	public int nextInt() {
		if (!hasNext())
			throw new NoSuchElementException();

		boolean hasNext = iterator.hasNext();
		int next = hasNext ? iterator.nextInt() : -1;

		int result = mapper.applyAndPeek(current, hasNext, next);

		current = next;
		hasCurrent = hasNext;
		return result;
	}

	@Override
	public boolean hasNext() {
		if (!started) {
			started = true;
			if (iterator.hasNext()) {
				current = iterator.next();
				hasCurrent = true;
			}
		}
		return hasCurrent;
	}
}