package org.eclipse.epsilon.emc.json;

import org.eclipse.epsilon.emc.plainxml.PlainXmlProperty;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.introspection.java.JavaPropertyGetter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JsonPropertyGetter extends JavaPropertyGetter {

	protected JsonModel model;

	@Override
	public Object invoke(Object object, String property)
			throws EolRuntimeException {

		if(object instanceof JSONElement 
				&& !((JSONElement) object).isArray()) 
		{

			final JSONElement e = (JSONElement) object;
			final JSONObject jsonObject = (JSONObject) e.getValue();

			if ("children".equals(property)) {
				return e.getChildren();
			}

			if ("text".equals(property)) {
				return jsonObject.toJSONString();
			}

			if ("parent".equals(property)) {
				return e.parent;
			}			

			PlainXmlProperty p = PlainXmlProperty.parse(property);

			if (p != null) {
				Object prop = jsonObject.get(p.getProperty());
				if (p.isAttribute()) {
					return p.cast(String.valueOf(prop));
				}
				else if (p.isElement()) {
					return new JSONElement(prop);
				}

				else if (p.isMany()) {
					if (prop == null) {
						return new JSONArray();
					} else {
						return (prop);
					}
				}
			}	
		} 
		return super.invoke(object, property);
	}
}
