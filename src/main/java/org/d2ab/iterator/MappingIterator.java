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

import java.util.Iterator;
import java.util.function.Function;

public class MappingIterator<T, U> extends DelegatingReferenceIterator<T, U> {
	private final Function<? super T, ? extends U> mapper;

	public MappingIterator(Iterator<T> iterator, Function<? super T, ? extends U> mapper) {
		super(iterator);
		this.mapper = mapper;
	}

	@Override
	public U next() {
		return mapper.apply(iterator.next());
	}
}
