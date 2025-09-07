package unorderedvec;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.ref.WeakReference;
import java.lang.reflect.AccessFlag;
import java.lang.reflect.Array;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public final class UnorderedVecTest {
  @Nested
  public class Q1 {
    @Test
    public void vecOfString() {
      var vec = new UnorderedVec<String>();
      vec.add("foo");
      vec.add("bar");
      assertEquals(2, vec.size());
    }

    @Test
    public void vecOfInteger() {
      var vec = new UnorderedVec<Integer>();
      vec.add(12);
      vec.add(12);
      vec.add(12);
      assertEquals(3, vec.size());
    }

    @Test
    public void vecOfPerson() {
      record Person(String name) {}
      var vec = new UnorderedVec<Person>();
      vec.add(new Person("Jane"));
      assertEquals(1, vec.size());
    }

    @Test
    public void emptyVec() {
      var vec = new UnorderedVec<>();
      assertEquals(0, vec.size());
    }

    @Test
    public void addPrecondition() {
      var vec = new UnorderedVec<>();
      assertThrows(NullPointerException.class, () -> vec.add(null));
    }

    @Test
    public void qualityOfImplementation() {
      assertAll(
          () -> assertTrue(UnorderedVec.class.accessFlags().contains(AccessFlag.PUBLIC)),
          () -> assertTrue(UnorderedVec.class.accessFlags().contains(AccessFlag.FINAL)),
          () -> assertEquals(1, UnorderedVec.class.getConstructors().length),
          () -> assertTrue(Arrays.stream(UnorderedVec.class.getDeclaredFields())
              .noneMatch(f -> f.getType().getPackageName().startsWith("java.util")))
      );
    }
  }


  @Nested
  public class Q2 {
    @Test
    public void vecOfString() {
      var vec = new UnorderedVec<String>();
      vec.add("foo");
      for (var s : vec) {
        assertEquals("foo", s);
      }
    }

    @Test
    public void vecOfInteger() {
      var vec = new UnorderedVec<Integer>();
      vec.add(12);
      vec.add(12);
      vec.add(12);
      for (var value : vec) {
        assertEquals(12, value);
      }
    }

    @Test
    public void emptyVec() {
      var vec = new UnorderedVec<>();
      for (var value : vec) {
        fail();
      }
    }

    @Test
    public void oneVec() {
      var vec = new UnorderedVec<>();
      vec.add("one");
      for (var value : vec) {
        assertEquals("one", value);
      }
    }

    @Test
    public void vecOfIntegerNoOrder() {
      var vec = new UnorderedVec<Integer>();
      vec.add(1);
      vec.add(2);
      vec.add(3);
      var list = new ArrayList<Integer>();
      for (var value : vec) {
        list.add(value);
      }

      assertEquals(List.of(2, 3, 1), list);
    }

    @Test
    public void vecOfInteger8NoOrder() {
      var vec = new UnorderedVec<Integer>();
      IntStream.range(0, 8).forEach(vec::add);
      var list = new ArrayList<Integer>();
      for (var value : vec) {
        list.add(value);
      }

      assertEquals(List.of(3, 4, 5, 6, 7, 0, 1, 2), list);
    }

    @Test
    public void vecOfInteger10NoOrder() {
      var vec = new UnorderedVec<Integer>();
      IntStream.range(0, 10).forEach(vec::add);
      var list = new ArrayList<Integer>();
      for (var value : vec) {
        list.add(value);
      }

      assertEquals(List.of(5, 6, 7, 8, 9, 0, 1, 2, 3, 4), list);
    }

    @Test
    public void vecOfStringNoOrder() {
      var vec = new UnorderedVec<String>();
      vec.add("foo");
      vec.add("bar");
      vec.add("baz");
      vec.add("whizz");
      var list = new ArrayList<String>();
      for (var value : vec) {
        list.add(value);
      }

      assertEquals(List.of("whizz", "foo", "bar", "baz"), list);
    }

    @Test
    public void testConsistencyBetweenIterations() {
      var vec = new UnorderedVec<String>();
      for (var i = 0; i < 16; i++) {
        vec.add("Element" + i);
      }

      var list1 = new ArrayList<String>();
      var it1 = vec.iterator();
      while (it1.hasNext()) {
        list1.add(it1.next());
      }

      var list2 = new ArrayList<String>();
      var it2 = vec.iterator();
      while (it2.hasNext()) {
        list2.add(it2.next());
      }

      assertEquals(list1, list2);
    }

    @Test
    public void testMultipleElementsIterator() {
      var vec = new UnorderedVec<String>();
      vec.add("A");
      vec.add("B");
      vec.add("C");

      // Track elements found during iteration
      var foundA = false;
      var foundB = false;
      var foundC = false;

      var it = vec.iterator();
      int count = 0;

      while (it.hasNext()) {
        var value = it.next();
        count++;

        if ("A".equals(value)) { foundA = true; }
        else if ("B".equals(value)) { foundB = true; }
        else if ("C".equals(value)) { foundC = true; }
      }

      // Should find all elements exactly once
      assertEquals(3, count);
      assertTrue(foundA && foundB && foundC);
    }

    @Test
    public void testSeveralValuesIterator() {
      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
        var vec = new UnorderedVec<Integer>();
        for (var i = 1; i <= 16; i++) {
          vec.add(i);

          var iterator = vec.iterator();
          var sum = 0;
          for (; iterator.hasNext(); ) {
            sum += iterator.next();
          }
          assertEquals(i * (i + 1) / 2, sum);
        }
      });
    }

    @Test
    public void testSeveralValuesIteratorNoHasNext() {
      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
        var vec = new UnorderedVec<Integer>();
        for (var i = 1; i <= 16; i++) {
          vec.add(i);

          var iterator = vec.iterator();
          var sum = 0;
          for (; ; ) {
            try {
              sum += iterator.next();
            } catch (NoSuchElementException e) {
              break;
            }
          }
          assertEquals(i * (i + 1) / 2, sum);
        }
      });
    }

    @Test
    public void qualityOfImplementation() throws IllegalAccessException {
      var field = Arrays.stream(UnorderedVec.class.getDeclaredFields())
          .filter(f -> f.getType().isArray())
          .findFirst().orElseThrow();
      field.setAccessible(true);
      var vec = new UnorderedVec<>();
      assertEquals(16, Array.getLength(field.get(vec)));
    }
  }


  @Nested
  public class Q3 {
//    @Test
//    public void vecVeryBiiiiiig() {
//      var maxMemory = Runtime.getRuntime().maxMemory();
//      assertTrue(maxMemory >= 16 * 1024 * 1014 * 1024L, "use -Xmx16G");
//
//      var vec = new UnorderedVec<Integer>();
//      IntStream.range(0, Integer.MAX_VALUE - 16).forEach(i -> vec.add(i % 128));
//
//      var sum = 0L;
//      for(var value : vec) {
//        sum += value;
//      }
//      System.out.println(sum);
//      assertEquals(136_365_209_625L, sum);
//    }

    @Test
    public void vecOfALotOfInteger1_000() {
      var vec = new UnorderedVec<Integer>();
      IntStream.range(0, 1_000).forEach(vec::add);
      var counter = 0;
      for (var value : vec) {
        assertEquals((843 + counter++) % 1_000, value);
      }
    }

    @Test
    public void vecOfALotOfInteger10_000() {
      var vec = new UnorderedVec<Integer>();
      IntStream.range(0, 10_000).forEach(vec::add);
      var counter = 0;
      for (var value : vec) {
        assertEquals((6443 + counter++) % 10_000, value);
      }
    }

    @Test
    public void vecOfALotOfInteger100_000() {
      var vec = new UnorderedVec<Integer>();
      IntStream.range(0, 100_000).forEach(vec::add);
      var counter = 0;
      for (var value : vec) {
        assertEquals((64331 + counter++) % 100_000, value);
      }
    }

    @Test
    public void vecOfALotOfInteger1_000_000() {
      var vec = new UnorderedVec<Integer>();
      IntStream.range(0, 1_000_000).forEach(vec::add);
      var counter = 0;
      for (var value : vec) {
        assertEquals((708619 + counter++) % 1_000_000, value);
      }
    }
  }


  @Nested
  public class Q4 {

    @Test
    public void removeExistingString() {
      var vec = new UnorderedVec<String>();
      vec.add("A");
      vec.add("B");
      vec.add("C");

      assertTrue(vec.remove("B"));
      assertEquals(2, vec.size());
    }

    @Test
    public void removeExistingInteger() {
      var vec = new UnorderedVec<Integer>();
      vec.add(16);
      vec.add(1024);
      vec.add(2048);

      assertTrue(vec.remove(16));
      assertTrue(vec.remove(1024));
      assertEquals(1, vec.size());
    }

    @Test
    public void removeNonExistingString() {
      var vec = new UnorderedVec<String>();
      vec.add("A");
      vec.add("B");
      vec.add("C");

      assertFalse(vec.remove("D"));
      assertEquals(3, vec.size());
    }

    @Test
    public void removeNonExistingElement() {
      var vec = new UnorderedVec<String>();
      vec.add("A");
      vec.add("B");

      assertFalse(vec.remove(333));
      assertEquals(2, vec.size());
    }

    @Test
    public void removeExistingStringIsNotPresent() {
      var vec = new UnorderedVec<String>();
      vec.add("A");
      vec.add("B");
      vec.add("C");

      vec.remove("C");

      var list = new ArrayList<String>();
      for(var value : vec) {
        list.add(value);
      }
      assertFalse(list.contains("C"));
    }

    @Test
    public void removeExistingIntegerIsNotPresent() {
      var vec = new UnorderedVec<Integer>();
      vec.add(512);
      vec.add(12);

      vec.remove(512);

      var list = new ArrayList<Integer>();
      for(var value : vec) {
        list.add(value);
      }
      assertFalse(list.contains(512));
    }

    @Test
    public void removeFirstWhenFull() {
      var vec = new UnorderedVec<Integer>();
      for(var i = 0; i < 16; i++) {
        vec.add(i);
      }

      vec.remove(0);

      var list = new ArrayList<Integer>();
      for(var value : vec) {
        list.add(value);
      }

      assertEquals(15, list.size());
      assertFalse(list.contains(0));
    }

    @Test
    public void removeLastWhenFull() {
      var vec = new UnorderedVec<Integer>();
      for(var i = 0; i < 16; i++) {
        vec.add(i);
      }

      vec.remove(15);

      var list = new ArrayList<Integer>();
      for(var value : vec) {
        list.add(value);
      }
      System.out.println(list);
      assertEquals(15, list.size());
      assertFalse(list.contains(15));
    }

    @Test
    public void removeFromEmpty() {
      var vec = new UnorderedVec<String>();
      assertFalse(vec.remove("A"));
      assertEquals(0, vec.size());
    }

    @Test
    public void removeFirstElementCanBeGCed() {
      record Person(String name) {}
      var john = new Person("John");
      var ref = new WeakReference<>(john);
      var vec = new UnorderedVec<Person>();
      vec.add(john);

      assertTrue(vec.remove(john));

      john = null;
      System.gc();
      assertTrue(ref.refersTo(null));
    }

    @Test
    public void removeSeveralElementsCanBeGCed() {
      record Person(String name) {}
      var john = new Person("John");
      var helena = new Person("Helena");
      var refJohn = new WeakReference<>(john);
      var refHelena = new WeakReference<>(helena);
      var vec = new UnorderedVec<Person>();
      vec.add(new Person("Jane"));
      vec.add(new Person("Anita"));
      vec.add(john);
      vec.add(helena);

      assertTrue(vec.remove(john));
      assertTrue(vec.remove(helena));

      john = null;
      helena = null;
      System.gc();
      assertTrue(refJohn.refersTo(null));
      assertTrue(refHelena.refersTo(null));
    }

    @Test
    public void removeLastString() {
      var vec = new UnorderedVec<String>();
      vec.add("A");

      assertTrue(vec.remove("A"));
      assertEquals(0, vec.size());
    }

    @Test
    public void removeAllString() {
      var vec = new UnorderedVec<String>();
      vec.add("A");
      vec.add("B");
      vec.add("C");

      assertTrue(vec.remove("A"));
      assertTrue(vec.remove("B"));
      assertTrue(vec.remove("C"));

      assertEquals(0, vec.size());
    }

    @Test
    public void removeAllFastInReverseOrder() {
      var vec = new UnorderedVec<Integer>();
      IntStream.range(0, 1_000_000).forEach(vec::add);

      assertTimeoutPreemptively(Duration.ofMillis(1_000), () -> {
        assertTrue(vec.remove(0));
        for(var i = 1_000_000; --i > 0;) {
          assertTrue(vec.remove(i));
        }
      });
      assertEquals(0, vec.size());
    }

    @Test
    public void removePrecondition() {
      var vec = new UnorderedVec<>();
      assertThrows(NullPointerException.class, () -> vec.remove(null));
    }
  }

  /*
  @Nested
  public class Q5 {

    @Test
    public void displayMultipleElements() {
      var vec = new UnorderedVec<String>();
      vec.add("Hello");
      vec.add("World");
      vec.add("!");
      assertEquals("<World, !, Hello>", "" + vec);
    }

    @Test
    public void displayEmpty() {
      var vec = new UnorderedVec<>();
      assertEquals("<>", "" + vec);
    }

    @Test
    public void displaySingleElement() {
      UnorderedVec<String> vec = new UnorderedVec<>();
      vec.add("Hello");
      assertEquals("<Hello>", "" + vec);
    }

    @Test
    public void displayWithIntegers() {
      var vec = new UnorderedVec<Integer>();
      vec.add(1);
      vec.add(2);
      vec.add(3);
      assertEquals("<2, 3, 1>", "" + vec);
    }

    @Test
    public void displayWithMixedTypes() {
      var vec = new UnorderedVec<>();
      vec.add(1);
      vec.add("Two");
      vec.add(3.0);
      vec.add(false);
      assertEquals("<false, 1, Two, 3.0>", "" + vec);
    }
  }

  @Nested
  public class Q6 {

    @Test
    public void equalsSameStrings() {
      var vec1 = new UnorderedVec<String>();
      vec1.add("Hello");
      vec1.add("World");
      var vec2 = new UnorderedVec<String>();
      vec2.add("Hello");
      vec2.add("World");

      assertTrue(vec1.equals(vec2));
    }

    @Test
    public void equalsSameIntegers() {
      var vec1 = new UnorderedVec<Integer>();
      vec1.add(1);
      vec1.add(1024);
      var vec2 = new UnorderedVec<Integer>();
      vec2.add(1);
      vec2.add(1024);

      assertTrue(vec1.equals(vec2));
    }

    @Test
    public void equalsSameInstance() {
      var vec = new UnorderedVec<String>();
      vec.add("Hello");
      assertTrue(vec.equals(vec));
    }

    @Test
    public void equalsNull() {
      var vec = new UnorderedVec<String>();
      vec.add("Hello");
      assertFalse(vec.equals(null));
    }

    @Test
    public void equalsDifferentClass() {
      var vec = new UnorderedVec<>();
      vec.add("Hello");
      assertFalse(vec.equals("Hello"));
    }

    @Test
    public void equalsDifferentClass2() {
      var vec = new UnorderedVec<>();
      vec.add("Hello");
      assertFalse(vec.equals(List.of("Hello")));
    }

    @Test
    public void equalsEmpty() {
      var vec1 = new UnorderedVec<>();
      var vec2 = new UnorderedVec<>();
      assertTrue(vec1.equals(vec2));
    }

    @Test
    public void equalsDifferentStrings() {
      var vec1 = new UnorderedVec<String>();
      vec1.add("Hello");
      vec1.add("World");

      var vec2 = new UnorderedVec<String>();
      vec2.add("Hello");
      vec2.add("Universe");

      assertFalse(vec1.equals(vec2));
    }

    @Test
    public void equalsDifferentSizes() {
      var vec1 = new UnorderedVec<String>();
      vec1.add("Hello");
      vec1.add("World");
      var vec2 = new UnorderedVec<String>();
      vec2.add("Hello");

      assertFalse(vec1.equals(vec2));
    }

    @Test
    public void equalsDifferentOrder() {
      var vec1 = new UnorderedVec<String>();
      vec1.add("Hello");
      vec1.add("World");
      var vec2 = new UnorderedVec<String>();
      vec2.add("World");
      vec2.add("Hello");

      assertFalse(vec1.equals(vec2));
    }

    @Test
    public void equalsWithMixedTypes() {
      var vec1 = new UnorderedVec<>();
      vec1.add(1);
      vec1.add("Two");
      var vec2 = new UnorderedVec<>();
      vec2.add(1);
      vec2.add("Two");

      assertTrue(vec1.equals(vec2));
    }

    @Test
    public void equalsWorksWithResize() {
      var vec = new UnorderedVec<Integer>();
      for(var i = 0; i < 17; i++) {
        vec.add(i);
      }
      vec.remove(16);

      var vec2 = new UnorderedVec<Integer>();
      for(var i = 0; i < 16; i++) {
        vec2.add(i);
      }

      assertEquals(vec, vec2);
    }
  }


  @Nested
  public class Q7 {

    @Test
    public void canBeUsedInCollection() {
      var vec1 = new UnorderedVec<String>();
      vec1.add("Hello");
      vec1.add("World");
      var vec2 = new UnorderedVec<String>();
      vec2.add("Hello");
      vec2.add("World");
      var vec3 = new UnorderedVec<Integer>();
      vec3.add(8008);
      vec3.add(42);
      var vec4 = new UnorderedVec<Integer>();
      vec4.add(1024);

      assertEquals(Set.of(vec1, vec3, vec4), Set.of(vec2, vec3, vec4));
    }

    @Test
    public void canBeUsedInCollection2() {
      var vec1 = new UnorderedVec<String>();
      vec1.add("Hello");
      var vec2 = new UnorderedVec<String>();
      vec2.add("Hello");
      var vec3 = new UnorderedVec<Integer>();
      vec3.add(42);
      var vec4 = new UnorderedVec<Integer>();
      vec4.add(8008);

      assertEquals(new HashSet<>() {{ addAll(List.of(vec1, vec3, vec4)); }},
          new HashSet<>() {{ addAll(List.of(vec2, vec3, vec4)); }});
    }

    @Test
    public void canBeUsedWhenResized() {
      var vec = new UnorderedVec<Integer>();
      for(var i = 0; i < 17; i++) {
        vec.add(i);
      }
      vec.remove(16);

      var vec2 = new UnorderedVec<Integer>();
      for(var i = 0; i < 16; i++) {
        vec2.add(i);
      }

      var set = new HashSet<>();
      set.add(vec);
      set.add(vec2);
      assertEquals(1, set.size());
    }
  }

  @Nested
  public class Q8 {

    @Test
    public void sizeEmpty() {
      Collection<String> vec = new UnorderedVec<String>();
      assertEquals(0, vec.size());
    }

    @Test
    public void sizeAfterAdd() {
      Collection<String> vec = new UnorderedVec<>();
      vec.add("Hello");
      assertEquals(1, vec.size());
    }

    @Test
    public void sizeAfterRemove() {
      Collection<String> vec = new UnorderedVec<>();
      vec.add("Hello");
      vec.remove("Hello");
      assertEquals(0, vec.size());
    }

    @Test
    public void isEmptyTrue() {
      Collection<String> vec = new UnorderedVec<>();
      assertTrue(vec.isEmpty());
    }

    @Test
    public void isEmptyFalse() {
      Collection<String> vec = new UnorderedVec<>();
      vec.add("Hello");
      assertFalse(vec.isEmpty());
    }

    @Test
    public void containsTrue() {
      Collection<String> vec = new UnorderedVec<>();
      vec.add("Hello");
      assertTrue(vec.contains("Hello"));
    }

    @Test
    public void containsFalse() {
      Collection<String> vec = new UnorderedVec<>();
      vec.add("Hello");
      assertFalse(vec.contains("World"));
    }

    @Test
    public void containsFalseWithAnInstanceOfAnotherClass() {
      Collection<String> vec = new UnorderedVec<>();
      vec.add("Hello");
      assertFalse(vec.contains(42));
    }


    @Test
    public void iteratorEmpty() {
      Collection<String> vec = new UnorderedVec<>();
      var iterator = vec.iterator();
      assertFalse(iterator.hasNext());
    }

    @Test
    public void iteratorNext() {
      Collection<String> vec = new UnorderedVec<>();
      vec.add("Hello");
      var iterator = vec.iterator();
      assertTrue(iterator.hasNext());
      assertEquals("Hello", iterator.next());
    }

    @Test
    public void iteratorNoSuchElementException() {
      Collection<String> vec = new UnorderedVec<>();
      var iterator = vec.iterator();
      assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    public void toArrayEmpty() {
      Collection<String> vec = new UnorderedVec<>();
      var array = vec.toArray();
      assertEquals(0, array.length);
    }

    @Test
    public void toArrayCorrectClass() {
      Collection<String> vec = new UnorderedVec<>();
      var array = vec.toArray();
      assertEquals(Object[].class, array.getClass());
    }

    @Test
    public void toArrayNonEmpty() {
      Collection<String> vec = new UnorderedVec<>();
      vec.add("Hello");
      vec.add("World");
      var array = vec.toArray();
      assertArrayEquals(new Object[]{ "World", "Hello" }, array);
    }

    @Test
    public void toArrayOfString() {
      Collection<String> vec = new UnorderedVec<>();
      vec.add("Hello");
      vec.add("World");
      var array = vec.toArray(String[]::new);
      assertArrayEquals(new String[]{ "World", "Hello" }, array);
    }

    @Test
    public void toArrayOfStringCorrectClass() {
      Collection<String> vec = new UnorderedVec<>();
      var array = vec.toArray(String[]::new);
      assertEquals(String[].class, array.getClass());
    }

    @Test
    public void toArrayOldOfString() {
      Collection<String> vec = new UnorderedVec<>();
      vec.add("Hello");
      vec.add("World");
      var array = vec.toArray(new String[0]);
      assertArrayEquals(new String[]{ "World", "Hello" }, array);
    }

    @Test
    public void toArrayOldOfStringCorrectClass() {
      Collection<String> vec = new UnorderedVec<>();
      var array = vec.toArray(new String[0]);
      assertEquals(String[].class, array.getClass());
    }

    @Test
    public void toArrayOldNonEmpty() {
      Collection<String> vec = new UnorderedVec<>();
      vec.add("Hello");
      vec.add("World");
      var array = vec.toArray(new String[] { "foo", "bar", "baz" });
      assertArrayEquals(new String[]{ "World", "Hello" , null}, array);
    }

    @Test
    public void addElement() {
      Collection<String> vec = new UnorderedVec<>();
      assertTrue(vec.add("Hello"));
      assertEquals(1, vec.size());
    }

    @Test
    public void addNull() {
      Collection<String> vec = new UnorderedVec<>();
      assertThrows(NullPointerException.class, () -> vec.add(null));
    }

    @Test
    public void removeExistingElement() {
      Collection<String> vec = new UnorderedVec<>();
      vec.add("Hello");
      assertTrue(vec.remove("Hello"));
      assertEquals(0, vec.size());
    }

    @Test
    public void removeNonExistingElement() {
      Collection<String> vec = new UnorderedVec<>();
      vec.add("Hello");
      assertFalse(vec.remove("World"));
    }

    @Test
    public void removeNonExistingAnInstanceOfAnotherClass() {
      UnorderedVec<String> vec = new UnorderedVec<>();
      vec.add("Hello");
      assertFalse(vec.remove(42));
    }

    @Test
    public void removeNull() {
      Collection<String> vec = new UnorderedVec<>();
      assertThrows(NullPointerException.class, () -> vec.remove(null));
    }

    @Test
    public void containsAllTrue() {
      UnorderedVec<String> vec = new UnorderedVec<>();
      vec.add("Hello");
      vec.add("World");
      var collection = List.of("Hello", "World");
      assertTrue(vec.containsAll(collection));
    }

    @Test
    public void containsAllFalse() {
      Collection<String> vec = new UnorderedVec<>();
      vec.add("Hello");
      var collection = List.of("Hello", "Universe");
      assertFalse(vec.containsAll(collection));
    }

    @Test
    public void addAll() {
      Collection<String> vec = new UnorderedVec<>();
      var collection = List.of("Hello", "World");
      assertTrue(vec.addAll(collection));
      assertEquals(2, vec.size());
    }

    @Test
    public void qualityOfImplementation() {
      var vec = (Object) new UnorderedVec<>();
      assertAll(
          () -> assertFalse(vec instanceof List<?>),
          () -> assertFalse(vec instanceof Set<?>),
          () -> assertFalse(vec instanceof java.util.SequencedCollection<?>),
          () -> assertFalse(vec instanceof java.util.Queue<?>),
          () -> assertFalse(vec instanceof java.util.Map<?,?>)
      );
    }
  }


  @Nested
  public class Q9 {
    @Test
    public void iteratorRemove() {
      UnorderedVec<String> vec = new UnorderedVec<>();
      vec.add("Hello");
      var iterator = vec.iterator();
      iterator.next();
      iterator.remove();

      assertEquals(0, vec.size());
    }

    @Test
    public void removeOneElementUsingIterator() {
      var vec = new UnorderedVec<String>();
      vec.add("A");
      vec.add("B");
      vec.add("C");

      var iterator = vec.iterator();
      while (iterator.hasNext()) {
        var element = iterator.next();
        if (element.equals("B")) {
          iterator.remove();
        }
      }

      assertEquals(2, vec.size());
      assertFalse(vec.contains("B"));
    }

    @Test
    public void removeManylementsUsingIterator() {
      var vec = new UnorderedVec<Integer>();
      vec.add(1);
      vec.add(3);
      vec.add(7);
      vec.add(2);

      var iterator = vec.iterator();
      while (iterator.hasNext()) {
        var element = iterator.next();
        if (element % 2 == 1) {
          iterator.remove();
        }
      }

      assertEquals(1, vec.size());
      assertEquals(2, vec.iterator().next());
    }

    @Test
    public void removeUsingIteratorWithoutNext() {
      var vec = new UnorderedVec<String>();
      vec.add("A");

      var iterator = vec.iterator();
      assertThrows(IllegalStateException.class, iterator::remove);
    }

    @Test
    public void removeUsingIteratorTwice() {
      var vec = new UnorderedVec<String>();
      vec.add("A");
      vec.add("B");

      var iterator = vec.iterator();
      iterator.next();
      iterator.remove();
      assertThrows(IllegalStateException.class, iterator::remove);
    }

    @Test
    public void removeElementCanBeGCed() {
      record Person(String name) {}
      var john = new Person("John");
      var ref = new WeakReference<>(john);
      var vec = new UnorderedVec<Person>();
      vec.add(john);

      var iterator = vec.iterator();
      iterator.next();
      iterator.remove();

      john = null;
      System.gc();
      assertTrue(ref.refersTo(null));
    }

    @Test
    public void removeUsingIteratorSeeAllElements3() {
      var vec = new UnorderedVec<String>();
      vec.add("A");
      vec.add("B");
      vec.add("C");

      var set = new LinkedHashSet<String>();
      var iterator = vec.iterator();
      while (iterator.hasNext()) {
        var value = iterator.next();
        set.add(value);
        iterator.remove();
      }

      assertEquals(0, vec.size());
      assertEquals(Set.of("A", "B", "C"), set);
    }

    @Test
    public void removeUsingIteratorSeeAllElements5() {
      var vec = new UnorderedVec<String>();
      vec.add("A");
      vec.add("B");
      vec.add("C");
      vec.add("D");
      vec.add("E");

      var set = new LinkedHashSet<String>();
      var iterator = vec.iterator();
      while (iterator.hasNext()) {
        var value = iterator.next();
        set.add(value);
        iterator.remove();
      }

      assertEquals(0, vec.size());
      assertEquals(Set.of("A", "B", "C", "D", "E"), set);
    }

    @Test
    public void removeUsingIteratorSeeAllElements11() {
      var vec = new UnorderedVec<Integer>();
      IntStream.range(0, 11).forEach(vec::add);

      var set = new LinkedHashSet<Integer>();
      var iterator = vec.iterator();
      while (iterator.hasNext()) {
        var value = iterator.next();
        set.add(value);
        iterator.remove();
      }

      assertEquals(0, vec.size());
      assertEquals(IntStream.range(0, 11).boxed().collect(toSet()), set);
    }

    @Test
    public void clear() {
      var vec = new UnorderedVec<String>();
      vec.add("Hello");
      vec.clear();

      assertEquals(0, vec.size());
    }

    @Test
    public void retainAll() {
      var vec = new UnorderedVec<>();
      vec.add("Hello");
      vec.add("World");
      var collection = List.of(("Hello"));

      assertTrue(vec.retainAll(collection));
      assertEquals(1, vec.size());
    }

    @Test
    public void removeAll() {
      var vec = new UnorderedVec<>();
      vec.add("Hello");
      vec.add("World");
      var collection = List.of("Hello");

      assertTrue(vec.removeAll(collection));
      assertEquals(1, vec.size());
    }

    @Test
    public void removeIf() {
      var vec = new UnorderedVec<String>();
      vec.add("A");
      vec.add("B");
      vec.add("C");

      vec.removeIf("B"::equals);

      assertEquals(2, vec.size());
      assertFalse(vec.contains("B"));
    }

    @Test
    public void testRemoveTwiceAfterSingleNext() {
      var vec = new UnorderedVec<String>();
      vec.add("A");
      vec.add("B");

      var it = vec.iterator();
      it.next();
      it.remove(); // This should work

      assertThrows(IllegalStateException.class, it::remove);
    }

    @Test
    public void testSizeAfterRemoveAtTheEnd() {
      var vec = new UnorderedVec<String>();
      vec.add("A");
      vec.add("B");
      vec.add("C");

      assertEquals(3, vec.size());

      var it = vec.iterator();
      it.next();
      it.next();
      it.next();
      it.remove();

      assertEquals(2, vec.size());
    }

    @Test
    public void testRemoveMiddleElement() {
      var vec = new UnorderedVec<String>();
      vec.add("A");
      vec.add("B");
      vec.add("C");

      var it = vec.iterator();
      assertEquals("B", it.next());
      it.remove();

      var foundA = false;
      var foundC = false;
      while (it.hasNext()) {
        var value = it.next();
        if ("A".equals(value)) { foundA = true; }
        else if ("C".equals(value)) { foundC = true; }
      }
      assertTrue(foundA && foundC);
    }

    @Test
    public void testRemoveAllElements() {
      var vec = new UnorderedVec<Integer>();
      for(var i = 0; i <10; i++) {
        vec.add(i);
      }

      var it = vec.iterator();
      for(var i = 0; i <10; i++) {
        it.next();
        it.remove();
      }

      assertEquals(0, vec.size());
      assertFalse(it.hasNext());
    }

    @Test
    public void testRemoveAndIterateAgain() {
      var vec = new UnorderedVec<String>();
      vec.add("A");
      vec.add("B");
      vec.add("C");

      var it = vec.iterator();
      it.next();
      it.remove();

      var remainingCount = 0;
      while (it.hasNext()) {
        it.next();
        remainingCount++;
      }
      assertEquals(2, remainingCount);
    }
  }
 */
}