package net.sf.l2j.util;

import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.commons.logging.CLogger;

public class CustomMessage
{
	
	public static final CLogger LOGGER = new CLogger(CustomMessage.class.getName());
	private String address;
	private List<Object> params = new ArrayList<>();
	
	
	public CustomMessage(String address)
	{
		this.address = address;
	}
	
	public CustomMessage(String address, Object... params)
	{
		this.address = address;
		add(params);
	}
	
	public CustomMessage add(Object param)
	{
		params.add(param);
		return this;
	}
	
	public CustomMessage add(Object... params)
	{
		if (params != null)
		{
			for (Object object : params)
				add(object);
		}
		
		return this;
	}
	
	@Override
	public String toString()
	{
		return address;
	}
	
	public String toString(String lang)
	{
		String text = LocalizationStorage.getInstance().getString(lang, address);
		
		if (text != null && params != null && params.size() > 0)
			text = String.format(text, params.toArray());
		
		return text;
	}
}