package io.ecx.aem.aemsp.core.slingmodels;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

import io.ecx.aem.aemsp.core.utils.TextUtils;

@Model(adaptables = Resource.class)
public class AemUserModel
{
    @Inject
    @Optional
    @Named("profile/givenName")
    private String name;

    @Inject
    @Optional
    @Named("profile/familyName")
    private String lastname;

    @Inject
    @Optional
    @Named("profile/email")
    private String email;

    @Inject
    @Optional
    @Named("preferences/language")
    private String language;

    @Inject
    @Optional
    @Named("profile/aboutMe")
    private String aboutMe;

    public String getName()
    {
        return name;
    }

    public String getLastname()
    {
        return lastname;
    }

    public String getEmail()
    {
        return email;
    }

    public String getLanguage()
    {
        return language;
    }

    public String getAboutMe()
    {
        return aboutMe;
    }

    public String getDisplayName()
    {
        return TextUtils.appendStringObjects(name, StringUtils.isNotBlank(name) ? " " : StringUtils.EMPTY, lastname);
    }

}
