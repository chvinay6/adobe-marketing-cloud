package io.ecx.aem.aemsp.core.components.par.relatedarticle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.RangeIterator;
import com.day.cq.i18n.I18n;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.slingmodels.ArticlePageModel;
import io.ecx.aem.aemsp.core.utils.ArticleUtils;
import io.ecx.aem.aemsp.core.utils.TagUtils;
import io.ecx.aem.aemsp.core.utils.TextUtils;
import io.ecx.cq.common.core.CqExceptionHandler;
import io.ecx.cq.common.core.components.par.ImageSightlyComponentBean;
import io.ecx.cq.common.core.vo.par.ImageResponsiveVO;

@Model(adaptables = Resource.class)
public class RelatedArticleComponentBean extends ImageSightlyComponentBean
{
    private static final Logger LOG = LoggerFactory.getLogger(RelatedArticleComponentBean.class);

    private static final String LIMIT_PROPERTY = "limit";
    private static final String ARTICLES_PROPERTY = "articles";
    private static final String I18N_RELATED_ARTICLE_PRE_ERROR = "aemsp_relatedArticlesNotValidPreError";
    private static final String I18N_RELATED_ARTICLE_CURRENT_ERROR = "aemsp_articleNotValidCurrentArticleError";
    private static final String HTML_BR_TAG = "<br />";

    private I18n i18n;

    private List<ArticleListItemVO> relatedArticles;
    private String errorMessage;
    private boolean selectionValid = true;
    private String breakingNewsText;

    private final Set<String> relatedArticleResourceSet = new HashSet<>();

    @Override
    protected void init() throws Exception
    {
        getParameter();

        relatedArticles = new ArrayList<>();
        i18n = new I18n(getRequest());

        final ValueMap properties = getProperties();
        final String[] articlePaths = properties.get(ARTICLES_PROPERTY, new String[] {});
        final int limit = Integer.parseInt(properties.get(LIMIT_PROPERTY, "-1"));
        getManuallySetArticles(articlePaths);
        if (relatedArticles.size() < limit)
        {
            final Page parent = getNearestCategoryParent();
            getTagSearchArticles(limit, parent);
            if (relatedArticles.size() < limit)
            {
                getArticlesInCategory(limit, parent);
            }
        }
        relatedArticles.sort(new Comparator<ArticleListItemVO>()
        {
            @Override
            public int compare(ArticleListItemVO listItem1, ArticleListItemVO listItem2)
            {
                final ArticlePageModel article1Page = listItem1.getArticle();
                final ArticlePageModel article2Page = listItem2.getArticle();
                return -article1Page.getPublishDate().compareTo(article2Page.getPublishDate());
            }
        });
    }

    private void getArticlesInCategory(final int limit, final Page parent) throws RepositoryException, InvalidQueryException, ItemNotFoundException, AccessDeniedException, Exception
    {
        final ResourceResolver resourceResolver = getResourceResolver();
        final Session session = resourceResolver.adaptTo(Session.class);
        final QueryManager queryManager = session.getWorkspace().getQueryManager();
        final String queryString = generateQueryString(parent.getPath(), getCurrentPage().getPath());
        final Query query = queryManager.createQuery(queryString, Query.JCR_SQL2);
        query.setLimit(limit);
        final NodeIterator queryResult = query.execute().getNodes();
        while (queryResult.hasNext() && relatedArticles.size() < limit)
        {
            final Node nextNode = queryResult.nextNode();
            final String articlePagePath = nextNode.getParent().getPath();
            final Resource articleResource = resourceResolver.getResource(articlePagePath);
            addArticleListItem(articleResource);
        }
    }

    private String generateQueryString(final String categoryPath, final String articlePath)
    {

        return TextUtils.appendStringObjects("SELECT * FROM [cq:PageContent] AS page WHERE ISDESCENDANTNODE([", categoryPath, "]) AND NOT ISDESCENDANTNODE([", articlePath, "])  AND page.[", ResourceResolver.PROPERTY_RESOURCE_TYPE, "] = '", GlobalConstants.ARTICLE_PAGE_RESOURCETYPE, "' AND page.[", GlobalConstants.ARTICLE_PUBLISH_DATE_PROPERTY, "] IS NOT NULL ORDER BY page.[", GlobalConstants.ARTICLE_PUBLISH_DATE_PROPERTY, "] DESC");
    }

    private void getTagSearchArticles(final int limit, final Page parent) throws Exception
    {
        final List<Resource> relatedByTags = getRelatedByTags(parent);
        int i = 0;
        while (relatedArticles.size() < limit && i < relatedByTags.size())
        {
            final Resource currentArticleResource = relatedByTags.get(i).getParent();
            addArticleListItem(currentArticleResource);
            i++;
        }
    }

