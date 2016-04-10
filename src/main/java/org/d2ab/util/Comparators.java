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

package org.d2ab.util;

import java.util.Comparator;

import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;

/**
 * Utilities for comparators.
 */
public class Comparators {
	public static final Comparator<?> NATURAL_ORDER_NULLS_FIRST = nullsFirst((Comparator<?>) naturalOrder());

	@SuppressWarnings("unchecked")
	public static <T> Comparator<T> naturalOrderNullsFirst() {
		return (Comparator<T>) NATURAL_ORDER_NULLS_FIRST;
	}
}
