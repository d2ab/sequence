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

import java.util.NoSuchElementException;

/**
 * An iterator that skips a set number of steps at the end of another iterator.
 */
public class TailSkippingIntIterator extends DelegatingUnaryIntIterator {
	private final int skip;

	private boolean started;
	private int[] buffer;
	private int position;

	public TailSkippingIntIterator(IntIterator iterator, int skip) {
		super(iterator);
		this.skip = skip;
	}

	@Override
	public boolean hasNext() {
		if (!started) {
			buffer = new int[skip];
			while (position < skip && iterator.hasNext())
				buffer[position++] = iterator.nextInt();
			position = 0;

			started = true;
		}
		return super.hasNext();
	}

	@Override
	public int nextInt() {
		if (!hasNext())
			throw new NoSuchElementException();

		int next = buffer[position];
		buffer[position++] = iterator.nextInt();
		position = position % skip;
		return next;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
