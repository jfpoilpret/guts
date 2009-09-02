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

package net.guts.common.injection;

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
		OneTypeListener<Number> listener = 
			new OneTypeListener<Number>(Number.class, null);
		assertThat(listener.typeMatches(TypeLiteral.get(Number.class)))
			.as("typeMatches(Number.class)").isTrue();
	}

	public void checkCompatibleNonGenericTypesMatch()
	{
		OneTypeListener<Number> listener = 
			new OneTypeListener<Number>(Number.class, null);
		assertThat(listener.typeMatches(TypeLiteral.get(Integer.class)))
			.as("typeMatches(Integer.class)").isTrue();
	}

	public void checkNonCompatibleNonGenericTypesDontMatch()
	{
		OneTypeListener<Number> listener = 
			new OneTypeListener<Number>(Number.class, null);
		assertThat(listener.typeMatches(TypeLiteral.get(Object.class)))
			.as("typeMatches(Object.class)").isFalse();
	}

	public void checkNonGenericTypeMatchesImplementedInterface()
	{
		OneTypeListener<Runnable> listener = 
			new OneTypeListener<Runnable>(Runnable.class, null);
		assertThat(listener.typeMatches(TypeLiteral.get(Thread.class)))
			.as("typeMatches(Thread.class)").isTrue();
	}

	public void checkIdenticalGenericTypesMatch()
	{
		OneTypeListener<List<String>> listener = 
			new OneTypeListener<List<String>>(new TypeLiteral<List<String>>(){}, null);
		assertThat(listener.typeMatches(new TypeLiteral<List<String>>(){}))
			.as("typeMatches(List<String>.class)").isTrue();
	}

	public void checkIncompatibleParametersGenericTypesDontMatch()
	{
		OneTypeListener<List<String>> listener = 
			new OneTypeListener<List<String>>(new TypeLiteral<List<String>>(){}, null);
		assertThat(listener.typeMatches(new TypeLiteral<List<Integer>>(){}))
			.as("typeMatches(List<Integer>.class)").isFalse();
	}

	public void checkIncompatibleGenericTypesDontMatch()
	{
		OneTypeListener<List<String>> listener = 
			new OneTypeListener<List<String>>(new TypeLiteral<List<String>>(){}, null);
		assertThat(listener.typeMatches(TypeLiteral.get(String.class)))
			.as("typeMatches(String.class)").isFalse();
	}

	public void checkCompatibleGenericTypesMatch()
	{
		OneTypeListener<List<String>> listener = 
			new OneTypeListener<List<String>>(new TypeLiteral<List<String>>(){}, null);
		assertThat(listener.typeMatches(new TypeLiteral<ArrayList<String>>(){}))
			.as("typeMatches(ArrayList<String>.class)").isTrue();
	}

}
