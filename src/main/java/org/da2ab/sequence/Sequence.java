package org.da2ab.sequence;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyIterator;
import static java.util.Collections.singleton;

/**
 * An {@link Iterable} sequence of elements with {@link Stream}-like operations for refining, transforming and
 * collating the list of elements.
 */
@FunctionalInterface
public interface Sequence<T> extends Iterable<T> {
	@Nonnull
	static <T> Sequence<T> of(@Nonnull T item) {
		return from(singleton(item));
	}

	@Nonnull
	static <T> Sequence<T> from(@Nonnull Iterable<T> items) {
		return items::iterator;
	}

	@SafeVarargs
	@Nonnull
	static <T> Sequence<T> of(@Nonnull T... items) {
		return from(asList(items));
	}

	@Nonnull
	static <T> Sequence<T> empty() {
		return from(emptyIterator());
	}

	@Nonnull
	static <T> Sequence<T> from(@Nonnull Iterator<T> iterator) {
		return () -> iterator;
	}

	@SafeVarargs
	@Nonnull
	static <T> Sequence<T> from(@Nonnull Iterable<T>... iterables) {
		return () -> new ConcatenatingIterator<>(iterables);
	}

	@Nonnull
	static <T> Sequence<T> from(@Nonnull Supplier<Iterator<T>> iteratorSupplier) {
		return iteratorSupplier::get;
	}

	static <T> Sequence<T> recurse(T seed, UnaryOperator<T> op) {
		return () -> new RecursiveIterator<>(seed, op);
	}

	static <T, S> Sequence<S> recurse(T seed, Function<T, S> f, Function<S, T> g) {
		return () -> new RecursiveIterator<>(f.apply(seed), f.compose(g)::apply);
	}

	static <K, V> Sequence<Pair<K, V>> from(Map<K, V> map) {
		return from(map.entrySet()).map(Pair::from);
	}

	@Nonnull
	default <U> Sequence<U> map(@Nonnull Function<? super T, ? extends U> mapper) {
		return () -> new MappingIterator<>(iterator(), mapper);
	}

	@Nonnull
	default Sequence<T> skip(int skip) {
		return () -> new SkippingIterator<>(iterator(), skip);
	}

	@Nonnull
	default Sequence<T> limit(int limit) {
		return () -> new LimitingIterator<>(iterator(), limit);
	}

	@Nonnull
	default Sequence<T> then(@Nonnull Sequence<T> then) {
		return () -> new ConcatenatingIterator<>(this, then);
	}

	@Nonnull
	default Sequence<T> filter(@Nonnull Predicate<T> predicate) {
		return () -> new FilteringIterator<>(iterator(), predicate);
	}

	@Nonnull
	default <U> Sequence<U> flatMap(@Nonnull Function<? super T, ? extends Iterable<U>> mapper) {
		ConcatenatingIterable<U> result = new ConcatenatingIterable<>();
		forEach(each -> result.add(mapper.apply(each)));
		return result::iterator;
	}

	default Sequence<T> untilNull() {
		return () -> new TerminalIterator<>(iterator(), null);
	}

	default Sequence<T> until(T terminal) {
		return () -> new TerminalIterator<>(iterator(), terminal);
	}

	default Set<T> toSet() {
		return toSet(HashSet::new);
	}

	default <S extends Set<T>> S toSet(Supplier<S> constructor) {
		return toCollection(constructor);
	}

	default <U extends Collection<T>> U toCollection(Supplier<U> constructor) {
		return collect(constructor, Collection::add);
	}

	default <C> C collect(Supplier<C> constructor, BiConsumer<C, T> adder) {
		C result = constructor.get();
		forEach(each -> adder.accept(result, each));
		return result;
	}

	default SortedSet<T> toSortedSet() {
		return toSet(TreeSet::new);
	}

	default <K, V> Map<K, V> pairsToMap(Function<? super T, ? extends Pair<K, V>> mapper) {
		return pairsToMap(HashMap::new, mapper);
	}

	default <M extends Map<K, V>, K, V> M pairsToMap(Supplier<? extends M> constructor, Function<? super T, ? extends Pair<K, V>> mapper) {
		M result = constructor.get();
		forEach(each -> mapper.apply(each).put(result));
		return result;
	}

