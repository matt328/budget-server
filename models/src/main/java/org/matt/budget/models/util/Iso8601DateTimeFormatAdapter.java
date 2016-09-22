package org.matt.budget.models.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class Iso8601DateTimeFormatAdapter extends XmlAdapter<String, Date> {

  private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS");

  @Override
  public String marshal(Date date) {
    if (date == null) {
      return null;
    }

    return dateFormat.format(date);
  }

  @Override
  public Date unmarshal(String date) throws Exception {
    if (date == null || date.isEmpty()) {
      return null;
    }

    return dateFormat.parse(date);
  }
}
