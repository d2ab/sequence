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

package org.d2ab.primitive.ints;

import java.util.function.IntConsumer;

public class PeekingIntIterator implements IntIterator {
	private final IntIterator iterator;
	private final IntConsumer action;

	public PeekingIntIterator(IntIterator iterator, IntConsumer action) {
		this.iterator = iterator;
		this.action = action;
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public int nextInt() {
		int next = iterator.nextInt();
		action.accept(next);
		return next;
	}
}