    private void getManuallySetArticles(final String[] articlePaths)
    {
        try
        {
            final ResourceResolver resourceResolver = getResourceResolver();
            final Page languagePage = getCurrentPage().getAbsoluteParent(GlobalConstants.LANGUAGE_PAGE_DEPTH);
            breakingNewsText = languagePage.getProperties().get(GlobalConstants.BREAKING_NEWS_TEXT_PROPERTY, String.class);
            final String currentLanguageNode = languagePage.getName();
            String validationMessage = StringUtils.EMPTY;
            for (final String path : articlePaths)
            {
                final Resource articleResource = resourceResolver.getResource(path);
                if (StringUtils.isBlank(path) || articleResource == null)
                {
                    continue;
                }
                if (StringUtils.equals(path, getCurrentPage().getPath()))
                {
                    validationMessage = i18n.get(I18N_RELATED_ARTICLE_CURRENT_ERROR);
                }
                else
                {
                    validationMessage = ArticleUtils.isValidArticle(articleResource, currentLanguageNode, i18n);
                }
                if (StringUtils.equals(validationMessage, ArticleUtils.ARTICLE_VALID))
                {
                    addArticleListItem(articleResource);
                }
                else
                {
                    selectionValid = false;
                    errorMessage = TextUtils.appendStringObjects(errorMessage, path, " - ", validationMessage, HTML_BR_TAG);
                }
            }
        }
        catch (final Exception ex)
        {
            selectionValid = false;
            CqExceptionHandler.handleException(ex, LOG);
        }
    }

    private List<Resource> getRelatedByTags(final Page parent)
    {
        List<Resource> relatedPublished = new LinkedList<Resource>();
        final TagManager tagManager = getResourceResolver().adaptTo(TagManager.class);
        final Tag[] articleTags = tagManager.getTags(getCurrentPage().getContentResource());

        final String[] articleTagIDs = TagUtils.getTagIds(articleTags);

        final String categoryPath = parent.getPath();
        final RangeIterator<Resource> relatedAll = tagManager.find(categoryPath, articleTagIDs, true);
        if (relatedAll != null && relatedAll.hasNext())
        {
            final ImmutableList<Resource> iml = ImmutableList.copyOf(relatedAll);
            // search only among published articles and eliminate the current article from results
            relatedPublished = Lists.newLinkedList(Iterables.filter(iml, new Predicate<Resource>()
            {
                @Override
                public boolean apply(Resource articleContent)
                {
                    final ArticlePageModel articlePage = articleContent.getParent().adaptTo(ArticlePageModel.class);
                    return articlePage != null && articlePage.getPublishDate() != null && !StringUtils.equals(getCurrentPage().getPath(), articleContent.getParent().getPath());
                }
            }));

            relatedPublished.sort(new Comparator<Resource>()
            {
                @Override
                public int compare(Resource article1Content, Resource article2Content)
                {
                    final ArticlePageModel article1Page = article1Content.getParent().adaptTo(ArticlePageModel.class);
                    final ArticlePageModel article2Page = article2Content.getParent().adaptTo(ArticlePageModel.class);
                    return -article1Page.getPublishDate().compareTo(article2Page.getPublishDate());
                }
            });
        }

        return relatedPublished;
    }

    private Page getNearestCategoryParent()
    {
        Page parent = getCurrentPage().getParent();
        String parentTemplate = parent.getContentResource().getValueMap().get(NameConstants.NN_TEMPLATE, StringUtils.EMPTY);
        while (!StringUtils.equals(parentTemplate, GlobalConstants.CATEGORY_PAGE_TEMPLATE))
        {
            parent = parent.getParent();
            parentTemplate = parent.getContentResource().getValueMap().get(NameConstants.NN_TEMPLATE, StringUtils.EMPTY);
        }
        return parent;
    }

    private void addArticleListItem(final Resource articleResource) throws Exception
    {
        if (!relatedArticleResourceSet.contains(articleResource.getPath()))
        {
            relatedArticleResourceSet.add(articleResource.getPath());

            final ArticlePageModel article = articleResource.adaptTo(ArticlePageModel.class);
            final Resource imageResource = article.getArticleThumbnailResource();
            final String originalRendition = renditionSelector;

            final ImageResponsiveVO image = new ImageResponsiveVO(imageResource, getImageRenditionInfos());

            renditionSelector = TextUtils.appendStringObjects(originalRendition, "_tablet");
            final ImageResponsiveVO tabletImage = new ImageResponsiveVO(imageResource, getImageRenditionInfos());

            renditionSelector = TextUtils.appendStringObjects(originalRendition, "_mobile");
            final ImageResponsiveVO mobileImage = new ImageResponsiveVO(imageResource, getImageRenditionInfos());

            renditionSelector = originalRendition;
            final ArticleListItemVO listItem = new ArticleListItemVO(article, getResourceResolver().map(articleResource.getPath()), image, tabletImage, mobileImage);
            relatedArticles.add(listItem);
        }

    }

    public List<ArticleListItemVO> getRelatedArticles()
    {
        return relatedArticles;
    }

    public boolean isSelectionValid()
    {
        return selectionValid;
    }

    public String getBreakingNewsText()
    {
        return breakingNewsText;
    }

    public String getErrorMessage()
    {
        return TextUtils.appendStringObjects(i18n.get(I18N_RELATED_ARTICLE_PRE_ERROR), HTML_BR_TAG, errorMessage);
    }

}
