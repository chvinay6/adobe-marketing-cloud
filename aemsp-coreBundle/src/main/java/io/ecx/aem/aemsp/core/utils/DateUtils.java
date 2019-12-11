package io.ecx.aem.aemsp.core.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;

import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.value.ValueFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * i.mihalina
 */
public final class DateUtils
{
    private static final Logger LOG = LoggerFactory.getLogger(DateUtils.class);
    public static final DateFormat RFC822_DATE_FORMATTER = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss Z", Locale.US);
    public static final String DISPLY_DATE_FORMAT = "dd.MM.yyyy - HH:mm";
    public static final String NO_TIME_FORMAT = "dd.MM.yyyy";

    private DateUtils()
    {

    }

    public static Calendar dateToCalendar(final Date date)
    {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static String formatDateToString(final Date date, final String formatting, final Locale locale)
    {
        String result = StringUtils.EMPTY;

        if (date != null)
        {
            final SimpleDateFormat format = new SimpleDateFormat(formatting, locale);
            result = format.format(date);
        }

        return result;
    }

    public static String getCalendarStringFromDate(final Date date) throws RepositoryException
    {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return ValueFactoryImpl.getInstance().createValue(calendar).getString();
    }

    public static String formatRssDate(final Calendar calendar)
    {
        String result = StringUtils.EMPTY;
        try
        {
            calendar.setTimeZone(new SimpleTimeZone(0, "UTC"));
            synchronized (RFC822_DATE_FORMATTER)
            {
                result = RFC822_DATE_FORMATTER.format(calendar.getTime());
            }
        }
        catch (final Exception ex)
        {
            LOG.warn("can't format date", ex);
        }
        return result;
    }

    public static String getFormattedJCRDate(final Date date)
    {
        final SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", new Locale("de", "AT"));
        return inputDateFormat.format(date).replaceAll("(.*)(\\d\\d)$", "$1:$2");
    }

}
