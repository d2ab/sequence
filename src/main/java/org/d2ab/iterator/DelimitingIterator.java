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
import java.util.Optional;

/**
 * An {@link Iterator} that delimits the items of another {@link Iterator} with a delimiter object.
 */
public class DelimitingIterator<T> implements Iterator<T> {
	private Iterator<? extends T> iterator;
	private Optional<T> prefix;
	private Optional<T> delimiter;
	private Optional<T> suffix;
	private boolean delimiterNext;
	private boolean prefixDone;
	private boolean suffixDone;

	public DelimitingIterator(Iterator<? extends T> iterator, Optional<T> prefix, Optional<T> delimiter,
	                          Optional<T> suffix) {
		this.iterator = iterator;
		this.prefix = prefix;
		this.delimiter = delimiter;
		this.suffix = suffix;
	}

	@Override
	public boolean hasNext() {
		return prefix.isPresent() && !prefixDone ||
		       iterator.hasNext() ||
		       suffix.isPresent() && !suffixDone;
	}

	@Override
	public T next() {
		if (!hasNext())
			throw new NoSuchElementException();

		if (!prefixDone && prefix.isPresent()) {
			prefixDone = true;
			return prefix.get();
		}

		if (!iterator.hasNext() && suffix.isPresent() && !suffixDone) {
			suffixDone = true;
			return suffix.get();
		}

		boolean sendDelimiter = delimiter.isPresent() && !(delimiterNext = !delimiterNext);
		return sendDelimiter ? delimiter.get() : iterator.next();
	}
}