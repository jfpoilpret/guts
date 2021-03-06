package bench.idea_02.provider;

import java.lang.reflect.Method;
import java.util.Arrays;

import bench.idea_02.api.Bind;
import bench.idea_02.api.BindId;

import com.google.inject.internal.asm.Type;
import com.google.inject.internal.cglib.core.Signature;
import com.google.inject.internal.cglib.proxy.Enhancer;

public final class BindUtil {

	public static Signature getSignature(final Method method) {

		final String name = method.getName();

		final Type returnType = getReturnType(method);

		final Type[] argumentTypes = getArgumentTypes(method);

		final Signature signature = new Signature(name, returnType,
				argumentTypes);

		return signature;

	}

	public static Type getReturnType(final Method method) {

		final Type returnType = Type.getType(method.getReturnType());

		return returnType;

	}

	public static Type[] getArgumentTypes(final Method method) {

		final Class<?>[] paramTypeArray = method.getParameterTypes();

		final int paramCount = paramTypeArray.length;

		final Type[] argumentTypes = new Type[paramCount];

		for (int k = 0; k < paramCount; k++) {
			argumentTypes[k] = Type.getType(paramTypeArray[k]);
		}

		return argumentTypes;

	}

	public static Method getBoundMethod(final Class<?> proxyKlaz,
			final BindId key) {

		assert Enhancer.isEnhanced(proxyKlaz);

		final Class<?> superKlaz = proxyKlaz.getSuperclass();

		for (final Method method : superKlaz.getDeclaredMethods()) {

			final Bind annotation = method.getAnnotation(Bind.class);

			if (annotation == null) {
				continue;
			}

			if (annotation.value() == key) {
				return method;
			}

		}

		return null;

	}

	public static boolean isSameArgsTypes(final Method methodOne,
			final Method methodTwo) {

		final Type[] argsTypesOne = getArgumentTypes(methodOne);

		final Type[] argsTypesTwo = getArgumentTypes(methodTwo);

		return Arrays.equals(argsTypesOne, argsTypesTwo);

	}

	public static boolean isEnhanced(final Class<?> klaz) {
		return Enhancer.isEnhanced(klaz);
	}

}
