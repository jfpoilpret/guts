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

package net.guts.event;

/**
 * Called when a consumer method returns a non {@code null} result, this
 * interface must be implemented for every return type of consumer methods that
 * you want specifically handled.
 * <p/>
 * Every implementation must be registered with GUTS-Events as follows:
 * <pre>
 * Injector injector = Guice.createInjector(new EventModule(), new AbstractModule()
 * {
 *     &#64;Override protected void configure()
 *     {
 *         Events.bindHandler(binder(), new TypeLiteral&lt;SwingWorker&lt;?, ?&gt;&gt;(){})
 *             toInstance(new SwingWorkerHandler());
 *     }
 * });
 * </pre>
 * With this feature, you can, for example, handle {@code SwingWorker} instances
 * returned by a consumer method because it has to perform a lengthy operation
 * and report its results to a Swing component. Here is how you can implement
 * this capability:
 * <pre>
 * public class SwingWorkerHandler 
 *     implements ConsumerReturnHandler&lt;SwingWorker&lt;?, ?&gt;&gt;
 * {
 *     &#64;Override public void handle(SwingWorker&lt;?, ?&gt; result)
 *     {
 *         result.execute();
 *     }
 * }
 * </pre>
 * <p/>
 * Note that {@link #handle} and the related consumer method are called from the 
 * same {@code Thread}, as determined by an optional "Thread Policy" annotation
 * on the consumer method.
 * <p/>
 * <b>Important!</b> Types bound with {@link Events#bindHandler} must perfectly 
 * match with declared return types of consumer methods for the bound 
 * {@link ConsumerReturnHandler#handle} to be called with the result returned by
 * the consumer method.
 *
 * @param <T> type of result (returned by consumer methods) handled by {@code this}
 * implementation
 * 
 * @author Jean-Francois Poilpret
 */
public interface ConsumerReturnHandler<T>
{
	/**
	 * Called by GUTS-Events after each consumer method returns a result of type
	 * {@code T}, this method can further process this result in any suitable way.
	 * 
	 * @param result the result returned by a consumer method
	 */
	public void handle(T result);
}
