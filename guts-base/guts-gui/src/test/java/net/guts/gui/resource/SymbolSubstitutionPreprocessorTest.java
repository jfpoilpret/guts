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

package net.guts.gui.resource;

import java.util.HashMap;
import java.util.Map;

import org.fest.assertions.Assertions;
import org.testng.annotations.Test;

@Test(groups = "utest")
public class SymbolSubstitutionPreprocessorTest extends SymbolSubstitutionResourcePreprocessor
{
	public void checkStringWithoutSymbol()
	{
		checkConversion("dummy", "dummy");
	}

	public void checkStringWithJustOneSymbol()
	{
		checkConversion("${sym3}", "three");
	}

	public void checkStringWithOneSymbol()
	{
		checkConversion("one-${sym2}-three", "one-two-three");
	}

	public void checkStringWithSeveralSymbols()
	{
		checkConversion("${sym1}-${sym2}-three", "one-two-three");
	}

	public void checkStringWithSameSymbolTwice()
	{
		checkConversion("${sym1}-${sym1}-three", "one-one-three");
	}

	public void checkStringWithUnexistingSymbol()
	{
		checkConversion("${XXX}-three", "${XXX}-three");
	}

	public void checkStringWithJustUnexistingSymbol()
	{
		checkConversion("${XXX}", "${XXX}");
	}

	public void checkStringWithExistingAndUnexistingSymbols()
	{
		checkConversion("${XXX}${sym1}", "${XXX}one");
	}

	public void checkStringWithPartialMatch()
	{
		checkConversion("Z}ZZZ${XXX", "Z}ZZZ${XXX");
	}
	
	@Override protected String substitute(String symbol, ResourceMap map)
	{
		return _symbols.get(symbol);
	}
	
	private void checkConversion(String source, String target)
	{
		Assertions.assertThat(convert(null, source)).as(source).isEqualTo(target);
	}
	
	static final private Map<String, String> _symbols = new HashMap<String, String>();
	static
	{
		_symbols.put("sym1", "one");
		_symbols.put("sym2", "two");
		_symbols.put("sym3", "three");
	}
}
