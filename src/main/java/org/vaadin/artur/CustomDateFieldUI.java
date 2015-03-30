package org.vaadin.artur;

import java.text.DateFormat;
import java.util.Date;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.validator.DateRangeValidator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("valo")
public class CustomDateFieldUI extends UI {

	private DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM);

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		setContent(layout);
		final Label log = new Label("Log:", ContentMode.PREFORMATTED);
		final CustomDateField d = new CustomDateField();
		d.setCaption("Enter the date");
		d.setValue(new Date(2015 - 1900, 12 - 1, 31));

		layout.addComponents(d, log);
		d.setRequired(true);
		Date minValue = new Date(2000 - 1900, 1 - 1, 1);
		Date maxValue = new Date(2050 - 1900, 5 - 1, 31);
		d.addValidator(new DateRangeValidator("The date must be between "
				+ format.format(minValue) + " and " + format.format(maxValue),
				minValue, maxValue, Resolution.DAY));

		d.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				Date date = d.getValue();
				String dateString;
				if (date != null) {
					dateString = format.format(date);
				} else {
					dateString = "null";
				}

				log.setValue(log.getValue() + "\nValue changed to: "
						+ dateString);
			}
		});

	}

	@WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = CustomDateFieldUI.class, productionMode = false)
	public static class MyUIServlet extends VaadinServlet {
	}
}
