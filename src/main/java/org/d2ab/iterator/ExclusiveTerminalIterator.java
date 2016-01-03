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
package org.d2ab.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class ExclusiveTerminalIterator<T> implements Iterator<T> {
	private final Iterator<T> iterator;
	private final T terminal;
	private T next;
	private boolean gotNext;

	public ExclusiveTerminalIterator(Iterator<T> iterator, T terminal) {
		this.iterator = iterator;
		this.terminal = terminal;
	}

	@Override
	public boolean hasNext() {
		if (!gotNext && iterator.hasNext()) {
			next = iterator.next();
			gotNext = true;
		}
		return gotNext && !Objects.equals(next, terminal);
	}

	@Override
	public T next() {
		if (!hasNext())
			throw new NoSuchElementException();

		T result = next;
		gotNext = false;
		next = null;
		return result;
	}
}
