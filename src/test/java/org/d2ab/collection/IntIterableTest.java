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

package org.d2ab.collection;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.d2ab.test.IsIntIterableContainingInOrder.containsInts;
import static org.d2ab.test.Tests.twice;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class IntIterableTest {
	private final IntIterable iterable = IntIterable.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

	@Test
	public void read() {
		InputStream inputStream = new ByteArrayInputStream(new byte[]{1, 2, 3, 4, 5});

		IntIterable iterable = IntIterable.read(inputStream);
		twice(() -> assertThat(iterable, containsInts(1, 2, 3, 4, 5)));
	}

	@Test
	public void readEmpty() {
		InputStream inputStream = new ByteArrayInputStream(new byte[0]);

		IntIterable iterable = IntIterable.read(inputStream);
		twice(() -> assertThat(iterable, is(emptyIterable())));
	}

	@Test
	public void readNegatives() {
		InputStream inputStream = new ByteArrayInputStream(new byte[]{-1, -2, -3, -4, -5});

		IntIterable iterable = IntIterable.read(inputStream);
		twice(() -> assertThat(iterable, containsInts(255, 254, 253, 252, 251)));
	}

	@Test
	public void readAlreadyBegun() throws IOException {
		InputStream inputStream = new ByteArrayInputStream(new byte[]{1, 2, 3, 4, 5});
		assertThat(inputStream.read(), CoreMatchers.is(1));

		IntIterable iterable = IntIterable.read(inputStream);
		assertThat(iterable, containsInts(2, 3, 4, 5));
		assertThat(iterable, containsInts(1, 2, 3, 4, 5));
	}

	@Test
	public void readWithMark() throws IOException {
		InputStream inputStream = new ByteArrayInputStream(new byte[]{1, 2, 3, 4, 5});
		assertThat(inputStream.read(), CoreMatchers.is(1));
		inputStream.mark(0);

		IntIterable iterable = IntIterable.read(inputStream);
		twice(() -> assertThat(iterable, containsInts(2, 3, 4, 5)));
	}

	@Test
	public void asInputStreamReadSingleInts() throws Exception {
		InputStream inputStream = iterable.asInputStream();

		assertThat(inputStream.read(), is(1));
		assertThat(inputStream.read(), is(2));
		assertThat(inputStream.read(), is(3));
		assertThat(inputStream.read(), is(4));
		assertThat(inputStream.read(), is(5));
		assertThat(inputStream.read(), is(6));
		assertThat(inputStream.read(), is(7));
		assertThat(inputStream.read(), is(8));
		assertThat(inputStream.read(), is(9));
		assertThat(inputStream.read(), is(10));

		assertThat(inputStream.read(), is(-1));
		assertThat(inputStream.read(), is(-1));
	}

	@Test
	public void asInputStreamIntsOutOfBounds() throws Exception {
		InputStream inputStream = IntIterable.of(1, 256, -1, 2).asInputStream();

		assertThat(inputStream.read(), is(1));
		try {
			inputStream.read();
			fail("Expected IOException");
		} catch (IOException ignored) {
		}
		try {
			inputStream.read();
			fail("Expected IOException");
		} catch (IOException ignored) {
		}
		assertThat(inputStream.read(), is(2));
	}

	@Test
	public void asInputStreamReset() throws Exception {
		InputStream inputStream = iterable.asInputStream();

		assertThat(inputStream.read(), is(1));
		assertThat(inputStream.read(), is(2));

		inputStream.reset();
		assertThat(inputStream.read(), is(1));
		assertThat(inputStream.read(), is(2));
		assertThat(inputStream.read(), is(3));
		assertThat(inputStream.read(), is(4));
		assertThat(inputStream.read(), is(5));
	}

	@Test
	public void asInputStreamMarkAndReset() throws Exception {
		InputStream inputStream = iterable.asInputStream();

		assertThat(inputStream.read(), is(1));
		assertThat(inputStream.read(), is(2));

		inputStream.mark(17);
		assertThat(inputStream.read(), is(3));
		assertThat(inputStream.read(), is(4));

		inputStream.reset();
		assertThat(inputStream.read(), is(3));
		assertThat(inputStream.read(), is(4));

		inputStream.reset();
		assertThat(inputStream.read(), is(3));
		assertThat(inputStream.read(), is(4));
	}

	@Test
	public void asInputStreamReadMultipleBytes() throws Exception {
		InputStream inputStream = iterable.asInputStream();
		byte[] buf = new byte[10];

		assertThat(inputStream.read(buf, 2, 5), is(5));
		assertThat(buf[0], is((byte) 0));
		assertThat(buf[1], is((byte) 0));
		assertThat(buf[2], is((byte) 1));
		assertThat(buf[3], is((byte) 2));
		assertThat(buf[4], is((byte) 3));
		assertThat(buf[5], is((byte) 4));
		assertThat(buf[6], is((byte) 5));
		assertThat(buf[7], is((byte) 0));
		assertThat(buf[8], is((byte) 0));
		assertThat(buf[9], is((byte) 0));

		assertThat(inputStream.read(buf, 0, 0), is(0));
		assertThat(inputStream.read(buf, 0, 10), is(5));
		assertThat(inputStream.read(buf, 0, 10), is(-1));
	}

	@Test
	public void asInputStreamSkip() throws Exception {
		InputStream inputStream = iterable.asInputStream();

		assertThat(inputStream.read(), is(1));
		assertThat(inputStream.read(), is(2));

		assertThat(inputStream.skip(2), is(2L));
		assertThat(inputStream.read(), is(5));

		assertThat(inputStream.skip(10), is(5L));
	}

	@Test
	public void asInputStreamClose() throws Exception {
		InputStream inputStream = iterable.asInputStream();

		assertThat(inputStream.read(), is(1));
		assertThat(inputStream.read(), is(2));
		inputStream.close();

		try {
			inputStream.read();
			fail("Expected IOException");
		} catch (IOException ignored) {
		}
	}
}