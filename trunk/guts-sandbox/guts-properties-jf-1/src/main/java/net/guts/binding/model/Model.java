package net.guts.binding.model;

import net.guts.properties.Bean;
import net.guts.properties.Property;

// DSL:
//ValueModel<T> model = model(myBean, forBean(MyBean.class).getXxxx());
// or better:
//import static net.guts.binding.model.Model.of;
//import static net.guts.binding.model.Model.model;
//ValueModel<T> model = model(of(myBean).getXxxx());
public final class Model
{	
	private Model()
	{
	}

	//TODO: put Bean<B> into a ThreadLocal for use in model()?
	//TODO: directly pass the actual bean for which we want a ValueModel later
	static public <B> B of(B bean)
	{
		Bean<B> beanHelper = Bean.create(classOf(bean));
		Context<B> context = new Context<B>();
		context._bean = bean;
		context._beanHelper = beanHelper;
		_context.set(context);
		return beanHelper.mock();
	}

	//TODO avoid passing bean here
	static public <B, T> ValueModel<T> model(T property)
	{
		Context<B> context = (Context<B>) _context.get();
		_context.remove();
		if (context == null)
		{
			//TODO
		}
		//TODO also check that context is in phase with property?
		// eg context._beanHelper.type() == context._bean.getClass()
		Bean<B> beanHelper = context._beanHelper;
		Property<B, T> beanProperty = beanHelper.property(property);
		return new BeanPropertyValueModel<B, T>(context._bean, beanProperty);
	}

	@SuppressWarnings("unchecked")
	static private <B> Class<B> classOf(B bean)
	{
		return (Class<B>) bean.getClass();
	}

	static private class Context<B>
	{
		Bean<B> _beanHelper;
		B _bean;
	}
	
	static final private ThreadLocal<Context<?>> _context = new ThreadLocal<Context<?>>();
}
