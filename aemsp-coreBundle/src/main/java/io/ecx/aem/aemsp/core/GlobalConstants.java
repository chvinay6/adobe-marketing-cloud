package io.ecx.aem.aemsp.core;

import java.util.Locale;

/**
 * Contains static variables for the project.
 *
 * @author i.mihalina
 */
public final class GlobalConstants
{
    public static final String ARTICLE_PAGE_RESOURCETYPE = "aemsp/components/page/articlepage";
    public static final String COMMERCIAL_PAGE_RESOURCETYPE = "aemsp/components/page/commentrypage";
    public static final String OFFER_PAGE_RESOURCETYPE = "aemsp/components/page/biddingpage";
    public static final String EVENT_PAGE_RESOURCETYPE = "aemsp/components/page/eventpage";
    public static final String ARTICLE_PAGE_TEMPLATE = "/apps/aemsp/templates/articlepage";
    public static final String LANGUAGE_PAGE_TEMPLATE = "/apps/aemsp/templates/languagepage";
    public static final String GROUP_PAGE_TEMPLATE = "/apps/aemsp/templates/grouppage";
    public static final String CATEGORY_PAGE_TEMPLATE = "/apps/aemsp/templates/categorypage";
    public static final String CONTENT_PAGE_TEMPLATE = "/apps/aemsp/templates/contentpage";
    public static final String VOTING_MODULE_PAGE_TEMPLATE = "/apps/aemsp/templates/votingmodulepage";
    public static final String HOMEPAGE_CONFIG_PAGE_TEMPLATE = "/apps/aemsp/templates/homeconfigpage";
    public static final String OFFER_CONFIG_PAGE_TEMPLATE = "/apps/aemsp/templates/offerpage";
    public static final String EVENT_CONFIG_PAGE_TEMPLATE = "/apps/aemsp/templates/calendarpage";
    public static final String OFFER_PAGE_TEMPLATE = "/apps/aemsp/templates/biddingpage";
    public static final String EVENT_PAGE_TEMPLATE = "/apps/aemsp/templates/eventpage";
    public static final String ARTICLE_PUBLISHER_PROPERTY = "articlePublisher";
    public static final String ARTICLE_PUBLISHER_FULL_NAME_PROPERTY = "articlePublisherFullName";
    public static final String ARTICLE_PUBLISH_DATE_PROPERTY = "publishDate";
    public static final String COMMERCIAL_DATE_PROPERTY = "date";
    public static final String OFFER_DATE_PROPERTY = "endDate";
    public static final String ARTICLE_MAINHEADLINE_PROPERTY = "./parHead/articlemainheadline/text";
    public static final String MAIN_CONTENT = "mainContent";
    public static final String SIDEBAR_CONTENT = "sidebarContent";
    public static final String PAR_ARTICLE_IMAGE = "parArticleImage";
    public static final String PAR_HEAD = "parHead";
    public static final String PAR_TEASER = "parTeaser";
    public static final String PAR_RTE = "rtePar";
    public static final String TEXT_PROPERTY = "text";
    public static final String ARTICLE_IMAGE_COMPONENT_NAME = "articleimage";
    public static final String ARTICLE_PREFIX_COMPONENT_NAME = "articleprefix";
    public static final String MAINHEADLINE_COMPONENT_NAME = "articlemainheadline";
    public static final String SUBLINE_COMPONENT_NAME = "articlesubline";
    public static final String ARTICLE_TEASER_COMPONENT_NAME = "articleteaser";
    public static final String ARTICLE_THUMBNAIL_COMPONENT_NAME = "articleThumbnail";
    public static final String MAGAZINE_BACKSHAREBAR_POSITION_PROPERTY = "backSharePosition";
    public static final String MAGAZINE_BACKSHAREBAR_POSITION_NONE = "none";
    public static final String MAGAZINE_BROWSERBANNER_LINK_PROPERTY = "browserLink";
    public static final String MAGAZINE_COOKIEBANNER_LINK_PROPERTY = "cookieLink";
    public static final String MAGAZINE_LOGO_PATH_PROPERTY = "logoPath";
    public static final String BREAKING_NEWS_TEXT_PROPERTY = "breakingNewsText";
    public static final String BREAKING_NEWS_DATE_PROPERTY = "breakingNewsDate";
    public static final String DEFAULT_LANGUAGE = "de";
    public static final Locale DEFAULT_LANGUAGE_LOCALE = Locale.GERMAN;
    public static final String LANGUAGE_GERMAN = "de";
    public static final String LANGUAGE_ENGLISH = "en";
    public static final String LANGUAGE_ITALIAN = "it";
    public static final int CATEGORY_PAGE_DEPTH = 4;
    public static final int LANGUAGE_PAGE_DEPTH = 3;
    public static final int SITE_PAGE_DEPTH = 2;
    public static final int MAGAZINE_PAGE_DEPTH = 1;
    public static final int MAIN_NAVIGATION_DEPTH = 3;
    public static final String NAV_HIDE_IN_NAV = "hideInNav";
    public static final String TYPE_JCR_PATH = "JCR_PATH";
    public static final String HTML_EXTENSION = ".html";
    public static final String CONTACT_EXTENSION = "contact";
    public static final String DEFAULT_SERVLET_SELECTOR = "fitIn";
    public static final String DEFAULT_FILE_EXTENSION = "*";
    public static final String DEFAULT_IMAGE_FORMAT = ".jpg";
    public static final String DEFAULT_IMAGE_FILEREFERENCE_PROPERTY = "fileReference";
    public static final String DEFAULT_IMAGE_FILENAME_PROPERTY = "fileName";
    public static final String VOTING_MODULE_PATH_PROPERTY = "votingModulePath";
    public static final String EVENT_PAGE_FROMDATE_PROPERTY = "fromDate";

    private GlobalConstants()
    {
    }

}
