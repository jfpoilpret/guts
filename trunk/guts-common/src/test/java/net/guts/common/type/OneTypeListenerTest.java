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

package net.guts.common.type;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

import com.google.inject.TypeLiteral;

@Test(groups = "utest")
public class OneTypeListenerTest
{
	public void checkIdenticalNonGenericTypesMatch()
	{
		boolean result = 
			TypeHelper.typeIsSubtypeOf(type(Number.class), type(Number.class));
		assertThat(result).as("typeIsSubtypeOf(Number, Number)").isTrue();
	}

	public void checkCompatibleNonGenericTypesMatch()
	{
		boolean result = 
			TypeHelper.typeIsSubtypeOf(type(Integer.class), type(Number.class));
		assertThat(result).as("typeIsSubtypeOf(Integer, Number)").isTrue();
	}

	public void checkNonCompatibleNonGenericTypesDontMatch()
	{
		boolean result = 
			TypeHelper.typeIsSubtypeOf(type(Object.class), type(Number.class));
		assertThat(result).as("typeIsSubtypeOf(Object, Number)").isFalse();
	}

	public void checkNonGenericTypeMatchesImplementedInterface()
	{
		boolean result = 
			TypeHelper.typeIsSubtypeOf(type(Thread.class), type(Runnable.class));
		assertThat(result).as("typeIsSubtypeOf(Thread, Runnable)").isTrue();
	}

	public void checkIdenticalGenericTypesMatch()
	{
		boolean result = TypeHelper.typeIsSubtypeOf(
			new TypeLiteral<List<String>>(){}, new TypeLiteral<List<String>>(){});
		assertThat(result).as("typeIsSubtypeOf(List<String>, List<String>)").isTrue();
	}

	public void checkIncompatibleParametersGenericTypesDontMatch()
	{
		boolean result = TypeHelper.typeIsSubtypeOf(
			new TypeLiteral<List<String>>(){}, new TypeLiteral<List<Integer>>(){});
		assertThat(result).as("typeIsSubtypeOf(List<String>, List<Integer>)").isFalse();
	}

	public void checkIncompatibleGenericTypesDontMatch()
	{
		boolean result = TypeHelper.typeIsSubtypeOf(
			new TypeLiteral<List<String>>(){}, type(String.class));
		assertThat(result).as("typeIsSubtypeOf(List<String>, String)").isFalse();
	}

	public void checkCompatibleGenericTypesMatch()
	{
		boolean result = TypeHelper.typeIsSubtypeOf(
			new TypeLiteral<ArrayList<String>>(){}, new TypeLiteral<List<String>>(){});
		assertThat(result).as("typeIsSubtypeOf(ArrayList<String>, List<String>)").isTrue();
	}

	//TODO test raw type: List<String> assignable to List<?>?
	@Test(enabled = false)
	public void checkCompatibleGenericAndRawTypesMatch()
	{
		List<?> list = new ArrayList<String>();
		boolean result = TypeHelper.typeIsSubtypeOf(
			new TypeLiteral<List<String>>(){}, new TypeLiteral<List<?>>(){});
		assertThat(result).as("typeIsSubtypeOf(List<String>, List<?>)").isTrue();
	}
	
	private TypeLiteral<?> type(Class<?> type)
	{
		return TypeLiteral.get(type);
	}
}
