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
import java.util.function.IntPredicate;

public class ExclusiveTerminalIntIterator extends DelegatingUnaryIntIterator {
	private final IntPredicate terminal;

	private int next;
	private boolean hasNext;

	public ExclusiveTerminalIntIterator(IntIterator iterator, int terminal) {
		this(iterator, i -> i == terminal);
	}

	public ExclusiveTerminalIntIterator(IntIterator iterator, IntPredicate terminal) {
		super(iterator);
		this.terminal = terminal;
	}

	@Override
	public boolean hasNext() {
		if (!hasNext && iterator.hasNext()) {
			next = iterator.nextInt();
			hasNext = true;
		}
		return hasNext && !terminal.test(next);
	}

	@Override
	public int nextInt() {
		if (!hasNext())
			throw new NoSuchElementException();

		hasNext = false;
		return next;
	}
}
