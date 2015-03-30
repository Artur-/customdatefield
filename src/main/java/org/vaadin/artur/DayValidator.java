package org.vaadin.artur;

import com.vaadin.data.validator.IntegerRangeValidator;

public class DayValidator extends IntegerRangeValidator {

	public DayValidator() {
		super("", 1, 31);
	}

	public void setMonth(int month) {
		int daysInMonth = getDaysInMonth(month);
		setMaxValue(daysInMonth);
	}

	@Override
	public String getErrorMessage() {
		// FIXME Can't I refer to range in a simple way?? e.g using
		// setErrormMessage("Day must be between {0} and {1}")
		return "Must be between " + getMinValue() + " and " + getMaxValue();
	}

	@Override
	protected boolean isValidValue(Integer value) {
		// FIXME: Null is not valid in this case.
		// Maybe a setter would be useful

		// Can't use required because this is used inside a custom field and
		// required on every sub field looks stupid
		if (value == null) {
			return false;
		}
		return super.isValidValue(value);
	}

	private static int getDaysInMonth(int month) {
		if (month == 2) {
			return 28; // Simplified for this example
		} else if (month == 1 || month == 3 || month == 5 || month == 7
				|| month == 8 || month == 10 || month == 12) {
			return 31;
		} else {
			return 30;
		}
	}

}
