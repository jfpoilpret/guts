//  Copyright 2009 Jean-Francois Poilpret
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package bench.idea_03.provider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.guts.common.type.TypeHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;

public final class SyncMatchers {

	static private final Logger log = LoggerFactory
			.getLogger(SyncMatchers.class);

	private SyncMatchers() {
	}

	static final public Matcher<TypeLiteral<?>> hasFieldsOfType(
			final Class<?>... fieldTypes) {

		return new AbstractMatcher<TypeLiteral<?>>() {

			@Override
			public boolean matches(TypeLiteral<?> type) {

				Class<?> clazz = type.getRawType();

				while (clazz != null) {
					for (Field field : clazz.getDeclaredFields()) {
						for (Class<?> fieldType : fieldTypes) {
							if (fieldType.isAssignableFrom(field.getType())) {
								return true;
							}
						}
					}
					clazz = clazz.getSuperclass();
				}

				return false;

			}

		};

	}

	static final public Matcher<TypeLiteral<?>> isSubtypeOf(
			final Class<?> supertype) {
		return isSubtypeOf(TypeLiteral.get(supertype));
	}

	static final public Matcher<TypeLiteral<?>> isSubtypeOf(
			final TypeLiteral<?> supertype) {

		return new AbstractMatcher<TypeLiteral<?>>() {

			@Override
			public boolean matches(TypeLiteral<?> type) {
				boolean match = TypeHelper.typeIsSubtypeOf(type, supertype);
				return match;
			}

		};

	}

	static final public Matcher<TypeLiteral<?>> hasMethodAnnotatedWith(
			final Class<? extends Annotation> annotation) {

		log.debug("visit : {}", annotation);

		return new AbstractMatcher<TypeLiteral<?>>() {

			@Override
			public boolean matches(TypeLiteral<?> type) {

				log.debug("visit : {}", type);

				for (Method method : type.getRawType().getMethods()) {

					if (method.isAnnotationPresent(annotation)) {

						log.debug("visit : {}", method);

						return true;
					}

				}

				return false;

			}

		};

	}

}