	default <K, V> Map<K, V> toMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper) {
		return toMap(HashMap::new, keyMapper, valueMapper);
	}

	default <M extends Map<K, V>, K, V> M toMap(Supplier<? extends M> constructor, Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper) {
		M result = constructor.get();
		forEach(each -> {
			K key = keyMapper.apply(each);
			V value = valueMapper.apply(each);
			result.put(key, value);
		});
		return result;
	}

	default <K, V> SortedMap<K, V> toSortedMap(Function<T, K> keyMapper, Function<T, V> valueMapper) {
		return toMap(TreeMap::new, keyMapper, valueMapper);
	}

	default <S, R> S collect(Collector<T, R, S> collector) {
		R result = collector.supplier().get();
		BiConsumer<R, T> accumulator = collector.accumulator();
		forEach(each -> accumulator.accept(result, each));
		return collector.finisher().apply(result);
	}

	default String join(String delimiter) {
		return join("", delimiter, "");
	}

	default String join(String prefix, String delimiter, String suffix) {
		StringBuilder result = new StringBuilder();
		result.append(prefix);
		boolean first = true;
		for (T each : this) {
			if (first)
				first = false;
			else
				result.append(delimiter);
			result.append(each);
		}
		result.append(suffix);
		return result.toString();
	}

	default T reduce(T identity, BinaryOperator<T> operator) {
		return reduce(identity, operator, iterator());
	}

	default T reduce(T identity, BinaryOperator<T> operator, Iterator<T> iterator) {
		T result = identity;
		while (iterator.hasNext())
			result = operator.apply(result, iterator.next());
		return result;
	}

	default Optional<T> first() {
		Iterator<T> iterator = iterator();
		if (!iterator.hasNext())
			return Optional.empty();

		return Optional.of(iterator.next());
	}

	default Optional<T> second() {
		Iterator<T> iterator = iterator();

		skip(iterator);
		if (!iterator.hasNext())
			return Optional.empty();

		return Optional.of(iterator.next());
	}

	default void skip(Iterator<T> iterator) {
		if (iterator.hasNext())
			iterator.next();
	}

	default Optional<T> third() {
		Iterator<T> iterator = iterator();

		skip(iterator);
		skip(iterator);
		if (!iterator.hasNext())
			return Optional.empty();

		return Optional.of(iterator.next());
	}

	default Optional<T> last() {
		Iterator<T> iterator = iterator();
		if (!iterator.hasNext())
			return Optional.empty();

		T last;
		do {
			last = iterator.next();
		} while (iterator.hasNext());

		return Optional.of(last);
	}

	default Sequence<Pair<T, T>> pairs() {
		return () -> new PairingIterator<>(iterator());
	}

	default Sequence<List<T>> partition(int window) {
		return () -> new PartitioningIterator<>(iterator(), window);
	}

	default Sequence<T> step(int step) {
		return () -> new SteppingIterator<>(iterator(), step);
	}

	default Sequence<T> distinct() {
		return () -> new DistinctIterator<>(iterator());
	}

	default <S extends Comparable<? super S>> Sequence<S> sorted() {
		return () -> new SortingIterator<>((Iterator<S>) iterator());
	}

	default Sequence<T> sorted(Comparator<? super T> comparator) {
		return () -> new SortingIterator<T>(iterator(), comparator);
	}

	default Optional<T> min(Comparator<? super T> comparator) {
		return reduce(BinaryOperator.minBy(comparator));
	}

	default Optional<T> reduce(BinaryOperator<T> operator) {
		Iterator<T> iterator = iterator();
		if (!iterator.hasNext())
			return Optional.empty();

		T result = reduce(iterator.next(), operator, iterator);
		return Optional.of(result);
	}

	default Optional<T> max(Comparator<? super T> comparator) {
		return reduce(BinaryOperator.maxBy(comparator));
	}

	default int count() {
		int count = 0;
		for (T ignored : this) {
			count++;
		}
		return count;
	}

	default Object[] toArray() {
		return toList().toArray();
	}

	default List<T> toList() {
		return toList(ArrayList::new);
	}

	default List<T> toList(Supplier<List<T>> constructor) {
		return toCollection(constructor);
	}

	default <A> A[] toArray(IntFunction<A[]> constructor) {
		List result = toList();
		return (A[]) result.toArray(constructor.apply(result.size()));
	}

	default boolean all(Predicate<T> predicate) {
		for (T each : this) {
			if (!predicate.test(each))
				return false;
		}
		return true;
	}

	default boolean none(Predicate<T> predicate) {
		return !any(predicate);
	}

	default boolean any(Predicate<T> predicate) {
		for (T each : this) {
			if (predicate.test(each))
				return true;
		}
		return false;
	}

	default Sequence<T> peek(Consumer<T> action) {
		return () -> new PeekingIterator(iterator(), action);
	}
}
