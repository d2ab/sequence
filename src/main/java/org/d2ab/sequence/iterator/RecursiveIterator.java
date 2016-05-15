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

package org.d2ab.sequence.iterator;

import org.d2ab.collection.iterator.InfiniteIterator;

import java.util.function.UnaryOperator;

public class RecursiveIterator<T> implements InfiniteIterator<T> {
	private final T seed;
	private final UnaryOperator<T> op;

	private T previous;
	private boolean hasPrevious;

	public RecursiveIterator(T seed, UnaryOperator<T> op) {
		this.seed = seed;
		this.op = op;
	}

	@Override
	public T next() {
		T next = hasPrevious ? op.apply(previous) : seed;
		previous = next;
		hasPrevious = true;
		return next;
	}
}
