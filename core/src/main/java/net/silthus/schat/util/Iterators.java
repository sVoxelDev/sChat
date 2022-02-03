/*
 * sChat, a Supercharged Minecraft Chat Plugin
 * Copyright (C) Silthus <https://www.github.com/silthus>
 * Copyright (C) sChat team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.silthus.schat.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Iterators {
    private Iterators() {
    }

    public static <E> boolean tryIterate(Iterable<E> iterable, Throwing.Consumer<E> action) {
        boolean success = true;
        for (E element : iterable) {
            try {
                action.accept(element);
            } catch (Exception e) {
                e.printStackTrace();
                success = false;
            }
        }
        return success;
    }

    public static <I, O> boolean tryIterate(Iterable<I> iterable, Function<I, O> mapping, Consumer<O> action) {
        boolean success = true;
        for (I element : iterable) {
            try {
                action.accept(mapping.apply(element));
            } catch (Exception e) {
                e.printStackTrace();
                success = false;
            }
        }
        return success;
    }

    public static <E> boolean tryIterate(E[] array, Consumer<E> action) {
        boolean success = true;
        for (E element : array) {
            try {
                action.accept(element);
            } catch (Exception e) {
                e.printStackTrace();
                success = false;
            }
        }
        return success;
    }

    public static <I, O> boolean tryIterate(I[] array, Function<I, O> mapping, Consumer<O> action) {
        boolean success = true;
        for (I element : array) {
            try {
                action.accept(mapping.apply(element));
            } catch (Exception e) {
                e.printStackTrace();
                success = false;
            }
        }
        return success;
    }

    public static <E> List<List<E>> divideIterable(Iterable<E> source, int size) {
        List<List<E>> lists = new ArrayList<>();
        Iterator<E> it = source.iterator();
        while (it.hasNext()) {
            List<E> subList = new ArrayList<>();
            for (int i = 0; it.hasNext() && i < size; i++) {
                subList.add(it.next());
            }
            lists.add(subList);
        }
        return lists;
    }

}
