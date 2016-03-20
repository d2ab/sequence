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

package org.d2ab.function.chars;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/**
 * Represents an operation that accepts a single {@code char}-valued argument and returns no result.
 * <p>
 * This is the primitive type specialization of {@link Consumer} for {@code char}. Unlike most other functional
 * interfaces, {@code CharConsumer} is expected to operate via side-effects.
 * <p>
 * This is a functional interface whose functional method is {@link #accept(char)}.
 *
 * @see Consumer
 * @see IntConsumer
 */
@FunctionalInterface
public interface CharConsumer {
	/**
	 * Returns a composed {@code CharConsumer} that performs, in sequence, this operation followed by the {@code after}
	 * operation.
	 * <p>
	 * If performing either operation throws an exception, it is relayed to the caller of the composed operation. If
	 * performing this operation throws an exception, the {@code after} operation will not be performed.
	 *
	 * @param after the operation to perform after this operation
	 *
	 * @return a composed {@code CharConsumer} that performs in sequence this operation followed by the {@code after}
	 * operation
	 *
	 * @throws NullPointerException if {@code after} is null
	 */
	default CharConsumer andThen(CharConsumer after) {
		Objects.requireNonNull(after);
		return (char t) -> {
			accept(t);
			after.accept(t);
		};
	}

	/**
	 * Performs this operation on the given argument.
	 *
	 * @param value the input argument
	 */
	void accept(char value);
}
