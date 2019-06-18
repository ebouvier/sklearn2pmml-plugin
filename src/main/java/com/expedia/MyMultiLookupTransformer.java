package com.expedia;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.DataType;
import org.dmg.pmml.FieldName;
import org.jpmml.converter.Feature;
import org.jpmml.converter.XMLUtil;
import org.jpmml.sklearn.SkLearnEncoder;

public class MyMultiLookupTransformer extends MyLookupTransformer {

	public MyMultiLookupTransformer(String module, String name){
		super(module, name);
	}

	@Override
	public DataType getDataType(){
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Feature> encodeFeatures(List<Feature> features, SkLearnEncoder encoder){
		return super.encodeFeatures(features, encoder);
	}

	@Override
	protected List<String> formatColumns(List<Feature> features){
		List<String> result = new ArrayList<>();

		for(Feature feature : features){
			FieldName name = feature.getName();

			result.add("data:" + XMLUtil.createTagName(name.getValue()));
		}

		if(result.contains("data:output")){
			throw new IllegalArgumentException();
		}

		result.add("data:output");

		return result;
	}

	@Override
	protected Map<String, List<Object>> parseMapping(List<String> inputColumns, String outputColumn, Map<?, ?> mapping){
		Map<String, List<Object>> result = new LinkedHashMap<>();

		for(String inputColumn : inputColumns){
			result.put(inputColumn, new ArrayList<>());
		}

		result.put(outputColumn, new ArrayList<>());

		Collection<? extends Map.Entry<?, ?>> entries = mapping.entrySet();
		for(Map.Entry<?, ?> entry : entries){
			Object[] inputValue = (Object[])entry.getKey();
			Object outputValue = entry.getValue();

			if(inputValue == null || inputValue.length != inputColumns.size()){
				throw new IllegalArgumentException();
			} // End if

			if(outputValue == null){
				continue;
			}

			for(int i = 0; i < inputColumns.size(); i++){
				List<Object> inputValues = result.get(inputColumns.get(i));

				inputValues.add(inputValue[i]);
			}

			List<Object> outputValues = result.get(outputColumn);

			outputValues.add(outputValue);
		}

		return result;
	}
}
