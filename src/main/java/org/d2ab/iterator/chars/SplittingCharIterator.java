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

package org.d2ab.iterator.chars;

import org.d2ab.collection.chars.CharList;
import org.d2ab.function.CharPredicate;
import org.d2ab.iterator.DelegatingTransformingIterator;
import org.d2ab.sequence.CharSeq;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An {@link Iterator} that can batch up another iterator by comparing two items in sequence and deciding whether
 * to split up in a batch on those items.
 */
public class SplittingCharIterator extends DelegatingTransformingIterator<Character, CharIterator, CharSeq> {
	private final CharPredicate predicate;

	public SplittingCharIterator(CharIterator iterator, char element) {
		this(iterator, c -> c == element);
	}

	public SplittingCharIterator(CharIterator iterator, CharPredicate predicate) {
		super(iterator);
		this.predicate = predicate;
	}

	@Override
	public CharSeq next() {
		if (!hasNext())
			throw new NoSuchElementException();

		CharList buffer = CharList.create();
		while (iterator.hasNext()) {
			char next = iterator.nextChar();
			if (predicate.test(next))
				break;

			buffer.addChar(next);
		}

		return CharSeq.from(buffer);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
