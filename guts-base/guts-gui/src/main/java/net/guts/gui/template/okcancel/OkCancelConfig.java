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

package net.guts.gui.template.okcancel;

import javax.swing.Action;

import net.guts.gui.template.okcancel.OkCancel.Result;

//CSOFF: VisibilityModifier
class OkCancelConfig
{
	Action _apply = null;
	Action _cancel = null;
	boolean _hasApply = false;
	boolean _hasCancel = false;
	boolean _hasOK = false;
	boolean _dontChangeView = false;
	Result _result = null;
}
//CSON: VisibilityModifier
