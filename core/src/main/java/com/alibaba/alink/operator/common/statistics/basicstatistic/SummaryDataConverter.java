package com.alibaba.alink.operator.common.statistics.basicstatistic;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.ml.api.misc.param.Params;

import com.alibaba.alink.common.linalg.VectorUtil;
import com.alibaba.alink.common.model.SimpleModelDataConverter;
import com.alibaba.alink.common.utils.JsonConverter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * summary data converter.
 */
public class SummaryDataConverter extends SimpleModelDataConverter <TableSummary, TableSummary> {
	/**
	 * default constructor.
	 */
	public SummaryDataConverter() {
	}

	@Override
	public Tuple2 <Params, Iterable <String>> serializeModel(TableSummary summary) {
		List <String> data = null;
		if (summary != null) {
			data = new ArrayList <>();
			data.add(JsonConverter.toJson(summary.colNames));
			data.add(String.valueOf(summary.count));
			data.add(JsonConverter.toJson(summary.numericalColIndices));
			if(summary.count() != 0) {
				data.add(VectorUtil.toString(summary.numMissingValue));
				data.add(VectorUtil.toString(summary.sum));
				data.add(VectorUtil.toString(summary.squareSum));
				data.add(VectorUtil.toString(summary.min));
				data.add(VectorUtil.toString(summary.max));
				data.add(VectorUtil.toString(summary.normL1));
			}
		}

		return Tuple2.of(new Params(), data);
	}

	/**
	 * Deserialize the model data.
	 *
	 * @param meta The model meta data.
	 * @param data The model concrete data.
	 * @return The deserialized model data.
	 */
	@Override
	public TableSummary deserializeModel(Params meta, Iterable <String> data) {
		if (data == null) {
			return null;
		}

		Iterator <String> dataIterator = data.iterator();
		TableSummary summary = new TableSummary();
		summary.colNames = JsonConverter.fromJson(dataIterator.next(), String[].class);
		summary.count = Long.parseLong(dataIterator.next());
		summary.numericalColIndices = JsonConverter.fromJson(dataIterator.next(), int[].class);
		if(summary.count != 0) {
			summary.numMissingValue = VectorUtil.parseDense(dataIterator.next());
			if(dataIterator.hasNext()) {
				summary.sum = VectorUtil.parseDense(dataIterator.next());
				summary.squareSum = VectorUtil.parseDense(dataIterator.next());
				summary.min = VectorUtil.parseDense(dataIterator.next());
				summary.max = VectorUtil.parseDense(dataIterator.next());
				summary.normL1 = VectorUtil.parseDense(dataIterator.next());
			}
		}

		return summary;
	}
}
