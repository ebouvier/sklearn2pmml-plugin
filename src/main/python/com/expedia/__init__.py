import pandas as pd
from sklearn.base import BaseEstimator, TransformerMixin

class AttributesAdder(BaseEstimator, TransformerMixin):
        def __init__(self,
                     dataframe_1a = None
                     ):
            self._map_1a = dataframe_1a
        def fit(self, X, y=None):
            return self  
        def transform(self, X, y=None):
            # step 1a
            X = pd.DataFrame.merge(X, self._map_1a, on = 'tpid' )
            return X

import numpy
from collections import defaultdict
from sklearn.utils import column_or_1d

class MyLookupTransformer(BaseEstimator, TransformerMixin):

	def __init__(self, mapping, default_value):
		if type(mapping) is not dict:
			raise ValueError("Input value to output value mapping is not a dict")
		for k, v in mapping.items():
			if k is None:
				raise ValueError("Key is None")
		self.mapping = mapping
		self.default_value = default_value

	def _transform_dict(self):
		transform_dict = defaultdict(lambda: self.default_value)
		transform_dict.update(self.mapping)
		return transform_dict

	def fit(self, X, y = None):
		X = column_or_1d(X, warn = True)
		return self

	def transform(self, X):
		X = column_or_1d(X, warn = True)
		transform_dict = self._transform_dict()
		func = lambda k: transform_dict[k]
		if hasattr(X, "apply"):
			return X.apply(func)
		return numpy.vectorize(func)(X).reshape(len(X), 1)
