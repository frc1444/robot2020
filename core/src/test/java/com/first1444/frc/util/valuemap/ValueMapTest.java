package com.first1444.frc.util.valuemap;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

final class ValueMapTest {
	enum MyValue implements ValueKey{
		FRANK_AGE("frank age", ValueType.DOUBLE, 0.0),
		FRANK_HEIGHT("frank height", ValueType.DOUBLE, 100),
		FRANK_IS_MALE("frank is male", ValueType.BOOLEAN, true)
		;
		private final String name;
		private final ValueType type;
		private final Object defaultValue;

		MyValue(String name, ValueType type, Object defaultValue){
			this.name = name;
			this.type = type;
			this.defaultValue = defaultValue;
		}


		@Override
		public String getName() {
			return name;
		}

		@Override
		public ValueType getValueType() {
			return type;
		}
		public Object getDefaultValue(){
			return defaultValue;
		}
	}

	@Test
	void testValueMap(){
		final MutableValueMap<MyValue> valueMap = new MutableValueMap<>(MyValue.class);
		assertEquals(0, valueMap.getDouble(MyValue.FRANK_AGE));
		assertEquals(100, valueMap.getDouble(MyValue.FRANK_HEIGHT));
		assertTrue(valueMap.getBoolean(MyValue.FRANK_IS_MALE));

		valueMap.setDouble(MyValue.FRANK_AGE, 10);
		assertEquals(10, valueMap.getDouble(MyValue.FRANK_AGE));

		valueMap.setDouble(MyValue.FRANK_HEIGHT, 200);
		assertEquals(200, valueMap.getDouble(MyValue.FRANK_HEIGHT));

		valueMap.setBoolean(MyValue.FRANK_IS_MALE, false);
		assertFalse(valueMap.getBoolean(MyValue.FRANK_IS_MALE));

		assertThrows(IllegalArgumentException.class, () -> valueMap.setString(MyValue.FRANK_AGE, ""));
		assertThrows(IllegalArgumentException.class, () -> valueMap.setString(MyValue.FRANK_HEIGHT, ""));
		assertThrows(IllegalArgumentException.class, () -> valueMap.setString(MyValue.FRANK_IS_MALE, ""));

		final ValueMap<MyValue> copy = valueMap.build();
		assertEquals(10, copy.getDouble(MyValue.FRANK_AGE));
		assertEquals(200, copy.getDouble(MyValue.FRANK_HEIGHT));
		assertFalse(copy.getBoolean(MyValue.FRANK_IS_MALE));

		valueMap.setBoolean(MyValue.FRANK_IS_MALE, true); // make frank a guy
		assertTrue(valueMap.getBoolean(MyValue.FRANK_IS_MALE)); // make sure it changed
		assertFalse(copy.getBoolean(MyValue.FRANK_IS_MALE)); // make sure it didn't mutate
	}
}
