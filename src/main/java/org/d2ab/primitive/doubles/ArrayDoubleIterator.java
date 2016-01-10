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

package org.d2ab.primitive.doubles;

import javax.annotation.Nonnull;
import java.util.Iterator;

/**
 * An {@link Iterator} over an array of items.
 */
public class ArrayDoubleIterator implements DoubleIterator {
	@Nonnull
	private double[] values;
	private int index;

	public ArrayDoubleIterator(@Nonnull double... values) {
		this.values = values;
	}

	@Override
	public boolean hasNext() {
		return index < values.length;
	}

	@Override
	public double nextDouble() {
		return values[index++];
	}
}
