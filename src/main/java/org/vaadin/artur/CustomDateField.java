package org.vaadin.artur;

import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

import com.vaadin.data.Property;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

public class CustomDateField extends CustomField<Date> {

	HorizontalLayout layout = new HorizontalLayout();
	TextField y, m, d;
	private boolean updatingDateFromOneField = false;

	private ValueChangeListener updateValueFromFields = new ValueChangeListener() {

		@Override
		public void valueChange(Property.ValueChangeEvent event) {
			getLogger().info(
					"valueChange of field " + getField(event) + " ("
							+ updatingFieldsFromDate + ")");

			if (updatingFieldsFromDate) {
				// Avoid infinite loop, see updateFieldsFromDate
				return;
			}

			updatingDateFromOneField = true;
			try {
				if (!d.isValid() || !m.isValid() || !y.isValid()) {
					setValue(null);
				} else {
					Date date = new Date(
							(Integer) y.getConvertedValue() - 1900,
							(Integer) m.getConvertedValue() - 1,
							(Integer) d.getConvertedValue());
					setValue(date);
				}
			} finally {
				updatingDateFromOneField = false;
			}

		}

		private String getField(com.vaadin.data.Property.ValueChangeEvent event) {
			if (event.getProperty() == d) {
				return "d";
			} else if (event.getProperty() == m) {
				return "m";
			} else {
				return "y";
			}

		}
	};
	private boolean updatingFieldsFromDate = false;

	StringToIntegerConverter integerConverter = new StringToIntegerConverter() {
		@Override
		protected NumberFormat getFormat(Locale locale) {
			NumberFormat f = super.getFormat(locale);
			f.setGroupingUsed(false);
			return f;
		}
	};

	@Override
	protected Component initContent() {
		// FIXME IntegerField/NumberField would be handy...
		d = new TextField("Day");
		d.setConverter(integerConverter);
		// d.setRequired(true);
		d.setWidth("4em");

		m = new TextField("Month");
		m.setConverter(integerConverter);
		// m.setRequired(true);
		m.setWidth("4em");

		y = new TextField("Year");
		y.setConverter(integerConverter);
		// y.setRequired(true);
		y.setWidth("6em");

		d.setNullRepresentation("");
		m.setNullRepresentation("");
		y.setNullRepresentation("");

		final DayValidator dayValidator = new DayValidator();
		d.addValidator(dayValidator);

		IntegerRangeValidator monthValidator = new IntegerRangeValidator(
				"Month must be between 1 and 12", 1, 12);
		m.addValidator(monthValidator);

		IntegerRangeValidator yearValidator = new IntegerRangeValidator(
				"Year must be between 1900 and 2100", 1900, 2100);
		y.addValidator(yearValidator);

		m.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(
					com.vaadin.data.Property.ValueChangeEvent event) {
				int month;
				if (m.getValue() == null) {
					month = 1;
				} else {
					month = (Integer) m.getConvertedValue();
				}
				dayValidator.setMonth(month);

				// Retrigger validation so d error status is updated also
				d.markAsDirty();
			}
		});
		layout.addComponents(d, m, y);

		d.addValueChangeListener(updateValueFromFields);
		m.addValueChangeListener(updateValueFromFields);
		y.addValueChangeListener(updateValueFromFields);
		updateFieldsFromDate(getValue());
		return layout;
	}

	@Override
	protected void setInternalValue(Date newValue) {
		getLogger().info("setInternalValue(" + newValue + ")");
		// Ugh, this must be called before updateFields to avoid an infinite
		// loop
		super.setInternalValue(newValue);
		// Ugh, this can be called before initContent
		if (d != null) {
			updateFieldsFromDate(newValue);
		}
	}

	private void updateFieldsFromDate(Date newValue) {
		getLogger().info("updateFieldsFromValue(" + newValue + ")");
		// Avoid infinite loop when updating value as d.setValue calls
		// updateValueFromFields, which would call setValue, which would
		// call this.
		// FIXME: isUserOriginated on events would help...
		updatingFieldsFromDate = true;
		try {
			if (newValue == null) {
				if (updatingDateFromOneField) {
					// Don't reset all fields if one field is set to an invalid
					// value
					return;
				}
				d.setValue("");
				m.setValue("");
				y.setValue("");
			} else {
				d.setConvertedValue(newValue.getDate());
				m.setConvertedValue(newValue.getMonth() + 1);
				y.setConvertedValue(newValue.getYear() + 1900);
			}
		} finally {
			updatingFieldsFromDate = false;
		}
	}

	@Override
	public Class<? extends Date> getType() {
		return Date.class;
	}

	private Logger getLogger() {
		return Logger.getLogger(CustomDateField.class.getName());

	}
}
