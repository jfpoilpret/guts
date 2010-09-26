package bench.idea_01;

import java.lang.reflect.Method;
import java.util.Arrays;

import com.google.inject.internal.asm.Type;
import com.google.inject.internal.cglib.core.Signature;
import com.google.inject.internal.cglib.proxy.Enhancer;

public class AopUtil {

	static Signature getSignature(final Method method) {

		final String name = method.getName();

		final Type returnType = getReturnType(method);

		final Type[] argumentTypes = getArgumentTypes(method);

		final Signature signature = new Signature(name, returnType,
				argumentTypes);

		return signature;

	}

	static Type getReturnType(final Method method) {

		final Type returnType = Type.getType(method.getReturnType());

		return returnType;

	}

	static Type[] getArgumentTypes(final Method method) {

		final Class<?>[] paramTypeArray = method.getParameterTypes();

		final int paramCount = paramTypeArray.length;

		final Type[] argumentTypes = new Type[paramCount];

		for (int k = 0; k < paramCount; k++) {
			argumentTypes[k] = Type.getType(paramTypeArray[k]);
		}

		return argumentTypes;

	}

	static Method getBoundMethod(final Class<?> proxyKlaz, final BeanBindKey key) {

		assert Enhancer.isEnhanced(proxyKlaz);

		final Class<?> superKlaz = proxyKlaz.getSuperclass();

		for (final Method method : superKlaz.getDeclaredMethods()) {

			final BeanBind annotation = method.getAnnotation(BeanBind.class);

			if (annotation == null) {
				continue;
			}

			if (annotation.value() == key) {
				return method;
			}

		}

		return null;

	}

	static boolean isSameArgsTypes(final Method methodOne,
			final Method methodTwo) {

		final Type[] argsTypesOne = getArgumentTypes(methodOne);

		final Type[] argsTypesTwo = getArgumentTypes(methodTwo);

		return Arrays.equals(argsTypesOne, argsTypesTwo);

	}

	static boolean isEnhanced(final Class<?> klaz) {
		return Enhancer.isEnhanced(klaz);
	}

}
