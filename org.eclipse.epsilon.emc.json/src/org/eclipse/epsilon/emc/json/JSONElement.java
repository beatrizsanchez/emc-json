package org.eclipse.epsilon.emc.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.epsilon.emc.plainxml.PlainXmlType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONElement {

	protected JSONElement parent;
	protected boolean array;
	protected boolean root;
	private String tag;
	private Object value;
	private String id;

	public JSONElement(JSONElement parent, String tag, Object value, String id) {
		if (parent == null){
			root=true;
			this.parent = null;
		} else {
			this.parent = parent;
		}
		if (value instanceof JSONObject){
			this.value = (JSONObject) value;
			this.array = false;
		} else if (value instanceof JSONArray){
			this.value = (JSONArray) value;
			this.array = true;
		}
		if (tag == null || tag.isEmpty()){
			setTag();
		} else {
			this.tag = PlainXmlType.parse("t_" +tag).getTagName();
		}	
		if (id == null || id.isEmpty()){
			this.id = this.tag;
		} else {
			this.id = id;
		}
	}
	public String getId() {
		return id;
	}
	public JSONElement(JSONElement parent, String tag, Object value) {
		this(parent, tag, value, null);
	}	
	public JSONElement(JSONElement parent, Object value) {
		this(parent, null, value);
	}

	public JSONElement(Object value) {
		this(null, null, value);
	}

	public JSONElement getParent() {
		return parent;
	}

	public void setParent(JSONElement parent) {
		this.parent = parent;
	}

	public String getTag() {
		return tag;
	}

	public void setTag() {
		if (parent != null){
			if (parent.isArray()){
				this.tag = parent.getParent().getTag();
			} else {
				JSONObject parentObject = (JSONObject) parent.getValue();
				Iterator<?> iterator = parentObject.keySet().iterator();
				while (iterator.hasNext()){
					Object key = iterator.next();
					Object element = parentObject.get(key);
					if (this.isArray() && element instanceof JSONArray){
						if (((JSONArray) element).equals((JSONArray) this.value)){
							this.tag = PlainXmlType.parse("t_"+String.valueOf(key)).getTagName();
						}
					} else if (!this.isArray() && element instanceof JSONObject){
						if (((JSONObject) element).equals((JSONObject) this.value)){
							this.tag = PlainXmlType.parse("t_"+String.valueOf(key)).getTagName();
						}
					}
				}
			}

		} else {
			this.tag = PlainXmlType.parse("t_root").getTagName();
		}
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public boolean isArray() {
		return array;
	}

	public void setArray(boolean array) {
		this.array = array;
	}

	public boolean isRoot() {
		return root;
	}

	public void setRoot(boolean root) {
		this.root = root;
	}

	public Object getProperty(String key){
		if (!isArray() && ((JSONObject) value).containsKey(key)){
			return ((JSONObject) value).get(key);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Collection<JSONElement> getProperties(){
		if (!isArray()){
			HashMap<String, JSONElement> result = new HashMap<String, JSONElement>();
			JSONObject jsonObject = (JSONObject) value;
			Iterator<String> iterator = jsonObject.keySet().iterator();			
			while (iterator.hasNext()){
				String key = iterator.next();
				Object child = ((JSONObject) value).get(key);
				result.put(key, new JSONElement(this,child));
			}
			return result.values();
		}
		return null;
	}

	public List<JSONElement> getChildren(){
		List<JSONElement> result = new ArrayList<JSONElement>();
		if (isArray()){
			Iterator<?> iterator = ((JSONArray) this.getValue()).iterator();
			while (iterator.hasNext()){
				JSONObject object = (JSONObject) iterator.next();
				result.add(new JSONElement(this, this.tag, object));
			}
		} else {
			JSONObject element = (JSONObject) this.getValue();
			Iterator<?> iterator = element.keySet().iterator();
			while (iterator.hasNext()){
				String key = (String) iterator.next();
				//result.add(new JSONElement(this, this.tag+":"+key, element.get(key)));
				result.add(new JSONElement(this, this.tag, element.get(key), key));
				}
		}
		return result;
	}

	public static List<JSONElement> cast(Object array){
		if (array instanceof JSONArray){
			JSONArray jsonArray = (JSONArray) array;
			Iterator<?> iterator = jsonArray.iterator();
			List<JSONElement> result = new ArrayList<JSONElement>();
			while (iterator.hasNext()){
				Object element = iterator.next();
				result.add(new JSONElement(element));
			}
			return result;
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public String toString() {
		return "JSONElement [parent=" + parent + ", array=" + array + ", root=" + root + ", tag=" + tag + "]";
	}

}