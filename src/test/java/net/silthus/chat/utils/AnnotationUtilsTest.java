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

package net.silthus.chat.utils;

import net.silthus.chat.annotations.Name;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AnnotationUtilsTest {

    @Test
    void name_withAnnotation_returnsName() {
        assertThat(AnnotationUtils.name(TestClass.class)).isEqualTo("foo");
    }

    @Test
    void clazz_withoutNameAnnotation_returnsClazzName() {
        assertThat(AnnotationUtils.name(SecondTest.class)).isEqualTo("second-test");
    }

    @Test
    void clazz_withSuperclass_getsStripedOfName() {
        assertThat(AnnotationUtils.name(FooBar.class)).isEqualTo("foo");
    }

    @Name("foo")
    public static class TestClass {

    }

    public static class SecondTest {

    }

    public static class FooBar implements Bar {

    }

    public interface Bar {

    }
}