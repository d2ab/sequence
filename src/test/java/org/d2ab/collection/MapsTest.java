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

import org.junit.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.d2ab.test.Tests.expecting;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class MapsTest {
	@Test
	public void constructor() {
		new Maps() {
			// test coverage
		};
	}

	@Test
	public void entry() {
		Entry<Integer, String> entry = Maps.entry(1, "2");

		assertThat(entry.getKey(), is(1));
		assertThat(entry.getValue(), is("2"));
		expecting(UnsupportedOperationException.class, () -> entry.setValue("3"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void entrySerialization() throws IOException, ClassNotFoundException {
		Entry<Integer, String> original = Maps.entry(1, "2");

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bytes);
		out.writeObject(original);

		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
		Entry<Integer, String> deserialized = (Entry<Integer, String>) in.readObject();

		assertThat(deserialized, is(equalTo(original)));
	}

	@Test
	public void builderFromEmpty() {
		assertThat(Maps.builder().build(), is(emptyMap()));

		assertThat(Maps.builder().put(1, "2").build(), is(singletonMap(1, "2")));

		Map<Integer, String> expectedTwo = new HashMap<>();
		expectedTwo.put(1, "2");
		expectedTwo.put(3, "4");
		assertThat(Maps.builder().put(1, "2").put(3, "4").build(), is(expectedTwo));

		Map<Integer, String> expectedThree = new HashMap<>();
		expectedThree.put(1, "2");
		expectedThree.put(3, "4");
		expectedThree.put(5, "6");
		assertThat(Maps.builder().put(1, "2").put(3, "4").put(5, "6").build(), is(expectedThree));
	}

	@Test
	public void builderFromCapacityConstructor() {
		assertThat(Maps.builder(HashMap::new, 17).build(), is(emptyMap()));

		assertThat(Maps.builder(HashMap::new, 17).put(1, "2").build(), is(singletonMap(1, "2")));

		Map<Integer, String> expectedTwo = new HashMap<>();
		expectedTwo.put(1, "2");
		expectedTwo.put(3, "4");
		assertThat(Maps.builder(HashMap::new, 17).put(1, "2").put(3, "4").build(), is(expectedTwo));

		Map<Integer, String> expectedThree = new HashMap<>();
		expectedThree.put(1, "2");
		expectedThree.put(3, "4");
		expectedThree.put(5, "6");
		assertThat(Maps.builder(HashMap::new, 17).put(1, "2").put(3, "4").put(5, "6").build(),
		           is(expectedThree));
	}

	@Test
	public void builderFromSingleton() {
		assertThat(Maps.builder(1, "2").build(), is(singletonMap(1, "2")));

		Map<Integer, String> expectedTwo = new HashMap<>();
		expectedTwo.put(1, "2");
		expectedTwo.put(3, "4");
		assertThat(Maps.builder(1, "2").put(3, "4").build(), is(expectedTwo));

		Map<Integer, String> expectedThree = new HashMap<>();
		expectedThree.put(1, "2");
		expectedThree.put(3, "4");
		expectedThree.put(5, "6");
		assertThat(Maps.builder(1, "2").put(3, "4").put(5, "6").build(), is(expectedThree));
	}

	@Test
	public void entryIterator() {
		Iterator<Integer> iterator = Maps.iterator(Maps.entry(1, 2));
		assertThat(iterator.hasNext(), is(true));
		assertThat(iterator.next(), is(1));
		assertThat(iterator.hasNext(), is(true));
		assertThat(iterator.next(), is(2));
		assertThat(iterator.hasNext(), is(false));
		expecting(NoSuchElementException.class, iterator::next);
	}

	@Test
	public void entryEquals() {
		Entry<Integer, String> _1 = Maps.entry(1, "1");
		assertThat(_1, is(equalTo(_1)));
		assertThat(_1, is(equalTo(Maps.entry(1, "1"))));
		assertThat(_1, is(not(equalTo(Maps.entry(1, "2")))));
		assertThat(_1, is(not(equalTo(Maps.entry(2, "1")))));
		assertThat(_1, is(not(equalTo(Maps.entry(2, "2")))));
		assertThat(_1, is(not(equalTo(new Object()))));
		assertThat(_1, is(not(equalTo(null))));
	}

	@Test
	public void entryHashCode() {
		assertThat(Maps.entry(1, "2").hashCode(), is(81));
		assertThat(Maps.entry(1, "3").hashCode(), is(82));
		assertThat(Maps.entry(2, "2").hashCode(), is(112));
		assertThat(Maps.entry(2, "3").hashCode(), is(113));
		assertThat(Maps.entry(1, null).hashCode(), is(31));
		assertThat(Maps.entry(null, "2").hashCode(), is(50));
		assertThat(Maps.entry(null, null).hashCode(), is(0));
	}
}