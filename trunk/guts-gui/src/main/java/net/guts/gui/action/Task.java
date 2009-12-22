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

package net.guts.gui.action;

import java.util.List;

public interface Task<T, V>
{
	public T doInBackground(TaskResultPublisher<V> publisher);
	public void process(List<V> chunks);
	public void failed(Throwable cause);
	public void succeeded(T result);
	public void finished();
	//TODO more methods here: cancelled? interrupted?
}
