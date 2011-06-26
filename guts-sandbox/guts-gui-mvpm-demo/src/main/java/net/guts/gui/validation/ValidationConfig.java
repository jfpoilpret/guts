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

package net.guts.gui.validation;

import com.jgoodies.validation.Validatable;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.util.DefaultValidationResultModel;

//CSOFF: VisibilityModifier
class ValidationConfig
{	
	ValidationResultModel _model = new DefaultValidationResultModel();
	Validatable _validator = null;
	boolean _validateAtFirstDiplay = false;
	boolean _autoFocus = false;
}
//CSON: VisibilityModifier
