package com.expedia;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.DataType;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.FieldColumnPair;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.MapValues;
import org.dmg.pmml.OpType;
import org.jpmml.converter.ContinuousFeature;
import org.jpmml.converter.Feature;
import org.jpmml.converter.FeatureUtil;
import org.jpmml.converter.PMMLEncoder;
import org.jpmml.converter.PMMLUtil;
import org.jpmml.converter.TypeUtil;
import org.jpmml.sklearn.ClassDictUtil;
import org.jpmml.sklearn.SkLearnEncoder;
import sklearn.Transformer;

public class AttributesAdder extends Transformer {

	public AttributesAdder(String module, String name){
		super(module, name);
	}

	@Override
	public OpType getOpType(){
		return OpType.CATEGORICAL;
	}

	@Override
	public DataType getDataType(){
		Map<?, ?> map_1a = getMapping();

		List<Object> inputValues = new ArrayList<>(map_1a.keySet());

		return TypeUtil.getDataType(inputValues, DataType.STRING);
	}

	@Override
	public List<Feature> encodeFeatures(List<Feature> features, SkLearnEncoder encoder){
		Map<?, ?> map_1a = getMapping();

		List<String> columns = formatColumns(features);

		ClassDictUtil.checkSize(features.size() + 1, columns);

		MapValues mapValues = new MapValues();

		List<String> inputColumns = new ArrayList<String>();
//		List<String> inputColumns = columns.subList(0, columns.size() - 1);

		for(int i = 0; i < features.size(); i++){
			Feature feature = features.get(i);
			if (feature.getName().equals("tpid")){
				String inputColumn = columns.get(i);
				inputColumns.add(inputColumn);
				mapValues.addFieldColumnPairs(new FieldColumnPair(feature.getName(), inputColumn));
			}
		}

//		String outputColumn = columns.get(columns.size() - 1);
		String outputColumn = "posa_country_code"; 

		mapValues.setOutputColumn(outputColumn);

		Map<String, List<Object>> data = parseMapping(inputColumns, outputColumn, map_1a);

		mapValues.setInlineTable(PMMLUtil.createInlineTable(data));

		List<Object> outputValues = new ArrayList<>();
		outputValues.addAll(data.get(outputColumn));

		DataType dataType = TypeUtil.getDataType(outputValues, DataType.STRING);

		mapValues.setDataType(dataType);

		FieldName name = FeatureUtil.createName("1a", features);

		DerivedField derivedField = encoder.createDerivedField(name, OpType.CATEGORICAL, dataType, mapValues);

		Feature feature = new Feature(encoder, derivedField.getName(), derivedField.getDataType()){

			@Override
			public ContinuousFeature toContinuousFeature(){
				PMMLEncoder encoder = getEncoder();

				DerivedField derivedField = (DerivedField)encoder.toContinuous(getName());

				return new ContinuousFeature(encoder, derivedField);
			}
		};

		return Collections.singletonList(feature);
	}

	protected List<String> formatColumns(List<Feature> features){
		ClassDictUtil.checkSize(1, features);

		return Arrays.asList("data:input", "data:output");
	}

	protected Map<String, List<Object>> parseMapping(List<String> inputColumns, String outputColumn, Map<?, ?> map_1a){
		List<Object> inputValues = new ArrayList<>();
		List<Object> outputValues = new ArrayList<>();

		Collection<? extends Map.Entry<?, ?>> entries = map_1a.entrySet();
		for(Map.Entry<?, ?> entry : entries){
			Object inputValue = entry.getKey();
			Object outputValue = entry.getValue();

			if(inputValue == null){
				throw new IllegalArgumentException();
			} // End if

			if(outputValue == null){
				continue;
			}

			inputValues.add(inputValue);
			outputValues.add(outputValue);
		}

		String inputColumn = inputColumns.get(0);

		Map<String, List<Object>> result = new LinkedHashMap<>();
		result.put(inputColumn, inputValues);
		result.put(outputColumn, outputValues);

		return result;
	}

	public Map<?, ?> getMapping(){
		return get("dataframe_1a", Map.class);
	}

}
